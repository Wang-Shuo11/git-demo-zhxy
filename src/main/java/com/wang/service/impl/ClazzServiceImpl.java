package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mapper.ClazzMapper;
import com.wang.pojo.Clazz;
import com.wang.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("clazzServiceImpl")
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
    @Override
    public IPage<Clazz> getClazzsByOpr(Page<Clazz> page, Clazz clazz) {
        QueryWrapper<Clazz> wrapper = new QueryWrapper<>();
        if (clazz != null){
            //年级名称条件精确查询
            String gradeName = clazz.getGradeName();
            if (!StringUtils.isEmpty(gradeName)){
                wrapper.eq("grade_name",gradeName);
            }
            //按班级条件模糊查询
            String clazzName = clazz.getName();
            if (!StringUtils.isEmpty(clazzName)){
                wrapper.like("name",clazzName);
            }
            //排序
            wrapper.orderByDesc("id");
            wrapper.orderByAsc("name");
        }
        Page<Clazz> clazzPage = baseMapper.selectPage(page, wrapper);

        return clazzPage;
    }
}
