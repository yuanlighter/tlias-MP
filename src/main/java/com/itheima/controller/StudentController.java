package com.itheima.controller;


import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;
import com.itheima.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public Result list(StudentQueryParam student) {
        log.info("查询所有学生信息：{}", student);
        PageResult<Student> pageResult = studentService.listQuery(student);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Student student) {
        log.info("新增学生信息：{}", student);
        studentService.saveStu(student);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据id查询学生信息：{}", id);
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学生信息：{}", student);
        student.setUpdateTime(LocalDateTime.now());
        studentService.updateById(student);
        return Result.success();
    }

    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids) {
        log.info("根据id删除学生信息：{}", ids);
        studentService.removeByIds(ids);
        return Result.success();
    }

    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score) {
        log.info("根据id{}扣分, 扣分：{}", id, score);

        return Result.success();
    }
}
