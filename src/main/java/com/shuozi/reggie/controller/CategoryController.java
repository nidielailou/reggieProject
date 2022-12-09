package com.shuozi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.entity.Category;
import com.shuozi.reggie.service.CateGoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Log4j2
public class CategoryController
{
    @Autowired
    private CateGoryService cateGoryService;

//    添加一个菜品分类  共用全局异常处理器
    @PostMapping
    public R<String> add(@RequestBody Category category)
    {
        log.info("category={}",category);
        cateGoryService.save(category);
        return R.success("添加成功");
    }

//    做菜单的分页查询
    @GetMapping("/page")
    public  R<Page> pageSearch(int page, int pageSize)
    {
        log.info("分类查询中......");
//        分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
//        添加排序条件  根据sort字段升序排序
        LambdaQueryWrapper<Category> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        cateGoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

//    删除菜单分类信息(注意关联菜品和套餐的问题 不能直接调用逻辑  需要自定义)
      @DeleteMapping
    public R<String> delete(Long ids)
      {
          log.info("正在删除id为{}的分类信息...",ids);
//          cateGoryService.removeById(ids);
          cateGoryService.remove(ids);
          return R.success("删除分类信息成功");
      }

//     更新分类的名称和排序
    @PutMapping
    public R<String> update(@RequestBody Category category)
      {
          log.info("更新的数据信息是:{}",category);
           cateGoryService.updateById(category);
           return R.success("数据修改成功");
      }

}
