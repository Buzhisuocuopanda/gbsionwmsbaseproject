package com.ruoyi.system.domain.vo;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class InwuqusVo2 {

    private Integer id;

    @Excel(name = "仓库")
    private String cbwa09;

    @Excel(name = "库位")
    private String cbla09;

    @Excel(name = "商品分类")
    private String cbpa07;

    @Excel(name = "品牌")
    private String cala08;

    @Excel(name = "型号")
    private String cbpb12;

    @Excel(name = "商品")
    private String cbpb08;

    @Excel(name = "UPC")
    private String cbpb15;

    @Excel(name = "商品SN")
    private String sn;

//    @Excel(name = "入库日期",width = 30,dateFormat = "yyyy-MM-dd")
    private Date inTime;
    @Excel(name = "出库日期",width = 30,dateFormat = "yyyy-MM-dd")
    private Date outTime;
    //商品状态 1：已入库 2：出库中 3：已出库
    @Excel(name = "商品状态", readConverterExp = "1=已入库,2=出库中,3=已出库")
    private Integer status;

    //上架状态 1：上架 2：已下架
    @Excel(name = "上架状态", readConverterExp = "1=上架,2=已下架")
    private Integer groudStatus;
    @Excel(name = "质量状态", readConverterExp = "0=正常,1=维修中")
    private Integer repairStatus;

    private Integer goodsId;

    private Integer locationId;

}
