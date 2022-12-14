package com.ruoyi.system.service.gson;

import com.ruoyi.common.core.domain.entity.Cbpa;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.Cbba;
import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.Do.GsGoodsSnDo;
import com.ruoyi.system.domain.Do.SaleOrderCheckDo;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.vo.CbpdVo;

import java.util.Map;

/**
 * ClassName BaseCheckService
 * Description
 * Create by gfy
 * Date 2022/8/1 10:15
 */
public interface BaseCheckService {

    Cbpb checkGoodsForUpdate(Integer goodsId,String goodsName);


    Cbpb checkGoods(Integer goodsId,String goodsName);

    Cbba checkTotalExist(Integer goodsId, String orderNO);


    Boolean saleOrderStatusChekc (SaleOrderCheckDo saleOrderCheckDo);



    GsGoodsSku checkGoodsSkuForUpdate(Integer Id);

    Cbsa checksupplier(Integer supplierid);

    Cbwa checkStore(Integer Storeid);

    Cbpb checkGoods(Integer goodsId);

    Cbla checkStoresku(Integer Storeskuid);

    SysUser checkUserTask(Long userId, String auditPerm);

    GsGoodsSku checkGoodsSku(Integer goodsId,Integer storeId);

    Cbca checkCustomer(Integer customerId);

    Cboa checkSaleOrder(Integer orderId);

    GsGoodsSn checkGsGoodsSn(GsGoodsSnDo gsGoodsSnDo);



//    GoodsCheckStockVo checkGoodsStock(Integer goodsId, Integer orderClass);



    //模糊查询品牌型号描述
    CbpdVo selectgoodsinfo(CbpdVo cbpdVo);


    //获取品牌map
    Map<Integer ,String> brandMap();

    Map<Integer, Cbpa> classMap();

}
