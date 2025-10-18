package com.itmk.netSystem.userPatientPhone.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.netSystem.userPatientPhone.entity.WxUser;
import com.itmk.netSystem.userPatientPhone.entity.UserPatientPhonePage;
import com.itmk.netSystem.userPatientPhone.service.UserPatientPhoneService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

 
@RequestMapping("/api/wxUser")
@RestController
public class UserPatientPhoneController {

    @Autowired
    private UserPatientPhoneService userPatientPhoneService;

    @PreAuthorize("hasAuthority('sys:patient:enable')")
    @PostMapping("/enabledUser")
    public ResultVo enabledUser(@RequestBody WxUser wxUser){
        userPatientPhoneService.updateById(wxUser);
        return ResultUtils.success("成功");
    }

    /**
     * 根据用户ID获取单个用户的详细信息。
     * @param userId 用户ID
     * @return 包含用户信息的响应结果。
     */
    @GetMapping("/{userId}")
    public ResultVo getUserById(@PathVariable("userId") Long userId) {
        WxUser user = userPatientPhoneService.getById(userId);
        if (user != null) {
            return ResultUtils.success("查询成功", user);
        }
        return ResultUtils.error("未找到用户");
    }

    /**
     * 注册新用户。
     * @param wxUser 新用户信息
     * @return 操作结果。
     */
    @PostMapping("/register")
    public ResultVo register(@RequestBody WxUser wxUser) {
        if (userPatientPhoneService.register(wxUser)) {
            return ResultUtils.success("注册成功");
        }
        return ResultUtils.error("注册失败，手机号可能已存在");
    }

    /**
     * 根据手机号码查询用户。
     * @param phone 手机号
     * @return 包含用户信息的响应结果。
     */
    @GetMapping("/findByPhone")
    public ResultVo findByPhone(@RequestParam("phone") String phone) {
        WxUser user = userPatientPhoneService.findByPhone(phone);
        if (user != null) {
            return ResultUtils.success("查询成功", user);
        }
        return ResultUtils.error("未找到该手机号对应的用户");
    }

    /**
     * 更新用户信息。
     * @param wxUser 包含更新信息的用户对象
     * @return 操作结果。
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:patient:edit')")
    public ResultVo update(@RequestBody WxUser wxUser) {
        // 防止密码被意外更新，密码更新应走单独接口
        wxUser.setPassword(null);
        if (userPatientPhoneService.updateById(wxUser)) {
            return ResultUtils.success("更新成功");
        }
        return ResultUtils.error("更新失败");
    }

    /**
     * 修改用户密码。
     * @param wxUser 包含用户ID和新密码
     * @return 操作结果。
     */
    @PostMapping("/changePassword")
    public ResultVo changePassword(@RequestBody WxUser wxUser) {
        if (wxUser.getUserId() == null || StringUtils.isEmpty(wxUser.getPassword())) {
            return ResultUtils.error("参数错误");
        }
        WxUser userToUpdate = new WxUser();
        userToUpdate.setUserId(wxUser.getUserId());
        userToUpdate.setPassword(DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes()));
        userPatientPhoneService.updateById(userToUpdate);
        return ResultUtils.success("密码修改成功");
    }

    @PreAuthorize("hasAuthority('sys:patient:delete')")
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId){
        userPatientPhoneService.removeById(userId);
        return ResultUtils.success("成功");
    }

    @GetMapping("/getList")
    public ResultVo getList(UserPatientPhonePage parm){
        IPage<WxUser> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getName()),WxUser::getNickName,parm.getName())
                .orderByDesc(WxUser::getCreateTime);
        IPage<WxUser> list = userPatientPhoneService.page(page, query);
        return ResultUtils.success("成功",list);
    }

    @PreAuthorize("hasAuthority('sys:patient:reset')")
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody WxUser wxUser){
        wxUser.setPassword(DigestUtils.md5DigestAsHex("666666".getBytes()));
        userPatientPhoneService.updateById(wxUser);
        return ResultUtils.success("成功");
    }



}
