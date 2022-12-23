package com.shuozi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.dto.DishDto;
import com.shuozi.reggie.entity.Dish;
import com.shuozi.reggie.entity.DishFlavor;
import com.shuozi.reggie.mapper.DishMapper;
import com.shuozi.reggie.service.DishFlavorService;
import com.shuozi.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService
{
    @Autowired
    DishFlavorService dishFlavorService;

//    多表操作添加事务 且  flavor表中的 dishId 字段单独填充
    @Transactional
    public void saveWithFlavor(DishDto dishDto)
    {

        this.save(dishDto);
//      菜品id
        Long id = dishDto.getId();
//        菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors =flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;}).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

//    根据id查询对应的口味信息
    @Override
    public DishDto getByIdWithFlavour(Long id) {
//      首先把菜品表的数据查询并复制到 Dto中
        Dish dish = this.getById(id);
        DishDto dto = new DishDto();
        BeanUtils.copyProperties(dish,dto);
//      查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dto.setFlavors(list);
        return dto;
    }


//    更新对应的菜品  问题在更新菜品基础上去更新 口味表的信息
    @Override
    public void updateDishAndDishFlavour(DishDto dishDto) {
        this.updateById(dishDto);

//        根据菜品的口味 先移除相关的信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

//        把dishFlavours从dishDto中取出 重新进行赋值
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishFlavors=dishFlavors.stream().map((item)->
        {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);
    }


}
