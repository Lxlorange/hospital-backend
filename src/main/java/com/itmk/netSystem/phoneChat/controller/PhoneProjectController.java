package com.itmk.netSystem.phoneChat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.itmk.config.service.GeetestService;
import com.itmk.netSystem.call.entity.CallPage;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.evaluate.entity.Suggest;
import com.itmk.netSystem.evaluate.service.EvaluateService;
import com.itmk.netSystem.journal.entity.News;
import com.itmk.netSystem.journal.service.JournalService;
import com.itmk.netSystem.phoneChat.entity.*;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.service.SeeService;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.treatpatient.service.TreatPatientService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.waitlist.service.WaitlistService;
import com.itmk.tool.Utils;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



/**
 * @description: 手机端API接口控制器
 */
@RequestMapping("/wxapi/allApi")
@RestController
public class PhoneProjectController {
    @Autowired
    private UserPatientPhoneService userPatientPhoneService;
    @Autowired
    private JournalService journalService;
    @Autowired
    private teamDepartmentService teamDepartmentService;
    @Autowired
    private userWebService userWebService;
    @Autowired
    private setWorkService setWorkService;
    @Autowired
    private TreatPatientService treatPatientService;
    @Autowired
    private CallService callService;
    @Autowired
    private SeeService seeService;
    @Autowired
    private Utils jwtUtils;
    // 注入 JavaMailSender，用于发送邮件（需在 application.properties 配置 smtp）
    @Autowired
    private JavaMailSender mailSender;
    //@Autowired
    //private AdviceService adviceService;
    @Autowired
    private GeetestService geetestService;

    @Autowired
    private EvaluateService evaluateService;
    @Autowired
    private WaitlistService waitlistService;
    private int scheduleQueryDays=12;
    @GetMapping("/config/scheduleQueryDays")
    public ResultVo getScheduleQueryDays() {
        return ResultUtils.success("success", scheduleQueryDays);
    }

    @PostMapping("/config/scheduleQueryDays")
    public ResultVo setScheduleQueryDays(@RequestBody Map<String, Integer> body) {
        Integer days = body != null ? body.get("days") : null;
        if (days == null) {
            return ResultUtils.error("缺少参数: days");
        }
        if (days < 1 || days > 60) {
            return ResultUtils.error("设置范围为1-60天");
        }
        scheduleQueryDays = days;
        return ResultUtils.success("success", scheduleQueryDays);
    }
    // 创建一个 API 限流器缓存 (防“飞快抢号”)
    // 缓存 10000 个用户，10分钟后过期
    private final Cache<String, RateLimiter> userRateLimiters = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();


    /**
     * @description: 根据新闻ID获取单篇新闻的详细内容。
     * @param newsId 新闻文章的唯一标识ID
     * @return 返回包含新闻详细信息的结果。
     */
    @GetMapping("/getNewsById")
    public ResultVo getNewsById(@RequestParam("newsId") Integer newsId) {
        News news = journalService.getById(newsId);
        if (news != null) {
            return ResultUtils.success("成功", news);
        }
        return ResultUtils.error("未找到该新闻");
    }

    /**
     * @description: 根据医生ID获取医生的详细信息，包括所属科室。
     * @param doctorId 医生的唯一标识ID
     * @return 返回包含医生详细信息的结果。
     */
    @GetMapping("/getDoctorById")
    public ResultVo getDoctorById(@RequestParam("doctorId") Integer doctorId) {
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .eq(SysUser::getUserId, doctorId);
        SysUser doctor = userWebService.getOne(query);
        if (doctor != null) {
            return ResultUtils.success("成功", doctor);
        }
        return ResultUtils.error("未找到");
    }


    /**
     * @description: 为小程序用户更新登录密码。
     * @param parm 包含用户ID、旧密码和新密码的参数对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody ResetPasswordNum parm){
        WxUser user = userPatientPhoneService.getById(parm.getUserId());
        // 验证旧密码是否正确
        if(!DigestUtils.md5DigestAsHex(parm.getOldPassword().getBytes()).equals(user.getPassword())){
            return ResultUtils.error("原来的密码不正确！");
        }
        WxUser wxUser = new WxUser();
        // 对新密码进行MD5加密
        wxUser.setPassword(DigestUtils.md5DigestAsHex(parm.getPassword().getBytes()));
        wxUser.setUserId(parm.getUserId());
        if(userPatientPhoneService.updateById(wxUser)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    /**
     * @description: 更新小程序用户的个人基本信息。
     * @param wxUser 包含待更新用户信息的用户对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/updateUserInfo")
    public ResultVo updateUserInfo(@RequestBody WxUser wxUser){
        if (wxUser == null || wxUser.getUserId() == null) {
            return ResultUtils.error("参数错误");
        }
        WxUser existing = userPatientPhoneService.getById(wxUser.getUserId());
        if (existing == null) {
            return ResultUtils.error("用户不存在");
        }
        if (StringUtils.hasText(wxUser.getPhone())) {
            String phone = wxUser.getPhone().trim();
            if (!phone.matches("^\\d{11}$")) {
                return ResultUtils.error("手机号格式不正确");
            }
            WxUser byPhone = userPatientPhoneService.findByPhone(phone);
            if (byPhone != null && !byPhone.getUserId().equals(existing.getUserId())) {
                return ResultUtils.error("手机号已被占用");
            }
            existing.setPhone(phone);
        }
        if (StringUtils.hasText(wxUser.getEmail())) {
            String email = wxUser.getEmail().trim();
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.matches(emailRegex)) {
                return ResultUtils.error("邮箱格式不正确");
            }
            WxUser byEmail = userPatientPhoneService.findByEmail(email);
            if (byEmail != null && !byEmail.getUserId().equals(existing.getUserId())) {
                return ResultUtils.error("邮箱已被占用");
            }
            existing.setEmail(email);
        }
        if (StringUtils.hasText(wxUser.getNickName())) {
            existing.setNickName(wxUser.getNickName().trim());
        }
        if (StringUtils.hasText(wxUser.getName())) {
            existing.setName(wxUser.getName().trim());
        }
        if (StringUtils.hasText(wxUser.getSex())) {
            String sex = wxUser.getSex().trim();
            if (!("0".equals(sex) || "1".equals(sex))) {
                return ResultUtils.error("性别参数错误");
            }
            existing.setSex(sex);
        }
        if (StringUtils.hasText(wxUser.getImage())) {
            existing.setImage(wxUser.getImage().trim());
        }
        boolean ok = userPatientPhoneService.updateById(existing);
        if (ok) {
            return ResultUtils.success("成功！");
        }
        return ResultUtils.error("失败!");
    }



    /**
     * @description: 根据用户ID查询小程序用户的完整信息。
     * @param userId 小程序用户的唯一标识ID
     * @return 返回包含用户详细信息的结果视图对象
     */
    @GetMapping("/getWxUserById")
    public ResultVo getWxUserById(Integer userId){
        WxUser user = userPatientPhoneService.getById(userId);
        return ResultUtils.success("成功",user);
    }

