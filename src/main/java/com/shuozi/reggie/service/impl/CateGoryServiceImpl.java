package com.shuozi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuozi.reggie.common.CustomException;
import com.shuozi.reggie.entity.Category;
import com.shuozi.reggie.entity.Dish;
import com.shuozi.reggie.entity.Setmeal;
import com.shuozi.reggie.mapper.CategoryMapper;
import com.shuozi.reggie.service.CateGoryService;
import com.shuozi.reggie.service.DishService;
import com.shuozi.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CateGoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CateGoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    //    根据是否关联了菜品或者套餐来执行删除业务
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        判断是否含有分类的 id
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(lambdaQueryWrapper);
        if (count1 > 0) {
//            如果有关联的菜品报错
            throw new CustomException("该分类关联了菜品,不能删除");
        }
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
//        判断是否含有分类的 id
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(lambdaQueryWrapper1);
        if (count2 > 0) {
//            有关联的套餐  抛出业务异常
            throw new CustomException("该分类关联了套餐,不能删除");
        }

        super.removeById(id);

    }
}