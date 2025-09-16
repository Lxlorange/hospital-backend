package com.itmk.web.wxapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.department.entity.Department;
import com.itmk.web.department.service.DepartmentService;
import com.itmk.web.make_order.entity.MakeOrder;
import com.itmk.web.make_order.entity.MakeOrderPageParm;
import com.itmk.web.make_order.service.MakeOrderService;
import com.itmk.web.make_order_visit.entity.MakeOrderVisit;
import com.itmk.web.make_order_visit.service.MakeOrderVisitService;
import com.itmk.web.news.entity.News;
import com.itmk.web.news.service.NewsService;
import com.itmk.web.schedule_detail.entity.ScheduleDetail;
import com.itmk.web.schedule_detail.service.ScheduleDetailService;
import com.itmk.web.suggest.entity.Suggest;
import com.itmk.web.suggest.service.SuggestService;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.visit_user.entity.VisitUser;
import com.itmk.web.visit_user.service.VisitUserService;
import com.itmk.web.wx_user.entity.WxUser;
import com.itmk.web.wx_user.service.WxUserService;
import com.itmk.web.wxapi.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

 
@RequestMapping("/wxapi/allApi")
@RestController
public class WxController {
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ScheduleDetailService scheduleDetailService;
    @Autowired
    private VisitUserService visitUserService;
    @Autowired
    private MakeOrderService makeOrderService;
    @Autowired
    private SuggestService suggestService;
    @Autowired
    private MakeOrderVisitService makeOrderVisitService;


    /**
     * 查询就诊记录列表
     * @param parm
     * @return
     */
    @GetMapping("/getVisitOrderList")
    public ResultVo getVisitOrderList(MakeOrderPageParm parm){
        IPage<MakeOrderVisit> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
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
        IPage<MakeOrderVisit> list = makeOrderVisitService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 新增反馈
     */
    @PostMapping("/addSuggest")
    public ResultVo addSuggest(@RequestBody Suggest suggest){
        suggest.setCreateTime(new Date());
        if(suggestService.save(suggest)){
            return ResultUtils.success("新增成功!");
        }
        return ResultUtils.error("新增失败!");
    }

    /**
     * 修改密码
     * @param parm
     * @return
     */
    @PostMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody UpdatePasswordParm parm){
        WxUser user = wxUserService.getById(parm.getUserId());
        //判断原密码是否正确
        if(!DigestUtils.md5DigestAsHex(parm.getOldPassword().getBytes()).equals(user.getPassword())){
            return ResultUtils.error("原密码不正确！");
        }
        WxUser wxUser = new WxUser();
        wxUser.setPassword(DigestUtils.md5DigestAsHex(parm.getPassword().getBytes()));
        wxUser.setUserId(parm.getUserId());
        if(wxUserService.updateById(wxUser)){
            return ResultUtils.success("修改成功!");
        }
        return ResultUtils.error("修改失败!");
    }

    /**
     * 小程序更新个人信息
     * @param wxUser
     * @return
     */
    @PostMapping("/updateUserInfo")
    public ResultVo updateUserInfo(@RequestBody WxUser wxUser){
        if(wxUserService.updateById(wxUser)){
            return ResultUtils.success("更新成功！");
        }
        return ResultUtils.error("更新失败!");
    }

    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @GetMapping("/getWxUserById")
    public ResultVo getWxUserById(Integer userId){
        WxUser user = wxUserService.getById(userId);
        return ResultUtils.success("查询成功",user);
    }

