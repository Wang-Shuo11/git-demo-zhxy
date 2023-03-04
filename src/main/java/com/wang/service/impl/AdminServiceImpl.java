package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.AdminMapper;
import com.wang.pojo.Admin;
import com.wang.pojo.LoginForm;
import com.wang.pojo.Teacher;
import com.wang.service.AdminService;
import com.wang.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("adminServiceImpl")
@Transactional
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public Admin login(LoginForm loginForm) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        //拼接查询条件
        queryWrapper.eq("name",loginForm.getUsername());
        //吧密码转成密文查询
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public Admin getAdminById(Long userId) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("id",userId);
        Admin admin = baseMapper.selectOne(wrapper);
        return admin;
    }

    @Override
    public IPage<Admin> getAllAdmin(Page<Admin> page, String adminName) {

        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(adminName)){
            wrapper.like("name",adminName);
        }
        wrapper.orderByAsc("id");
        wrapper.orderByDesc("name");
        Page<Admin> adminPage = baseMapper.selectPage(page, wrapper);
        return adminPage;
    }


}
