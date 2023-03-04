package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.StudentMapper;
import com.wang.pojo.LoginForm;
import com.wang.pojo.Student;
import com.wang.service.StudentService;
import com.wang.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("studentServiceImpl")
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Student login(LoginForm loginForm) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        //拼接查询条件。
        wrapper.eq("name",loginForm.getUsername());
        wrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Student student = baseMapper.selectOne(wrapper);
        return student;
    }

    @Override
    public Student getStudentById(Long userId) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("id",userId);
        Student student = baseMapper.selectOne(wrapper);
        return student;
    }

    @Override
    public IPage<Student> getStudentByOpr(Page<Student> page, Student student) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        if (student != null){
            String clazzName = student.getClazzName();
            if (!StringUtils.isEmpty(clazzName)){
                wrapper.eq("clazz_name",clazzName);
            }
            String name = student.getName();
            if (!StringUtils.isEmpty(name)){
                wrapper.like("name",name);
            }
            wrapper.orderByAsc("id");
            wrapper.orderByAsc("name");

        }
        //创建分页对象
        IPage<Student> studentPage = baseMapper.selectPage(page, wrapper);
        return studentPage;
    }
}
