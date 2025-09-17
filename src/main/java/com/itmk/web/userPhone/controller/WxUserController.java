package com.itmk.web.userPhone.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import com.itmk.web.userPhone.entity.WxUser;
import com.itmk.web.userPhone.entity.WxUserPageParm;
import com.itmk.web.userPhone.service.WxUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

 
@RequestMapping("/api/wxUser")
@RestController
public class WxUserController {

    @Autowired
    private WxUserService wxUserService;

    @PreAuthorize("hasAuthority('sys:patient:enable')")
    @PostMapping("/enabledUser")
    public ResultVo enabledUser(@RequestBody WxUser wxUser){
        wxUserService.updateById(wxUser);
        return ResultUtils.success("操作成功!");
    }

    /**
     * 重置密码
     * @param wxUser
     * @return
     */
    @PreAuthorize("hasAuthority('sys:patient:reset')")
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody WxUser wxUser){
        wxUser.setPassword(DigestUtils.md5DigestAsHex("666666".getBytes()));
        wxUserService.updateById(wxUser);
        return ResultUtils.success("重置成功!");
    }

    //删除
    @PreAuthorize("hasAuthority('sys:patient:delete')")
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId){
        wxUserService.removeById(userId);
        return ResultUtils.success("删除成功!");
    }

    //列表
    @GetMapping("/getList")
    public ResultVo getList(WxUserPageParm parm){
        //构造分页对象
        IPage<WxUser> page = new Page<>(parm.getCurrentPage(),parm.getPageSize());
        //构造查询条件
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda()
                .like(StringUtils.isNotEmpty(parm.getName()),WxUser::getNickName,parm.getName())
                .orderByDesc(WxUser::getCreateTime);
        IPage<WxUser> list = wxUserService.page(page, query);
        return ResultUtils.success("查询成功",list);
    }

}