    /**
     * @description: 获取所有科室及其下属医生的层级列表，用于预约挂号界面的数据展示。
     * @return 返回一个包含科室和医生信息的树状结构列表
     */
    @GetMapping("/getCategoryList")
    public ResultVo getCategoryList(){
        // 初始化最终返回的树形结构列表
        List<DepartmentTre> list = new ArrayList<>();
        // 查询所有科室，并按排序号升序排列
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByAsc(Department::getOrderNum);
        List<Department> departmentList = teamDepartmentService.list(query);
        if(departmentList.size() >0){
            for (int i=0;i<departmentList.size();i++){
                DepartmentTre departmentTre = new DepartmentTre();
                departmentTre.setName(departmentList.get(i).getDeptName());
                // 根据当前科室ID，查询该科室下的所有医生，并按姓名排序
                QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SysUser::getDeptId,departmentList.get(i).getDeptId())
                        .orderByAsc(SysUser::getNickName);
                List<SysUser> listU = userWebService.list(queryWrapper);
                List<UserReverse> userReverseList = new ArrayList<>();
                UserInformation userInformation = new UserInformation();
                userInformation.setName(departmentList.get(i).getDeptName());
                // 遍历医生列表，转换为前端需要的格式
                if(listU.size() >0){
                    for (int j=0;j<listU.size();j++){
                        UserReverse u = new UserReverse();
                        BeanUtils.copyProperties(listU.get(j),u);
                        u.setDeptName(departmentList.get(i).getDeptName());
                        userReverseList.add(u);
                    }
                }
                userInformation.setDesc(userReverseList);
                departmentTre.getChildrens().add(userInformation);
                list.add(departmentTre);
            }
        }
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 获取所有科室的扁平列表，用于筛选或展示。
     * @return 返回所有科室的列表。
     */
    @GetMapping("/getAllDepartments")
    public ResultVo getAllDepartments() {
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByAsc(Department::getOrderNum);
        List<Department> list = teamDepartmentService.list(query);
        return ResultUtils.success("成功", list);
    }

    /**
     * @description: 根据姓名关键字模糊搜索医生。
     * @param name 搜索的医生姓名关键字
     * @return 返回匹配的医生列表。
     */
    @GetMapping("/searchDoctorByName")
    public ResultVo searchDoctorByName(@RequestParam("name") String name) {
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .like(SysUser::getNickName, name);
        List<SysUser> list = userWebService.list(query);
        return ResultUtils.success("成功", list);
    }

