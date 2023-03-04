package com.wang.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.pojo.Grade;
import com.wang.service.GradeService;
import com.wang.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @ApiOperation("获取全部年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> list = gradeService.list();
        return Result.ok(list);
    }

    //删除一个或多个年级
    @ApiOperation("删除Grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGradeById(
            @ApiParam("要删除的所有grade的id的JSON集合") @RequestBody List<Integer> ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }

    @ApiOperation("新增或者修改grade，有id属性是修改 没有是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(@ApiParam("json格式的grade对象")@RequestBody Grade grade){
        //接收参数
        //调用服务层完成新增 修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }


    //http://localhost:9001/sms/gradeController/getGrades/1/3?gradeName=..
    @ApiOperation("根据年级名称模糊查询 带分页")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrade(@PathVariable("pageNo")Integer pageNo,
                           @PathVariable("pageSize")Integer pageSize,
                           String gradeName){
        //分页带条件查询
        Page<Grade> page = new Page<>(pageNo, pageSize);
        //通过服务层查询
        IPage<Grade> pageRs = gradeService.getGradeByOpr(page,gradeName);
        //封装Result对象 并返回
        return Result.ok(pageRs);
    }


}
