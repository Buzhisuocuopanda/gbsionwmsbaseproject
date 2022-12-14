package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Cboa;
import com.ruoyi.system.domain.CboaCriteria;
import java.util.List;

import com.ruoyi.system.domain.Id;
import com.ruoyi.system.domain.dto.BaseSelectDto;
import com.ruoyi.system.domain.dto.SaleOrderListDto;
import com.ruoyi.system.domain.dto.SaleOrderSkuDto;
import com.ruoyi.system.domain.vo.BaseSelectVo;
import com.ruoyi.system.domain.vo.SaleOrderListVo;
import com.ruoyi.system.domain.vo.SaleOrderSkuVo;
import org.apache.ibatis.annotations.Param;

public interface CboaMapper {
    long countByExample(CboaCriteria example);

    int deleteByExample(CboaCriteria example);

    int deleteByPrimaryKey(Integer cboa01);

    int insert(Cboa record);

    int insertSelective(Cboa record);

    List<Cboa> selectByExample(CboaCriteria example);

    Cboa selectByPrimaryKey(Integer cboa01);

    int updateByExampleSelective(@Param("record") Cboa record, @Param("example") CboaCriteria example);

    int updateByExample(@Param("record") Cboa record, @Param("example") CboaCriteria example);

    int updateByPrimaryKeySelective(Cboa record);

    int updateByPrimaryKey(Cboa record);

    List<SaleOrderListVo> saleOrderList(SaleOrderListDto saleOrderListDto);

    List<SaleOrderSkuVo> saleOrderSkuList(SaleOrderSkuDto saleOrderSkuDto);

    List<SaleOrderListVo> finsaleOrderList(SaleOrderListDto saleOrderListDto);

    void insertWithId(Cboa cboa);

    List<BaseSelectVo> orderlistSelect(BaseSelectDto baseSelectDto);

    List<SaleOrderListVo> saleOrderListPj(SaleOrderListDto saleOrderListDto);

    List<SaleOrderListVo> saleOrderListGoods(SaleOrderListDto saleOrderListDto);

    int selectMaxnumber();

    int selectMinnumber();

    List<Id> selectid();

    Cboa selectByPrimaryKeyForUpdate(Integer id);

}
