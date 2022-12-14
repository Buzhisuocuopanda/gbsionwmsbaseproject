package com.ruoyi.system.service;

import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.Cbpf;
import com.ruoyi.system.domain.Do.CbpaDo;
import com.ruoyi.system.domain.Do.CbpbDo;
import com.ruoyi.system.domain.Do.CbpfDo;
import com.ruoyi.system.domain.dto.CbpbDto;
import com.ruoyi.system.domain.dto.CbpcDto;
import com.ruoyi.system.domain.dto.GoodsSelectDto;
import com.ruoyi.system.domain.vo.CbpbVo;
import com.ruoyi.system.domain.vo.BaseSelectVo;
import com.ruoyi.system.domain.vo.IdVo;

import java.util.List;

public interface ISwJsGoodsService {
    /**
     * 新增商品
     *
     * @param cbpbDo 商品
     * @return 结果
     */
    IdVo insertSwJsGoodsClassify(CbpbDo cbpbDo);
    /**
     * 修改商品
     *
     * @param cbpbDo 商品分类
     * @return 结果
     */
    int updateSwJsGoodsClassify(CbpbDo cbpbDo);
    /**
     * 删除商品
     *
     * @param cbpbDo 需要删除的商品分类主键
     * @return 结果
     */
    int deleteSwJsGoodsClassifyById(CbpbDo cbpbDo);

    List<CbpbVo> selectSwJsGoodsList(CbpbVo cbpbVo);

    String importSwJsGoods(List<CbpbDto> swJsGoodsList, boolean updateSupport, String operName);

    int insertSwJsGoodsClassifys(List<Cbpf> cbpf);

    String importSwJsCustomer(List<Cbpf> swJsCustomersList, boolean updateSupport, String operName);


    int insertSwJsStores(List<CbpbDto> itemList);

    /**
     * zgl
     * 查询全部商品信息(不分页)
     * @param cbpbVo
     * @return
     */
    List<CbpbVo> selectSwJsGoodsAll(CbpbVo cbpbVo);

    /**
     * gfy
     * @param goodsSelectDto
     * @return
     */
    List<BaseSelectVo> swJsGoodslistBySelect(GoodsSelectDto goodsSelectDto);

    List<Cbpf> selectcbpfList(Cbpf cbpf);

    List<CbpbVo> selectSwJsGoodsListout(CbpbVo cbpbVo);

    List<CbpbVo> selectSwJsGoodsAlls(CbpbVo cbpbVo);
}
