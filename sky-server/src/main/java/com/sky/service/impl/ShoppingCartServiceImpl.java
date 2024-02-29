package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId() );
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByDishIdAndSetmealId(shoppingCart);
        //如果本来就在购物车中，则数量加一
        if(shoppingCartList != null && shoppingCartList.size()>0){
            shoppingCartMapper.updateNumberById(shoppingCartList.get(0).getId(),shoppingCartList.get(0).getNumber()+1);
            return;
        }
        //若不在购物车中，则将其添加到购物车中
        Long dishId = shoppingCartDTO.getDishId();
        if(dishId!=null){
            Dish dish = dishMapper.queryById(dishId);
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setName(dish.getName());
        }else{
            Long setmealId = shoppingCartDTO.getSetmealId();
            Setmeal setmeal = setmealMapper.queryById(setmealId);
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setName(setmeal.getName());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.addShoppingCart(shoppingCart);
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> lookShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.lookShoppingCart(userId);
        return shoppingCartList;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.cleanShoppingCart(userId);
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    @Override
    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByDishIdAndSetmealId(shoppingCart);
        ShoppingCart deleteCart = shoppingCartList.get(0);
        if(deleteCart.getNumber()>1){
            shoppingCartMapper.updateNumberById(deleteCart.getId(),deleteCart.getNumber()-1);
        }else{
            shoppingCartMapper.deleteShoppingCartById(deleteCart.getId());
        }
    }
}