    /**
     * 小程序底部预约数据
     * @return
     */
    @GetMapping("/getCategoryList")
    public ResultVo getCategoryList(){
        //返回的数据
        List<DeptTree> list = new ArrayList<>();
        //查询科室
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().orderByAsc(Department::getOrderNum);
        List<Department> departmentList = departmentService.list(query);
        if(departmentList.size() >0){
            for (int i=0;i<departmentList.size();i++){
                DeptTree deptTree = new DeptTree();
                deptTree.setName(departmentList.get(i).getDeptName());
                //查询科室下面的医生
                QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SysUser::getDeptId,departmentList.get(i).getDeptId())
                        .orderByAsc(SysUser::getNickName);
                List<SysUser> listU = sysUserService.list(queryWrapper);
                List<UserDesc> userDescList = new ArrayList<>();
                UserInfo userInfo = new UserInfo();
                userInfo.setName(departmentList.get(i).getDeptName());
                if(listU.size() >0){
                    for (int j=0;j<listU.size();j++){
                        UserDesc u = new UserDesc();
                        BeanUtils.copyProperties(listU.get(j),u);
                        u.setDeptName(departmentList.get(i).getDeptName());
                        userDescList.add(u);
                    }
                }
                userInfo.setDesc(userDescList);
                deptTree.getChildrens().add(userInfo);
                list.add(deptTree);
            }
        }
        return ResultUtils.success("查询成功",list);
    }


    /**
     * 小程序资讯列表
     * @param parm
     * @return
     */
    @GetMapping("/getNewsList")
    public ResultVo getNewsList(DoctorPage parm){
        IPage<News> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().orderByDesc(News::getCreateTime);
        IPage<News> list = newsService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 首页科室医生列表
     * @param parm
     * @return
     */
    @GetMapping("/getDoctorByDeptId")
    public ResultVo getDoctorByDeptId(DoctorPage parm){
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(SysUser::getDeptId,parm.getDeptId());
        IPage<SysUser> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        IPage<SysUser> list = sysUserService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 取消预约
     * @param makeOrder
     * @return
     */
    @Transactional
    @PostMapping("/cancelOrder")
    public ResultVo cancelOrder(@RequestBody MakeOrder makeOrder){
        //查询订单
        MakeOrder order = makeOrderService.getById(makeOrder.getMakeId());
        if(order.getStatus().equals("2")){
            return ResultUtils.error("订单已经取消，不要重复操作!");
        }
        //设置订单为取消状态
        makeOrder.setStatus("2");
        makeOrderService.updateById(makeOrder);
        //剩余号加1
        scheduleDetailService.addCount(order.getScheduleId());
        return ResultUtils.success("取消成功");
    }



    /**
     * 查询预约列表
     * @param parm
     * @return
     */
    @GetMapping("/getOrderList")
    public ResultVo getOrderList(MakeOrderPageParm parm){
        IPage<MakeOrder> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
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
        IPage<MakeOrder> list = makeOrderService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 预约
     * @param makeOrde
     * @return
     */
    @PostMapping("/makeOrderAdd")
    @Transactional
    public ResultVo makeOrderAdd(@RequestBody MakeOrder makeOrde){
        //查询是否剩余号
        QueryWrapper<ScheduleDetail> query = new QueryWrapper<>();
        query.lambda().eq(ScheduleDetail::getScheduleId,makeOrde.getScheduleId());
        ScheduleDetail one = scheduleDetailService.getOne(query);
        if(one.getLastAmount() <=0){
            return ResultUtils.error("今日号数已经被预约完，明天再来!");
        }
        makeOrde.setCreateTime(new Date());
        makeOrde.setStatus("1");
        makeOrde.setHasVisit("0");
        makeOrde.setHasCall("0");
        if(makeOrderService.save(makeOrde)){
            scheduleDetailService.subCount(makeOrde.getScheduleId());
            return ResultUtils.success("预约成功!");
        }
        return ResultUtils.error("预约失败!");
    }

    @GetMapping("/getSelectVisitList")
    public ResultVo getSelectVisitList(Integer userId){
        //构造查询条件
        QueryWrapper<VisitUser> query = new QueryWrapper<>();
        query.lambda().eq(VisitUser::getUserId,userId);
        List<VisitUser> list = visitUserService.list(query);
        List<VisitVo> visitVos = new ArrayList<>();
        if(list.size() > 0){
            //组装下拉数据
            for (int i=0;i<list.size();i++){
                VisitVo vo = new VisitVo();
                vo.setVisitId(list.get(i).getVisitId());
                vo.setName(list.get(i).getVisitname());
                visitVos.add(vo);
            }
        }
        return ResultUtils.success("查询成功",visitVos);
    }
    /**
     * 小程序就诊人列表
     * @return
     */
    @GetMapping("/getVisitList")
    public ResultVo getVisitList(Integer userId){
        QueryWrapper<VisitUser> query = new QueryWrapper<>();
        query.lambda().eq(VisitUser::getUserId,userId)
                .orderByDesc(VisitUser::getVisitId);
        List<VisitUser> list = visitUserService.list(query);
        return ResultUtils.success("查询成功",list);
    }


    /**
     * 新增就诊人
     * @param visitUser
     * @return
     */
    @PostMapping("/visitAdd")
    public ResultVo visitAdd(@RequestBody VisitUser visitUser){
        if(visitUserService.save(visitUser)){
            return ResultUtils.success("新建成功!");
        }
        return ResultUtils.error("新建失败!");
    }

    /**
     * 编辑就诊人
     * @param visitUser
     * @return
     */
    @PutMapping("/visitEdit")
    public ResultVo visitEdit(@RequestBody VisitUser visitUser){
        if(visitUserService.updateById(visitUser)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //删除就诊人
    @PostMapping("/visitDelete")
    public ResultVo visitDelete(@RequestBody VisitUser visitUser){
        if(visitUserService.removeById(visitUser.getVisitId())){
            return ResultUtils.success("删除成功!");
        }
        return ResultUtils.error("删除失败!");
    }

    /**
     * 小程序医生详情排班
     * @return
     */
    @GetMapping("/getDoctor")
    public ResultVo getDoctor(String userId,String doctorId){
        DoctorDetailParm parm = new DoctorDetailParm();
        parm.setDoctorId(doctorId);
        //获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期为指定格式
        String formattedDate = currentDate.format(dateFormatter);
        parm.setStartDate(formattedDate);
        List<ScheduleDetail> scheduleDetails = scheduleDetailService.selectById(parm);
        return ResultUtils.success("查询成功",scheduleDetails);
    }

    /**
     * 小程序首页推荐医生
     * @return
     */
    @GetMapping("/getIndexDoctor")
    public ResultVo getIndexDoctor(){
        MPJLambdaWrapper<SysUser> query = new MPJLambdaWrapper<>();
        query.selectAll(SysUser.class)
                .select(Department::getDeptName)
                .leftJoin(Department.class,Department::getDeptId,SysUser::getDeptId)
                .eq(SysUser::getToHome,"1");
        List<SysUser> list = sysUserService.list(query).stream().filter(SysUser::isEnabled).collect(Collectors.toList());
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 小程序首页科室
     * @return
     */
    @GetMapping("/getIndexDept")
    public ResultVo getIndexDept(){
        QueryWrapper<Department> query = new QueryWrapper<>();
        query.lambda().eq(Department::getToHome,"1")
                .orderByAsc(Department::getOrderNum).last("limit 8");
        List<Department> list = departmentService.list(query);
        return ResultUtils.success("查询成功",list);
    }

    /**
     * 小程序首页轮播图
     * @return
     */
    @GetMapping("/getIndexNews")
    public ResultVo getIndexNews(){
        QueryWrapper<News> query = new QueryWrapper<>();
        query.lambda().eq(News::getToIndex,"1")
                .orderByDesc(News::getCreateTime);
        List<News> list = newsService.list(query);
        return ResultUtils.success("查询成功",list);
    }

    //注册
    @PostMapping("/add")
    public ResultVo add(@RequestBody WxUser wxUser){
        //判断账号是否被占用
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUserName,wxUser.getUserName());
        WxUser user = wxUserService.getOne(query);
        if(user != null){
            return ResultUtils.error("账号被注册!");
        }
        wxUser.setCreateTime(new Date());
        wxUser.setStatus(true);
        wxUser.setPassword(DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        if(wxUserService.save(wxUser)){
            return ResultUtils.success("注册成功!");
        }
        return ResultUtils.error("注册失败!");
    }

    //编辑
    @PutMapping("/edit")
    public ResultVo edit(@RequestBody WxUser wxUser){
        if(wxUserService.updateById(wxUser)){
            return ResultUtils.success("编辑成功!");
        }
        return ResultUtils.error("编辑失败!");
    }

    //登录
    @PostMapping("/login")
    public ResultVo login(@RequestBody WxUser wxUser){
        //查询账号
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUserName,wxUser.getUserName())
                .eq(WxUser::getPassword,DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        WxUser user = wxUserService.getOne(query);
        if(user == null){
            return ResultUtils.error("账号或密码错误!");
        }
        wxUser.setCreateTime(new Date());
        wxUser.setStatus(true);
        wxUser.setPassword(DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        if(!user.isStatus()){
            return ResultUtils.error("账号被停用，请联系管理员！");
        }
        LoginVo vo = new LoginVo();
        vo.setUserId(user.getUserId());
        return ResultUtils.success("登录成功!",vo);
    }
}
