package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.pojo.Emp;
import com.itheima.pojo.EmpQueryParam;
import com.itheima.pojo.PageResult;

import java.util.List;

public interface EmpService extends IService<Emp> {

    PageResult page(EmpQueryParam param);

    void saveEmp(Emp emp);

    void updateEmp(Emp emp);

    void deleteEmp(List<Integer> ids);

    Emp getEmpAndEmpExprById(Integer id);
}
