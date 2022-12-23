package com.shuozi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuozi.reggie.common.CustomException;
import com.shuozi.reggie.dto.SetmealDto;
import com.shuozi.reggie.entity.Dish;
import com.shuozi.reggie.entity.Setmeal;
import com.shuozi.reggie.entity.SetmealDish;
import com.shuozi.reggie.mapper.SetmealMapper;
import com.shuozi.reggie.service.SetmealDishService;
import com.shuozi.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;
    /**
     * 添加套餐 同时向菜品中添加信息
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的信息
        this.save(setmealDto);

//        用for循环的方式进行赋值  id在进行封装到SetmealDishes的时候 不会给值  所以需要手动赋值
        List<SetmealDish> list = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(list);
    }

    @Transactional
    @Override
    public void removeWithStatus(List<Long> ids) {
//        for (Long id : ids) {
//            Setmeal setmeal = this.getById(id);
//            if (setmeal.getStatus()!=1&&setmeal!=null)
//            {
//                this.removeById(id);
//                LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
//                queryWrapper.eq(SetmealDish::getSetmealId,id);
//                setmealDishService.remove(queryWrapper);
//            }
//            throw new CustomException("套餐正在售卖中无法删除");
//        }
//        首先根据id查询 记录  是否是  启用状态
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.in(Setmeal::getId,ids);
        int count = this.count(queryWrapper);
        if (count>0)
        {
            throw new CustomException("套餐正在售卖,无法删除.");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }
}
