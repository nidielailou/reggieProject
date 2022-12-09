package com.shuozi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.entity.Employee;
import com.shuozi.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController
{
    @Autowired
    EmployeeServiceImpl employeeService ;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee)
    {
//        把得到的密码进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        去数据库里面查询数据
        LambdaQueryWrapper<Employee> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(lambdaQueryWrapper);
//        用户名加了唯一性索引 所以判断用户名是否存在
        if (one==null) return R.error("登陆失败");
//        判断密码是否正确
        if (!one.getPassword().equals(password)) return R.error("密码错误");
        request.setAttribute("password",password);
//        此处也可以放一个验证码的判定
        if(one.getStatus() == 0) return R.error("该用户已经被禁用了");
//       将员工的id存入session并返回登陆成功的结果
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }
//退出功能
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request)
    {
//        清理Session中保存的用户的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

//    保存添加员工的信息
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee)
    {
        log.info("添加进来的员工的是{}",employee.toString());
//        下面注释的用mybatisplus的公共字段自动填充编写
//        Long userId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(userId);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(userId);
//        设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.save(employee);
        return R.success("添加用户成功");


    }

    @GetMapping("/page")
    public R<Page> pageR(int page ,int pageSize, String name)
    {
        log.info("我接收到的page={},pageSize={}，name={}",page,pageSize,name);
//        构建分页构造器
        Page pageInfo =new Page<>(page,pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        模糊查询判断
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        排序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, lambdaQueryWrapper);


        return R.success(pageInfo);
    }

//    更新账号状态   根据id修改员工信息   传递进来的Long类型丢失精度  要添加类型转换器
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee)
    {
        //        下面注释的用mybatisplus的公共字段自动填充编写
//        Long userId  = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(userId);
        employeeService.updateById(employee);
        return R.success("保存成功");
    }

//    修改员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id)
    {
        log.info("正在修改员工信息...");
        if (id!=null)
        {
            Employee emp = employeeService.getById(id);
            return R.success(emp);
        }
        return R.error("未获取到这个人的信息");
    }
}
