package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mapper.EmpMapper;
import com.itheima.pojo.*;
import com.itheima.service.DeptService;
import com.itheima.service.EmpExprService;
import com.itheima.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {

    //    private final EmpMapper empMapper;
//
//    public EmpServiceImpl(EmpMapper empMapper) {
//        this.empMapper = empMapper;
//    }
    @Autowired
    private EmpExprService empExprService;

    @Autowired
    private DeptService deptService;


    /**
     * 员工列表分页查询
     *
     * @param param
     * @return
     */
    @Override
    public PageResult page(EmpQueryParam param) {
        //创建员工分页对象
        Page<Emp> page = Page.of(param.getPage(), param.getPageSize());
        //按更新时间降序排序
        page.addOrder(OrderItem.desc("update_time"));
        //封装查询条件并封装查询结果
        this.lambdaQuery()
                .like(StringUtils.isNotBlank(param.getName()), Emp::getName, param.getName())
                .eq(ObjectUtils.isNotNull(param.getGender()), Emp::getGender, param.getGender())
                .between(ObjectUtils.isNotNull(param.getBegin()) && ObjectUtils.isNotNull(param.getEnd()), Emp::getEntryDate, param.getBegin(), param.getEnd())
                .page(page);
        //从数据库中获取的字段没有deptName，需要手动获取并封装
        //获取部门信息
        List<Dept> deptList = deptService.list();
        //构建deptMap<deptId,deptName>
        Map<Long, String> deptMap = deptList.stream().collect(Collectors.toMap(Dept::getId, Dept::getName));
        //获取当前页记录并为每条记录添加deptName
        List<Emp> emps = page.getRecords().stream().peek(emp -> {
            //当员工的deptId和部门的Id相等时
            emp.setDeptName(deptMap.get(emp.getDeptId()));
        }).toList();

        return new PageResult<>(page.getTotal(), emps);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEmp(Emp emp) {
        //0.补全属性
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        //1.保存员工基本信息
        baseMapper.insert(emp);
        //2.获取新增员工的id
        Long empId = emp.getId();
        //3.获取员工工作经历，为每段员工工作经历绑定id
        List<EmpExpr> exprList = emp.getExprList().stream().peek(expr -> {
            expr.setEmpId(empId);
        }).toList();

        //4.保存员工工作经历
        empExprService.saveBatch(exprList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmp(Emp emp) {
        emp.setUpdateTime(LocalDateTime.now());
        //更新员工基本信息
        this.updateById(emp);
        //删除以前的员工工作经历
        empExprService.remove(new LambdaQueryWrapper<>(EmpExpr.class).eq(EmpExpr::getEmpId, emp.getId()));
        //重新插入员工工作经历
        List<EmpExpr> list = emp.getExprList().stream().peek(expr -> {
            expr.setEmpId(emp.getId());
        }).toList();
        empExprService.saveBatch(list);
    }

    /**
     * 批量删除员工
     *
     * @param ids
     */
    @Override
    public void deleteEmp(List<Integer> ids) {
        //1.删除员工基本信息
        this.removeByIds(ids);
        //2.删除员工工作经历信息
        empExprService.remove(new LambdaQueryWrapper<>(EmpExpr.class).in(EmpExpr::getEmpId, ids));
    }

    @Override
    public Emp getEmpAndEmpExprById(Integer id) {
        //1.查询员工基本信息
        Emp emp = this.getById(id);
        //2.查询员工工作经历信息
        List<EmpExpr> list = empExprService.list(new LambdaQueryWrapper<>(EmpExpr.class).eq(
                EmpExpr::getEmpId, id
        ));
        emp.setExprList(list);

        return emp;
    }
}
