package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName OrderChangeGoodsMsgVo
 * Description
 * Create by gfy
 * Date 2022/8/10 14:04
 */
@Data
public class OrderChangeGoodsMsgVo {

    private Integer cbobId;

    //商品id
    @ApiModelProperty("商品id")
    private Integer goodsId;

    //商品信息 品牌-型号-描述
    @ApiModelProperty("商品信息 品牌-型号-描述")

    private String goodsMsg;



    //标准单价
    @ApiModelProperty("标准单价")

    private Double normalPrice;

    //金额
    @ApiModelProperty("金额")

    private Double totalPrice;

    //已发货数量
    @ApiModelProperty("已发货数量")

    private Double sendQty;

    //备注
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("品牌")

    private String brand;

    @ApiModelProperty("描述")

    private String description;

    @ApiModelProperty("型号")

    private String model;


    public String getGoodsMsg() {


        return brand+"-"+model+"-"+description;
    }
}
