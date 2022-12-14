package com.ruoyi.system.service;

import com.ruoyi.system.domain.Do.CbibDo;
import com.ruoyi.system.domain.GsAfterSales;
import com.ruoyi.system.domain.dto.GsAfterSalesDto;
import com.ruoyi.system.domain.vo.CbpcVo;
import com.ruoyi.system.domain.vo.GsAfterSalesVo;
import com.ruoyi.system.domain.vo.GsGoodsSnVo;
import com.ruoyi.system.domain.vo.IdVo;

import java.util.List;

public interface AftersalesService {


    int insertaftersales(GsAfterSalesDto gsAfterSalesDto);

    int updateaftersales(GsAfterSalesDto gsAfterSalesDto);

    int deletesales(GsAfterSalesDto gsAfterSalesDto);

    List<GsAfterSalesVo> aftersaleslist(GsAfterSalesVo gsAfterSalesVo);

    GsAfterSales saleOderDetail(Integer orderId);

    List<CbibDo> test(CbibDo cbibDo);

    List<GsGoodsSnVo> selectGoodsSnSelect(GsGoodsSnVo gsGoodsSnVo);
}
