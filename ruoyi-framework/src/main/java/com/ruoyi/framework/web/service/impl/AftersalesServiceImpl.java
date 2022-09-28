package com.ruoyi.framework.web.service.impl;

import com.ruoyi.common.enums.DeleteFlagEnum;
import com.ruoyi.common.enums.DeleteFlagEnum1;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.CbibDo;
import com.ruoyi.system.domain.dto.GsAfterSalesDto;
import com.ruoyi.system.domain.vo.CbpcVo;
import com.ruoyi.system.domain.vo.GsAfterSalesVo;
import com.ruoyi.system.domain.vo.IdVo;
import com.ruoyi.system.domain.vo.SaleOrderDetailVo;
import com.ruoyi.system.mapper.GsAfterSalesMapper;
import com.ruoyi.system.mapper.GsGoodsSkuMapper;
import com.ruoyi.system.service.AftersalesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class AftersalesServiceImpl implements AftersalesService {

    @Resource
    private GsAfterSalesMapper aftersalesMapper ;


    @Resource
    private GsGoodsSkuMapper gsGoodsSkuMapper;
    @Override
    public int insertaftersales(GsAfterSalesDto gsAfterSalesDto) {
        GsAfterSalesCriteria gsAfterSalesCriteria = new GsAfterSalesCriteria();
        gsAfterSalesCriteria.createCriteria().andSaleOrderNoEqualTo(gsAfterSalesDto.getSaleOrderNo())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        if(aftersalesMapper.selectByExample(gsAfterSalesCriteria).size()>0){
            throw new RuntimeException("该订单已存在售后单");
        }
        Long userid = SecurityUtils.getUserId();
        GsAfterSales gsAfterSales = BeanCopyUtils.coypToClass(gsAfterSalesDto, GsAfterSales.class, null);
        Date date = new Date();
        gsAfterSales.setCreateTime(date);
        gsAfterSales.setUpdateTime(date);
        gsAfterSales.setCreateBy(userid);
        gsAfterSales.setUpdateBy(userid);
        gsAfterSales.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        String saleOrderNo = gsAfterSalesDto.getSaleOrderNo();
        aftersalesMapper.insertSelective(gsAfterSales);
        return 1;
    }

    @Override
    public int updateaftersales(GsAfterSalesDto gsAfterSalesDto) {

        GsAfterSalesCriteria gsAfterSalesCriteria = new GsAfterSalesCriteria();
        gsAfterSalesCriteria.createCriteria().andSaleOrderNoEqualTo(gsAfterSalesDto.getSaleOrderNo())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        List<GsAfterSales> gsAfterSaless = aftersalesMapper.selectByExample(gsAfterSalesCriteria);
        if(gsAfterSaless.size()>0 &&! gsAfterSaless.get(0).getId().equals(gsAfterSalesDto.getId())){
            throw new SwException("该订单已存在售后单");
        }
        Long userid = SecurityUtils.getUserId();
        GsAfterSales gsAfterSales = BeanCopyUtils.coypToClass(gsAfterSalesDto, GsAfterSales.class, null);
        Date date = new Date();
        gsAfterSales.setUpdateTime(date);
        gsAfterSales.setUpdateBy(userid);
        gsAfterSales.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());

        GsAfterSalesCriteria gsAfterSalesCriteria1 = new GsAfterSalesCriteria();
        gsAfterSalesCriteria1.createCriteria().andIdEqualTo(gsAfterSalesDto.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        aftersalesMapper.updateByExampleSelective(gsAfterSales,gsAfterSalesCriteria1);
        return 1;
    }

    @Override
    public int deletesales(GsAfterSalesDto gsAfterSalesDto) {
        Long userid = SecurityUtils.getUserId();
        GsAfterSales gsAfterSales = BeanCopyUtils.coypToClass(gsAfterSalesDto, GsAfterSales.class, null);
        Date date = new Date();
        gsAfterSales.setUpdateTime(date);
        gsAfterSales.setUpdateBy(userid);
        gsAfterSales.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());

        GsAfterSalesCriteria gsAfterSalesCriteria1 = new GsAfterSalesCriteria();
        gsAfterSalesCriteria1.createCriteria().andIdEqualTo(gsAfterSalesDto.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        aftersalesMapper.updateByExampleSelective(gsAfterSales,gsAfterSalesCriteria1);
        return 1;    }

    @Override
    public List<GsAfterSalesVo> aftersaleslist(GsAfterSalesVo gsAfterSalesVo) {
        return aftersalesMapper.aftersaleslist(gsAfterSalesVo);
    }

    @Override
    public GsAfterSales saleOderDetail(Integer orderId) {
        GsAfterSalesVo res = new GsAfterSalesVo();
        GsAfterSales gsAfterSales = aftersalesMapper.selectByPrimaryKey(orderId);

        return gsAfterSales;
    }

    @Override
    public List<CbibDo> test(CbibDo cbibDo) {
        Long userid = SecurityUtils.getUserId();

        Date date = new Date();
        List<CbibDo> selecttest = aftersalesMapper.selecttest(cbibDo);
        for(int i=1;i<selecttest.size();i++){
            if(selecttest.get(i).getCbib02()!=null &&selecttest.get(i).getCbib08()!=null){
                GsGoodsSkuCriteria gsGoodsSkuCriteria = new GsGoodsSkuCriteria();
                gsGoodsSkuCriteria.createCriteria().andGoodsIdEqualTo(selecttest.get(i).getCbib08())
                        .andWhIdEqualTo(selecttest.get(i).getCbib02());
                List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(gsGoodsSkuCriteria);
                if(gsGoodsSkus.size()==0){
                    GsGoodsSku gsGoodsSku = new GsGoodsSku();
                    gsGoodsSku.setCreateTime(date);
                    gsGoodsSku.setUpdateTime(date);
                    gsGoodsSku.setCreateBy(Math.toIntExact(userid));
                    gsGoodsSku.setUpdateBy(Math.toIntExact(userid));
                    gsGoodsSku.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                    gsGoodsSku.setGoodsId(selecttest.get(i).getCbib08());
                    gsGoodsSku.setWhId(selecttest.get(i).getCbib02());
                    gsGoodsSku.setQty(selecttest.get(i).getCbib15());
                    gsGoodsSkuMapper.insertSelective(gsGoodsSku);
                }
            }




        }
        return selecttest;
    }


}

