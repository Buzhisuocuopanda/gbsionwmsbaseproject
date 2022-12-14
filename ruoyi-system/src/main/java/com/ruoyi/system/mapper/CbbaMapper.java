package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Cbba;
import com.ruoyi.system.domain.CbbaCriteria;
import java.util.List;

import com.ruoyi.system.domain.dto.TotalOrderListDto;
import com.ruoyi.system.domain.vo.TotalOrderListVo;
import org.apache.ibatis.annotations.Param;

public interface CbbaMapper {
    long countByExample(CbbaCriteria example);

    int deleteByExample(CbbaCriteria example);

    int deleteByPrimaryKey(Integer cbba01);

    int insert(Cbba record);

    int insertSelective(Cbba record);

    List<Cbba> selectByExample(CbbaCriteria example);

    Cbba selectByPrimaryKey(Integer cbba01);

    int updateByExampleSelective(@Param("record") Cbba record, @Param("example") CbbaCriteria example);

    int updateByExample(@Param("record") Cbba record, @Param("example") CbbaCriteria example);

    int updateByPrimaryKeySelective(Cbba record);

    int updateByPrimaryKey(Cbba record);

    List<Cbba> selectLowPriority(@Param("goodsId")Integer goodsId,@Param("poritiry") Long poritiry,@Param("cbba01") Integer cbba01);

    List<Cbba> selectByPriorityDureH2low(@Param("goodsId") Integer goodsId, @Param("newPriority")Long newPriority, @Param("oldPriority")Long oldPriority,@Param("cbba01")Integer cbba01);

    List<Cbba> selectByPriorityDurelow2H(@Param("goodsId")Integer goodsId, @Param("newPriority")Long newPriority, @Param("oldPriority")Long oldPriority,@Param("cbba01")Integer cbba01);


    List<Cbba> selectByGoodsId(@Param("goodsId") Integer goodsId);
    List<Cbba> selectByGoodsIdGive(@Param("goodsId") Integer goodsId);

    List<TotalOrderListVo> totalOrderList(TotalOrderListDto totalOrderListDto);

    Cbba selectByPrimaryKeyForUpdate(@Param("id") Integer id);

    List<Cbba> selectLowPriorityGet(@Param("goodsId")Integer goodsId,@Param("poritiry") Long poritiry,@Param("cbba01") Integer cbba01);

    List<Cbba> selectByOther(@Param("goodsId")Integer goodsId,@Param("cbba01") Integer cbba01);

    List<Cbba> selectByIds(@Param("ids") List<Integer> ids);

    void plDeleByIds(@Param("ids") List<Integer> ids);

    List<Cbba> selectByGoodsIdGn(Integer goodsId);

    List<TotalOrderListVo> selectorderno(TotalOrderListDto totalOrderListDto);
}
