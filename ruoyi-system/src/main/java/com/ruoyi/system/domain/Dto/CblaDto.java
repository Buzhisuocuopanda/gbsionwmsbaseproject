package com.ruoyi.system.domain.Dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
public class CblaDto {
    private Integer cbla01;

    private Date cbla02;

    private Integer cbla03;

    private Date cbla04;

    private Integer cbla05;

    private Integer cbla06;
    @NotNull(message = "库位码不能为空")
    @Length(min =1 , max = 11)
    private Integer cbla07;
    @NotBlank(message = "库位状态不能为空")
    @Length(min =1 , max = 5)
    private String cbla08;
    @NotBlank(message = "库位码不能为空")
    @Length(min =1 , max = 30)
    private String cbla09;

    private Integer cbla10;
    @NotNull(message = "库位容量不能为空")
    @Length(min =1 , max = 11)
    private Double cbla11;
    @NotBlank(message = "优选型号不能为空")
    @Length(min =1 , max = 30)
    private String cbla12;
    @NotBlank(message = "备注不能为空")
    @Length(min =1 , max = 30)
    private String cbla13;
}
