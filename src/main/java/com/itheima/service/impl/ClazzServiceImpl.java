package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mapper.ClazzMapper;
import com.itheima.mapper.StudentMapper;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.Emp;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Student;
import com.itheima.service.ClazzService;
import com.itheima.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private EmpService empService;

    @Override
    public PageResult<Clazz> listQuery(String name, LocalDate begin, LocalDate end, int page, int pageSize) {
        Page<Clazz> pageQuery = Page.of(page, pageSize);
        this.lambdaQuery()
                .like(StringUtils.isNotBlank(name), Clazz::getName, name)
                .ge(!ObjectUtils.isNull(begin), Clazz::getBeginDate, begin)
                .le(!ObjectUtils.isNull(end), Clazz::getEndDate, end)
                .page(pageQuery);

        Map<Long, String> empMap = empService.list().stream().collect(Collectors.toMap(Emp::getId, Emp::getName));
        List<Clazz> clazzList = pageQuery.getRecords().stream().peek(clazz -> {
            clazz.setMasterName(empMap.get(clazz.getId()));
            clazz.setStatus(clazz.getEndDate().isBefore(LocalDate.now()) ? "已结课" : clazz.getBeginDate().isAfter(LocalDate.now()) ? "未开班" : "已开班");
        }).toList();


        return new PageResult<>(pageQuery.getTotal(), clazzList);


    }

    @Override
    public void removeClazz(Integer id) {

        //检查班级下是否有学生
        long count = studentMapper.selectCount(new LambdaQueryWrapper<>(Student.class).eq(Student::getClazzId, id));
        if (count > 0) {
            throw new RuntimeException("该班级下有学生，无法删除");
        } else {
            this.removeById(id);
        }

    }
}
