package com.shuozi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shuozi.reggie.common.BaseContext;
import com.shuozi.reggie.common.R;
import com.shuozi.reggie.entity.ShoppingCart;
import com.shuozi.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加菜品到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("读取到的购物车添加数据是:{}", shoppingCart);
//      设定用户的id  指定当前是哪个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentLocal());
//        查询当前的菜品或者套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper1 = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null) {
            queryWrapper1.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper1.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingInfo = shoppingCartService.getOne(queryWrapper1);
//        如果已存在就在原来数量的基础上加一
        if (shoppingInfo != null) {
            int i = shoppingInfo.getNumber() + 1;
            shoppingInfo.setNumber(i);
            shoppingCartService.updateById(shoppingInfo);
        } else {
            //        如果不存在，则添加到购物车 默认数量是1
            shoppingCart.setNumber(1);
//                    设置创建的时间让购物车显示更加美观
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingInfo = shoppingCart;
        }

        return R.success(shoppingInfo);
    }

    /**
     * 渐少菜品到购物车   存在提升空间
     */
    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper2 = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null) {
            queryWrapper2.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            queryWrapper2.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper2);
        Integer number = cart.getNumber();
        if (number > 0) {
            cart.setNumber(number - 1);
        }
        shoppingCartService.updateById(cart);

        return R.success(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> lookShoppingCart() {
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentLocal());
//        根据添加时间展示购物车信息
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> deleteShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentLocal());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空购物车成功");
    }
}
