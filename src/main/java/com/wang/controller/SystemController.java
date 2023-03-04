package com.wang.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wang.pojo.Admin;
import com.wang.pojo.LoginForm;
import com.wang.pojo.Student;
import com.wang.pojo.Teacher;
import com.wang.service.AdminService;
import com.wang.service.StudentService;
import com.wang.service.TeacherService;
import com.wang.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/sms/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;


    @ApiOperation("修改密码")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(@RequestHeader("token") String token,
                            @PathVariable("oldPwd") String oldPwd,
                            @PathVariable("newPwd") String newPwd){
        //校验token是否过期 24小时无操作 需要重新登录
        boolean yOn = JwtHelper.isExpiration(token);
        if(yOn){
            //token过期
            return Result.fail().message("token失效!");
        }
        //通过token获取当前登录的用户id
        Long userId = JwtHelper.getUserId(token);
        //通过token获取当前登录的用户类型
        Integer userType = JwtHelper.getUserType(token);
        // 将明文密码转换为暗文
        oldPwd= MD5.encrypt(oldPwd);
        newPwd= MD5.encrypt(newPwd);
        if(userType == 1){
            QueryWrapper<Admin> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("id",userId.intValue()).eq("password",oldPwd);
            Admin admin = adminService.getOne(queryWrapper);
            if (null!=admin) {
                admin.setPassword(newPwd);
                adminService.saveOrUpdate(admin);
            }else{
                return Result.fail().message("原密码输入有误！");
            }
        }else if(userType == 2){
            QueryWrapper<Student> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("id",userId.intValue()).eq("password",oldPwd);
            Student student = studentService.getOne(queryWrapper);
            if (null!=student) {
                student.setPassword(newPwd);
                studentService.saveOrUpdate(student);
            }else{
                return Result.fail().message("原密码输入有误！");
            }
        }
        else if(userType == 3){
            QueryWrapper<Teacher> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("id",userId.intValue()).eq("password",oldPwd);
            Teacher teacher = teacherService.getOne(queryWrapper);
            if (null!=teacher) {
                teacher.setPassword(newPwd);
                teacherService.saveOrUpdate(teacher);
            }else{
                return Result.fail().message("原密码输入有误！");
            }
        }
        return Result.ok();
    }


    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(@ApiParam("文件二进制数据") @RequestPart("multipartFile")
                                              MultipartFile multipartFile){
        //使用UUID随机生成文件名
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        //生成新的文件名字
        String filename = uuid.concat(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        //生成文件的保存路径(实际生产环境这里会使用真正的文件存储服务器)
        String portraitPath ="D:/zhihuixiaoyuan/target/classes/public/upload/".concat(filename);
        //保存文件
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //响应图片路径
        String headerImg ="upload/"+filename;
        return Result.ok(headerImg);

    }

    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token")String token){
        boolean expiration = JwtHelper.isExpiration(token);
        //判断token是否过期
        if (expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token解析用户id 和 用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object> map = new LinkedHashMap<>();
        switch (userType){
            case 1: //admin
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",userType);
                map.put("user",admin);
                break;
            case 2: //学生
                Student student = studentService.getStudentById(userId);
                map.put("userType",userType);
                map.put("user",student);
                break;
            case 3://教师
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",userType);
                map.put("user",teacher);
                break;
        }
        return Result.ok(map);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm,HttpServletRequest request){
        HttpSession session = request.getSession();
        String sessionVerifiCode = (String) session.getAttribute("verifiCode");
        String verifiCode = loginForm.getVerifiCode();
        if ("".equals(sessionVerifiCode) || null == sessionVerifiCode){
            return Result.fail().message("验证码失效了,请刷新重试！");
        }
        if (!sessionVerifiCode.equals(verifiCode)){
            return Result.fail().message("验证码有误,请刷新重试！");
        }
        // 以下代码 代表成功了  从session域移除现有验证码
        session.removeAttribute("verifiCode");
        //分用户类型进行校验。。
        Map<String,Object> map = new LinkedHashMap<>();
        switch (loginForm.getUserType()){
            case 1://管理员身份
                try {
                    Admin admin = adminService.login(loginForm);
                    if (admin != null){
                        //将用户类型 和用户id 转成token 向客户端反馈
                        String token = JwtHelper.createToken(admin.getId().longValue(), 1);
                         map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码错误！");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 2://学生身份
                try {
                    Student student = studentService.login(loginForm);
                    if (student != null){
                        //将用户类型 和用户id 转成token 向客户端反馈
                        String token = JwtHelper.createToken(student.getId().longValue(), 2);
                        map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码错误！");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3://教师身份
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if (teacher != null){
                        //将用户类型 和用户id 转成token 向客户端反馈
                        String token = JwtHelper.createToken(teacher.getId().longValue(), 3);
                        map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码错误！");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }
        // 查无此用户,响应失败
        return Result.fail().message("没有此用户");

    }



    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码 放到session域中
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码响应给 浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
