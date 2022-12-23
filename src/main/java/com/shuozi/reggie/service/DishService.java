package com.shuozi.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuozi.reggie.dto.DishDto;
import com.shuozi.reggie.entity.Dish;

public interface DishService extends IService<Dish>
{
    void saveWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavour(Long id);

    void updateDishAndDishFlavour(DishDto dishDto);
}
