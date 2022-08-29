package com.ruoyi.system.service;

import com.ruoyi.system.domain.Cbpe;
import com.ruoyi.system.domain.Cbph;
import com.ruoyi.system.domain.Cbpi;
import com.ruoyi.system.domain.dto.CbpgDto;
import com.ruoyi.system.domain.vo.CbpgVo;
import com.ruoyi.system.domain.vo.IdVo;

import java.util.List;

public interface ISwJsPurchasereturnordersService {
    IdVo insertSwJsSkuBarcodes(CbpgDto cbpgDto);

    int deleteSwJsSkuBarcodsById(CbpgDto cbpgDto);

    int updateSwJsSkuBarcodes(CbpgDto cbpgDto);


    List<CbpgVo> selectSwJsTaskGoodsRelLists(CbpgVo cbpgVo);

    List<CbpgVo> selectSwJsTaskGoodsRelList(CbpgVo cbpgVo);

    List<CbpgVo> selectSwJsTaskGoodsRelListss(CbpgVo cbpgVo);

    int SwJsSkuBarcodeshs(CbpgDto cbpgDto);

    int SwJsSkuBarcodesh(CbpgDto cbpgDto);

    int SwJsSkuBarcodeshss(CbpgDto cbpgDto);

    int SwJsSkuBarcodes(CbpgDto cbpgDto);

    int insertSwJsSkuBarcodess(List<Cbph> itemList);

    int insertSwJsSkuBarcodesm(List<Cbpi> itemList);
}
