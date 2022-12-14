package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Cbla;
import com.ruoyi.system.domain.CblaCriteria;
import java.util.List;

import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.vo.CblaVo;
import org.apache.ibatis.annotations.Param;

public interface CblaMapper {
    long countByExample(CblaCriteria example);

    int deleteByExample(CblaCriteria example);

    int insert(Cbla record);

    int insertSelective(Cbla record);

    List<Cbla> selectByExample(CblaCriteria example);

    int updateByExampleSelective(@Param("record") Cbla record, @Param("example") CblaCriteria example);

    int updateByExample(@Param("record") Cbla record, @Param("example") CblaCriteria example);

    List<CblaVo> selectSwJsStoreList(CblaVo cblaVo);

    int insertCBLA(Cbla cbla);

    int updateCBLA(Cbla cbla);

    Cbla selectByPrimaryKey(Integer cbla01);
}
