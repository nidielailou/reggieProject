package com.shuozi.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuozi.reggie.entity.Employee;
import com.shuozi.reggie.mapper.EmployeeMapper;
import com.shuozi.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
