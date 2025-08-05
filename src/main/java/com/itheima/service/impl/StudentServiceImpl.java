package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mapper.StudentMapper;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;
import com.itheima.service.ClazzService;
import com.itheima.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private ClazzService clazzService;

    @Override
    public PageResult<Student> listQuery(StudentQueryParam studentQueryParam) {
        String name = studentQueryParam.getName();
        Integer degree = studentQueryParam.getDegree();
        Integer clazzId = studentQueryParam.getClazzId();
        Page<Student> page = Page.of(studentQueryParam.getPage(), studentQueryParam.getPageSize());

        this.lambdaQuery()
                .like(StringUtils.isNotBlank(name), Student::getName, name)
                .eq(!ObjectUtils.isNull(degree), Student::getDegree, degree)
                .eq(!ObjectUtils.isNull(clazzId), Student::getClazzId, clazzId)
                .page(page);


        List<Clazz> clazzList = clazzService.list();
        Map<Long, String> clazzMap = clazzList.stream().collect(Collectors.toMap(Clazz::getId, Clazz::getName));

        // 为每个学生设置班级名称
        List<Student> studentList = page.getRecords().stream().peek(student -> {
            if (student.getClazzId() != null) {
                student.setClazzName(clazzMap.get(student.getClazzId()));
            }
        }).toList();


        return new PageResult<>(page.getTotal(), studentList);
    }

    @Override
    public void saveStu(Student student) {
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        this.save(student);
    }
}
