package com.itheima.controller;

import com.itheima.pojo.Emp;
import com.itheima.pojo.EmpQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {
    @Autowired
    private EmpService empService;

    @GetMapping
    public Result page(EmpQueryParam param) {
        log.info("员工列表查询条件：{}", param);

        // 调用Service层查询分页数据
        PageResult pageResult = empService.page(param);

        // 返回结果
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Emp emp) {
        log.info("新增员工，员工信息：{}", emp);

        // 调用service层方法保存员工信息和员工的工作经历信息
        empService.saveEmp(emp);

        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("批量删除员工，{}", ids);

        // 调用service层方法批量删除员工信息和员工工作经历信息
        empService.deleteEmp(ids);

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据ID查询员工信息，id：{}", id);

        Emp emp = empService.getEmpAndEmpExprById(id);

        return Result.success(emp);
    }

    /**
     * 修改员工信息
     *
     * @param emp
     * @return
     */
    @PutMapping
    public Result update(@RequestBody Emp emp) {
        log.info("修改员工信息，员工信息：{}", emp);

        // 调用service层方法修改员工信息
        empService.updateEmp(emp);

        return Result.success();
    }

    @GetMapping("/list")
    public Result list() {
        log.info("查询员工列表");

        // 调用service层方法查询员工列表
        List<Emp> empList = empService.list();
        return Result.success(empList);
    }
}
