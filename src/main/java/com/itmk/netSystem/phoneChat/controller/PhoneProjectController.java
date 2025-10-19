package com.itmk.netSystem.phoneChat.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.teamDepartment.entity.Department;
import com.itmk.netSystem.teamDepartment.service.teamDepartmentService;
import com.itmk.netSystem.call.entity.MakeOrder;
import com.itmk.netSystem.call.entity.CallPage;
import com.itmk.netSystem.call.service.CallService;
import com.itmk.netSystem.see.entity.MakeOrderVisit;
import com.itmk.netSystem.see.service.SeeService;
import com.itmk.netSystem.journal.entity.News;
import com.itmk.netSystem.journal.service.JournalService;
import com.itmk.netSystem.phoneChat.entity.*;
import com.itmk.netSystem.setWork.entity.ScheduleDetail;
import com.itmk.netSystem.setWork.service.setWorkService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.netSystem.treatpatient.entity.VisitUser;
import com.itmk.netSystem.treatpatient.service.TreatPatientService;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
//import com.itmk.netSystem.advice.entity.Suggest;
//import com.itmk.netSystem.advice.service.AdviceService;


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
    //@Autowired
    //private AdviceService adviceService;



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
        if(userPatientPhoneService.updateById(wxUser)){
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
        DoctorInformationNum parm = new DoctorInformationNum();
        parm.setDoctorId(doctorId);
        // 获取服务器当前日期
        LocalDate currentDate = LocalDate.now();
        // 设置日期格式化模板
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 将当前日期格式化为 "yyyy-MM-dd" 字符串
        String formattedDate = currentDate.format(dateFormatter);
        parm.setStartDate(formattedDate);
        List<ScheduleDetail> scheduleDetails = setWorkService.selectById(parm);
        return ResultUtils.success("成功",scheduleDetails);
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
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrderVisit::getVisitUserId)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrderVisit::getDoctorId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(MakeOrderVisit::getUserId,parm.getUserId())
                .orderByDesc(MakeOrderVisit::getCreateTime);
        IPage<MakeOrderVisit> list = seeService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    /**
     * @description: 处理用户取消预约的请求，这是一个事务性操作。
     * @param makeOrder 包含预约ID的预约对象
     * @return 返回操作成功或失败的结果
     */
    @Transactional
    @PostMapping("/cancelOrder")
    public ResultVo cancelOrder(@RequestBody MakeOrder makeOrder){
        // 查询最新的订单信息，防止重复取消
        MakeOrder order = callService.getById(makeOrder.getMakeId());
        if(order.getStatus().equals("2")){
            return ResultUtils.error("订单已经取消，不要重复操作!");
        }
        // 将订单状态更新为"2"（已取消）
        makeOrder.setStatus("2");
        callService.updateById(makeOrder);
        // 将对应的医生排班号源数量加一
        setWorkService.addCount(order.getScheduleId());
        return ResultUtils.success("成功");
    }



    /**
     * @description: 分页查询指定用户的预约挂号列表。
     * @param parm 包含用户ID和分页信息的参数对象
     * @return 返回包含预约记录、医生、科室及就诊人信息的分页结果
     */
    @GetMapping("/getOrderList")
    public ResultVo getOrderList(CallPage parm){
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        // 构造多表连接查询，关联就诊人、医生和科室表
        MPJLambdaWrapper<MakeOrder> query = new MPJLambdaWrapper<>();
        query.selectAll(MakeOrder.class)
                .select(SysUser::getNickName)
                .select(Department::getDeptName)
                .select(VisitUser::getVisitname)
                .leftJoin(VisitUser.class,VisitUser::getVisitId,MakeOrder::getVisitUserId)
                .leftJoin(SysUser.class,SysUser::getUserId,MakeOrder::getDoctorId)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(MakeOrder::getUserId,parm.getUserId())
                .orderByDesc(MakeOrder::getCreateTime);
        IPage<MakeOrder> list = callService.page(page, query);
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


    /**
     * @description: 创建一个新的预约订单，这是一个事务性操作。
     * @param makeOrde 包含预约信息的订单对象
     * @return 返回操作成功或失败的结果
     */
    @PostMapping("/makeOrderAdd")
    @Transactional
    public ResultVo makeOrderAdd(@RequestBody MakeOrder makeOrde){
        // 检查所选的排班是否还有剩余号源
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda().eq(ScheduleDetail::getScheduleId,makeOrde.getScheduleId());
        ScheduleDetail one = setWorkService.getOne(query);
        if(one.getLastAmount() <=0){
            return ResultUtils.error("今日号数已经被预约完，明天再来!");
        }
        makeOrde.setCreateTime(new Date());
        makeOrde.setStatus("1"); // 设置状态为 "1": 已预约
        makeOrde.setHasVisit("0"); // 设置就诊状态为 "0": 未就诊
        makeOrde.setHasCall("0"); // 设置叫号状态为 "0": 未叫号
        if(callService.save(makeOrde)){
            // 预约成功后，对应排班的剩余号源数量减一
            setWorkService.subCount(makeOrde.getScheduleId());
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
    /*@PostMapping("/addSuggest")
    public ResultVo addSuggest(@RequestBody Suggest suggest){
        suggest.setCreateTime(new Date());
        if(adviceService.save(suggest)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }*/
}