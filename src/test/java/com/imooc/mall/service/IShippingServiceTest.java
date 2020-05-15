package com.imooc.mall.service;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
public class IShippingServiceTest extends MallApplicationTests {

    @Autowired
    private IShippingService shippingService;

    private Integer uid = 1;

    private ShippingForm form;

    private Integer shippingId;

    @Before
    public void before() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("tw");
        form.setReceiverAddress("hubei");
        form.setReceiverCity("enshi");
        form.setReceiverMobile("15971829753");
        form.setReceiverPhone("0718-2653658");
        form.setReceiverProvince("湖北");
        form.setReceiverDistrict("恩施區");
        form.setReceiverZip("445000");
        this.form = form;

        add();
    }

    public void add() {
        ResponseVo<Map<String,Integer>> responseVo = shippingService.add(uid,form);
        log.info("result={}",responseVo);
        this.shippingId = responseVo.getData().get("shippingId");
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @After
    public void delete() {
        ResponseVo<Map<String,Integer>> responseVo = shippingService.delete(uid,shippingId);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void update() {
        form.setReceiverCity("杭州");
        ResponseVo<Map<String,Integer>> responseVo = shippingService.update(uid,shippingId,form);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void list() {
        ResponseVo responseVo = shippingService.list(uid,1,10);
        log.info("result={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}