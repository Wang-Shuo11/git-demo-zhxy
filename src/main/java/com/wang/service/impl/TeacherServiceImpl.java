package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.TeacherMapper;
import com.wang.pojo.LoginForm;
import com.wang.pojo.Teacher;
import com.wang.service.TeacherService;
import com.wang.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("teacherServiceImpl")
@Transactional
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Override
    public Teacher login(LoginForm loginForm) {
        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        //拼接查询条件
        wrapper.eq("name",loginForm.getUsername())
                .eq("password", MD5.encrypt(loginForm.getPassword()));
        Teacher teacher = baseMapper.selectOne(wrapper);
        return teacher;
    }

    @Override
    public Teacher getTeacherById(Long userId) {
        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        wrapper.eq("id",userId);
        Teacher teacher = baseMapper.selectOne(wrapper);
        return teacher;
    }

    @Override
    public IPage<Teacher> getTeachersByOpr(Page<Teacher> page, Teacher teacher) {

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        if (teacher != null) {
            //班级名称条件
            if (!StringUtils.isEmpty(teacher.getClazzName())) {
                wrapper.eq("clazz_name",teacher.getClazzName());
            }
            //教师姓名条件
            String name = teacher.getName();
            if (!StringUtils.isEmpty(name)){
                wrapper.like("name",name);
            }
            wrapper.orderByAsc("id");
            wrapper.orderByDesc("name");
        }
        Page<Teacher> teacherPage = baseMapper.selectPage(page, wrapper);
        return teacherPage;
    }
}
