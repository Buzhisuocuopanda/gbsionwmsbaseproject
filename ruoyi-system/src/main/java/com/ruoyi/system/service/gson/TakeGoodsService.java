package com.ruoyi.system.service.gson;

import com.ruoyi.system.domain.Cbpm;
import com.ruoyi.system.domain.GsGoodsSn;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName TakeGoodsService
 * Description
 * Create by gfy
 * Date 2022/8/10 17:51
 */
public interface TakeGoodsService {
    List<TakeGoodsOrderListVo> takeOrderList(TakeGoodsOrderListDto takeGoodsOrderListDto);

    void addTakeGoodsOrder(TakeGoodsOrderAddDto takeGoodsOrderAddDto);

    TakeGoodsOrderDetailVo takeOrderDetail(Integer id);

    TakeGoodsOrderDetailVo takeOrderDetailBySaleId(Integer saleOrderId,Integer whId);

    TakeGoodsOrderDetailVo takeOrderDetailBySaleIdIds(List<Integer> saleOrderIds,Integer whId);

    void mdfTakeGoodsOrder(TakeGoodsOrderAddDto takeGoodsOrderAddDto);

    void delTakeGoodsOrder(Integer id, Long userId);

    void auditTakeOrder(AuditTakeOrderDto auditTakeOrderDto);

    void mdfTakeSuggest(ChangeSuggestDto changeSuggestDto);

    int TakeGoodsOrdersm(Cbpm itemList);

    List<GsOutStockAdivceVo> saleOrderSuggest(GsOutStockAdivceDto gsOutStockAdivceDto);

    void auditOutStockEnd(GsOutStockAdivceDto gsOutStockAdivceDto);

    List<GsGoodsSnVo> selectGoodsSnByWhIdAndGoodsId(Integer whId,Integer goodsId);

    List<GsGoodsSnVo> selectGoodsSnByStatus(GsGoodsSnVo gsGoodsSnVo);

    void mdfTakeSuggest2(CbpmDto cbpmDto);

    List<TakeGoodsOrderListVo> takeOrderListCk(TakeGoodsOrderListDto takeGoodsOrderListDto);

    TakeGoodsOrderDetailVo takeOrderDetailIds(List<Integer> ids);

    TakeGoodsOrderDetailVo saleExitDetailByIds(SaleExitDetailByIdsDto saleExitDetailByIdsDto);

    int TakeGoodsOrdersmtotal(List<Cbpm> itemList);

    int editTakeGoodsOrdersn(TakeGoodsOrderAddDto takeGoodsOrderAddDto);
}
