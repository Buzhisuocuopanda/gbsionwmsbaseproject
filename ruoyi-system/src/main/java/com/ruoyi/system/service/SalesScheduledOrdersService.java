package com.ruoyi.system.service;

import com.ruoyi.system.domain.Do.GsOrdersInDo;
import com.ruoyi.system.domain.Do.GsSalesChangeDo;
import com.ruoyi.system.domain.Do.GsSalesOrdersDo;
import com.ruoyi.system.domain.Do.GsSalesOrdersInDo;
import com.ruoyi.system.domain.GsSalesOrdersChange;
import com.ruoyi.system.domain.GsSalesOrdersIn;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;

import java.util.List;

public interface SalesScheduledOrdersService {
    void addSalesScheduledOrders(GsSalesOrdersDto gsSalesOrdersDto);

    void editSalesScheduledOrders(GsSalesOrdersDto gsSalesOrdersDto);

    void deleteSalesScheduledOrders(DeleteSaleOrderDto deleteSaleOrderDto);

    List<GsSalesOrdersVo> saleOrderList(GsSalesOrdersDo gsSalesOrdersDo);

    List<GsSalesOrdersDetailsVo> saleOrderListdetail(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo);

    //销售预订单变更单
    List<GsSalesOrdersDetailsVo> saleOrderAdvance(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo);

    void salesScheduledOrderssh(GsSalesOrdersDto gsSalesOrdersDto);

    void salesScheduledOrdersfs(GsSalesOrdersDto gsSalesOrdersDto);

    void salesScheduledOrdersbjwc(GsSalesOrdersDto gsSalesOrdersDto);

    void salesScheduledOrdersqxwc(GsSalesOrdersDto gsSalesOrdersDto);

    int addSubscribetotheinventoryslip(List<GsSalesOrdersIn> gsSalesOrdersInDto);

    void editSubscribetotheinventoryslip(GsSalesOrdersInDto gsSalesOrdersInDto);

    void deleteSubscribetotheinventoryslip(GsSalesOrdersInDto gsSalesOrdersInDto);

    List<GsSalesOrdersInVo> seleteSubscribetotheinventoryslip(GsSalesOrdersInVo gsSalesOrdersInVo);

    //销售预订单入库单详情
    List<GsSalesOrdersInVo> selectSalesReceiptList(GsSalesOrdersInVo gsSalesOrdersInVo);

    void subscribetotheinventoryslipsh(GsSalesOrdersInDto gsSalesOrdersInDto);

    void subscribetotheinventoryslipfs(GsSalesOrdersInDto gsSalesOrdersInDto);

    void subscribetotheinventoryslipbjwc(GsSalesOrdersInDto gsSalesOrdersInDto) throws InterruptedException;

    void subscribetotheinventoryslipqxwc(GsSalesOrdersInDto gsSalesOrdersInDto);

    int addGsSalesOrdersChange(List<GsSalesOrdersChange> gsSalesOrdersChange);

    void editGsSalesOrdersChange(GsSalesOrdersChangeDto gsSalesOrdersChangeDto);

    void deleteGsSalesOrdersChange(GsSalesOrdersChangeDto gsSalesOrdersChangeDto);

    List<GsSalesOrdersChangeVo> seleteGsSalesOrdersChange(GsSalesOrdersChangeVo gsSalesOrdersChangeVo);

    void gsSalesOrdersChangesh(GsSalesOrdersChangeDto gsSalesOrdersChangeDto);

    void gsSalesOrdersChangefs(GsSalesOrdersChangeDto gsSalesOrdersChangeDto);

    void gsSalesOrdersChangebjwc(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) throws InterruptedException;

    void gsSalesOrdersChangeqxwc(GsSalesOrdersChangeDto gsSalesOrdersChangeDto);

    List<GsSalesOrderssVo> seleteSalesbookingsummary(GsSalesOrderssVo gsSalesOrderssVo);


    //导入新增
    int insertSwJsStores(List<GsSalesOrdersdrDto> itemList);

    String importSwJsGoods(List<GsSalesOrdersdrDto> swJsGoodsList, boolean updateSupport, String operName);

 FgkVo seleteSaleFgkVomary(FgkVo fgkVo);

    int editGsSalesOrdersChanges(List<GsSalesOrdersChange> gsSalesOrdersChangeDto);

    void SwJsPurchaseinboundeditone(GsSalesChangeDo cbpdDto);

    void SwJsPurchaseinboundedibgdxg(GsSalesChangeDo cbpdDto);

    void SwJsPurchaseinboundedgdxgsh(GsSalesChangeDo cbpdDto);

    void SwJsPurchaseinboundbgdxgdelete(GsSalesChangeDo cbpdDto);

    List<GsSalesOrdersVo> saleOrderLists(GsSalesOrdersDo gsSalesOrdersDo);

    List<GsSalesOrdersDetailsVo> saleOrderListdetails(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo);

    String importSwJsGoodss(List<GsSalesOrdersInDo> swJsGoodsList, boolean updateSupport, String operName);


    void SwJsPurchaseinboundrkdxz(GsOrdersInDo cbpdDto);

    void SwJsPurchaseinboundedirkdxg(GsOrdersInDo cbpdDto);

    void SwJsPurchaseinbounderkdsh(GsSalesChangeDo cbpdDto);

    GsOrdersInDo SwJsPurchaseinboundrrkdxq(GsOrdersInDo cbpdDto);
}
