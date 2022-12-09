package com.shuozi.reggie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuozi.reggie.entity.Category;
import com.shuozi.reggie.entity.Dish;
import com.shuozi.reggie.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;


public interface CateGoryService extends IService<Category>
{
    public void remove(Long id);
}
