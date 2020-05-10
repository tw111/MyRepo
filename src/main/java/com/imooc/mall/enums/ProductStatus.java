package com.imooc.mall.enums;

import com.imooc.mall.vo.ValueLabelRes;

import java.util.LinkedList;
import java.util.List;

/**
 * 商品状态.1-在售 2-下架 3-删除
 */
public enum ProductStatus {

    ON_SALE(1,"在售"),

    OFF_SALE(2,"下架"),

    DELETE(3,"删除"),

    ;

    Integer code;

    String desc;

    private ProductStatus(Integer code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ProductStatus getByCode(int code){
        for (ProductStatus ps : ProductStatus.values()){
            if (ps.code == code){
                return ps;
            }
        }
        throw new IllegalArgumentException("Not supported client code:" + code);
    }

    public static List<ValueLabelRes> getAllCode(){
        List<ValueLabelRes> list = new LinkedList<>();
        for (ProductStatus ps : ProductStatus.values()){
            ValueLabelRes res = new ValueLabelRes();
            res.setLabel(ps.desc);
            res.setValue(ps.code.toString());
            list.add(res);
        }
        return list;
    }
}
