package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.GradeMapper;
import com.wang.pojo.Grade;
import com.wang.service.GradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("gradeServiceImpl")
@Transactional
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {
    @Override
    public IPage<Grade> getGradeByOpr(Page<Grade> pageParam, String gradeName) {
        QueryWrapper<Grade> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(gradeName)){
            wrapper.like("name",gradeName);
        }
        wrapper.orderByDesc("id");
        Page<Grade> page = baseMapper.selectPage(pageParam, wrapper);
        return page;
    }
}
