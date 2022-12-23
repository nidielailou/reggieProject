package com.shuozi.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.dto.DishDto;
import com.shuozi.reggie.entity.Category;
import com.shuozi.reggie.entity.Dish;
import com.shuozi.reggie.entity.DishFlavor;
import com.shuozi.reggie.service.CateGoryService;
import com.shuozi.reggie.service.DishFlavorService;
import com.shuozi.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    DishFlavorService flavorService;

    @Autowired
    DishService dishService;

    @Autowired
    CateGoryService cateGoryService;
    /**
     * 添加菜品
     * @param dish
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dish) {
        log.info("传入进来的dishDto={}",dish.toString());
        dishService.saveWithFlavor(dish);
        return R.success("添加菜品成功");
    }

    /**
     * 分页展示菜品
     * 注:下面是拷贝一个实体表的数据到dto并进行修改
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageShow(int page, int pageSize, String name)
    {
//       创建分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
//        创建条件查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        根据传递的name模糊查询  以及根据更新时间排序
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);
//      不拷贝Page里面的records(页面展示所承载的集合，也就是页面展示的数据)  自己去处理赋值
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records= pageInfo.getRecords();

        List<DishDto> list=records.stream().map((items)->{
            DishDto dto =new DishDto();
//            把page里面除了records自己拟定的 种类名称 加入到 DishDto中
            BeanUtils.copyProperties(items,dto);
//            下面是获取并加入名称
            Long categoryId = items.getCategoryId();
            Category byId = cateGoryService.getById(categoryId);
            if (byId!=null){
                String name1 = byId.getName();
                dto.setCategoryName(name1);
            }

            return dto;
        }).collect(Collectors.toList());
//        设置里面的数据是自己的数据
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

//    修改菜品数据信息的回显
    @GetMapping("/{id}")
    public R<DishDto> getDishByIdWithDishFlavour(@PathVariable Long id)
    {
        DishDto dto = dishService.getByIdWithFlavour(id);

        return R.success(dto);
    }

//    更新dish表的数据
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto)
    {
        dishService.updateDishAndDishFlavour(dishDto);
        return R.success("更新成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> getDishById(Dish dish)
//    {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
////        根据菜品的是否启用进行查询
//        queryWrapper.eq(Dish::getStatus,1);
////        排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 查询启用菜品和相关的口味信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishById(Dish dish)
    {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
//        根据菜品的是否启用进行查询
        queryWrapper.eq(Dish::getStatus,1);
//        排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> list1 = list.stream().map((item)->
        {
            DishDto dto =new DishDto();
            BeanUtils.copyProperties(item,dto);

            Long categoryId = item.getCategoryId();//分类id
            LambdaQueryWrapper<DishDto> queryWrapper1 =new LambdaQueryWrapper<>();
            queryWrapper1.eq(Dish::getCategoryId,categoryId);
            Category categorybyId = cateGoryService.getById(categoryId);
            if (categorybyId!=null)
            {
                String name = categorybyId.getName();
                dto.setCategoryName(name);
            }
//            当前菜品的id   并且根据id去查询口味  添加进去
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper2= new LambdaQueryWrapper<>();
            queryWrapper2.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = flavorService.list(queryWrapper2);
            dto.setFlavors(dishFlavorList);
            return dto;
        }).collect(Collectors.toList());

        return R.success(list1);
    }
}
