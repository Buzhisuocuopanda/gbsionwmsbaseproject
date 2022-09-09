package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName TotalOrderListVo
 * Description
 * Create by gfy
 * Date 2022/8/3 15:47
 */
@Data
public class TotalOrderListVo {
    @Excel(name = "优先级")
    @ApiModelProperty("优先级")

    private Integer priority;

    private Integer id;
    @Excel(name = "订单号")
    @ApiModelProperty("订单号")

    private String orderNo;
    @Excel(name = "型号")
    @ApiModelProperty("型号")

    private String model;
    @Excel(name = "描述")
    @ApiModelProperty("描述")

    private String description;

    @Excel(name = "订单数量")
    @ApiModelProperty("订单数量")

    private Double orderQty;
    @Excel(name = "生产数量")
    @ApiModelProperty("生产数量")

    private Double makeQty;
    @Excel(name = "已发货数量")
    @ApiModelProperty("已发货数量")

    private Double shippedQty;
    @ApiModelProperty("现有订单数量")
    @Excel(name = "现有订单数量")
    private Double currentOrderQty;

    private Integer orderType;
    @ApiModelProperty("类型")
    @Excel(name = "类型")
    private String orderTypeMsg;
    @Excel(name = "状态")
    @ApiModelProperty("状态 0：NO  4:OK")
    private String status;
}
