package com.wang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.pojo.Student;
import com.wang.service.StudentService;
import com.wang.util.MD5;
import com.wang.util.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/studentController")
public class StudentController {
    @Autowired
    private StudentService studentService;


    @ApiOperation("删除单个或多个学生")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(@RequestBody List<Integer> ids){
        studentService.removeByIds(ids);
        return Result.ok();
    }


    @ApiOperation("添加或者修改学生信息")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(@RequestBody Student student){

        //对学生密码进行加密
        if (!StringUtils.isEmpty(student.getPassword())){
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        //保存学生到数据库
        studentService.saveOrUpdate(student);
        return Result.ok();
    }

    ///sms/studentController/getStudentByOpr/1/3
    @ApiOperation("根据条件查询学生信息")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(@PathVariable("pageNo")Integer pageNo,
                                  @PathVariable("pageSize")Integer pageSize,
                                  Student student){
        //分页信息
        Page<Student> page = new Page<>(pageNo,pageSize);
        IPage<Student> iPage = studentService.getStudentByOpr(page,student);
        return Result.ok(iPage);

    }
}
