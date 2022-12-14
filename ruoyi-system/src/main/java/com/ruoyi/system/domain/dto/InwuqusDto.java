package com.ruoyi.system.domain.dto;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class InwuqusDto extends BaseEntity {
    private String cbwa09;
    private String[] cbwa09s;

    private String cbla09;
    private String[] cbla09s;

    private Integer cbig09;

    private String cbig10;

    private Integer cbpb01;

    private Integer cbpb10;

    //上架状态 1：上架 2：已下架
    private Integer groudStatus;
    //商品状态 1：已入库 2：出库中 3：已出库
    private Integer status;

    private Integer whid;

    private Long deptId;

    private Long userId;

    private Date startTime;

    private Date endTime;

}
