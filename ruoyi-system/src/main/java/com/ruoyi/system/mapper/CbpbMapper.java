package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.CbpbCriteria;
import java.util.List;

import com.ruoyi.system.domain.dto.GoodsSelectDto;
import com.ruoyi.system.domain.dto.InwuquDto;
import com.ruoyi.system.domain.vo.CbpbVo;
import org.apache.ibatis.annotations.Param;

public interface CbpbMapper {
    long countByExample(CbpbCriteria example);

    int deleteByExample(CbpbCriteria example);

    int deleteByPrimaryKey(Integer cbpb01);

    int insert(Cbpb record);

    int insertSelective(Cbpb record);

    List<Cbpb> selectByExample(CbpbCriteria example);

    Cbpb selectByPrimaryKey(Integer cbpb01);

    int updateByExampleSelective(@Param("record") Cbpb record, @Param("example") CbpbCriteria example);

    int updateByExample(@Param("record") Cbpb record, @Param("example") CbpbCriteria example);

    int updateByPrimaryKeySelective(Cbpb record);

    int updateByPrimaryKey(Cbpb record);

    List<CbpbVo> swJsGoodslistBySelect(GoodsSelectDto goodsSelectDto);

    List<CbpbVo> selectSwJsGoodsAll(CbpbVo cbpbVo);

    int updateCBPB(Cbpb cbpb);

    int insertCBPB(Cbpb cbpb);

    List<CbpbVo> selectSwJsGoodsList(CbpbVo cbpbVo);

    Cbpb selectByPrimaryKeyForUpdate(Integer goodsId);

    int selectcount();

    List<Cbpb> selectGnXs(InwuquDto inwuquDto);

    List<CbpbVo> selectSwJsGoodsListout(CbpbVo cbpbVo);

    List<CbpbVo> selectSwJsGoodsAlls(CbpbVo cbpbVo);
}
