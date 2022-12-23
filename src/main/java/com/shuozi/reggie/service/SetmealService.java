package com.shuozi.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shuozi.reggie.dto.SetmealDto;
import com.shuozi.reggie.entity.Setmeal;
import com.shuozi.reggie.mapper.SetmealMapper;

import java.util.List;

public interface SetmealService extends IService<Setmeal>
{

    void saveWithDish(SetmealDto setmealDto);

    void removeWithStatus(List<Long> ids);
}