    /**
     * @description: 分页查询新闻文章列表。
     * @param parm 包含当前页码和每页大小的分页参数对象
     * @return 返回包含新闻文章列表的分页结果
     */
    @GetMapping("/getNewsList")
    public ResultVo getNewsList(DoctorTeamPage parm){
        IPage<News> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().orderByDesc(News::getCreateTime);
        IPage<News> list = journalService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 查询特定医生的排班详情列表（从当天开始）。
     * @param userId   当前小程序用户ID (此方法中未使用)
     * @param doctorId 需要查询的医生ID
     * @return 返回该医生从当天起的排班信息列表
     */
    @GetMapping("/getDoctor")
    public ResultVo getDoctor(String userId,String doctorId){
        try {
            Long Id = Long.parseLong(doctorId);
            LocalDate date = LocalDate.now();
            LocalDate futureDate = LocalDate.now().plusDays(scheduleQueryDays);
            String now = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String future = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<ScheduleDetail> result= setWorkService.findSchedulesByDoctorAndDateRange(Id,now,future);
            return ResultUtils.success("成功",result);
        }catch (NumberFormatException e) {
            e.printStackTrace();
            return ResultUtils.error("错误的医生ID格式", 400);
        }
    }



    /**
     * @description: 获取被推荐到首页展示的新闻列表。
     * @return 返回推荐新闻的列表
     */
    @GetMapping("/getIndexNews")
    public ResultVo getIndexNews(){
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(News::getToIndex,"1")
                .orderByDesc(News::getCreateTime);
        List<News> list = journalService.list(query);
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 根据指定的科室ID，分页查询该科室下的医生列表。
     * @param parm 包含科室ID和分页信息的参数对象
     * @return 返回包含医生信息及所属科室名称的分页结果
     */
    @GetMapping("/getDoctorByDeptId")
    public ResultVo getDoctorByDeptId(DoctorTeamPage parm){
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(SysUser::getDeptId,parm.getDeptId());
        IPage<SysUser> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        IPage<SysUser> list = userWebService.page(page, query);
        return ResultUtils.success("成功",list);
    }


    /**
     * @description: 根据预约ID获取单个预约订单的详细信息。
     * @param makeId 预约订单的唯一标识ID
     * @return 返回预约订单的详细信息。
     */
    @GetMapping("/getOrderDetail")
    public ResultVo getOrderDetail(@RequestParam("makeId") Integer makeId) {
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(SysUser::getNickName)
                .select(Department::getDeptName)
                .select(VisitUser::getVisitname)
                .leftJoin(VisitUser.class, VisitUser::getVisitId, MakeOrder::getVisitUserId)
                .leftJoin(SysUser.class, SysUser::getUserId, MakeOrder::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                .eq(MakeOrder::getMakeId, makeId);
        MakeOrder order = callService.getOne(query);
        return ResultUtils.success("成功", order);
    }

    /**
     * @description: 根据就诊记录ID获取单次历史就诊的详细信息，包括医嘱。
     * @param visitId 就诊记录的唯一标识ID
     * @return 返回历史就诊的详细信息。
     */
    @GetMapping("/getVisitDetail")
    public ResultVo getVisitDetail(@RequestParam("visitId") Integer visitId) {
        MakeOrderVisit visit = seeService.getById(visitId);
        if (visit != null) {
            return ResultUtils.success("成功", visit);
        }
        return ResultUtils.error("未找到该记录");
    }

    /**
     * @description: 分页获取指定用户的历史就诊记录。
     * @param parm 包含用户ID和分页信息的参数对象
     * @return 返回包含就诊记录、医生、科室及就诊人信息的分页结果
     */
    @GetMapping("/getVisitOrderList")
    public ResultVo getVisitOrderList(CallPage parm){
        IPage<MakeOrderVisit> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        // 构造多表连接查询，关联就诊人、医生和科室表
        MPJLambdaWrapper<MakeOrderVisit> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrderVisit.class)
                .select(SysUser::getNickName)
                .select(Department::getDeptName)
                .select(VisitUser::getVisitname)
                .select(MakeOrder::getAddress)
                .select(MakeOrder::getPrice)
                .leftJoin(MakeOrder.class, MakeOrder::getMakeId, MakeOrderVisit::getMakeId)
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrderVisit::getVisitUserId)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrderVisit::getDoctorId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(MakeOrderVisit::getUserId,parm.getUserId())
                .orderByDesc(MakeOrderVisit::getCreateTime);
        IPage<MakeOrderVisit> list = seeService.page(page, query);
        return ResultUtils.success("成功",list);
    }







    /**
     * @description: 根据身份证号查询就诊人是否存在，用于添加就诊人前的校验。
     * @param idCard 身份证号码
     * @return 如果存在则返回就诊人信息，否则返回错误提示。
     */
    @GetMapping("/findPatientByIdCard")
    public ResultVo findPatientByIdCard(@RequestParam("idCard") String idCard) {
        QueryWrapper<VisitUser> query = new QueryWrapper<>();
        query.lambda().eq(VisitUser::getIdCard, idCard);
        VisitUser patient = treatPatientService.getOne(query);
        if (patient != null) {
            return ResultUtils.success("该就诊人已存在", patient);
        }
        return ResultUtils.error("系统中无此就诊人信息");
    }

    /**
     * @description: 获取特定排班ID的详细信息，包括剩余号源。
     * @param scheduleId 排班的唯一标识ID
     * @return 返回排班的详细信息。
     */
    @GetMapping("/getScheduleDetail")
    public ResultVo getScheduleDetail(@RequestParam("scheduleId") Long scheduleId) {
        ScheduleDetail detail = setWorkService.getById(scheduleId);
        if (detail != null) {
            return ResultUtils.success("成功", detail);
        }
        return ResultUtils.error("未找到该排班信息");
    }

    /**
     * @description: 修改已存在的就诊人信息。
     * @param visitUser 包含待更新信息的就诊人对象
     * @return 返回操作成功或失败的结果
     */
    @PutMapping("/visitEdit")
    public ResultVo visitEdit(@RequestBody VisitUser visitUser){
        if(treatPatientService.updateById(visitUser)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    /**
     * @description: 根据用户ID获取其关联的所有就诊人列表，格式化为适用于前端下拉选择框的格式。
     * @param userId 小程序用户的唯一标识ID
     * @return 返回一个精简的就诊人列表（ID和姓名）
     */
    @GetMapping("/getSelectVisitList")
    public ResultVo getSelectVisitList(Integer userId){
        // 根据用户ID查询其名下的所有就诊人
        QueryWrapper<VisitUser> query = new QueryWrapper<>();
        query.lambda().eq(VisitUser::getUserId,userId);
        List<VisitUser> list = treatPatientService.list(query);
        List<treat> treats = new ArrayList<>();
        if(list.size() > 0){
            // 将查询结果转换为前端需要的键值对格式
            for (int i=0;i<list.size();i++){
                treat vo = new treat();
                vo.setVisitId(list.get(i).getVisitId());
                vo.setName(list.get(i).getVisitname());
                treats.add(vo);
            }
        }
        return ResultUtils.success("成功", treats);
    }

    /**
     * @description: 获取小程序首页的统计数据，如科室、医生和新闻总数。
     * @return 返回包含各项统计数量的结果。
     */
    @GetMapping("/getHomeStats")
    public ResultVo getHomeStats() {
        long departmentCount = teamDepartmentService.count();
        long doctorCount = userWebService.count();
        long newsCount = journalService.count();
        return ResultUtils.success("查询成功");
    }

    /**
     * @description: 根据科室ID获取科室简介和旗下医生列表。
     * @param deptId 科室的唯一标识ID
     * @return 返回包含科室信息和医生列表的结果。
     */
    @GetMapping("/getDepartmentInfo")
    public ResultVo getDepartmentInfo(@RequestParam("deptId") Integer deptId) {
        Department department = teamDepartmentService.getById(deptId);
        if (department == null) {
            return ResultUtils.error("未找到该科室");
        }
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getDeptId, deptId);
        List<SysUser> doctors = userWebService.list(query);
        return ResultUtils.success("查询成功");
    }

    /**
     * @description: 获取被推荐到首页展示的科室列表（最多8个）。
     * @return 返回推荐科室的列表
     */
    @GetMapping("/getIndexDept")
    public ResultVo getIndexDept(){
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().eq(Department::getToHome,"1")
                .orderByAsc(Department::getOrderNum).last("limit 8");
        List<Department> list = teamDepartmentService.list(query);
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 编辑并更新小程序用户的个人信息。
     * @param wxUser 包含待更新内容的用户对象
     * @return 返回操作成功或失败的结果
     */
    @PutMapping("/edit")
    public ResultVo edit(@RequestBody WxUser wxUser){
        if(userPatientPhoneService.updateById(wxUser)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }



    private int scheduleQueryHours=6;
    private int scheduleQueryMinutes=30;
    @GetMapping("/config/scheduleQueryTime")
    public ResultVo getScheduleQueryTime() {
        return ResultUtils.success("success", scheduleQueryHours,scheduleQueryMinutes);
    }

    @PostMapping("/config/scheduleQueryTime")
    public ResultVo setScheduleQueryTime(@RequestBody Map<String, Integer> body) {
        Integer hours = body != null ? body.get("hours") : null;
        Integer minutes = body != null ? body.get("minutes") : null;
        if (hours == null || minutes == null) {
            return ResultUtils.error("缺少参数: hours 或 minutes");
        }
        if (hours < 0 || hours >= 24) {
            return ResultUtils.error("设置范围为0-23时");
        }
        if (minutes < 0 || minutes >= 60) {
            return ResultUtils.error("设置范围为0-59分");
        }
        scheduleQueryHours=hours;
        scheduleQueryMinutes=minutes;
        return ResultUtils.success("success", scheduleQueryHours,scheduleQueryMinutes);
    }
    /**
     * @description: 创建一个新的预约订单，这是一个事务性操作。
     * @param makeOrde 包含预约信息的订单对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/makeOrderAdd")
    @Transactional
    public ResultVo makeOrderAdd(@RequestBody MakeOrder makeOrde){
        // 人机验证 (防 Bot) ---
        // (注意: 必须能从 makeOrde 对象拿到 userId)
        String userId = makeOrde.getUserId().toString();
        boolean isHuman = geetestService.verify(makeOrde, userId);
        if (!isHuman) {
            return ResultUtils.error("安全验证未通过，请刷新重试",401);
        }

        // API 限流 (防“飞快抢号”) ---
        RateLimiter limiter;
        try {
            // 限制：每 5 秒 1 次请求 (0.2 = 1/5)
            // (你可以根据业务调整这个速率)
            limiter = userRateLimiters.get(userId, () -> RateLimiter.create(0.2));
        } catch (Exception e) {
            return ResultUtils.error("服务器繁忙，请稍后",500);
        }

        if (!limiter.tryAcquire()) {
            // 如果获取不到（即请求过快），立即拒绝
            return ResultUtils.error("您点击太快了，请 5 秒后再试",429);
        }
        // 从数据库查询排班信息，并使用行锁防止并发问题
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda().eq(ScheduleDetail::getScheduleId, makeOrde.getScheduleId()).last("for update");
        ScheduleDetail schedule = setWorkService.getOne(query);

        // 校验排班是否存在
        if (schedule == null) {
            return ResultUtils.error("无效的排班信息!");
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();
            LocalTime nowTime = now.toLocalTime();

            // 每日 6:30 开始放号
            LocalTime dailyReleaseStart = LocalTime.of(scheduleQueryHours, scheduleQueryMinutes);
            if (nowTime.isBefore(dailyReleaseStart)) {
            return ResultUtils.error("当前未到放号时间（每日"+scheduleQueryHours+":"+scheduleQueryMinutes+"开始）");
            }

            LocalDate appointmentDate = schedule.getTimes();
            if (appointmentDate == null) {
                return ResultUtils.error("排班日期异常，无法预约");
            }
            if (appointmentDate.isBefore(today)) {
                return ResultUtils.error("不能预约已过期号源");
            }
            if (appointmentDate.isAfter(today.plusDays(scheduleQueryDays))) {
                return ResultUtils.error("仅可预约"+scheduleQueryDays+"日内号源");
            }
        } catch (Exception ignored) {
            return ResultUtils.error("系统错误，无法校验放号规则");
        }
        // 检查剩余号源
        if(schedule.getLastAmount() <= 0){
            return ResultUtils.error("今日号数已经被预约完，请选择其他排班!");
        }

        QueryWrapper<MakeOrder> duplicateCheckQuery = new QueryWrapper<>();
        duplicateCheckQuery.lambda()
                .eq(MakeOrder::getVisitUserId, makeOrde.getVisitUserId())
                .eq(MakeOrder::getScheduleId, makeOrde.getScheduleId())
                .eq(MakeOrder::getStatus, "1"); // 仅检查状态为“已预约”的订单

        if (callService.count(duplicateCheckQuery) > 0) {
            return ResultUtils.error("该就诊人已预约过该时段，请勿重复挂号!");
        }

        // 价格校验与实际支付金额计算：以后端数据库中的价格为准，结合用户身份计算折后实付
        BigDecimal originalPrice = schedule.getPrice() == null ? BigDecimal.ZERO : schedule.getPrice();
        BigDecimal payPrice = originalPrice;
        try {
            WxUser wxUser = userPatientPhoneService.getById(makeOrde.getUserId());
            String identity = wxUser != null ? wxUser.getIdentityStatus() : null;
            if (identity != null) {
                String s = identity.trim().toLowerCase();
                if ("学生".equals(identity) || "student".equals(s)) {
                    payPrice = originalPrice.multiply(new BigDecimal("0.05"));
                } else if ("教师".equals(identity) || "老师".equals(identity) || "teacher".equals(s)) {
                    payPrice = originalPrice.multiply(new BigDecimal("0.10"));
                }
            }
        } catch (Exception ignored) {}
        makeOrde.setPrice(payPrice.setScale(2, java.math.RoundingMode.HALF_UP));

        // 设置订单初始状态
        makeOrde.setCreateTime(new Date());
        makeOrde.setStatus("1"); // 状态 "1": 已预约
        makeOrde.setHasVisit("0"); // 就诊状态 "0": 未就诊
        makeOrde.setHasCall("0"); // 叫号状态 "0": 未叫号

        // 保存订单并更新号源
        if(callService.save(makeOrde)){
            // 预约成功后，对应排班的剩余号源数量减一
            setWorkService.subCount(makeOrde.getScheduleId());

            // 发送邮件提醒（若用户邮箱存在）
            try {
                WxUser wxUser = userPatientPhoneService.getById(makeOrde.getUserId());
                if (wxUser != null && StringUtils.hasText(wxUser.getEmail())) {
                    String toEmail = wxUser.getEmail();
                    // 尽量获取医生姓名以便在邮件中展示
                    String doctorName = "";
                    if (makeOrde.getDoctorId() != null) {
                        SysUser doctor = userWebService.getById(makeOrde.getDoctorId());
                        if (doctor != null) {
                            doctorName = doctor.getNickName();
                        }
                    }
                    // 处理上午/下午时段
                    String timesAreaLabel = "";
                    try {
                        String ta = makeOrde.getTimesArea();
                        if ("0".equals(ta)) {
                            timesAreaLabel = "上午";
                        } else if ("1".equals(ta)) {
                            timesAreaLabel = "下午";
                        }
                    } catch (Exception ignored) {
                    }
                    SimpleMailMessage message = new SimpleMailMessage();
                    // 注意：这里的发件人地址应与 application.properties 中配置的 spring.mail.username 一致
                    message.setFrom("18201500146@163.com");
                    message.setTo(toEmail);
                    message.setSubject("挂号成功通知");
                    StringBuilder sb = new StringBuilder();
                    sb.append("尊敬的用户，您好！\n\n");
                    sb.append("您已成功预约挂号，相关信息如下：\n");
                    if (StringUtils.hasText(doctorName)) {
                        sb.append("医生：").append(doctorName).append("\n");
                    }
                    if (StringUtils.hasText(makeOrde.getTimes())) {
                        sb.append("预约时间：").append(makeOrde.getTimes());
                        if (StringUtils.hasText(timesAreaLabel)) {
                            sb.append(" （").append(timesAreaLabel).append("）");
                        }
                        sb.append("\n");
                    } else {
                        // 如果没有具体日期，但有时段信息也可单独展示
                        if (StringUtils.hasText(timesAreaLabel)) {
                            sb.append("预约时段：").append(timesAreaLabel).append("\n");
                        }
                    }
                    sb.append("订单号：").append(Optional.ofNullable(makeOrde.getMakeId()).orElse(0)).append("\n\n");
                    sb.append("请按预约时间前来就诊，祝您健康！");
                    message.setText(sb.toString());
                    mailSender.send(message);
                    System.out.println("邮件已发送到：" + toEmail);
                } else {
                    System.out.println("未找到用户邮箱，跳过邮件发送。");
                }
            } catch (Exception e) {
                // 发送邮件失败不影响主流程，记录异常即可
                System.err.println("发送挂号成功邮件失败：" + e.getMessage());
            }

            return ResultUtils.success("预约成功!");
        }

        return ResultUtils.error("预约失败，请稍后重试!");
    }

    /**
     * @description: 处理用户取消预约的请求，这是一个事务性操作。
     * @param makeOrder 包含预约ID的预约对象
     * @return 返回操作成功或失败的结果
     */
    @Transactional
    @PostMapping("/cancelOrder")
    public ResultVo cancelOrder(@RequestBody MakeOrder makeOrder){
        // 查询最新的订单信息，确保数据准确性
        MakeOrder order = callService.getById(makeOrder.getMakeId());

        if (order == null) {
            return ResultUtils.error("订单不存在!");
        }

        // 状态校验：防止重复取消
        if("2".equals(order.getStatus())){
            return ResultUtils.error("订单已经取消，请勿重复操作!");
        }

        // 取消时间限制：就诊前一天（含当天）不允许取消；
        // 但如果订单为“待修改”来源（originalStatus='3'），放宽限制以便用户处理停诊
        String originalStatusForTimeCheck = order.getStatus();
        if (!"3".equals(originalStatusForTimeCheck)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate appointmentDate = LocalDate.parse(order.getTimes(), formatter);
                LocalDate today = LocalDate.now();

                if (appointmentDate.isBefore(today.plusDays(1))) {
                    return ResultUtils.error("已临近就诊时间（少于1天），无法取消预约!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultUtils.error("系统错误，无法处理您的取消请求。");
            }
        }

        // 更新订单状态前记录原状态
        String originalStatus = order.getStatus();
        order.setStatus("2");
        callService.updateById(order);

        // 恢复号源：仅当原订单为正常预约 (status='1') 时恢复；
        // 若是“待修改”(status='3') 取消，则不恢复原排班号源
        if ("1".equals(originalStatus)) {
            // 释放一个号源
            setWorkService.addCount(order.getScheduleId());
            // 尝试将释放的号源分配给候补队列
            try {
                waitlistService.allocateFromWaitlistForSchedule(order.getScheduleId());
            } catch (Exception ignored) {}
        }

        return ResultUtils.success("取消成功");
    }

    @GetMapping("/queueStatus")
    public ResultVo getQueueStatus(@RequestParam("makeId") Integer makeId) {
        java.util.Map<String, Object> status = callService.getQueueStatus(makeId);
        return ResultUtils.success("success", status);
    }

    /**
     * 号源为0时，加入候补队列
     */
    @PostMapping("/waitlist/join")
    @Transactional
    public ResultVo joinWaitlist(@RequestBody WaitlistJoinRequest req) {
        if (req == null || req.getScheduleId() == null || req.getUserId() == null || req.getVisitUserId() == null) {
            return ResultUtils.error("参数不完整");
        }
        // 校验排班
        QueryWrapper<ScheduleDetail> q = new QueryWrapper<>();
        q.lambda().eq(ScheduleDetail::getScheduleId, req.getScheduleId());
        ScheduleDetail schedule = setWorkService.getOne(q);
        if (schedule == null) {
            return ResultUtils.error("无效的排班信息!");
        }
        if (schedule.getLastAmount() != null && schedule.getLastAmount() > 0) {
            return ResultUtils.error("当前仍有余号，请直接预约，无需候补!");
        }

        boolean ok = waitlistService.joinWaitlist(req.getScheduleId(), schedule.getDoctorId(), req.getUserId(), req.getVisitUserId());
        if (ok) {
            return ResultUtils.success("已加入候补队列");
        }
        return ResultUtils.error("加入候补失败，可能已在候补或已有预约");
    }

    /**
     * 手动触发：为指定排班分配候补（如有余量）
     */
    @PostMapping("/waitlist/allocate")
    @Transactional
    public ResultVo allocateWaitlist(@RequestParam("scheduleId") Integer scheduleId) {
        if (scheduleId == null) return ResultUtils.error("缺少排班ID");
        boolean allocated = waitlistService.allocateFromWaitlistForSchedule(scheduleId);
        if (allocated) return ResultUtils.success("已分配一个候补到新余号");
        return ResultUtils.error("无候补或当前无余号");
    }



    @GetMapping("/waitlist/my")
    public ResultVo myWaitlist(@RequestParam("userId") Integer userId,
                               @RequestParam(value = "status", required = false) String status) {
        QueryWrapper<com.itmk.netSystem.waitlist.entity.WaitlistEntry> q = new QueryWrapper<>();
        q.lambda().eq(com.itmk.netSystem.waitlist.entity.WaitlistEntry::getUserId, userId);
        if (StringUtils.hasText(status)) {
            q.lambda().eq(com.itmk.netSystem.waitlist.entity.WaitlistEntry::getStatus, status);
        }
        q.lambda().orderByDesc(com.itmk.netSystem.waitlist.entity.WaitlistEntry::getCreateTime);
        List<com.itmk.netSystem.waitlist.entity.WaitlistEntry> list = waitlistService.list(q);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> data = new ArrayList<>();
        for (com.itmk.netSystem.waitlist.entity.WaitlistEntry e : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("scheduleId", e.getScheduleId());
            m.put("doctorId", e.getDoctorId());
            m.put("userId", e.getUserId());
            m.put("visitUserId", e.getVisitUserId());
            VisitUser v = treatPatientService.getById(e.getVisitUserId());
            m.put("visitname", v != null ? v.getVisitname() : "");
            SysUser d = userWebService.getById(e.getDoctorId());
            m.put("doctorName", d != null ? d.getNickName() : "");
            Department dept = null;
            if (d != null && d.getDeptId() != null) {
                dept = teamDepartmentService.getById(d.getDeptId());
            }
            m.put("deptName", dept != null ? dept.getDeptName() : "");
            ScheduleDetail sd= setWorkService.selectByWorkId(e.getScheduleId()).get(0);
            String times = "";
            String timesAreaLabel = "";
            String week = "";
            if (sd != null) {
                try { times = sd.getTimes() == null ? "" : sd.getTimes().format(df); } catch (Exception ignored) {}
                if (sd.getTimeSlot() != null) {
                    timesAreaLabel = "0".equals(String.valueOf(sd.getTimeSlot())) ? "上午" : "下午";
                }
                week = sd.getWeek();
            }
            m.put("times", times);
            m.put("timesAreaLabel", timesAreaLabel);
            m.put("week", week);
            m.put("status", e.getStatus());
            m.put("createTime", e.getCreateTime());
            data.add(m);
        }
        return ResultUtils.success("查询成功", data);
    }

    /**
     * @description: 用户将“待修改”的订单改签到同医生的其他排班。
     * @param makeOrder 传入 makeId（原订单ID）与 scheduleId（新排班ID）
     */
    @Transactional
    @PostMapping("/rescheduleOrder")
    public ResultVo rescheduleOrder(@RequestBody MakeOrder makeOrder) {
        // 拉取原订单
        MakeOrder order = callService.getById(makeOrder.getMakeId());
        if (order == null) {
            return ResultUtils.error("订单不存在!");
        }

        // 仅允许改签“待修改”的订单
        if (!"3".equals(order.getStatus())) {
            return ResultUtils.error("仅允许改签待修改订单!");
        }

        // 行锁获取目标排班，确保并发安全
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda().eq(ScheduleDetail::getScheduleId, makeOrder.getScheduleId()).last("for update");
        ScheduleDetail newSchedule = setWorkService.getOne(query);

        if (newSchedule == null) {
            return ResultUtils.error("目标排班不存在!");
        }
        // 停诊不可改签
        if (!"1".equals(newSchedule.getType())) {
            return ResultUtils.error("目标排班已停诊或不可用!");
        }
        // 仅允许改签到同一医生的其他排班
        if (newSchedule.getDoctorId() == null || !newSchedule.getDoctorId().equals(order.getDoctorId())) {
            return ResultUtils.error("仅可改签到该医生的其他排班!");
        }

        // 重复预约校验：同就诊人，同排班，不允许已有有效预约
        QueryWrapper<MakeOrder> duplicateCheckQuery = new QueryWrapper<>();
        duplicateCheckQuery.lambda()
                .eq(MakeOrder::getVisitUserId, order.getVisitUserId())
                .eq(MakeOrder::getScheduleId, newSchedule.getScheduleId())
                .eq(MakeOrder::getStatus, "1");
        if (callService.count(duplicateCheckQuery) > 0) {
            return ResultUtils.error("该就诊人已在该时段有预约，无法改签!");
        }

        // 更新订单为新排班信息
        order.setScheduleId(newSchedule.getScheduleId());
        try {
            String newDate = newSchedule.getTimes() == null ? null : newSchedule.getTimes().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            order.setTimes(newDate);
        } catch (Exception ignored) {}
        if (newSchedule.getTimeSlot() != null) {
            order.setTimesArea(String.valueOf(newSchedule.getTimeSlot()));
        }
        order.setWeek(newSchedule.getWeek());
        BigDecimal originalPrice2 = newSchedule.getPrice() == null ? BigDecimal.ZERO : newSchedule.getPrice();
        BigDecimal payPrice2 = originalPrice2;
        try {
            WxUser wxUser2 = userPatientPhoneService.getById(order.getUserId());
            String identity2 = wxUser2 != null ? wxUser2.getIdentityStatus() : null;
            if (identity2 != null) {
                String s2 = identity2.trim().toLowerCase();
                if ("学生".equals(identity2) || "student".equals(s2)) {
                    payPrice2 = originalPrice2.multiply(new BigDecimal("0.05"));
                } else if ("教师".equals(identity2) || "老师".equals(identity2) || "teacher".equals(s2)) {
                    payPrice2 = originalPrice2.multiply(new BigDecimal("0.10"));
                }
            }
        } catch (Exception ignored) {}
        order.setPrice(payPrice2.setScale(2, java.math.RoundingMode.HALF_UP));
        order.setStatus("1"); // 改签后恢复为已预约状态
        order.setHasVisit("0");
        order.setHasCall("0");

        // 持久化订单更新
        callService.updateById(order);

        // 若还有号则号数-1，没号则强加
        if (!(newSchedule.getLastAmount() == null || newSchedule.getLastAmount() <= 0)) {
            setWorkService.subCount(newSchedule.getScheduleId());
        }
        return ResultUtils.success("改签成功!");
    }

    /**
     * @description: 分页查询指定用户的预约挂号列表。
     * @param parm 包含用户ID和分页信息的参数对象
     * @return 返回包含预约记录、医生、科室及就诊人信息的分页结果
     */
    @GetMapping("/getOrderList")
    public ResultVo getOrderList(CallPage parm){
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());

        // 构造多表连接查询
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(SysUser::getNickName)
                .select(Department::getDeptName)
                .select(VisitUser::getVisitname)
                .leftJoin(VisitUser.class, VisitUser::getVisitId, MakeOrder::getVisitUserId)
                .leftJoin(SysUser.class, SysUser::getUserId, MakeOrder::getDoctorId)
                .leftJoin(Department.class, Department::getDeptId, SysUser::getDeptId)
                // 直接使用前端传入的userId进行查询
                .eq(MakeOrder::getUserId, parm.getUserId())
                .orderByDesc(MakeOrder::getCreateTime);

        // 根据状态筛选
        if (StringUtils.hasText(parm.getStatus())) {
            query.eq(MakeOrder::getStatus, parm.getStatus());
        }

        IPage<MakeOrder> list = callService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }

    @PostMapping("/reapply")
    public ResultVo reapply(@RequestBody Map<String, Object> body) {
        Integer makeId = null;
        if (body != null) {
            Object v = body.get("makeId");
            if (v instanceof Number) {
                makeId = ((Number) v).intValue();
            } else if (v instanceof String) {
                try { makeId = Integer.parseInt((String) v); } catch (Exception ignored) {}
            }
        }
        if (makeId == null) {
            return ResultUtils.error("缺少参数: makeId");
        }
        UpdateWrapper<MakeOrder> uw = new UpdateWrapper<>();
        uw.lambda()
                .eq(MakeOrder::getMakeId, makeId)
                .set(MakeOrder::getHasCall, "1")
                .set(MakeOrder::getMissed, "0")
                .set(MakeOrder::getSignInStatus, "0")
                .set(MakeOrder::getCalledTime, new java.util.Date())
                .set(MakeOrder::getStatus, "1");
        boolean ok = callService.update(null, uw);
        if (ok) {
            MakeOrder updated = callService.getById(makeId);
            return ResultUtils.success("已申请重新排号", updated);
        }
        return ResultUtils.error("申请失败");
    }

    @PostMapping("/checkIn")
    public ResultVo checkIn(@RequestBody MakeOrder makeOrder){
        if (callService.checkIn(makeOrder.getMakeId())) {
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    /**
     * @description: 为指定用户账户添加一个新的就诊人信息。
     * @param visitUser 包含新就诊人信息的对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/visitAdd")
    public ResultVo visitAdd(@RequestBody VisitUser visitUser){
        if(treatPatientService.save(visitUser)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    /**
     * @description: 获取指定用户账户下绑定的所有就诊人完整信息列表。
     * @param userId 小程序用户的唯一标识ID
     * @return 返回就诊人详细信息列表
     */
    @GetMapping("/getVisitList")
    public ResultVo getVisitList(Integer userId){
        QueryWrapper<VisitUser> query = new QueryWrapper<>();
        query.lambda().eq(VisitUser::getUserId,userId)
                .orderByDesc(VisitUser::getVisitId);
        List<VisitUser> list = treatPatientService.list(query);
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 根据就诊人ID删除指定的就诊人信息。
     * @param visitUser 包含就诊人ID的对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/visitDelete")
    public ResultVo visitDelete(@RequestBody VisitUser visitUser){
        if(treatPatientService.removeById(visitUser.getVisitId())){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }



    /**
     * @description: 处理新用户的注册请求。
     * @param wxUser 包含用户名和密码等注册信息的用户对象
     * @return 返回注册成功或失败的结果
     */
    @PostMapping("/add")
    public ResultVo add(@RequestBody WxUser wxUser){
        // 检查用户名是否已存在
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUserName,wxUser.getUserName());
        WxUser user = userPatientPhoneService.getOne(query);
        if(user != null){
            return ResultUtils.error("账号被注册!");
        }

        //手机是否存在
        if(StringUtils.hasText(wxUser.getPhone())){
            QueryWrapper<WxUser> phoneQuery = new QueryWrapper<>();
            phoneQuery.lambda().eq(WxUser::getPhone, wxUser.getPhone());
            if(userPatientPhoneService.getOne(phoneQuery) != null){
                return ResultUtils.error("手机号已被注册!");
            }
        }

        //邮箱是否存在+格式
        if(StringUtils.hasText(wxUser.getEmail())){
            String email = wxUser.getEmail().trim();
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if(!email.matches(emailRegex)){
                return ResultUtils.error("邮箱格式不正确!");
            }
            QueryWrapper<WxUser> emailQuery = new QueryWrapper<>();
            emailQuery.lambda().eq(WxUser::getEmail, email);
            if(userPatientPhoneService.getOne(emailQuery) != null){
                return ResultUtils.error("邮箱已被注册!");
            }
            wxUser.setEmail(email);
        }

        wxUser.setCreateTime(new Date());
        wxUser.setStatus(true);
        // 对用户密码进行MD5加密处理
        wxUser.setPassword(DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        if(userPatientPhoneService.save(wxUser)){
            return ResultUtils.success("成功!");
        }
        return ResultUtils.error("失败!");
    }

    /**
     * @description: 验证小程序用户的登录凭证。
     * @param wxUser 包含用户名和密码的登录对象
     * @return 如果验证成功，返回包含用户ID的成功结果；否则返回错误信息。
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody WxUser wxUser){
        // 根据用户名和MD5加密后的密码查询用户
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUserName,wxUser.getUserName())
                .eq(WxUser::getPassword,DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        WxUser user = userPatientPhoneService.getOne(query);
        if(user == null){
            return ResultUtils.error("错误!");
        }
        wxUser.setCreateTime(new Date());
        wxUser.setStatus(true);
        wxUser.setPassword(DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        // 检查用户账户是否被禁用
        if(!user.isStatus()){
            return ResultUtils.error("账号被停用，请联系管理员！");
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(user.getUserId()));
        map.put("username", user.getUserName());
        String token = jwtUtils.generateToken(map); // 调用项目中已有的jwtUtils实例

        System.out.println("🎉 小程序登录成功，为用户 " + user.getUserName() + " 生成的Token是: " + token);
        Login vo = new Login();
        vo.setUserId(user.getUserId());
        return ResultUtils.success("成功!",vo);
    }




    /**
     * @description: 获取被推荐到首页展示的医生列表。
     * @return 返回推荐医生及其所属科室信息的列表
     */
    @GetMapping("/getIndexDoctor")
    public ResultVo getIndexDoctor(){
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(SysUser::getToHome,"1");
        List<SysUser> list = userWebService.list(query).stream().filter(SysUser::isEnabled).collect(Collectors.toList());
        return ResultUtils.success("成功",list);
    }


    /**
     * @description: 提交新的用户建议。
     * @param suggest 包含意见反馈内容的对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/addSuggest")
    public ResultVo addSuggest(@RequestBody Suggest suggest){
        suggest.setCreateTime(new Date());
        if(evaluateService.save(suggest)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }
}
