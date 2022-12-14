package com.ruoyi.system.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CbsbsVo extends BaseEntity {
    private Integer cbsc01;
    private Date cbsd03;
    private Integer cbsb10;
    private Integer cbsb09;

    private String cbsb07;
    private String cbca09;
    private Date cbsb08;
    private String cbca08;
    private String cbwa09;
    private String caua15;
    private String cbpb15;
    private String cbsb17;

    private String cbsb18;
    private String cbsb19;
    private Integer cbca28;
    private String cala08;
    private Integer cbsb31;
    private String cbsb30;
    private Integer cbsb20;

    private String cbsb21;
    private String cbsa08;
    private Integer cbsc17;
    private String cbpb12;
    private String cbpb08;
    private Integer cbsc08;

    private Double cbsc09;
    private Double cbsc11;
    private Double cbsc12;
    private String cbsc13;
    private String cbsd09;
    private String cbla09;
    private Integer cbsd11;
    private Integer cbsb01;
    private Integer cbsc02;
    private String cbpa07;
    private Integer cbsd02;

private String cny;
    private Integer saoma;

    private List<ScanVo> goods = new ArrayList<>();



    private List<TakeOrderSugestVo> outsuggestion = new ArrayList<>();

    private Integer saomanums;

    private Double nums;
}
