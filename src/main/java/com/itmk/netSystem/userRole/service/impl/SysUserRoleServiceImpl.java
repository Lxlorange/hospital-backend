package com.itmk.netSystem.userRole.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.netSystem.userRole.entity.SysUserRole;
import com.itmk.netSystem.userRole.mapper.SysUserRoleMapper;
import com.itmk.netSystem.userRole.service.SysUserRoleService;
import org.springframework.stereotype.Service;

 
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
    @Override
    public void saveRole(SysUserRole sysUserRole) {

    }
}
