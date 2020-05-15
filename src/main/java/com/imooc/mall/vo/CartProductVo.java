package com.imooc.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartProductVo {

    private Integer productId;

    private Integer quantity;

    private String productName;//购物车里的数量

    private String productSubtitle;

    private String productMainImage;

    private BigDecimal productPrice;

    private BigDecimal productTotalPrice;//等于 quantity * productPrice

    private Integer productStock;

    private Integer productStatus;

    private Boolean productSelected;//商品是否选中

    public CartProductVo(Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImage, BigDecimal productPrice, BigDecimal productTotalPrice, Integer productStock, Integer productStatus, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImage = productMainImage;
        this.productPrice = productPrice;
        this.productTotalPrice = productTotalPrice;
        this.productStock = productStock;
        this.productStatus = productStatus;
        this.productSelected = productSelected;
    }
}
