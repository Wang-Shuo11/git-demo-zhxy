package com.wang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.pojo.LoginForm;
import com.wang.pojo.Student;

public interface StudentService extends IService<Student> {
    //登录
    Student login(LoginForm loginForm);
    //通过id 查询学生
    Student getStudentById(Long userId);
    //分页
    IPage<Student> getStudentByOpr(Page<Student> page, Student student);
}
