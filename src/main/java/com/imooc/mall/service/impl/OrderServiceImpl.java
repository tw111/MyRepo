package com.imooc.mall.service.impl;

import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.enums.OrderStatus;
import com.imooc.mall.enums.PaymentType;
import com.imooc.mall.enums.ProductStatus;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.pojo.*;
import com.imooc.mall.service.ICartService;
import com.imooc.mall.service.IOrderService;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ICartService cartService;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //收货地址校验
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uid,shippingId);
        if (shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        //获取购物车，校验（是否有商品、库存）
        List<Cart> cartList = cartService.listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartList)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        //获取cartList里的products
        Set<Integer> productIdSet = cartList.stream().map(Cart::getProductId).collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        Map<Integer,Product> map = productList.stream().collect(Collectors.toMap(Product::getId,product -> product));

        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();
        for (Cart cart : cartList){
            //根据productId查数据库
            Product product = map.get(cart.getProductId());
            if (product == null){
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在. productId = " + cart.getProductId());
            }
            //商品的上下架状态
            if (!ProductStatus.getByCode(product.getStatus()).equals(ProductStatus.ON_SALE)){
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE,
                        "商品不是在售状态. " + product.getName());
            }

            //库存是否充足
            if (product.getStock() < cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,
                        "库存不正确. " + product.getName());
            }
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);
        }
        //计算总价，只计算被选中的商品

        //生成订单，入库：order和order_item,事务
        Order order = buildOrder(uid,orderNo,shippingId,orderItemList);
        int row = orderMapper.insertSelective(order);
        if (row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        //减库存

        //更新购物车（选中的商品）

        //构造orderVo对象

        return null;
    }

    private OrderItem  buildOrderItem(Integer uid,Long orderNo,Integer quantity,Product product){
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    /**
     * 企业级：分布式唯一id/主键
     */
    private Long generateOrderNo(){
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    private Order buildOrder(Integer uid,Long orderNo,Integer shippingId,List<OrderItem> orderItemList){
        BigDecimal payment = orderItemList.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PaymentType.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatus.NO_PAY.getCode());
        return order;
    }
}
