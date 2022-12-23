package com.shuozi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.dto.SetmealDto;
import com.shuozi.reggie.entity.Category;
import com.shuozi.reggie.entity.Setmeal;
import com.shuozi.reggie.service.CateGoryService;
import com.shuozi.reggie.service.SetmealDishService;
import com.shuozi.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealDishController
{
    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    SetmealService setmealService;

    @Autowired
    CateGoryService cateGoryService;

    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto)
    {
        log.info("添加的套餐数据是={}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐信息成功");
    }

    /**
     * 套餐的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageSearch(int page,int pageSize,String name)
    {
//      创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
//        判断是否通过名称查询
        queryWrapper.eq(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
//      把pageInfo中除了records都拷贝到setmealDtoPage中
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
//        获取records并进行赋值
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list =records.stream().map((item)->
        {
            SetmealDto setmealDto = new SetmealDto();
//            对象的拷贝      创建对象 对名称进行查询和单独拷贝
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = cateGoryService.getById(categoryId);
            if (category!=null)
            {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
//      把records写入到setmealDtoPage
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐   根据状态  状态是0被停用的  才能去删除  下面也可以用   接收
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids)
    {
        log.info("删除传入进来的id是{}",ids.toString());
//        根据状态去判断是否删除数据
        setmealService.removeWithStatus(ids);
        return R.success("删除套餐成功");
    }

    @PostMapping("/status/{id}")
    public R<String>  updateStatus(@PathVariable("id") int status,@RequestParam List<Long> ids)
    {
        log.info("status={},ids={}",status,ids.toString());
        SetmealDto setmealDto =new SetmealDto();
        if (status == 1) {
            setmealDto.setStatus(1);
        } else {
            setmealDto.setStatus(0);
        }
        LambdaQueryWrapper<Setmeal> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        setmealService.update(setmealDto,queryWrapper);

        return R.success("修改状态成功");

    }

//     在移动客户端查询套餐 的展示
    @GetMapping("/list")
    public R<List<Setmeal>> setmealShow(Setmeal setmeal)
    {
        LambdaQueryWrapper<Setmeal> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
 }
