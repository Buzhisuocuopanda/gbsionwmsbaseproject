package com.ruoyi.system.service;

import com.ruoyi.system.domain.Cbpc;
import com.ruoyi.system.domain.Cbpd;
import com.ruoyi.system.domain.Cbpe;
import com.ruoyi.system.domain.Cbsc;
import com.ruoyi.system.domain.Do.CbpcDo;
import com.ruoyi.system.domain.dto.CbpcDto;
import com.ruoyi.system.domain.dto.CbpdDto;
import com.ruoyi.system.domain.vo.CbpcVo;
import com.ruoyi.system.domain.vo.IdVo;

import java.util.List;

public interface ISwJsPurchaseinboundService {
    /**
     * 新增采购入库单
     *
     * @param cbpdDto
     * @return 结果
     */

    IdVo insertSwJsSkuBarcodes(CbpdDto cbpdDto);

    int SwJsSkuBarcodeshs(CbpdDto cbpdDto);

    int deleteSwJsSkuBarcodsById(CbpdDto cbpdDto);

    int updateSwJsSkuBarcodes(CbpcDo cbpcDo);

    List<CbpcVo> selectSwJsTaskGoodsRelLists(CbpcVo cbpcVo);

    public List<Cbpc> selectCBPCList(Cbpc cbpc);

    String importSwJsGoods(List<CbpcDto> swJsGoodsList, boolean updateSupport, String operName);

    int SwJsSkuBarcodeshss(CbpdDto cbpdDto);

    int SwJsSkuBarcodeshsss(CbpdDto cbpdDto) throws InterruptedException;

    int SwJsSkuBarcodesh(CbpdDto cbpdDto);

    List<CbpcVo> selectSwJsTaskGoodsRelListss(CbpcVo cbpcVo);

    List<CbpcVo> selectSwJsTaskGoodsRelListsss(CbpcVo cbpcVo);

    int insertSwJsSkuBarcodesm(Cbpe itemList);


    int insertSwJsStores(List<CbpcDto> itemList);

    int insertSwJsSkuBarcsodesm(List<Cbpd> itemList);

    void SwJsPurchaseinboundeditone(CbpdDto cbpdDto);

    int insertSwJsSkuBarcodesplus(CbpdDto cbpdDto);
}
