package com.ruoyi.system.service.gson.impl;

import com.ruoyi.common.constant.AuditStatusConstants;
import com.ruoyi.common.constant.TotalOrderConstants;

import com.ruoyi.common.constant.WareHouseType;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.NumberToChineseUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.gson.BaseCheckService;
import com.ruoyi.system.service.gson.OrderDistributionService;
import com.ruoyi.system.service.gson.SaleOrderService;
import com.ruoyi.system.service.gson.TaskService;
import lombok.SneakyThrows;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * ClassName SaleOrderServiceImpl
 * Description
 * Create by gfy
 * Date 2022/8/1 17:27
 */
@Service
public class SaleOrderServiceImpl implements SaleOrderService {

    @Resource
    private CboaMapper cboaMapper;

    @Resource
    private CbbaMapper cbbaMapper;

    @Resource
    private BaseCheckService baseCheckService;

    @Resource
    private OrderDistributionService orderDistributionService;

    @Resource
    private GsGoodsUseMapper gsGoodsUseMapper;

    @Resource
    private CbpbMapper cbpbMapper;

    @Resource
    private NumberGenerate numberGenerate;

    @Resource
    private CbobMapper cbobMapper;

    @Resource
    private GsOutStockAdivceMapper gsOutStockAdivceMapper;

    @Resource
    private CbcaMapper cbcaMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private CalaMapper calaMapper;

    @Resource
    private CabraaMapper cabraaMapper;

    @Resource
    private CbpfMapper cbpfMapper;

    @Resource
    private CbwaMapper cbwaMapper;


    @Resource
    private CbibMapper cbibMapper;

    @Resource
    private GsSaleShoppingMapper gsSaleShoppingMapper;

    @Resource
    private CbocMapper cbocMapper;

    @Resource
    private CbodMapper cbodMapper;

    @Resource
    private TaskService taskService;

    @Resource
    private GsGoodsSkuMapper gsGoodsSkuMapper;

    @Resource
    private CbpkMapper cbpkMapper;

    @Resource
    private GsGoodsSnMapper gsGoodsSnMapper;

    @Resource
    private CbdaMapper cbdaMapper;

    @Resource
    private CblaMapper cblaMapper;

    @Resource
    private CbplMapper cbplMapper;


    @Override
    public List<SaleOrderSkuVo> saleOrderSkuList(SaleOrderSkuDto saleOrderSkuDto) {

        List<SaleOrderSkuVo> saleOrderSkuVos = cboaMapper.saleOrderSkuList(saleOrderSkuDto);
//        //?????????????????? ?????????????????????????????????????????????????????????
//        CbbaCriteria example=new CbbaCriteria();
//        example.createCriteria()
//                .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCbba12EqualTo(TotalOrderConstants.NO)
//                .andCbba08EqualTo(saleOrderSkuDto.get())
//                .andCbba07NotLike("GBSH"+"%");
//        List<Cbba> cbbas = cbbaMapper.selectByExample(example);
//        Double sum = cbbas.stream().collect(Collectors.summingDouble(Cbba::getCbba13));
        Map<Integer, String> brandMap = baseCheckService.brandMap();
        //??????????????????????????????
        for (SaleOrderSkuVo saleOrderSkuVo : saleOrderSkuVos) {
            if (saleOrderSkuVo.getBrand() != null) {
                String bm = brandMap.get(Integer.valueOf(saleOrderSkuVo.getBrand()));
                saleOrderSkuVo.setGoodsMsg(bm + "-" + saleOrderSkuVo.getModel() + "-" + saleOrderSkuVo.getDescription());
            }

            //?????????????????????????????????
            List<GsGoodsUse> list = gsGoodsUseMapper.selectLockByTotalOrderNo(saleOrderSkuVo.getTotalOrderNo(),saleOrderSkuVo.getGoodsId());
            saleOrderSkuVo.setGoodsUses(list);
            double sum = list.stream().mapToDouble(GsGoodsUse::getLockQty).sum();
            saleOrderSkuVo.setLockQty(sum);
            if (saleOrderSkuVo.getMakeQty() != null) {
                saleOrderSkuVo.setCanUseQty(saleOrderSkuVo.getMakeQty() - sum);
            }


        }

        return saleOrderSkuVos;
    }


    /**
     * ?????????????????????
     *
     * @param totalOrderListDto
     * @return
     */
    @Override
    @SneakyThrows
    public List<TotalOrderListVo> totalOrderList(TotalOrderListDto totalOrderListDto) {


        List<TotalOrderListVo> res = cbbaMapper.totalOrderList(totalOrderListDto);
        Map<Integer, String> brandMap = baseCheckService.brandMap();
        for (TotalOrderListVo re : res) {
            if (re.getBrand() != null) {
                re.setBrand(brandMap.get(Integer.valueOf(re.getBrand())));
            }

            re.setCurrentOrderQty(re.getOrderQty() - re.getShippedQty());
            if (OrderTypeEnum.GUOJIDINGDAN.getCode().equals(re.getOrderType())) {
                re.setOrderTypeMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
            } else {
                re.setOrderTypeMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
            }

//            if(re.getOrderNo().startsWith(TotalOrderConstants.GUONEIORDER)){
//                re.setOrderType(OrderTypeEnum.GUONEIDINGDAN.getCode());
//            }else {
//                re.setOrderType(OrderTypeEnum.GUOJIDINGDAN.getCode());
//            }

        }
        CompletableFuture<List<TotalOrderListVo>> f1 =
                CompletableFuture.supplyAsync(()->{
                    List<TotalOrderListVo> outofstockregistrationVos =
                            totalOrderLists(totalOrderListDto);
                    return outofstockregistrationVos;
                });
        List<TotalOrderListVo> totalOrderListVos = f1.get();
        if(totalOrderListVos.size()>0 && res.size()>0){
            res.get(0).setTotalorderQty(totalOrderListVos.get(0).getTotalorderQty());
            res.get(0).setTotalmakeQty(totalOrderListVos.get(0).getTotalmakeQty());
            res.get(0).setTotaluseQty(totalOrderListVos.get(0).getTotaluseQty());
            res.get(0).setTotalshippedQty(totalOrderListVos.get(0).getTotalshippedQty());
            res.get(0).setTotalcurrentOrderQty(totalOrderListVos.get(0).getTotalcurrentOrderQty());
        }
        return res;

    }

    public List<TotalOrderListVo> totalOrderLists(TotalOrderListDto totalOrderListDto) {


        List<TotalOrderListVo> res = cbbaMapper.totalOrderList(totalOrderListDto);
        double sum = res.stream().mapToDouble(TotalOrderListVo::getOrderQty).sum();
        double sum1 = res.stream().mapToDouble(TotalOrderListVo::getMakeQty).sum();
        double sum2 = res.stream().mapToDouble(TotalOrderListVo::getUseQty).sum();
        double sum3 = res.stream().mapToDouble(TotalOrderListVo::getShippedQty).sum();
        double totalcurrentorderqty=sum-sum3;
        if(res.size()>0){
            res.get(0).setTotalorderQty(sum);
            res.get(0).setTotalmakeQty(sum1);
            res.get(0).setTotaluseQty(sum2);
            res.get(0).setTotalshippedQty(sum3);
            res.get(0).setTotalcurrentOrderQty(totalcurrentorderqty);
        }

        return res;

    }

    @Transactional
    @Override
    public Cbba addTotalOrder(TotalOrderAddDto totalOrderAddDto) {
        Cbba cbba = baseCheckService.checkTotalExist(totalOrderAddDto.getGoodsId(), totalOrderAddDto.getOrderNo());
        if (cbba != null) {
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(totalOrderAddDto.getGoodsId());
            throw new SwException("?????????????????????????????????????????????????????????,????????????" + totalOrderAddDto.getOrderNo() + "?????????" + cbpb.getCbpb12());
        }
        //??????????????????
        baseCheckService.checkGoods(totalOrderAddDto.getGoodsId(), null);


        //?????????????????????
        cbba = new Cbba();
        Date date = new Date();
        cbba.setCbba02(date);
        cbba.setCbba03(totalOrderAddDto.getUserId());
        cbba.setCbba04(date);
        cbba.setCbba05(totalOrderAddDto.getUserId());
        cbba.setCbba06(DeleteFlagEnum.NOT_DELETE.getCode());
        cbba.setCbba07(totalOrderAddDto.getOrderNo());
        cbba.setCbba08(totalOrderAddDto.getGoodsId());
        cbba.setCbba09(totalOrderAddDto.getQty());
        if (totalOrderAddDto.getOrderNo().contains(TotalOrderConstants.GUONEIORDER)) {
            cbba.setCbba10(OrderTypeEnum.GUONEIDINGDAN.getCode());
        } else {
            cbba.setCbba10(OrderTypeEnum.GUOJIDINGDAN.getCode());

        }

        cbba.setCbba11(0.0);
        cbba.setCbba12(TotalOrderConstants.NO);
        cbba.setCbba13(0.0);
        cbba.setCbba14(0.0);
        cbba.setCbba15(totalOrderAddDto.getPriority());

        OrderDistributionDo send = new OrderDistributionDo();
        send.setCbba(cbba);
        send.setGoodsId(totalOrderAddDto.getGoodsId());
        send.setNum(totalOrderAddDto.getQty());

        send.setPriority(totalOrderAddDto.getPriority());
        send.setType(TotalOrderOperateEnum.MAKEORDER.getCode());

        //?????????????????????????????????
        List<Cbba> cbbas = cbbaMapper.selectByGoodsId(totalOrderAddDto.getGoodsId());
//        if(cbbas.size()>0){
            cbba = orderDistributionService.reassign(send);

//        }

        cbbaMapper.insert(cbba);
        return cbba;


    }
    @Transactional
    @Override
    public Cbba addTotalOrderPl(TotalOrderAddDto totalOrderAddDto) {
        Cbba cbba = baseCheckService.checkTotalExist(totalOrderAddDto.getGoodsId(), totalOrderAddDto.getOrderNo());
        if (cbba != null) {

            //???????????????
            Double nqty=0.0;
//            if(totalOrderAddDto.getQty()<0.0){
//
//                //????????????
//                nqty=  cbba.getCbba09()-totalOrderAddDto.getQty();
//
//            }else {
                nqty=  cbba.getCbba09()+totalOrderAddDto.getQty();
//            }
            totalOrderAddDto.setQty(nqty);
            totalOrderAddDto.setOrderNo(cbba.getCbba07());
            totalOrderAddDto.setId(cbba.getCbba01());

            mdfTotalOrder(totalOrderAddDto);
            return cbba;

         }



        //?????????????????????
        cbba = new Cbba();
        Date date = new Date();
        cbba.setCbba02(date);
        cbba.setCbba03(totalOrderAddDto.getUserId());
        cbba.setCbba04(date);
        cbba.setCbba05(totalOrderAddDto.getUserId());
        cbba.setCbba06(DeleteFlagEnum.NOT_DELETE.getCode());
        cbba.setCbba07(totalOrderAddDto.getOrderNo());
        cbba.setCbba08(totalOrderAddDto.getGoodsId());
        cbba.setCbba09(totalOrderAddDto.getQty());
        if (totalOrderAddDto.getOrderNo().contains(TotalOrderConstants.GUONEIORDER)) {
            cbba.setCbba10(OrderTypeEnum.GUONEIDINGDAN.getCode());
        } else {
            cbba.setCbba10(OrderTypeEnum.GUOJIDINGDAN.getCode());

        }

        cbba.setCbba11(0.0);
        cbba.setCbba12(TotalOrderConstants.NO);
        cbba.setCbba13(0.0);
        cbba.setCbba14(0.0);
        cbba.setCbba15(totalOrderAddDto.getPriority());

        OrderDistributionDo send = new OrderDistributionDo();
        send.setCbba(cbba);
        send.setGoodsId(totalOrderAddDto.getGoodsId());
        send.setNum(totalOrderAddDto.getQty());

        send.setPriority(totalOrderAddDto.getPriority());
        send.setType(TotalOrderOperateEnum.MAKEORDER.getCode());

        //?????????????????????????????????
//        List<Cbba> cbbas = cbbaMapper.selectByGoodsId(totalOrderAddDto.getGoodsId());
//        if(cbbas.size()>0){
            cbba = orderDistributionService.reassign(send);

//        }

        cbbaMapper.insert(cbba);
        return cbba;


    }
    @Transactional
    @Override
    public Cbba mdfTotalOrder(TotalOrderAddDto totalOrderAddDto) {
        Cbba cbba = cbbaMapper.selectByPrimaryKeyForUpdate(totalOrderAddDto.getId());

        if (cbba == null || DeleteFlagEnum.DELETE.getCode().equals( cbba.getCbba06())) {
            throw new SwException("??????????????????????????????");
        }
        OrderDistributionDo send = new OrderDistributionDo();
        send.setCbba(cbba);
        send.setGoodsId(cbba.getCbba08());
        //?????????????????????
        if (DeleteFlagEnum.DELETE.getCode().equals(totalOrderAddDto.getDelete())) {
            //??????
            //?????????????????? ?????????????????????
            //???????????????????????????????????????????????????0
            Double useNum = 0.0;
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByTotalOrderNo(cbba.getCbba08(),cbba.getCbba07());
            useNum = gsGoodsUses.stream().collect(Collectors.summingDouble(GsGoodsUse::getLockQty));


//            if (gsGoodsUses.size() > 0) {
//                useNum = gsGoodsUses.get(0).getLockQty();
//            }

            if (cbba.getCbba11() != 0.0) {
                throw new SwException("??????????????????????????????????????????");
            }

            if (useNum != 0.0) {
                throw new SwException("??????????????????????????????????????????");
            }
            Double cbba09 = cbba.getCbba09();
            cbba.setCbba09(0.0);
            if (cbba.getCbba13() != 0.0) {
                send.setNum(0.0);
                send.setOldNum(cbba.getCbba09());

                send.setPriority(cbba.getCbba15());
                send.setType(TotalOrderOperateEnum.DELETE.getCode());
                cbba = orderDistributionService.reassign(send);

            }
            cbba.setCbba09(cbba09);
            cbba.setCbba06(DeleteFlagEnum.DELETE.getCode());
            cbbaMapper.updateByPrimaryKey(cbba);

        } else {
            //????????????,???????????????
            //?????????????????????
            Integer oldgoodsId = cbba.getCbba08();
            if (oldgoodsId.equals(totalOrderAddDto.getGoodsId())) {


                Long oldPoririty = cbba.getCbba15();

                Double oldQty = cbba.getCbba09();
                cbba.setCbba15(totalOrderAddDto.getPriority());
                cbba.setCbba09(totalOrderAddDto.getQty());
                //???????????????
                if (!oldPoririty.equals(totalOrderAddDto.getPriority())) {
                    send.setNum(totalOrderAddDto.getQty());
                    send.setOldNum(oldQty);
                    send.setOldPriority(oldPoririty);
                    send.setPriority(cbba.getCbba15());
                    send.setType(TotalOrderOperateEnum.MDFPRIORITY.getCode());
                    cbba = orderDistributionService.reassign(send);
                } else if (!oldQty.equals(totalOrderAddDto.getQty())) {

                    send.setNum(totalOrderAddDto.getQty());
                    send.setOldNum(oldQty);

                    send.setPriority(cbba.getCbba15());
                    send.setType(TotalOrderOperateEnum.MDFQTY.getCode());
                    cbba = orderDistributionService.reassign(send);
                }

                cbba.setCbba08(totalOrderAddDto.getGoodsId());
                cbba.setCbba07(totalOrderAddDto.getOrderNo());
                cbba.setCbba05(totalOrderAddDto.getUserId());
                cbba.setCbba04(new Date());

                cbbaMapper.updateByPrimaryKey(cbba);
            }
//            } else {
//                Double useNum = 0.0;
//                baseCheckService.checkGoods(totalOrderAddDto.getGoodsId(), null);
//                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByTotalOrderNo(cbba.getCbba08(),cbba.getCbba07());
//                useNum = gsGoodsUses.stream().collect(Collectors.summingDouble(GsGoodsUse::getLockQty));
//
//
////                if (gsGoodsUses.size() > 0) {
////                    useNum = gsGoodsUses.get(0).getLockQty();
////                }
//
//                if (cbba.getCbba11() != 0.0) {
//                    throw new SwException("????????????????????????????????????????????????");
//                }
//
//                if (useNum != 0.0) {
//                    throw new SwException("????????????????????????????????????????????????");
//                }
//                //?????????????????????
//                Double cbba09 = cbba.getCbba09();
//                cbba.setCbba09(0.0);
//                if (cbba.getCbba13() != 0.0) {
//                    send.setNum(0.0);
//                    send.setOldNum(cbba.getCbba09());
//
//                    send.setPriority(cbba.getCbba15());
//                    send.setType(TotalOrderOperateEnum.MDFQTY.getCode());
//                    Cbba cbbadel = orderDistributionService.reassign(send);
//
//                }
//                cbba.setCbba09(totalOrderAddDto.getQty());
//                cbba.setCbba08(totalOrderAddDto.getGoodsId());
//
//                OrderDistributionDo sendAdd = new OrderDistributionDo();
//                sendAdd.setCbba(cbba);
//                sendAdd.setGoodsId(totalOrderAddDto.getGoodsId());
//                sendAdd.setNum(totalOrderAddDto.getQty());
//
//                sendAdd.setPriority(totalOrderAddDto.getPriority());
//                sendAdd.setType(TotalOrderOperateEnum.MAKEORDER.getCode());
//                cbba = orderDistributionService.reassign(sendAdd);
//                //?????????????????????
//                cbba.setCbba07(totalOrderAddDto.getOrderNo());
//                cbba.setCbba05(totalOrderAddDto.getUserId());
//                cbba.setCbba04(new Date());
//                cbbaMapper.updateByPrimaryKey(cbba);
//
//            }

        }
        return cbba;

    }




    @Override
    public List<SaleOrderListVo> saleOrderList(SaleOrderListDto saleOrderListDto) {

        List<SaleOrderListVo> saleOrderListVos = cboaMapper.saleOrderList(saleOrderListDto);
        for (SaleOrderListVo saleOrderListVo : saleOrderListVos) {
            if (saleOrderListVo.getSettleCurrent().equals(6)) {
                saleOrderListVo.setSettleCurrentMsg("CNY");
            } else {
                saleOrderListVo.setSettleCurrentMsg("USD");
            }

            if (saleOrderListVo.getOrderClass().equals(OrderTypeEnum.GUOJIDINGDAN.getCode())) {
                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
            } else {
                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
            }

            if (saleOrderListVo.getOrderType().equals(10)) {
                saleOrderListVo.setOrderTypeMsg("????????????");
            } else {
                saleOrderListVo.setOrderTypeMsg("?????????");
            }

            saleOrderListVo.setStatusMsg(SaleOrderStatusEnums.findByKey(saleOrderListVo.getStatus()).getMsg());

        }
        return saleOrderListVos;
    }

//    @Transactional
//    @Override
//    public String importTotalOrder(List<TotalOrderExcelDto> list, Long userId) {
//
//
//        TotalOrderAddDto totalOrderAddDto = null;
//        for (TotalOrderExcelDto totalOrderExcelDto : list) {
//
//            if (totalOrderExcelDto.getPriority()==null) {
//                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//            if (StringUtils.isBlank(totalOrderExcelDto.getOrderNo())) {
//                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//            if (totalOrderExcelDto.getOrderQty() == null) {
//                throw new SwException("????????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//
//            if (StringUtils.isBlank(totalOrderExcelDto.getModel())) {
//                throw new SwException("????????????????????????");
//            }
//
//            totalOrderAddDto = new TotalOrderAddDto();
//            totalOrderAddDto.setUserId(userId.intValue());
//            totalOrderAddDto.setDelete(DeleteFlagEnum.NOT_DELETE.getCode());
//            //????????????
//            CbpbCriteria example = new CbpbCriteria();
//            example.createCriteria()
//                    .andCbpb06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                    .andCbpb12EqualTo(totalOrderExcelDto.getModel());
//            List<Cbpb> cbpbs = cbpbMapper.selectByExample(example);
//            if (cbpbs.size() == 0) {
//                throw new SwException("??????????????????????????????:" + totalOrderExcelDto.getModel());
//            }
//            totalOrderAddDto.setGoodsId(cbpbs.get(0).getCbpb01());
//            totalOrderAddDto.setOrderNo(totalOrderExcelDto.getOrderNo());
//            totalOrderAddDto.setPriority(totalOrderExcelDto.getPriority());
//            totalOrderAddDto.setQty(totalOrderExcelDto.getOrderQty());
//            addTotalOrder(totalOrderAddDto);
//
//        }
//
//        return "????????????";
//
//
//    }
    @Transactional
    @Override
    public String importTotalOrder(List<TotalOrderExcelDto> list, Long userId) {

        StringBuilder sb=new StringBuilder();
        TotalOrderAddDto totalOrderAddDto = null;
        List<String> errors=new ArrayList<>();
        Map<String,Integer> goodsMap=new HashMap<>();
        List<TotalOrderExcelDto> list2=new ArrayList<>();
        for (TotalOrderExcelDto totalOrderExcelDto : list) {

            if (totalOrderExcelDto.getPriority()==null) {
                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
            }

            if (StringUtils.isBlank(totalOrderExcelDto.getOrderNo())) {
                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
            }

            if (totalOrderExcelDto.getOrderQty() == null) {
                throw new SwException("????????????????????????,?????????" + totalOrderExcelDto.getModel());
            }


            if (StringUtils.isBlank(totalOrderExcelDto.getModel())) {
                throw new SwException("????????????????????????");
            }

            //????????????
            CbpbCriteria example = new CbpbCriteria();
            example.createCriteria()
                    .andCbpb06EqualTo(DeleteFlagEnum1.NOT_DELETE.getCode())
                    .andCbpb12EqualTo(totalOrderExcelDto.getModel());
            List<Cbpb> cbpbs = cbpbMapper.selectByExample(example);
            if (cbpbs.size() == 0) {
                errors.add(totalOrderExcelDto.getModel());
//                throw new SwException("??????????????????????????????:" + totalOrderExcelDto.getModel());
            }else {
                goodsMap.put(totalOrderExcelDto.getModel(),cbpbs.get(0).getCbpb01());
                list2.add(totalOrderExcelDto);
            }


        }
        if(errors.size()>0){

            throw new SwException("??????????????????????????????:" + errors.toString());

        }

            for (TotalOrderExcelDto totalOrderExcelDto : list) {

//            if (totalOrderExcelDto.getPriority()==null) {
//                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//            if (StringUtils.isBlank(totalOrderExcelDto.getOrderNo())) {
//                throw new SwException("?????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//            if (totalOrderExcelDto.getOrderQty() == null) {
//                throw new SwException("????????????????????????,?????????" + totalOrderExcelDto.getModel());
//            }
//
//
//            if (StringUtils.isBlank(totalOrderExcelDto.getModel())) {
//                throw new SwException("????????????????????????");
//            }

            totalOrderAddDto = new TotalOrderAddDto();
            totalOrderAddDto.setUserId(userId.intValue());
            totalOrderAddDto.setDelete(DeleteFlagEnum.NOT_DELETE.getCode());

            totalOrderAddDto.setGoodsId(goodsMap.get(totalOrderExcelDto.getModel()));
            totalOrderAddDto.setOrderNo(totalOrderExcelDto.getOrderNo());
            totalOrderAddDto.setPriority(totalOrderExcelDto.getPriority());
            totalOrderAddDto.setQty(totalOrderExcelDto.getOrderQty());
            addTotalOrderPl(totalOrderAddDto);

        }

        return "????????????????????????????????????"+list.size()+"???";



    }
//    /**
//     * ??????????????????
//     *
//     * @param saleOrderAddDto
//     */
//    @Transactional
//    @Override
//    public void addSaleOrder(SaleOrderAddDto saleOrderAddDto) {
//
//        //????????????????????????
//        Cbca cbca = baseCheckService.checkCustomer(saleOrderAddDto.getCustomerId());
//
//        //????????????????????????
//        List<SaleOrderGoodsDto> goods = saleOrderAddDto.getGoods();
//        if (goods.size() == 0) {
//            throw new SwException("???????????????????????????");
//        }
//
//        //
//        //????????????????????????
//        Cboa cboa = new Cboa();
//        Date date = new Date();
//        cboa.setCboa02(date);
//        cboa.setCboa03(saleOrderAddDto.getUserId());
//        cboa.setCboa04(date);
//        cboa.setCboa05(saleOrderAddDto.getUserId());
//        cboa.setCboa06(DeleteFlagEnum.NOT_DELETE.getCode());
//        NumberDo numberDo = new NumberDo();
//        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
//        cboa.setCboa07(numberGenerate.createOrderNo(numberDo).getOrderNo());
//        cboa.setCboa08(date);
//        cboa.setCboa09(saleOrderAddDto.getCustomerId());
//        cboa.setCboa10(saleOrderAddDto.getSaleUserId());
//        cboa.setCboa11(AuditStatusConstants.SO_COMMIT);
//        cboa.setCboa13(date);
//        cboa.setCboa16(saleOrderAddDto.getCurrency());
//        cboa.setCboa17(saleOrderAddDto.getReceiveName());
//        cboa.setCboa18(saleOrderAddDto.getAddress());
//        cboa.setCboa19(saleOrderAddDto.getReceivePhone());
//        cboa.setCboa22(saleOrderAddDto.getInvoiceType());
//        cboa.setCboa24(saleOrderAddDto.getOrderType());
//        cboa.setCboa25(saleOrderAddDto.getCustomerNo());
//        cboa.setCboa27(saleOrderAddDto.getOrderClass());
//        cboa.setCboa20(saleOrderAddDto.getFcNumber());
//        cboa.setCboa21(saleOrderAddDto.getRemark());
//        cboaMapper.insertWithId(cboa);
//        GsWorkInstanceDo gsWorkInstanceDo = new GsWorkInstanceDo();
//        gsWorkInstanceDo.setOrderType((byte) 1);
//        gsWorkInstanceDo.setOrderClose((byte) 2);
//        gsWorkInstanceDo.setOrderStatus((byte) 1);
//        gsWorkInstanceDo.setOrderNo(cboa.getCboa07());
//        taskService.addGsWorkInstance(gsWorkInstanceDo);
//        Cbob cbob = null;
//        //???????????????????????????
//        for (SaleOrderGoodsDto good : goods) {
//            if(good.getGoodsId()!=null){
//                good.setId(good.getGoodsId());
//            }else {
//                good.setGoodsId(good.getId());
//            }
//
//            if(good.getQty()==null || good.getQty()==0){
//                throw new SwException("?????????????????????");
//            }
//
//            if(good.getTotalPrice() ==null ){
//                throw new SwException("?????????????????????");
//            }
//
//            //????????????????????????
////            GoodsCheckStockVo goodsCheckStockVo=baseCheckService.checkGoodsStock(good.getGoodsId(),saleOrderAddDto.getOrderClass());
//            cbob = new Cbob();
//            cbob.setCbob02(good.getNumber());
//            cbob.setCbob03(date);
//            cbob.setCbob04(saleOrderAddDto.getUserId());
//            cbob.setCbob05(date);
//            cbob.setCbob06(saleOrderAddDto.getUserId());
//            cbob.setCbob07(DeleteFlagEnum.NOT_DELETE.getCode());
//            cbob.setCbob08(good.getGoodsId());
//            cbob.setCbob09(good.getQty());
//            cbob.setCbob10(0.0);
//            cbob.setCbob11(good.getCurrentPrice());
//            cbob.setCbob12(good.getTotalPrice());
//            cbob.setCbob13(good.getRemark());
//            cbob.setCboa01(cboa.getCboa01());
//            cbob.setCbob14(good.getNormalPrice());
//            cbobMapper.insert(cbob);
//            //?????????????????????????????????
//            GoodsOperationDo goodsOperationDo = new GoodsOperationDo();
//            goodsOperationDo.setGoodsId(good.getGoodsId());
//            goodsOperationDo.setNum(good.getQty());
//            goodsOperationDo.setOrderNo(cboa.getCboa07());
//            goodsOperationDo.setOrderNum(good.getQty());
//            goodsOperationDo.setOrderType(saleOrderAddDto.getOrderClass());
//            goodsOperationDo.setUserId(saleOrderAddDto.getUserId());
//
//            SaleOrderMakeDo saleOrderMakeDo = orderDistributionService.saleOrderMake(goodsOperationDo);
//            //??????????????????
//            if (saleOrderMakeDo.getPrompt() == 1 && saleOrderMakeDo.getPrompt() != null) {
//                GsOutStockAdivce advice = null;
//                for (OutSuggestionsDo outSuggestionsDo : saleOrderMakeDo.getList()) {
//                    if(outSuggestionsDo.getQty()!=0){
//                        advice = new GsOutStockAdivce();
//                        advice.setCreateBy(saleOrderAddDto.getUserId());
//                        advice.setCreateTime(date);
//                        advice.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
//                        advice.setGoodsId(outSuggestionsDo.getGoodsId());
//                        advice.setQty(outSuggestionsDo.getQty());
//                        advice.setSaleOrderNo(cboa.getCboa07());
//                        if(WareHouseType.CDCWHID.equals(outSuggestionsDo.getWhId()) || WareHouseType.GQWWHID.equals(outSuggestionsDo.getWhId())){
//                            advice.setStatus(new Byte("3"));
//                        }else {
//                            advice.setStatus(new Byte("2"));
//                        }
//
//                        advice.setUpdateBy(saleOrderAddDto.getUserId());
//                        advice.setUpdateTime(date);
//                        advice.setWhId(outSuggestionsDo.getWhId());
//                        gsOutStockAdivceMapper.insert(advice);
//                    }
//
//
//
//                }
//            }
//            //  ?????????????????????????????????????????????????????????
//            if (saleOrderAddDto.getShoppongIds().size()>=0) {
//                List<Integer> shoppongIds = saleOrderAddDto.getShoppongIds();
//                for (Integer shoppongId : shoppongIds) {
//
//                    int i = gsSaleShoppingMapper.deleteByPrimaryKey(shoppongId);
//                }
//
////                GsSaleShoppingCriteria shex = new GsSaleShoppingCriteria();
////                shex.createCriteria()
////                        .andUserIdEqualTo(saleOrderAddDto.getUserId())
////                        .andGoodsIdEqualTo(good.getGoodsId());
////                int i = gsSaleShoppingMapper.deleteByExample(shex);
//            }
//
//        }
//
//        return;
//
//    }


    /**
     * ??????????????????
     *
     * @param saleOrderAddDto
     */
    @Transactional
    @Override
    public void addSaleOrder(SaleOrderAddDto saleOrderAddDto) {

        //????????????????????????
        Cbca cbca = baseCheckService.checkCustomer(saleOrderAddDto.getCustomerId());

        //????????????????????????
        List<SaleOrderGoodsDto> goods = saleOrderAddDto.getGoods();
        if (goods.size() == 0) {
            throw new SwException("???????????????????????????");
        }

        //
        //????????????????????????
        Cboa cboa = new Cboa();
        Date date = new Date();
        cboa.setCboa02(date);
        cboa.setCboa03(saleOrderAddDto.getUserId());
        cboa.setCboa04(date);
        cboa.setCboa05(saleOrderAddDto.getUserId());
        cboa.setCboa06(DeleteFlagEnum.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
        cboa.setCboa07(numberGenerate.createOrderNo(numberDo).getOrderNo());
        cboa.setCboa08(date);
        cboa.setCboa09(saleOrderAddDto.getCustomerId());
        cboa.setCboa10(saleOrderAddDto.getSaleUserId());
        cboa.setCboa11(AuditStatusConstants.SO_NO_COMMIT);
        cboa.setCboa13(date);
        cboa.setCboa16(saleOrderAddDto.getCurrency());
        cboa.setCboa17(saleOrderAddDto.getReceiveName());
        cboa.setCboa18(saleOrderAddDto.getAddress());
        cboa.setCboa19(saleOrderAddDto.getReceivePhone());
        cboa.setCboa22(saleOrderAddDto.getInvoiceType());
        cboa.setCboa24(saleOrderAddDto.getOrderType());
        cboa.setCboa25(saleOrderAddDto.getCustomerNo());
        cboa.setCboa27(saleOrderAddDto.getOrderClass());
        cboa.setCboa20(saleOrderAddDto.getFcNumber());
        cboa.setCboa21(saleOrderAddDto.getRemark());
        cboaMapper.insertWithId(cboa);
        GsWorkInstanceDo gsWorkInstanceDo = new GsWorkInstanceDo();
        gsWorkInstanceDo.setOrderType((byte) 1);
        gsWorkInstanceDo.setOrderClose((byte) 2);
        gsWorkInstanceDo.setOrderStatus((byte) 1);
        gsWorkInstanceDo.setOrderNo(cboa.getCboa07());
        taskService.addGsWorkInstance(gsWorkInstanceDo);
        Cbob cbob = null;
        //???????????????????????????
        for (SaleOrderGoodsDto good : goods) {
            if(good.getGoodsId()!=null){
                good.setId(good.getGoodsId());
            }else {
                good.setGoodsId(good.getId());
            }

            if(good.getQty()==null || good.getQty()==0){
                throw new SwException("?????????????????????");
            }

            if(good.getCurrentPrice() ==null ){
                throw new SwException("?????????????????????");
            }
            if(good.getTotalPrice() ==null ){
                throw new SwException("?????????????????????");
            }

            //????????????????????????
//            GoodsCheckStockVo goodsCheckStockVo=baseCheckService.checkGoodsStock(good.getGoodsId(),saleOrderAddDto.getOrderClass());
            cbob = new Cbob();
            cbob.setCbob02(good.getNumber());
            cbob.setCbob03(date);
            cbob.setCbob04(saleOrderAddDto.getUserId());
            cbob.setCbob05(date);
            cbob.setCbob06(saleOrderAddDto.getUserId());
            cbob.setCbob07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbob.setCbob08(good.getGoodsId());
            cbob.setCbob09(good.getQty());
            cbob.setCbob10(0.0);
            cbob.setCbob11(good.getCurrentPrice());
            cbob.setCbob12(good.getTotalPrice());
            cbob.setCbob13(good.getRemark());
            cbob.setCboa01(cboa.getCboa01());
            cbob.setCbob14(good.getNormalPrice());
            cbobMapper.insert(cbob);
            //?????????????????????????????????
//            GoodsOperationDo goodsOperationDo = new GoodsOperationDo();
//            goodsOperationDo.setGoodsId(good.getGoodsId());
//            goodsOperationDo.setNum(good.getQty());
//            goodsOperationDo.setOrderNo(cboa.getCboa07());
//            goodsOperationDo.setOrderNum(good.getQty());
//            goodsOperationDo.setOrderType(saleOrderAddDto.getOrderClass());
//            goodsOperationDo.setUserId(saleOrderAddDto.getUserId());
//
//            SaleOrderMakeDo saleOrderMakeDo = orderDistributionService.saleOrderMake(goodsOperationDo);
//            //??????????????????
//            if (saleOrderMakeDo.getPrompt() == 1 && saleOrderMakeDo.getPrompt() != null) {
//                GsOutStockAdivce advice = null;
//                for (OutSuggestionsDo outSuggestionsDo : saleOrderMakeDo.getList()) {
//                    if(outSuggestionsDo.getQty()!=0){
//                        advice = new GsOutStockAdivce();
//                        advice.setCreateBy(saleOrderAddDto.getUserId());
//                        advice.setCreateTime(date);
//                        advice.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
//                        advice.setGoodsId(outSuggestionsDo.getGoodsId());
//                        advice.setQty(outSuggestionsDo.getQty());
//                        advice.setSaleOrderNo(cboa.getCboa07());
//                        if(WareHouseType.CDCWHID.equals(outSuggestionsDo.getWhId()) || WareHouseType.GQWWHID.equals(outSuggestionsDo.getWhId())){
//                            advice.setStatus(new Byte("3"));
//                        }else {
//                            advice.setStatus(new Byte("2"));
//                        }
//
//                        advice.setUpdateBy(saleOrderAddDto.getUserId());
//                        advice.setUpdateTime(date);
//                        advice.setWhId(outSuggestionsDo.getWhId());
//                        gsOutStockAdivceMapper.insert(advice);
//                    }
//
//
//
//                }
//            }
            //  ?????????????????????????????????????????????????????????
            if (saleOrderAddDto.getShoppongIds().size()>=0) {
                List<Integer> shoppongIds = saleOrderAddDto.getShoppongIds();
                for (Integer shoppongId : shoppongIds) {

                    int i = gsSaleShoppingMapper.deleteByPrimaryKey(shoppongId);
                }

//                GsSaleShoppingCriteria shex = new GsSaleShoppingCriteria();
//                shex.createCriteria()
//                        .andUserIdEqualTo(saleOrderAddDto.getUserId())
//                        .andGoodsIdEqualTo(good.getGoodsId());
//                int i = gsSaleShoppingMapper.deleteByExample(shex);
            }

        }

        return;

    }

    @Override
    public SaleOrderDetailVo saleOderDetail(Integer orderId) {
        SaleOrderDetailVo res = new SaleOrderDetailVo();
        Cboa cboa = cboaMapper.selectByPrimaryKey(orderId);
        if (cboa == null || DeleteFlagEnum.DELETE.getCode().equals(cboa.getCboa06())) {
            throw new SwException("??????????????????");
        }
        res.setId(cboa.getCboa01());
        res.setAddress(cboa.getCboa18());
        res.setCustomerId(cboa.getCboa09());

        res.setCurrency(cboa.getCboa16());
        if (cboa.getCboa09() != null) {
            Cbca cbca = cbcaMapper.selectByPrimaryKey(cboa.getCboa09());
            if (cbca != null) {
                res.setCustomerName(cbca.getCbca08());
            }

//            SysUser customer = sysUserMapper.selectByPrimaryKey(cboa.getCboa09().longValue());
//            if(customer!=null){
//                res.setCustomerName(customer.getNickName());
//            }
        }

        res.setCustomerNo(cboa.getCboa25());
        Cbca cbca = cbcaMapper.selectByPrimaryKey(cboa.getCboa09());
        if (cbca != null) {
            res.setFcNumber(cboa.getCboa20());
            res.setRemark(cboa.getCboa21());
            res.setOther(cboa.getCboa21());
            res.setFpAdress(cbca.getCbca26());
            res.setFpbank(cbca.getCbca11());
            res.setFpNumber(cbca.getCbca12());
            res.setFpPhone(cbca.getCbca27());
            res.setFpReceiveAddress(cbca.getCbca25());
            res.setTaxpayerid(cbca.getCbca13());

        }
        res.setInvoiceType(cboa.getCboa22());
        SysUser createUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa03().longValue());
        if (createUser != null) {
            res.setMakeUser(createUser.getNickName());
        }

        res.setOrderClass(cboa.getCboa27());
        if (OrderTypeEnum.GUONEIDINGDAN.getCode().equals(cboa.getCboa27())) {
            res.setOrderClassMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
        } else {
            res.setOrderClassMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
        }

        res.setOrderDate(cboa.getCboa08());
        res.setOrderNo(cboa.getCboa07());
        res.setOrderType(cboa.getCboa24());
//        res.setOther();
        res.setReceiveName(cboa.getCboa17());
        res.setReceivePhone(cboa.getCboa19());
        if (cboa.getCboa10() != null) {
            SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa10().longValue());
            if (saleUser != null) {
                res.setSaleUser(saleUser.getNickName());
                res.setSaleUserId(saleUser.getUserId().intValue());

            }
        }


        //????????????
        CbobCriteria example = new CbobCriteria();
        example.createCriteria()
                .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                .andCboa01EqualTo(cboa.getCboa01());
        List<Cbob> cbobs = cbobMapper.selectByExample(example);

        Double sunPrice = 0.0;
        Double sumQty = 0.0;

        List<SaleOderDetailGoods> goods = res.getGoods();
        SaleOderDetailGoods good = null;
        CalaCriteria laexample = new CalaCriteria();
        laexample.createCriteria()
                .andCala10EqualTo("????????????");
        List<Cala> calas = calaMapper.selectByExample(laexample);
//        Map<Integer, String> brandMap = new HashMap<>();
//        for (Cala cala : calas) {
//            brandMap.put(cala.getCala01(), cala.getCala08());
//        }

        Map<Integer, String> brandMap = baseCheckService.brandMap();
        for (Cbob cbob : cbobs) {
            good = new SaleOderDetailGoods();

            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
            good.setId(cbob.getCbob08());

            if (cbpb != null) {
                good.setBrand(brandMap.get(cbpb.getCbpb10()));
                good.setDescription(cbpb.getCbpb08());
                good.setModel(cbpb.getCbpb12());

            }

            good.setCurrentPrice(cbob.getCbob11());
            good.setNormalPrice(cbob.getCbob14());
            good.setRemark(cbob.getCbob13());
            good.setQty(cbob.getCbob09());
            good.setTotalPrice(cbob.getCbob12());
            good.setTotalOrderNo(cbob.getCbob18());
            good.setConfirmQty(cbob.getConfirmQty());
            GoodsPriceAndSkuDto goodsPriceAndSkuDto=new GoodsPriceAndSkuDto();
            goodsPriceAndSkuDto.setGoodsId(cbob.getCbob08());
            goodsPriceAndSkuDto.setCbobId(cbob.getCbob01());
            goodsPriceAndSkuDto.setCustomerId(cboa.getCboa09());
            goodsPriceAndSkuDto.setCurrency(cboa.getCboa16());
            GoodsPriceAndSkuVo goodsPriceAndSkuVo = goodsPriceAndSku(goodsPriceAndSkuDto);
            if(goodsPriceAndSkuVo!=null){
                good.setCanUseSku(goodsPriceAndSkuVo.getCanUseSku());
                good.setNormalPrice(goodsPriceAndSkuVo.getNormalPrice());
            }

            sunPrice = sunPrice + cbob.getCbob12();
            sumQty = sumQty + cbob.getCbob09();
            CheckSkuDo checkDo = new CheckSkuDo();
            checkDo.setOrderClass(cboa.getCboa27());
            checkDo.setGoodsId(good.getGoodsId());
//            QtyMsgVo qtyMsgVo = orderDistributionService.checkSku(checkDo);
//            good.setCanUseSku(qtyMsgVo.getCanUseNum());
//            GsGoodsUseCriteria guex=new GsGoodsUseCriteria();
//            guex.createCriteria()
//                    .andGoodsIdEqualTo(good.getGoodsId())
//                    .andOrderNoEqualTo(cboa.getCboa07());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
            good.setConfirmQty(cbob.getConfirmQty());
            res.getGoods().add(good);


        }
        //??????????????????

        res.setSumPrice(sunPrice);
        res.setSumQty(sumQty);
        res.setCapPrice(NumberToChineseUtil.moneyToChinese(res.getSumPrice()));

        //????????????
        CabraaCriteria raaexample = new CabraaCriteria();
        raaexample.createCriteria()
                .andCabraa14EqualTo(cboa.getCboa07());
        List<Cabraa> cabraas = cabraaMapper.selectByExample(raaexample);

        SaleOrderAudit saleOrderAudit = null;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Cabraa cabraa : cabraas) {
            saleOrderAudit = new SaleOrderAudit();
            saleOrderAudit.setAuditResult(cabraa.getCabraa18());
            String createTime = sd.format(cabraa.getCabraa15());
            SysUser auditUser = sysUserMapper.selectByPrimaryKey(cabraa.getCabraa04().longValue());
            String audit = "";
            if (auditUser != null) {
                audit = auditUser.getNickName() == null ? "" : auditUser.getNickName();
                res.setAuditUser(auditUser.getNickName());
            }

            saleOrderAudit.setDescription(createTime + " ??? " + audit + " ??????");
            saleOrderAudit.setId(cabraa.getCabraa01());
            saleOrderAudit.setRole(res.getAuditUser());
            res.getAudits().add(saleOrderAudit);
        }


        BaseSelectDto baseDto=new BaseSelectDto();
        baseDto.setOrderId(cboa.getCboa01());
        List<BaseSelectVo> baseSelectVos = orderChangeGoodsSelect(baseDto);
        res.setGoodsSelects(baseSelectVos);
        res.setFpNumber(cboa.getCboa21());
        return res;
    }

    /**
     * ??????????????????
     *
     * @param saleOrderAddDto
     */
    @Transactional
    @Override
    public void mdfSaleOrder(SaleOrderAddDto saleOrderAddDto) {
        //???????????????????????????????????????
        Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderAddDto.getId());
        if (cboa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())) {
            throw new SwException("?????????????????????");
        }
        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboa.getCboa11())) {
            throw new SwException("??????????????????????????????????????????");
        }

        Date date = new Date();
        if (!cboa.getCboa10().equals(saleOrderAddDto.getUserId())) {
            throw new SwException("?????????????????????????????????");
        }

        cboa.setCboa04(date);
        cboa.setCboa05(saleOrderAddDto.getUserId());
        cboa.setCboa06(DeleteFlagEnum.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
        cboa.setCboa07(cboa.getCboa07());
        cboa.setCboa08(date);
        cboa.setCboa09(saleOrderAddDto.getCustomerId());
        cboa.setCboa10(saleOrderAddDto.getSaleUserId());
        cboa.setCboa11(AuditStatusConstants.SO_NO_COMMIT);
        cboa.setCboa13(date);
        cboa.setCboa16(saleOrderAddDto.getCurrency());
        cboa.setCboa17(saleOrderAddDto.getReceiveName());
        cboa.setCboa18(saleOrderAddDto.getAddress());
        cboa.setCboa19(saleOrderAddDto.getReceivePhone());
        cboa.setCboa22(saleOrderAddDto.getInvoiceType());
        cboa.setCboa24(saleOrderAddDto.getOrderType());
        cboa.setCboa27(saleOrderAddDto.getOrderClass());
        cboa.setCboa25(saleOrderAddDto.getCustomerNo());
        cboa.setCboa20(saleOrderAddDto.getFcNumber());
        cboa.setCboa21(saleOrderAddDto.getRemark());
        int insert = cboaMapper.updateByPrimaryKey(cboa);

        //???????????????????????????
        CbobCriteria example = new CbobCriteria();
        example.createCriteria()
                .andCboa01EqualTo(cboa.getCboa01());
        int i = cbobMapper.deleteByExample(example);


        //????????????
        List<SaleOrderGoodsDto> goods = saleOrderAddDto.getGoods();
        Cbob cbob = null;
        for (SaleOrderGoodsDto good : goods) {
            if (OrderTypeEnum.GUOJIDINGDAN.getCode().equals(saleOrderAddDto.getOrderClass())) {
                if (StringUtils.isBlank(good.getTotalOrderNo())) {
                    throw new SwException("???????????????????????????????????????");
                }
            }
            if(good.getQty()==null || good.getQty()==0){
                throw new SwException("?????????????????????");
            }

            if(good.getCurrentPrice() ==null ){
                throw new SwException("?????????????????????");
            }
            if(good.getTotalPrice() ==null ){
                throw new SwException("?????????????????????");
            }

            //????????????????????????
//            GoodsCheckStockVo goodsCheckStockVo=baseCheckService.checkGoodsStock(good.getGoodsId(),saleOrderAddDto.getOrderClass());
            cbob = new Cbob();
            cbob.setCbob02(good.getNumber());
            cbob.setCbob03(date);
            cbob.setCbob04(saleOrderAddDto.getUserId());
            cbob.setCbob05(date);
            cbob.setCbob06(saleOrderAddDto.getUserId());
            cbob.setCbob07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbob.setCbob08(good.getGoodsId());
            cbob.setCbob09(good.getQty());
            cbob.setCbob10(0.0);
            cbob.setCbob11(good.getCurrentPrice());
            cbob.setCbob12(good.getTotalPrice());
            cbob.setCbob13(good.getRemark());
            cbob.setCboa01(cboa.getCboa01());
            cbob.setCbob14(good.getNormalPrice());
            if (StringUtils.isNotBlank(good.getTotalOrderNo())) {
                CbbaCriteria baex = new CbbaCriteria();
                baex.createCriteria()
                        .andCbba07EqualTo(good.getTotalOrderNo())
                        .andCbba08EqualTo(good.getGoodsId())
                        .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                List<Cbba> cbbas = cbbaMapper.selectByExample(baex);
                if (cbbas.size() == 0) {
                    throw new SwException("????????????????????????????????????????????????????????????????????????:" + good.getTotalOrderNo());
                }
                Cbba cbba = cbbas.get(0);
                cbob.setCbob17(cbba.getCbba01());
                cbob.setCbob18(good.getTotalOrderNo());
            }

            cbobMapper.insert(cbob);


        }


    }

    @Transactional
    @Override
    public void delSaleOrder(DelSaleOrderDto delSaleOrderDto) {
        //?????????????????????
        Cboa cboa = cboaMapper.selectByPrimaryKey(delSaleOrderDto.getOrderId());
        if (!cboa.getCboa10().equals(delSaleOrderDto.getUserId())) {
            throw new SwException("?????????????????????????????????");
        }
//        if(OrderTypeEnum.GUOJIDINGDAN.getCode().equals(cboa.getCboa27())){
//            if(cboa.getCboa11()!=1){
//                throw new SwException("??????????????????????????????????????????");
//
//            }
//
//        }else {
            if(cboa.getCboa11()!=0){
                throw new SwException("??????????????????????????????????????????");

            }

//
//        }else {
//
//        }

        cboa.setCboa01(delSaleOrderDto.getOrderId());
        cboa.setCboa06(DeleteFlagEnum.DELETE.getCode());
        cboa.setCboa04(new Date());
        cboa.setCboa05(delSaleOrderDto.getUserId());
        int i = cboaMapper.updateByPrimaryKeySelective(cboa);

    }

    /**
     * ??????  ????????????
     *
     * @param orderId
     * @param userId
     */
    @Override
    public void revokeSaleOrder(Integer orderId, Integer userId) {
        Cboa cboa = cboaMapper.selectByPrimaryKeyForUpdate(orderId);
        if (cboa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())) {
            throw new SwException("?????????????????????");
        }



        CbobCriteria obex=new CbobCriteria();
        obex.createCriteria()
                .andCboa01EqualTo(cboa.getCboa01());
        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
        Date date = new Date();

        if(cboa.getCboa27()==2){
            //????????????????????????????????????
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cboa.getCboa11())) {
                throw new SwException("??????????????????????????????????????????");
            }

            for (Cbob cbob : cbobs) {
                //???????????????????????????

                GsGoodsUseCriteria usexample = new GsGoodsUseCriteria();
                usexample.createCriteria()
                        .andGoodsIdEqualTo(cbob.getCbob08());
                usexample.setOrderByClause("wh_id desc ");
                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usexample);
                Double cbob09 = cbob.getCbob09();
                GsGoodsUse gqwGsUse=null;
                for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                    if(gsGoodsUs.getWhId().equals(WareHouseType.GQWWHID)){
                        gqwGsUse=gsGoodsUs;
                        continue;
                    }
                    if(cbob09>gsGoodsUs.getLockQty()){
                        cbob09=cbob09-gsGoodsUs.getLockQty();
                        gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());

                    }else {

                        gsGoodsUs.setLockQty(gsGoodsUs.getLockQty()-cbob09);
                        gsGoodsUs.setUpdateTime(date);
                        cbob09=0.0;
                        gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);

                    }
                    if(cbob09==0){
                        break;
                    }

                }

                if(cbob09!=0.0 & gqwGsUse!=null){
                    if(cbob09>gqwGsUse.getLockQty()){
                        throw new SwException("??????????????????????????????????????????????????????");
                    }
                    gqwGsUse.setLockQty(gqwGsUse.getLockQty()-cbob09);
                    gqwGsUse.setUpdateTime(date);
                    gsGoodsUseMapper.updateByPrimaryKey(gqwGsUse);
                    CbbaCriteria baex=new CbbaCriteria();
                    baex.createCriteria()
                            .andCbba08EqualTo(cbob.getCbob08())
                            .andCbba12EqualTo(0)
                            .andCbba07Like("%"+"GBSH" + "%")
                            .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                    baex.setOrderByClause("CBBA15 asc");
                    List<Cbba> cbbas = cbbaMapper.selectByExample(baex);
                    for (Cbba cbba : cbbas) {
                        if(cbba.getCbba14()>cbob09){
                            cbba.setCbba14(cbba.getCbba14()-cbob09);
                            cbba.setCbba04(date);
                            cbbaMapper.updateByPrimaryKey(cbba);
                            cbob09=0.0;
                        }else {
                            cbob09=cbob09-cbba.getCbba14();
                            cbba.setCbba14(0.0);
                            cbba.setCbba04(date);
                            cbbaMapper.updateByPrimaryKey(cbba);

                        }
                        if(cbob09==0.0){
                            break;
                        }
                    }

                }

                if(cbob09!=0.0){
                    throw new SwException("?????????????????????????????????????????????????????????????????????");
                }


//            int i = gsGoodsUseMapper.deleteByExample(usexample);
            }

        }else {
            //????????????????????????????????????
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cboa.getCboa11())) {
                throw new SwException("??????????????????????????????????????????");
            }

//            for (Cbob cbob : cbobs) {
//
//
//
//                GsGoodsUseCriteria usexample = new GsGoodsUseCriteria();
//                usexample.createCriteria()
//                        .andWhIdEqualTo(WareHouseType.GQWWHID)
//                        .andGoodsIdEqualTo(cbob.getCbob08());
//                usexample.setOrderByClause("wh_id desc ");
//                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usexample);
//                Double cbob09 = cbob.getCbob09();
//                GsGoodsUse gqwGsUse=null;
//                for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
//                    if(gsGoodsUs.getWhId().equals(WareHouseType.GQWWHID)){
//                        gqwGsUse=gsGoodsUs;
//                        continue;
//                    }
//                    if(cbob09>gsGoodsUs.getLockQty()){
//                        cbob09=cbob09-gsGoodsUs.getLockQty();
//                        gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());
//
//                    }else {
//
//                        gsGoodsUs.setLockQty(gsGoodsUs.getLockQty()-cbob09);
//                        gsGoodsUs.setUpdateTime(date);
//                        cbob09=0.0;
//                        gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
//
//                    }
//                    if(cbob09==0){
//                        break;
//                    }
//
//                }
//                if(cbob09!=0.0){
//                    throw new SwException("??????????????????????????????????????????????????????");
//
//                }
//
//
//
//                Cbba cbba= cbbaMapper.selectByPrimaryKey(cbob.getCbob17());
//                if(cbba==null){
//                    throw new SwException("???????????????????????????????????????????????????"+cbob.getCbob18());
//
//                }
//                cbba.setCbba14(cbba.getCbba14()-cbob.getCbob09());
//
//
//            }
        }



        //??????????????????
        //??????????????????
        GsOutStockAdivceCriteria adexample = new GsOutStockAdivceCriteria();
        adexample.createCriteria()
                .andSaleOrderNoEqualTo(cboa.getCboa07());
        int i1 = gsOutStockAdivceMapper.deleteByExample(adexample);

        cboa.setCboa11(0);
        cboaMapper.updateByPrimaryKey(cboa);
    }

    @Transactional
    @Override
    public String importySaleOrder(List<SaleOrderExcelDto> list, Long userId) {
        //?????????????????????????????????????????????
        if (list.size() == 0) {
            throw new SwException("???????????????????????????");
        }

        Map<String, List<String>> customerMap = new HashMap<>();
        Map<String, List<SaleOrderExcelDto>> customerGoodsMap = new HashMap<>();
        Map<String, List<SaleOrderExcelDto>> goodsMap = new HashMap<>();
//        Map<String, Integer> totalOrderMap = new HashMap<>();
        Map<String, Integer> totalCustomerOrderMap = new HashMap<>();
        Map<String,Integer> customerGoods=new HashMap<>();
        String errors="";

        for (SaleOrderExcelDto saleOrderExcelDto : list) {
            if(customerGoodsMap.get(saleOrderExcelDto.getCustomerName()+saleOrderExcelDto.getSku())!=null){
                throw new SwException("?????????????????????????????????????????????????????????????????????");
            }


        }

        for (SaleOrderExcelDto saleOrderExcelDto : list) {
            if (StringUtils.isBlank(saleOrderExcelDto.getCustomerName())) {
                throw new SwException("?????????????????????????????????");
            }

            if (StringUtils.isBlank(saleOrderExcelDto.getSku())) {
                throw new SwException("SKU???????????????????????????");
            }

//            if (StringUtils.isBlank(saleOrderExcelDto.getSaleUserName())) {
//                throw new SwException("???????????????????????????????????????");
//            }


            if (saleOrderExcelDto.getQty() == null) {
                throw new SwException("Release Quantity???????????????????????????");
            }

            if(customerGoodsMap.get(saleOrderExcelDto.getCustomerName())==null) {
                List<SaleOrderExcelDto> res = new ArrayList<>();
                res.add(saleOrderExcelDto);
                customerGoodsMap.put(saleOrderExcelDto.getCustomerName(), res);
            }else {
                customerGoodsMap.get(saleOrderExcelDto.getCustomerName()).add(saleOrderExcelDto);
            }



//            totalOrderMap.put(saleOrderExcelDto.getTotalOrderNo(), 1);

            List<SaleOrderExcelDto> strings = goodsMap.get(saleOrderExcelDto.getTotalOrderNo() + "_" + saleOrderExcelDto.getCustomerName());
            if (strings == null) {
                strings = new ArrayList<>();
            }
            strings.add(saleOrderExcelDto);
            goodsMap.put(saleOrderExcelDto.getTotalOrderNo() + "_" + saleOrderExcelDto.getCustomerName(), strings);

            List<String> customers = customerMap.get(saleOrderExcelDto.getTotalOrderNo());
            if (customers == null) {
                customers = new ArrayList<>();
            }
            if(totalCustomerOrderMap.get(saleOrderExcelDto.getTotalOrderNo()+"-"+saleOrderExcelDto.getCustomerName())==null){
                customers.add(saleOrderExcelDto.getCustomerName());
                customerMap.put(saleOrderExcelDto.getTotalOrderNo(), customers);
                totalCustomerOrderMap.put(saleOrderExcelDto.getTotalOrderNo()+"-"+saleOrderExcelDto.getCustomerName(),1);
            }



        }

        for (String key : customerGoodsMap.keySet()) {
//            List<String> customers = customerMap.get(key);
//            if (customers == null) {
//                customers = new ArrayList<>();
//            }
//            for (String customer : customers) {
                List<SaleOrderExcelDto> saleOrderExcelDtos = customerGoodsMap.get(key);
                CbcaCriteria caex = new CbcaCriteria();
                caex.createCriteria()
                        .andCbca08EqualTo(key)
                        .andCbca06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                        .andCbca07EqualTo("??????");
                List<Cbca> cbcas = cbcaMapper.selectByExample(caex);
                if (cbcas.size() == 0) {
                    throw new SwException("???????????????????????????:" + key);
                }
                Cbca cbca = cbcas.get(0);
                String saleUser = "";
                String orderType = "";
                String currency = "1";
                Double normalPrice = 0.0;
                if (saleOrderExcelDtos.size() > 0) {
                    SaleOrderExcelDto saleOrderExcelDto = saleOrderExcelDtos.get(0);
//                    saleUser = saleOrderExcelDto.getSaleUserName();
                    orderType = saleOrderExcelDto.getOrderType();
//                    CalaCriteria laex = new CalaCriteria();
//                    laex.createCriteria()
//                            .andCala08EqualTo(saleOrderExcelDto.getCurrency());
//                    List<Cala> calas = calaMapper.selectByExample(laex);
//                    if("CNY".equals(saleOrderExcelDto.getCurrency())){
//                        currency="6";
//                    }else {
                        currency="5";
//                    }

//                    if (calas.size() > 0) {
//                        currency = calas.get(0).getCala02();
//                    }
                }
                //???????????????
//                SysUserCriteria suex = new SysUserCriteria();
//                suex.createCriteria()
//                        .andNickNameEqualTo(saleUser);
//                List<SysUser> sysUsers = sysUserMapper.selectByExample(suex);
//                Integer saleUserId = null;
//                if (sysUsers.size() > 0) {
//                    saleUserId = sysUsers.get(0).getUserId().intValue();
//                }

                //????????????????????????
                Cboa cboa = new Cboa();
                Date date = new Date();
                cboa.setCboa02(date);
                cboa.setCboa03(userId.intValue());
                cboa.setCboa04(date);
                cboa.setCboa05(userId.intValue());
                cboa.setCboa10(userId.intValue());
                cboa.setCboa06(DeleteFlagEnum.NOT_DELETE.getCode());
                NumberDo numberDo = new NumberDo();
                numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
                cboa.setCboa07(numberGenerate.createOrderNo(numberDo).getOrderNo());
                cboa.setCboa08(date);
                cboa.setCboa09(cbca.getCbca01());
                cboa.setCboa10(userId.intValue());
                cboa.setCboa11(AuditStatusConstants.SO_COMMIT);
                cboa.setCboa13(date);
                cboa.setCboa16(Integer.valueOf(currency));
                cboa.setCboa17(cbca.getCbca14());
                cboa.setCboa18(cbca.getCbca15());
                cboa.setCboa19(cbca.getCbca16());
                cboa.setCboa22(cbca.getCbca24());
//                if (SaleOrderTypeEnum.XIAOSHOUDINGDAN.getMsg().equals(orderType)) {
                    cboa.setCboa24(SaleOrderTypeEnum.XIAOSHOUDINGDAN.getCode());
//                } else {
//                    cboa.setCboa24(SaleOrderTypeEnum.YUDINGDAN.getCode());
//                }


                cboa.setCboa27(OrderTypeEnum.GUOJIDINGDAN.getCode());
                cboaMapper.insertWithId(cboa);
                Cbob cbob = null;
                for (int i = 0; i < saleOrderExcelDtos.size(); i++) {

                    SaleOrderExcelDto saleOrderExcelDto = saleOrderExcelDtos.get(i);


                    CbpbCriteria pbex = new CbpbCriteria();
                    pbex.createCriteria()
                            .andCbpb12EqualTo(saleOrderExcelDto.getSku())
                            .andCbpb07EqualTo("??????")
                            .andCbpb06EqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
                    List<Cbpb> cbpbs = cbpbMapper.selectByExample(pbex);


                    if (cbpbs.size() == 0) {
                        errors=errors+"???????????????????????????" + saleOrderExcelDto.getSku()+"???????????????????????????";
//                        throw new SwException("??????????????????????????????" + saleOrderExcelDto.getSku());
                        continue;
                    }
                    Cbpb cbpb = cbpbs.get(0);
                    //???????????????????????????
                    CheckSkuDo checkSkuDo=new CheckSkuDo();
                    checkSkuDo.setGoodsId(cbpb.getCbpb01());
                    checkSkuDo.setTotalOrderNo(saleOrderExcelDto.getTotalOrderNo());

//                    checkSkuDo.setTotalOrderNo();
                    checkSkuDo.setOrderClass(OrderTypeEnum.GUOJIDINGDAN.getCode());
                    QtyMsgVo qtyMsgVo = orderDistributionService.checkSku(checkSkuDo);

                    if(qtyMsgVo.getCanUseNum()<saleOrderExcelDto.getQty()){
                        errors=errors+"??????????????????????????????" + saleOrderExcelDto.getSku()+"??????"+i+"??????"+qtyMsgVo.getCanUseNum()+"??????"+saleOrderExcelDto.getQty();

//                        throw new SwException("???????????????????????????" + saleOrderExcelDto.getSku());
                        continue;
                    }

                    //???????????????????????????????????????
//                    Cbba cbba = cbbaMapper.selectByPrimaryKey(cbob.getCbob17());




                    CbpfCriteria pfex = new CbpfCriteria();
                    pfex.createCriteria()
                            .andCbpf03EqualTo(6)
                            .andCbpb01EqualTo(cbpb.getCbpb01())
                            .andCbpf02EqualTo(cbca.getCbca28());
                    pfex.setOrderByClause("CBPF07 desc");
                    List<Cbpf> cbpfs = cbpfMapper.selectByExample(pfex);
                    if (cbpfs.size() > 0 && cbpfs.get(0).getCbpf07().getTime() <= date.getTime()) {
                        normalPrice = cbpfs.get(0).getCbpf05();
                    }

                    //??????????????????
                    CbbaCriteria baex = new CbbaCriteria();
                    baex.createCriteria()
                            .andCbba07EqualTo(saleOrderExcelDto.getTotalOrderNo())
                            .andCbba08EqualTo(cbpb.getCbpb01())
                            .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                    List<Cbba> cbbas = cbbaMapper.selectByExample(baex);
                    if (cbbas.size() == 0) {
                        errors=errors+"????????????????????????????????????????????????????????????????????????:" + saleOrderExcelDto.getTotalOrderNo()+",\\n";

                        throw new SwException("????????????????????????????????????????????????????????????????????????:" + saleOrderExcelDto.getTotalOrderNo());
                    }
                    Cbba cbba = cbbas.get(0);

                    Double canUserQty=cbba.getCbba13()-cbba.getCbba14();
                    if(saleOrderExcelDto.getQty()>canUserQty){
                        String goodsMsg="";
                        if(cbpb!=null){
                            goodsMsg=cbpb.getCbpb12();

                        }

                        errors=errors+"??????????????????????????????"+goodsMsg+saleOrderExcelDto.getQty()+">"+canUserQty+"cbbaid"+cbba.getCbba01();
                        continue;
                    }
                    cbob = new Cbob();
                    cbob.setCbob02(i);
                    cbob.setCbob03(date);
                    cbob.setCbob04(userId.intValue());
                    cbob.setCbob05(date);
                    cbob.setCbob06(userId.intValue());
                    cbob.setCbob07(DeleteFlagEnum.NOT_DELETE.getCode());
                    cbob.setCbob08(cbpb.getCbpb01());
                    cbob.setCbob09(saleOrderExcelDto.getQty());
                    cbob.setCbob10(0.0);
                    cbob.setCbob11(saleOrderExcelDto.getPrice());
                    if(saleOrderExcelDto.getPrice()==null){
                        cbob.setCbob12(0.0);
                    }else {
                        cbob.setCbob12(saleOrderExcelDto.getPrice() * saleOrderExcelDto.getQty());
                    }
                    //cbob.setCbob12(saleOrderExcelDto.getPrice() * saleOrderExcelDto.getQty());
                    cbob.setCbob13(saleOrderExcelDto.getRemark());
                    cbob.setCboa01(cboa.getCboa01());
                    cbob.setCbob14(normalPrice);
                    cbob.setCboa01(cboa.getCboa01());
                    cbob.setCbob17(cbba.getCbba01());
                    cbob.setCbob18(saleOrderExcelDto.getTotalOrderNo());
                    cbob.setConfirmQty(0.0);
                    cbobMapper.insert(cbob);
                    //?????????????????????????????????
//                    GoodsOperationDo goodsOperationDo = new GoodsOperationDo();
//                    goodsOperationDo.setGoodsId(cbpb.getCbpb01());
//                    goodsOperationDo.setNum(saleOrderExcelDto.getQty());
//                    goodsOperationDo.setOrderNo(cbba.getCbba07());
//                    goodsOperationDo.setOrderNum(saleOrderExcelDto.getQty());
//                    goodsOperationDo.setOrderType(OrderTypeEnum.GUOJIDINGDAN.getCode());
//                    goodsOperationDo.setUserId(userId.intValue());
//                    SaleOrderMakeDo saleOrderMakeDo = orderDistributionService.saleOrderMake(goodsOperationDo);

                }


            }

        if(!errors.equals("")){
            throw new SwException("????????????"+errors);
        }



        return "????????????";


    }

    @Transactional
    @Override
    public void reAddSaleOrder(Integer orderId, Long userId) {

        //?????????????????????
        Cboa cboa = cboaMapper.selectByPrimaryKey(orderId);
        if (cboa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())) {
            throw new SwException("?????????????????????");
        }
        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboa.getCboa11())) {
            throw new SwException("??????????????????????????????????????????");
        }

        Date date = new Date();
        if (!cboa.getCboa10().equals(userId.intValue())) {
            throw new SwException("?????????????????????????????????");
        }

        //????????????????????????
        if (OrderTypeEnum.GUONEIDINGDAN.getCode().equals(cboa.getCboa27())) {
            CbobCriteria obex = new CbobCriteria();
            obex.createCriteria()
                    .andCboa01EqualTo(cboa.getCboa01())
                    .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbob> cbobs = cbobMapper.selectByExample(obex);


            for (Cbob good : cbobs) {


                //?????????????????????????????????
                GoodsOperationDo goodsOperationDo = new GoodsOperationDo();
                goodsOperationDo.setGoodsId(good.getCbob08());
                goodsOperationDo.setNum(good.getCbob09().doubleValue());
                goodsOperationDo.setOrderNo(cboa.getCboa07());
                goodsOperationDo.setOrderNum(good.getCbob09().doubleValue());
                goodsOperationDo.setOrderType(OrderTypeEnum.GUONEIDINGDAN.getCode());
                goodsOperationDo.setUserId(userId.intValue());
                SaleOrderMakeDo saleOrderMakeDo = orderDistributionService.saleOrderMake(goodsOperationDo);
                //??????????????????
                if (saleOrderMakeDo.getPrompt() == 1) {
                    GsOutStockAdivce advice = null;
                    for (OutSuggestionsDo outSuggestionsDo : saleOrderMakeDo.getList()) {
                        if(outSuggestionsDo.getQty()!=0) {

                            advice = new GsOutStockAdivce();
                            advice.setCreateBy(userId.intValue());
                            advice.setCreateTime(date);
                            advice.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
                            advice.setGoodsId(outSuggestionsDo.getGoodsId());
                            advice.setQty(outSuggestionsDo.getQty());
                            advice.setSaleOrderNo(cboa.getCboa07());
                            if (WareHouseType.CDCWHID.equals(outSuggestionsDo.getWhId())) {
                                advice.setStatus(new Byte("3"));
                            } else {
                                advice.setStatus(new Byte("2"));
                            }
                            advice.setUpdateBy(userId.intValue());
                            advice.setUpdateTime(date);
                            advice.setWhId(outSuggestionsDo.getWhId());
                            gsOutStockAdivceMapper.insert(advice);

                        }
                    }
                }

            }
            GsWorkInstanceDo goodsWorkInstanceDo = new GsWorkInstanceDo();
            goodsWorkInstanceDo.setOrderType((byte) 2);
            goodsWorkInstanceDo.setOrderClose(OrdercloseEnum.WEIJIESHU.getCode());
            goodsWorkInstanceDo.setOrderStatus(OrderstatusEnum.DAISHENPI.getCode());
            goodsWorkInstanceDo.setOrderNo(cboa.getCboa07());
//            taskService.editGsWorkInstance(goodsWorkInstanceDo);
        }

        //????????????????????????
        cboa.setCboa11(SaleOrderStatusEnums.YITIJIAO.getCode());
        cboa.setCboa04(new Date());
        cboa.setCboa05(userId.intValue());
        cboaMapper.updateByPrimaryKey(cboa);


    }


    @Override
    public GoodsPriceAndSkuVo goodsPriceAndSku(GoodsPriceAndSkuDto goodsPriceAndSkuDto) {
        GoodsPriceAndSkuVo res = new GoodsPriceAndSkuVo();

        //????????????????????????
        Cbca cbca = baseCheckService.checkCustomer(goodsPriceAndSkuDto.getCustomerId());
       if (cbca.getCbca28()!=null){
           CbpfCriteria pfex = new CbpfCriteria();
           if(goodsPriceAndSkuDto.getCurrency()==null){
               pfex.createCriteria()
                       .andCbpf02EqualTo(cbca.getCbca28())
                       .andCbpb01EqualTo(goodsPriceAndSkuDto.getGoodsId());
               pfex.setOrderByClause("CBPF07 desc");
           }else {
               pfex.createCriteria()
                       .andCbpf02EqualTo(cbca.getCbca28())
//                    .andCbpf06EqualTo(goodsPriceAndSkuDto.getCurrency())
                       .andCbpb01EqualTo(goodsPriceAndSkuDto.getGoodsId());
               pfex.setOrderByClause("CBPF07 desc");
           }

           List<Cbpf> cbpfs = cbpfMapper.selectByExample(pfex);
           Date date = new Date();
           if (cbpfs.size() > 0 && cbpfs.get(0).getCbpf07().getTime() <= date.getTime()) {
               res.setNormalPrice(cbpfs.get(0).getCbpf05());

           }
        }


        res.setGoodsId(goodsPriceAndSkuDto.getGoodsId());

        res.setPrice(res.getNormalPrice());
        //?????????
        CheckSkuDo chekDo = new CheckSkuDo();
        chekDo.setGoodsId(goodsPriceAndSkuDto.getGoodsId());
        chekDo.setOrderClass(2);
        QtyMsgVo qtyMsgVo = orderDistributionService.checkSku(chekDo);
        res.setCanUseSku(qtyMsgVo.getCanUseNum());
        res.setCkSku(qtyMsgVo.getCanUseNum());
//        List<Cbwa> list = cbwaMapper.selectCalculationOrderPriority();
//
//        if (list.size() == 0) {
//            throw new SwException("??????????????????????????????");
//        }

//        Cbib cbib = cbibMapper.selectLastByGoodsId(goodsPriceAndSkuDto.getGoodsId());
//        if(cbib==null){
//            throw new SwException("????????????????????????????????????");
//        }
//        List<GsGoodsUse> goodsUseList = gsGoodsUseMapper.selectByGoodsId(goodsPriceAndSkuDto.getGoodsId());
//        Double useNum = goodsUseList.stream().collect(Collectors.summingDouble(GsGoodsUse::getLockQty));
//        if(cbib.getCbib15()!=null){
//            Double num = cbib.getCbib15() - useNum;
//            if(num<0){
//                num=0.0;
//            }
//            res.setCanUseSku(num);
//            res.setCkSku(num);
//        }
//


        if (res.getNormalPrice() == null) {
            res.setNormalPrice(0.0);
        }
        return res;

    }


    private Double getCanUseSku(GoodsPriceAndSkuDto goodsPriceAndSkuDto) {
//       Double canUseQty=0.0;
//        if(OrderTypeEnum.GUONEIDINGDAN.getCode().equals(goodsPriceAndSkuDto.getOrderClass())){
//        da
//        }else {
//            GsGoodsSkuCriteria example=new GsGoodsSkuCriteria();
//            example.createCriteria()
//                    .andWhIdEqualTo(WareHouseType.GQWWHID)
//                    .andGoodsIdEqualTo(goodsPriceAndSkuDto.getGoodsId())
//                    .andDeleteFlagEqualTo(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
//            List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(example);
//            Double countQty=gsGoodsSkus.stream().mapToDouble(GsGoodsSku::getQty).sum();
//
//            GsGoodsUseCriteria usex=new GsGoodsUseCriteria();
//            usex.createCriteria()
//                    .andWhIdEqualTo(WareHouseType.GQWWHID)
//                    .andGoodsIdEqualTo(goodsPriceAndSkuDto.getGoodsId());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//
//            Double useQty=gsGoodsUses.stream().mapToDouble(GsGoodsUse::getLockQty).sum();
//            canUseQty= countQty-useQty;
//
//        }
//       if(canUseQty<0){
//            canUseQty=0.0
//       }


        return null;

    }

    @Transactional
    @Override
    public void auditSaleOrder(AuditSaleOrderDto auditSaleOrderDto) {
        Date date = new Date();

        Cboa cboa = baseCheckService.checkSaleOrder(auditSaleOrderDto.getOrderId());
        Integer orderStatus = cboa.getCboa11();
        //??????????????????????????????
        String perType = "1";
        if (OrderTypeEnum.GUOJIDINGDAN.getCode().equals(cboa.getCboa27())) {
            perType = "2";
        }
        baseCheckService.checkUserTask(auditSaleOrderDto.getUserId().longValue(), perType);

        if (auditSaleOrderDto.getOpeateType().equals(7)) {

            if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(orderStatus)) {
                throw new SwException("????????????????????????????????????????????????");
            }
            //????????????
            cboa.setCboa11(SaleOrderStatusEnums.YIWANCHENG.getCode());
            //????????????????????????????????????????????????
            if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboa.getCboa11())) {
                CbobCriteria obex = new CbobCriteria();
                obex.createCriteria()
                        .andCboa01EqualTo(cboa.getCboa01())
                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                List<Cbob> cbobs = cbobMapper.selectByExample(obex);
                for (Cbob cbob : cbobs) {
                    GsGoodsUseCriteria guex = new GsGoodsUseCriteria();
                    guex.createCriteria()
//                            .andOrderNoEqualTo(cboa.getCboa07())
                            .andGoodsIdEqualTo(cbob.getCbob08());
                    List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
                    Double noUse = cbob.getCbob09() - cbob.getCbob10();

                    for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                        if (noUse >= gsGoodsUs.getLockQty()) {
                            gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());
                            noUse = noUse - gsGoodsUs.getLockQty();

                        } else {
                            gsGoodsUs.setLockQty(gsGoodsUs.getLockQty() - noUse);
                            gsGoodsUs.setUpdateTime(new Date());
                            gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
                            noUse = 0.0;
                        }

                        if (noUse == 0) {
                            break;
                        }
                    }

                }
            }


        }
        else if(auditSaleOrderDto.getOpeateType().equals(2)){
            revokeSaleOrder(auditSaleOrderDto.getOrderId(),auditSaleOrderDto.getUserId());
            return;
        }
        else if (auditSaleOrderDto.getOpeateType().equals(3)) {
            String errors="";
            //????????????
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(orderStatus)) {
                throw new SwException("???????????????????????????????????????");
            }
            if(cboa.getCboa27()==1){
                cboa.setCboa11(SaleOrderStatusEnums.YIFUSHEN.getCode());

                //????????????????????????
                CbobCriteria example=new CbobCriteria();
                example.createCriteria()
                        .andCboa01EqualTo(cboa.getCboa01())
                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                List<Cbob> cbobs = cbobMapper.selectByExample(example);
                for (Cbob cbob : cbobs) {
                    CheckSkuDo chedto=new CheckSkuDo();
                    chedto.setOrderClass(OrderTypeEnum.GUOJIDINGDAN.getCode());
                    chedto.setGoodsId(cbob.getCbob08());
                    QtyMsgVo qtyMsgVo = orderDistributionService.checkSku(chedto);

                    //???????????????????????????????????????
                    Cbba cbba = cbbaMapper.selectByPrimaryKey(cbob.getCbob17());

                    if(cbba==null){
                        throw new SwException("??????????????????????????????"+cbob.getCbob18());
                    }
                    Double canUserQty=cbba.getCbba13()-cbba.getCbba14();
                    if(cbob.getCbob09()>canUserQty){
                        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
                        String goodsMsg="";
                        if(cbpb!=null){
                            goodsMsg=cbpb.getCbpb12();

                        }

                        errors=errors+"??????????????????????????????"+goodsMsg+cbob.getCbob09()+">"+canUserQty;
                        continue;
                    }


                    if(qtyMsgVo.getCanUseNum()<cbob.getCbob09()){
                        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
                        String goodsMsg="";
                        if(cbpb!=null){
                            goodsMsg=cbpb.getCbpb12();

                        }

                        errors=errors+"??????????????????????????????"+goodsMsg+qtyMsgVo.getCanUseNum()+"<"+cbob.getCbob09();
                        continue;
                    }else {
                        //????????????
                        GoodsOperationDo goodsOperationDo = new GoodsOperationDo();
                        goodsOperationDo.setTotalOrderNo(cbob.getCbob18());
                        goodsOperationDo.setGoodsId(cbob.getCbob08());
                        goodsOperationDo.setNum(cbob.getCbob09());
                        goodsOperationDo.setOrderNo(cboa.getCboa07());
                        goodsOperationDo.setOrderNum(cbob.getCbob09());
                        goodsOperationDo.setOrderType(OrderTypeEnum.GUOJIDINGDAN.getCode());
                        goodsOperationDo.setUserId(auditSaleOrderDto.getUserId());
                        orderDistributionService.saleOrderMake(goodsOperationDo);

                    }

                }
                if(!errors.equals("")){
                    throw new SwException("????????????"+errors);
                }

            }else {
                cboa.setCboa11(SaleOrderStatusEnums.YISHENHE.getCode());
            }

            Cabraa cabraa = new Cabraa();
            cabraa.setCabraa02(date);
            cabraa.setCabraa03(date);
            cabraa.setCabraa04(auditSaleOrderDto.getUserId());
            cabraa.setCabraa05(auditSaleOrderDto.getUserId());
            cabraa.setCabraa06(DeleteFlagEnum.NOT_DELETE.getCode());
            cabraa.setCabraa07("????????????");
            cabraa.setCabraa12(date);
            cabraa.setCabraa14(cboa.getCboa07());
            cabraa.setCabraa15(date);
            cabraa.setCabraa18("[??????]ok");
            cabraa.setCabraa11(auditSaleOrderDto.getUserId());
            cabraaMapper.insert(cabraa);

        }
        else if (auditSaleOrderDto.getOpeateType().equals(6)) {
            //??????


            if (!SaleOrderStatusEnums.YISHENHE.getCode().equals(orderStatus)) {
                throw new SwException("???????????????????????????????????????");
            }
            cboa.setCboa11(SaleOrderStatusEnums.YITIJIAO.getCode());


        }
        else if (auditSaleOrderDto.getOpeateType().equals(5)) {

            if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(orderStatus)) {
                throw new SwException("????????????????????????????????????????????????");
            }
            //????????????
            cboa.setCboa11(SaleOrderStatusEnums.YIWANCHENG.getCode());
            //????????????????????????????????????????????????
            if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboa.getCboa11())) {
                CbobCriteria obex = new CbobCriteria();
                obex.createCriteria()
                        .andCboa01EqualTo(cboa.getCboa01())
                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                List<Cbob> cbobs = cbobMapper.selectByExample(obex);
                for (Cbob cbob : cbobs) {
                    GsGoodsUseCriteria guex = new GsGoodsUseCriteria();
                    guex.createCriteria()
                            .andOrderNoEqualTo(cboa.getCboa07())
                            .andGoodsIdEqualTo(cbob.getCbob08());
                    List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
                    Double noUse = cbob.getCbob09() - cbob.getCbob10();

                    for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                        if (noUse >= gsGoodsUs.getLockQty()) {
                            gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());
                            noUse = noUse - gsGoodsUs.getLockQty();

                        } else {
                            gsGoodsUs.setLockQty(gsGoodsUs.getLockQty() - noUse);
                            gsGoodsUs.setUpdateTime(new Date());
                            gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
                            noUse = 0.0;
                        }

                        if (noUse == 0) {
                            break;
                        }
                    }

                }
            }
        }


        cboa.setCboa05(auditSaleOrderDto.getUserId());
        cboa.setCboa04(date);
        cboaMapper.updateByPrimaryKey(cboa);

    }

    @Override
    public List<SaleOrderListVo> finsaleOrderList(SaleOrderListDto saleOrderListDto) {


        List<SaleOrderListVo> saleOrderListVos = cboaMapper.finsaleOrderList(saleOrderListDto);
        for (SaleOrderListVo saleOrderListVo : saleOrderListVos) {
            if (saleOrderListVo.getSettleCurrent().equals(6)) {
                saleOrderListVo.setSettleCurrentMsg("CNY");
            } else {
                saleOrderListVo.setSettleCurrentMsg("USD");
            }

            if (saleOrderListVo.getOrderClass().equals(OrderTypeEnum.GUOJIDINGDAN.getCode())) {
                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
            } else {
                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
            }

            if (saleOrderListVo.getOrderType().equals(10)) {
                saleOrderListVo.setOrderTypeMsg("????????????");
            } else {
                saleOrderListVo.setOrderTypeMsg("?????????");
            }

            saleOrderListVo.setStatusMsg(SaleOrderStatusEnums.findByKey(saleOrderListVo.getStatus()).getMsg());

        }
        return saleOrderListVos;

    }

    @Override
    public void auditFinSaleOrder(AuditSaleOrderDto auditSaleOrderDto) {

        Cboa cboa = baseCheckService.checkSaleOrder(auditSaleOrderDto.getOrderId());
        //??????????????????????????????

        SysUser sysUser = baseCheckService.checkUserTask(auditSaleOrderDto.getUserId().longValue(), new String("11"));

        //???????????????????????????
        if (auditSaleOrderDto.getOpeateType().equals(4)) {

            if (!SaleOrderStatusEnums.YISHENHE.getCode().equals(cboa.getCboa11())) {
                throw new SwException("???????????????????????????????????????");
            }

            Date date = new Date();
            cboa.setCboa04(date);
            cboa.setCboa05(auditSaleOrderDto.getUserId());
            cboa.setCboa11(SaleOrderStatusEnums.YIFUSHEN.getCode());
            cboaMapper.updateByPrimaryKey(cboa);

        } else {

            if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(cboa.getCboa11())) {
                throw new SwException("???????????????????????????????????????");
            }

            CbpkCriteria pkex=new CbpkCriteria();
            pkex.createCriteria()
                    .andCbpk06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                    .andSaleOrderNoEqualTo(cboa.getCboa07());
            List<Cbpk> cbpks = cbpkMapper.selectByExample(pkex);
            if(cbpks.size()>0){
                throw new SwException("??????????????????????????????");
            }

            Date date = new Date();
            cboa.setCboa04(date);
            cboa.setCboa05(auditSaleOrderDto.getUserId());
            if(OrderTypeEnum.GUOJIDINGDAN.getCode().equals(cboa.getCboa27())){
                cboa.setCboa11(SaleOrderStatusEnums.YITIJIAO.getCode());
                if(cboa.getCboa27()==1){
                    CbobCriteria obex=new CbobCriteria();
                    obex.createCriteria()
                            .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                            .andCboa01EqualTo(cboa.getCboa01());
                    List<Cbob> cbobs = cbobMapper.selectByExample(obex);
                    for (Cbob cbob : cbobs) {



                        GsGoodsUseCriteria usexample = new GsGoodsUseCriteria();
                        usexample.createCriteria()
                                .andWhIdEqualTo(WareHouseType.GQWWHID)
                                .andGoodsIdEqualTo(cbob.getCbob08());
                        usexample.setOrderByClause("wh_id desc ");
                        List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usexample);
                        Double cbob09 = cbob.getCbob09();
                        GsGoodsUse gqwGsUse=null;
                        for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
//                            if(gsGoodsUs.getWhId().equals(WareHouseType.GQWWHID)){
//                                gqwGsUse=gsGoodsUs;
//                                continue;
//                            }
                            if(cbob09>gsGoodsUs.getLockQty()){
                                cbob09=cbob09-gsGoodsUs.getLockQty();
                                gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());

                            }else {

                                gsGoodsUs.setLockQty(gsGoodsUs.getLockQty()-cbob09);
                                gsGoodsUs.setUpdateTime(date);
                                cbob09=0.0;
                                gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);

                            }
                            if(cbob09==0){
                                break;
                            }

                        }
                        if(cbob09!=0.0){
                            throw new SwException("??????????????????????????????????????????????????????");

                        }



                        Cbba cbba= cbbaMapper.selectByPrimaryKey(cbob.getCbob17());
                        if(cbba==null){
                            throw new SwException("???????????????????????????????????????????????????"+cbob.getCbob18());

                        }
                        cbba.setCbba14(cbba.getCbba14()-cbob.getCbob09());
                        cbbaMapper.updateByPrimaryKey(cbba);

                    }
                }
            }else {
                cboa.setCboa11(SaleOrderStatusEnums.YISHENHE.getCode());
            }

            cboaMapper.updateByPrimaryKey(cboa);
        }


    }


    @Override
    public List<GoodsShopVo> goodsShopList(Integer userId) {
        return gsSaleShoppingMapper.goodsShopList(userId);

    }


    @Override
    public List<SaleOrderListVo> saleChangeList(SaleOrderListDto saleOrderListDto) {

        List<SaleOrderListVo> saleOrderListVos = cbocMapper.saleChangeList(saleOrderListDto);
        for (SaleOrderListVo saleOrderListVo : saleOrderListVos) {
            if (saleOrderListVo.getSettleCurrent().equals(6)) {
                saleOrderListVo.setSettleCurrentMsg("CNY");
            } else {
                saleOrderListVo.setSettleCurrentMsg("USD");
            }

//            if (saleOrderListVo.getOrderClass().equals(OrderTypeEnum.GUOJIDINGDAN.getCode())) {
//                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
//            } else {
                saleOrderListVo.setOrderClassMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
//            }

            if (saleOrderListVo.getOrderType().equals(10)) {
                saleOrderListVo.setOrderTypeMsg("????????????");
            } else {
                saleOrderListVo.setOrderTypeMsg("?????????");
            }

            saleOrderListVo.setStatusMsg(SaleOrderStatusEnums.findByKey(saleOrderListVo.getStatus()).getMsg());

        }
        return saleOrderListVos;
    }

    @Transactional
    @Override
    public void addSaleOrderChange(SaleOrderChangeDto saleOrderChangeDto) {
        Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderChangeDto.getOrderId());
        if (cboa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())) {
            throw new SwException("???????????????????????????");
        }

        //???????????????????????????????????????
//        if (!cboa.getCboa11().equals(saleOrderChangeDto.getUserId())) {
//            throw new SwException("???????????????????????????");
//        }
        if(!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(cboa.getCboa11())){
            throw new SwException("??????????????????????????????????????????");
        }



        //????????????????????????????????????
        CbocCriteria ocex=new CbocCriteria();
        ocex.createCriteria()
                .andCboc26EqualTo(cboa.getCboa01())
                .andCboc11EqualTo(SaleOrderStatusEnums.YITIJIAO.getCode());
        List<Cboc> cbocs = cbocMapper.selectByExample(ocex);
        if(cbocs.size()>0){
            throw new SwException("??????????????????????????????????????????????????????");

        }
        //???????????????????????????????????????
        if (!cboa.getCboa10().equals(saleOrderChangeDto.getUserId())) {
            throw new SwException("???????????????????????????");
        }



        //????????????????????????
        Cboc cboc = new Cboc();
        Date date = new Date();
        cboc.setCboc02(date);
        cboc.setCboc03(saleOrderChangeDto.getUserId());
        cboc.setCboc04(date);
        cboc.setCboc05(saleOrderChangeDto.getUserId());
        cboc.setCboc06(DeleteFlagEnum.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDERCHANGE.getCode());
        cboc.setCboc07(numberGenerate.createOrderNo(numberDo).getOrderNo());
        cboc.setCboc08(saleOrderChangeDto.getOrderDate());
        cboc.setCboc09(saleOrderChangeDto.getCustomerId());
        cboc.setCboc10(saleOrderChangeDto.getSaleUserId());
        cboc.setCboc11(AuditStatusConstants.SO_COMMIT);

        cboc.setCboc16(saleOrderChangeDto.getCurrency());
        cboc.setCboc17(saleOrderChangeDto.getReceiveName());
        cboc.setCboc18(saleOrderChangeDto.getAddress());
        cboc.setCboc19(saleOrderChangeDto.getReceivePhone());
        cboc.setCboc22(saleOrderChangeDto.getInvoiceType());
        cboc.setCboc24(10);
        cboc.setCboc25(saleOrderChangeDto.getCustomerNo());
        cboc.setCboc26(cboa.getCboa01());
        cbocMapper.insertWithId(cboc);
        GsWorkInstanceDo gsWorkInstanceDo = new GsWorkInstanceDo();
        gsWorkInstanceDo.setOrderType((byte) 2);
        gsWorkInstanceDo.setOrderClose(OrdercloseEnum.WEIJIESHU.getCode());
        gsWorkInstanceDo.setOrderStatus(OrderstatusEnum.DAISHENPI.getCode());
        gsWorkInstanceDo.setOrderNo(cboc.getCboc07());
        taskService.addGsWorkInstance(gsWorkInstanceDo);
        Cbod cbod = null;
        for (SaleOrderChangeGoodsDto good : saleOrderChangeDto.getGoods()) {

            if(good.getGoodsId()==null){
                good.setGoodsId(good.getId());
            }
            //?????????????????????
//            CbobCriteria obexample = new CbobCriteria();
//            obexample.createCriteria()
//                    .andCbob08EqualTo(good.getGoodsId())
//                    .andCboa01EqualTo(cboa.getCboa01())
//                    .andCbob06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            if(good.getCbobId()==null){
                continue;
            }
            Cbob cbob = cbobMapper.selectByPrimaryKey(good.getCbobId());
            if (cbob==null) {
                throw new SwException("???????????????????????????????????????:" + good.getGoodsId());
            }
            good.setGoodsId(cbob.getCbob08());

//            if(cbob.getTakeQty()!=null &&  good.getQty()<cbob.getTakeQty()){
//                throw new SwException("???????????????????????????????????????");
//
//            }

            //?????????????????????????????? ????????????????????????
//            Cbob cbob = cbobs.get(0);
            if (good.getQty() > cbob.getCbob09()) {
                throw new SwException("??????????????????????????????");
            }

            if (good.getQty() < cbob.getCbob10()) {
                throw new SwException("??????????????????????????????????????????");
            }

            //??????????????????
            List<Cbpl> cbpls=cbplMapper.selectBySaleOrderNoAndGoodsId(cboa.getCboa07(),good.getGoodsId());
            Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

            if (good.getQty() < collect) {
                throw new SwException("??????????????????????????????????????????");
            }


            cbod = new Cbod();
            cbod.setCbobid(good.getCbobId());
            cbod.setCboc01(cboc.getCboc01());
            cbod.setCbod02(good.getNumber());
            cbod.setCbod03(date);
            cbod.setCbod04(saleOrderChangeDto.getUserId());
            cbod.setCbod05(date);
            cbod.setCbod06(saleOrderChangeDto.getUserId());
            cbod.setCbod07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbod.setCbod08(good.getGoodsId());
            cbod.setCbod09(good.getQty());
            cbod.setCbod10(cbob.getCbob10());
            cbod.setCbod11(good.getCurrentPrice());
            cbod.setCbod12(good.getCurrentPrice() * good.getQty());
            cbod.setCbod13(good.getRemark());
            cbod.setCboc01(cboc.getCboc01());
            cbod.setCbod14(good.getNormalPrice());
            cbod.setBefPrice(cbob.getCbob11());
            cbod.setBefQty(cbob.getCbob09());
            cbodMapper.insert(cbod);
            //??????????????? ??????????????????
            Double noneedNum = cbob.getCbob09() - cbod.getCbod09();
//            if (noneedNum != 0.0) {
//                GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
//                usex.createCriteria()
//                        .andGoodsIdEqualTo(good.getGoodsId())
//                        .andOrderNoEqualTo(cboa.getCboa17());
//                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//                for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
//                    gsGoodsUs.setLockQty(gsGoodsUs.getLockQty() - noneedNum);
//                    gsGoodsUs.setOrderQty(cbod.getCbod09());
//                    gsGoodsUs.setUpdateTime(date);
//                    if (gsGoodsUs.getLockQty() == 0.0) {
//                        gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());
//                    } else {
//                        gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
//                    }
//                }
//            }


        }
    }

    @Override
    public List<OrderChangeGoodsMsgVo> orderChangeGoodsMsg(OrderChangeGoodsMsgDto orderChangeGoodsMsgDto) {
        return cbobMapper.orderChangeGoodsMsg(orderChangeGoodsMsgDto);

    }

    @Transactional
    @Override
    public OrderChangeDetailVo orderChangeDetail(Integer orderId) {
        OrderChangeDetailVo res = new OrderChangeDetailVo();
        Cboc cboc = cbocMapper.selectByPrimaryKey(orderId);
        if (cboc == null || DeleteFlagEnum.DELETE.getCode().equals(cboc.getCboc06())) {
            throw new SwException("??????????????????");
        }
        res.setId(cboc.getCboc01());
        res.setAddress(cboc.getCboc18());
        res.setCustomerId(cboc.getCboc09());
        if (cboc.getCboc09() != null) {
            Cbca cbca = cbcaMapper.selectByPrimaryKey(cboc.getCboc09());
            if (cbca != null) {
                res.setCustomerName(cbca.getCbca08());
            }

//            SysUser customer = sysUserMapper.selectByPrimaryKey(cboa.getCboa09().longValue());
//            if(customer!=null){
//                res.setCustomerName(customer.getNickName());
//            }
        }
        res.setCustomerNo(cboc.getCboc25());
        Cbca cbca = cbcaMapper.selectByPrimaryKey(cboc.getCboc09());
        if (cbca != null) {

            res.setFpAdress(cbca.getCbca26());
            res.setFpbank(cbca.getCbca11());
            res.setFpNumber(cbca.getCbca12());
            res.setFpPhone(cbca.getCbca27());
            res.setFpReceiveAddress(cbca.getCbca25());
            res.setTaxpayerid(cbca.getCbca13());

        }
        res.setInvoiceType(cboc.getCboc22());
        SysUser createUser = sysUserMapper.selectByPrimaryKey(cboc.getCboc03().longValue());
        if (createUser != null) {
            res.setMakeUser(createUser.getNickName());
        }


        res.setOrderDate(cboc.getCboc08());
        res.setOrderChangeNo(cboc.getCboc07());
        res.setCurrency(cboc.getCboc16());

        Cboa cboa = cboaMapper.selectByPrimaryKey(cboc.getCboc26());
        if (cboa != null) {
            res.setOrderNo(cboa.getCboa07());
            res.setOrderId(cboa.getCboa01());
        }

        res.setOrderType(cboc.getCboc24());
        if (OrderTypeEnum.GUONEIDINGDAN.getCode().equals(cboa.getCboa27())) {
            res.setOrderClassMsg(OrderTypeEnum.GUONEIDINGDAN.getMsg());
        } else {
            res.setOrderClassMsg(OrderTypeEnum.GUOJIDINGDAN.getMsg());
        }

        //todo ?????????
//        res.setOther();
        res.setReceiveName(cboc.getCboc17());
        res.setReceivePhone(cboc.getCboc19());
        res.setFcNumber(cboc.getCboc20());
        res.setOther(cboc.getCboc21());
        SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboc.getCboc10().longValue());
        if (saleUser != null) {
            res.setSaleUser(saleUser.getNickName());
            res.setSaleUserId(saleUser.getUserId().intValue());
        }


        //????????????
        CbodCriteria example = new CbodCriteria();
        example.createCriteria()
                .andCboc01EqualTo(cboc.getCboc01());
        List<Cbod> cbods = cbodMapper.selectByExample(example);

        Double sunPrice = 0.0;
        Double sumQty = 0.0;

        List<SaleOrderChangeDetailGoods> goods = res.getGoods();
        SaleOrderChangeDetailGoods good = null;
        CalaCriteria laexample = new CalaCriteria();
        laexample.createCriteria()
                .andCala10EqualTo("????????????");
        List<Cala> calas = calaMapper.selectByExample(laexample);
        Map<Integer, String> brandMap = new HashMap<>();
        for (Cala cala : calas) {
            brandMap.put(cala.getCala01(), cala.getCala08());
        }


        for (Cbod cbod : cbods) {
            good = new SaleOrderChangeDetailGoods();
            good.setCbob01(cbod.getCbobid());
            good.setId(cbod.getCbod01());
            good.setGoodsId(cbod.getCbod08());
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbod.getCbod08());
            if (cbpb != null) {
                good.setBrand(brandMap.get(cbpb.getCbpb10()));
                good.setDescription(cbpb.getCbpb08());
                good.setModel(cbpb.getCbpb12());

            }
            good.setCurrentPrice(cbod.getCbod11());
            good.setNormPrice(cbod.getCbod14());
            good.setRemark(cbod.getCbod13());
            good.setQty(cbod.getCbod09());
            good.setTotalPrice(cbod.getCbod12());
            good.setBfPrice(cbod.getBefPrice());
            good.setBfQty(cbod.getBefQty());
            //?????????
            CheckSkuDo chekDo = new CheckSkuDo();
            chekDo.setGoodsId(good.getGoodsId());
            chekDo.setOrderClass(cboa.getCboa27());
            QtyMsgVo qtyMsgVo = orderDistributionService.checkSku(chekDo);
            good.setCanUseSku(qtyMsgVo.getCanUseNum());

            sunPrice = sunPrice + cbod.getCbod12();
            sumQty = sumQty + cbod.getCbod09();
            res.getGoods().add(good);
        }
        //??????????????????

        res.setSumPrice(sunPrice);
        res.setSumQty(sumQty);
        res.setCapPrice(NumberToChineseUtil.moneyToChinese(res.getSumPrice()));

        //????????????
        CabraaCriteria raaexample = new CabraaCriteria();
        raaexample.createCriteria()
                .andCabraa14EqualTo(cboc.getCboc07());
        List<Cabraa> cabraas = cabraaMapper.selectByExample(raaexample);

        SaleOrderAudit saleOrderAudit = null;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Cabraa cabraa : cabraas) {
            saleOrderAudit = new SaleOrderAudit();
            saleOrderAudit.setAuditResult(cabraa.getCabraa18());
            String createTime = sd.format(cabraa.getCabraa15());
            SysUser auditUser = sysUserMapper.selectByPrimaryKey(cabraa.getCabraa04().longValue());
            String audit = null;
            if (auditUser != null) {
                audit = auditUser.getNickName() == null ? "" : auditUser.getNickName();
            }

            saleOrderAudit.setDescription(createTime + " ??? " + audit + " ??????");
            saleOrderAudit.setId(cabraa.getCabraa01());
            saleOrderAudit.setRole(cabraa.getCabraa19());
            res.getAudits().add(saleOrderAudit);
        }

        return res;

    }

    @Transactional
    @Override
    public void mdfSaleOrderChange(SaleOrderChangeDto saleOrderChangeDto) {


        Cboc cboc = cbocMapper.selectByPrimaryKey(saleOrderChangeDto.getId());
        if (cboc == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboc.getCboc06())) {
            throw new SwException("??????????????????????????????");
        }

        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboc.getCboc11())) {
            throw new SwException("??????????????????????????????????????????");
        }

        Cboa cboa = cboaMapper.selectByPrimaryKey(cboc.getCboc26());


        //???????????????????????????????????????
        if (!cboc.getCboc10().equals(saleOrderChangeDto.getUserId())) {
            throw new SwException("???????????????????????????");
        }

        Date date = new Date();

        cboc.setCboc04(date);
        cboc.setCboc05(saleOrderChangeDto.getUserId());
        cboc.setCboc06(DeleteFlagEnum.NOT_DELETE.getCode());


        cboc.setCboc08(saleOrderChangeDto.getOrderDate());
        cboc.setCboc09(saleOrderChangeDto.getCustomerId());
        cboc.setCboc10(saleOrderChangeDto.getSaleUserId());
        cboc.setCboc11(AuditStatusConstants.SO_COMMIT);

        cboc.setCboc16(saleOrderChangeDto.getCurrency());
        cboc.setCboc17(saleOrderChangeDto.getReceiveName());
        cboc.setCboc18(saleOrderChangeDto.getAddress());
        cboc.setCboc19(saleOrderChangeDto.getReceivePhone());
        cboc.setCboc22(saleOrderChangeDto.getInvoiceType());
        cboc.setCboc24(10);
        cboc.setCboc25(saleOrderChangeDto.getCustomerNo());
        cboc.setCboc26(cboa.getCboa01());
        cbocMapper.updateByPrimaryKey(cboc);

        //????????????????????????


//
//        cboc.setCboc04(date);
//        cboc.setCboc05(saleOrderChangeDto.getUserId());
//
//        int insert = cbocMapper.insert(cboc);
        Cbod cbod = null;
        //???????????????
        CbodCriteria example = new CbodCriteria();
        example.createCriteria()
                .andCboc01EqualTo(cboc.getCboc01());
        int i = cbodMapper.deleteByExample(example);

        for (SaleOrderChangeGoodsDto good : saleOrderChangeDto.getGoods()) {

            //????????????????????????
            CbobCriteria obexample = new CbobCriteria();
            obexample.createCriteria()
                    .andCbob08EqualTo(good.getGoodsId())
                    .andCboa01EqualTo(cboc.getCboc26())
                    .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbob> cbobs = cbobMapper.selectByExample(obexample);
            if (cbobs.size() == 0) {
                throw new SwException("???????????????????????????????????????:" + good.getGoodsId());
            }
            //?????????????????????????????? ????????????????????????
            Cbob cbob = cbobs.get(0);
            if (good.getQty() > cbob.getCbob09()) {
                throw new SwException("??????????????????????????????");
            }

            if (good.getQty() < cbob.getCbob10()) {
                throw new SwException("??????????????????????????????????????????");
            }

            cbod = new Cbod();
            cbod.setCbobid(cbob.getCbob01());
            cbod.setCbod02(good.getNumber());
            cbod.setCbod03(date);
            cbod.setCbod04(saleOrderChangeDto.getUserId());
            cbod.setCbod05(date);
            cbod.setCbod06(saleOrderChangeDto.getUserId());
            cbod.setCbod07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbod.setCbod08(good.getGoodsId());
            cbod.setCbod09(good.getQty());
            cbod.setCbod10(cbob.getCbob10());
            cbod.setCbod11(good.getCurrentPrice());
            cbod.setCbod12(good.getCurrentPrice() * good.getQty());
            cbod.setCbod13(good.getRemark());

            cbod.setCbod14(good.getNormalPrice());
            cbod.setBefPrice(cbob.getCbob11());
            cbod.setCboc01(cboc.getCboc01());
            cbod.setBefQty(cbob.getCbob09());
            cbodMapper.insert(cbod);


        }
        cboc.setCboc11(SaleOrderStatusEnums.YITIJIAO.getCode());
        cboc.setCboc04(date);
        cbocMapper.updateByPrimaryKey(cboc);
    }

    @Transactional
    @Override
    public void auditSaleChange(AuditSaleOrderDto auditSaleOrderDto) {
        Cboc cboc = cbocMapper.selectByPrimaryKey(auditSaleOrderDto.getOrderId());
        if (cboc == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboc.getCboc06())) {
            throw new SwException("??????????????????????????????");
        }
        Date date = new Date();
        cboc.setCboc04(date);
        cboc.setCboc05(auditSaleOrderDto.getUserId());

        if (auditSaleOrderDto.getOpeateType() == 1) {
            //??????
            if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboc.getCboc11())) {
                throw new SwException("???????????????????????????????????????");
            }


            cboc.setCboc11(SaleOrderStatusEnums.YITIJIAO.getCode());
            cbocMapper.updateByPrimaryKey(cboc);
            //?????????????????????
//            GsWorkInstanceDo goodsWorkInstanceDo = new GsWorkInstanceDo();
//            goodsWorkInstanceDo.setOrderType((byte) 2);
//            goodsWorkInstanceDo.setOrderClose(OrdercloseEnum.WEIJIESHU.getCode());
//            goodsWorkInstanceDo.setOrderStatus(OrderstatusEnum.DAISHENPI.getCode());
//            taskService.editGsWorkInstance(goodsWorkInstanceDo);

        } else if (auditSaleOrderDto.getOpeateType() == 2) {
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cboc.getCboc11())) {
                throw new SwException("???????????????????????????????????????");
            }
            cboc.setCboc11(SaleOrderStatusEnums.WEITIJIAO.getCode());
            cbocMapper.updateByPrimaryKey(cboc);
        } else if (auditSaleOrderDto.getOpeateType() == 3) {
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cboc.getCboc11())) {
                throw new SwException("???????????????????????????????????????");
            }
            baseCheckService.checkUserTask(auditSaleOrderDto.getUserId().longValue(), new String("12"));

            cboc.setCboc11(SaleOrderStatusEnums.YISHENHE.getCode());
            cboc.setCboc13(new Date());
            cbocMapper.updateByPrimaryKey(cboc);
            Cboa cboa = cboaMapper.selectByPrimaryKey(cboc.getCboc26());
            if (cboa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())) {
                throw new SwException("???????????????????????????????????????");
            }
            CbodCriteria odex = new CbodCriteria();
            odex.createCriteria()
                    .andCboc01EqualTo(cboc.getCboc01());
            List<Cbod> cbods = cbodMapper.selectByExample(odex);
            for (Cbod cbod : cbods) {
//                CbobCriteria obexample = new CbobCriteria();
//                obexample.createCriteria()
//                        .andCbob08EqualTo(cbod.getCbod08())
//                        .andCboa01EqualTo(cboc.getCboc26())
//                        .andCbob06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                if(cbod.getCbobid()==null){
                    throw new SwException("??????????????????????????????????????????????????????");

                }
                Cbob cbob = cbobMapper.selectByPrimaryKey(cbod.getCbobid());
                if (cbob==null) {
                    throw new SwException("???????????????????????????????????????:" + cbod.getCbod08());
                }
                //?????????????????????????????? ????????????????????????


                Double noneedNum = cbob.getCbob09() - cbod.getCbod09();
                if (noneedNum != 0.0) {
                    GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
                    usex.createCriteria()
                            .andGoodsIdEqualTo(cbod.getCbod08());
                    usex.setOrderByClause("wh_id desc");
//                            .andOrderNoEqualTo(cboa.getCboa07());
                    List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
                    for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                        if(noneedNum==0){
                            break;
                        }
                        if(gsGoodsUs.getWhId().equals(WareHouseType.GQWWHID)){
                            if(cbob.getCbob17()!=null){
                                Cbba cbba = cbbaMapper.selectByPrimaryKey(cbob.getCbob17());

                                cbba.setCbba14(cbba.getCbba14()-noneedNum);
                                cbba.setCbba04(date);
                                cbbaMapper.updateByPrimaryKey(cbba);
                            }else {
                                CbbaCriteria baex = new CbbaCriteria();
                                baex.createCriteria()
                                        .andCbba08EqualTo(cbob.getCbob08())
                                        .andCbba12EqualTo(TotalOrderConstants.NO)
                                        .andCbba07Like("%"+"GBSH" + "%");
                                baex.setOrderByClause("CBBA15 asc");
                                List<Cbba> cbbas = cbbaMapper.selectByExample(baex);
                                for (Cbba cbba : cbbas) {
                                    if(cbba.getCbba14()>noneedNum){
                                        cbba.setCbba14(cbba.getCbba14()-noneedNum);
                                        cbba.setCbba04(date);
                                        noneedNum=0.0;
                                        cbbaMapper.updateByPrimaryKey(cbba);
                                    }else {
                                        noneedNum=cbba.getCbba14()-noneedNum;
                                        cbba.setCbba14(0.0);
                                        cbba.setCbba04(date);

                                        cbbaMapper.updateByPrimaryKey(cbba);
                                    }
                                }
                            }
                        }

                        GsOutStockAdivceCriteria adex=new GsOutStockAdivceCriteria();
                        adex.createCriteria()
                                .andGoodsIdEqualTo(gsGoodsUs.getGoodsId())
                                .andWhIdEqualTo(gsGoodsUs.getWhId())
                                .andSaleOrderNoEqualTo(cboa.getCboa07());
                        List<GsOutStockAdivce> gsOutStockAdivces = gsOutStockAdivceMapper.selectByExample(adex);
                        Double lockQty=0.0;
                        if(noneedNum>=gsGoodsUs.getLockQty()){
                            noneedNum=noneedNum-gsGoodsUs.getLockQty();
                            gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());
                            for (GsOutStockAdivce gsOutStockAdivce : gsOutStockAdivces) {
                                gsOutStockAdivce.setUpdateTime(date);
                                gsOutStockAdivce.setDeleteFlag(DeleteFlagEnum.DELETE.getCode().byteValue());
                                gsOutStockAdivceMapper.updateByPrimaryKey(gsOutStockAdivce);

                            }
                        }else {
                            lockQty=gsGoodsUs.getLockQty()-noneedNum;
                            noneedNum=0.0;
                            gsGoodsUs.setLockQty(lockQty);
                            gsGoodsUs.setOrderQty(cbod.getCbod09());
                            gsGoodsUs.setUpdateTime(date);
                            gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
                            if(gsOutStockAdivces.size()>0){
                                GsOutStockAdivce gsOutStockAdivce = gsOutStockAdivces.get(0);
                                gsOutStockAdivce.setUpdateTime(date);
                                gsOutStockAdivce.setQty(lockQty);
                                gsOutStockAdivceMapper.updateByPrimaryKey(gsOutStockAdivce);
                            }

                        }


                    }


                }
                //??????cbob?????????

                cbob.setCbob12(cbod.getCbod12());
                cbob.setCbob05(date);
                cbob.setCbob06(auditSaleOrderDto.getUserId());
                cbob.setCbob09(cbod.getCbod09());
                cbob.setCbob11(cbod.getCbod11());
                cbob.setCbob12(cbob.getCbob12());
                cbobMapper.updateByPrimaryKey(cbob);


            }


        }

    }

    @Transactional
    @Override
    public void delSaleChange(DelSaleChangeDto delSaleChangeDto) {

        Cboc cboc = cbocMapper.selectByPrimaryKey(delSaleChangeDto.getId());
        if (cboc == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboc.getCboc06())) {
            throw new SwException("??????????????????????????????");
        }
        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cboc.getCboc11())) {
            throw new SwException("???????????????????????????????????????");
        }

        cboc.setCboc06(DeleteFlagEnum.DELETE.getCode());
        cbocMapper.updateByPrimaryKey(cboc);
        Cbod cbod = new Cbod();
        cbod.setCbod07(DeleteFlagEnum.DELETE.getCode());

        CbodCriteria example = new CbodCriteria();
        example.createCriteria()
                .andCboc01EqualTo(cboc.getCboc01());
        int i = cbodMapper.updateByExampleSelective(cbod, example);

    }

    @Override
    public void printSaleOrder(Integer sqleOrderId) {

    }

    @Override
    public TotalOrderVo totalOrderDetail(Integer id) {
        Map<Integer, String> brandMap = baseCheckService.brandMap();
        Cbba cbba = cbbaMapper.selectByPrimaryKey(id);
        if(cbba==null){
            throw new SwException("??????????????????????????????");
        }
        TotalOrderVo res = new TotalOrderVo();
        res.setGoodsId(cbba.getCbba08());
        res.setId(cbba.getCbba01());
        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbba.getCbba08());
        if (cbpb != null) {

            if (brandMap.get(cbpb.getCbpb10()) != null) {
                res.setGoods(brandMap.get(cbpb.getCbpb10()) + "-" + cbpb.getCbpb12() + "-" + cbpb.getCbpb08());

            }
        }

        res.setOrderNo(cbba.getCbba07());
        res.setPriority(cbba.getCbba15());
        res.setQty(cbba.getCbba09());
        return res;
    }

//    @Transactional
//    @Override
//    public void updateGjQty(UpdateGjQtyDto updateGjQtyDto) {
//
//        Cbob cbob = cbobMapper.selectByPrimaryKey(updateGjQtyDto.getId());
//        Cboa cboa = cboaMapper.selectByPrimaryKey(cbob.getCboa01());
//        if(new Byte("1").equals(cboa.getConfirmSkuStatus())){
//            throw new SwException("??????????????????????????????????????????");
//        }
//        if (cbob == null) {
//            throw new SwException("???????????????????????????");
//        }
//        if (updateGjQtyDto.getQty() > cbob.getCbob09()) {
//            throw new SwException("??????????????????????????????????????????");
//        }
//
//
//        //???????????????????????????
//        Date date = new Date();
//        Double confirmQty = cbob.getConfirmQty();
//        Cbba cbba = cbbaMapper.selectByPrimaryKey(cbob.getCbob17());
//        if(cbba==null){
//            throw new SwException("??????????????????????????????");
//        }
//        List<GsGoodsUse> list = gsGoodsUseMapper.selectLockByTotalOrderNo(cbob.getCbob18(),cbob.getCbob08());
//        double lockQty = list.stream().mapToDouble(GsGoodsUse::getLockQty).sum();
//        double canUse=cbba.getCbba13()-lockQty;
//
//
//        if (confirmQty == null || confirmQty == 0.0) {
//            if(updateGjQtyDto.getQty()>canUse){
//                throw new SwException("??????????????????????????????????????????");
//            }
//
//            confirmQty = 0.0;
//
//            //????????????
//            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
//            usex.createCriteria()
//                    .andGoodsIdEqualTo(updateGjQtyDto.getGoodsId())
//                    .andOrderNoEqualTo(updateGjQtyDto.getSaleOrderNo());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//
//
//            if (gsGoodsUses.size() > 0) {
//                GsGoodsUse goodsUse = gsGoodsUses.get(0);
//                goodsUse.setLockQty(goodsUse.getLockQty() + updateGjQtyDto.getQty());
//                goodsUse.setUpdateTime(date);
//                gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
//            } else {
//                GsGoodsUse goodsUse = new GsGoodsUse();
//                goodsUse.setUpdateTime(date);
//                goodsUse.setLockQty(updateGjQtyDto.getQty());
//                goodsUse.setNoOutQty(0.0);
//                goodsUse.setOrderQty(cbob.getCbob09());
//                goodsUse.setCreateBy(updateGjQtyDto.getUserId());
//                goodsUse.setCreateTime(date);
//                goodsUse.setGoodsId(updateGjQtyDto.getGoodsId());
//                goodsUse.setOrderNo(updateGjQtyDto.getSaleOrderNo());
//                goodsUse.setOrderType(new Byte("2"));
//                goodsUse.setWhId(WareHouseType.GQWWHID);
//                goodsUse.setUpdateBy(updateGjQtyDto.getUserId());
//                gsGoodsUseMapper.insert(goodsUse);
//            }
//        } else {
//
//            //????????????
//             Double qty = updateGjQtyDto.getQty();
//            //????????????
//            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
//            usex.createCriteria()
//                    .andGoodsIdEqualTo(updateGjQtyDto.getGoodsId())
//                    .andOrderNoEqualTo(updateGjQtyDto.getSaleOrderNo());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//            GsGoodsUse goodsUse = gsGoodsUses.get(0);
//            Double mdfqty=0.0;
//            if(qty>confirmQty){
//                 //????????????
//                 mdfqty=qty-confirmQty;
//                goodsUse.setLockQty(goodsUse.getLockQty() +mdfqty);
//                if(mdfqty>canUse){
//                    throw new SwException("??????????????????????????????????????????");
//                }
//
//
//             }else {
//                 //????????????
//                 mdfqty=confirmQty-qty;
//                goodsUse.setLockQty(goodsUse.getLockQty() -mdfqty);
//             }
//
//
//
//            goodsUse.setUpdateTime(date);
//            gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
//        }
//
//
//        //cbob????????????????????????
//        cbob.setConfirmQty(updateGjQtyDto.getQty());
//        cbob.setCbob05(date);
//        cbobMapper.updateByPrimaryKey(cbob);
//
//    }

    @Transactional
    @Override
    public void updateGjQty(UpdateGjQtyDto updateGjQtyDto) {

        Cbob cbob = cbobMapper.selectByPrimaryKey(updateGjQtyDto.getId());
        Cboa cboa = cboaMapper.selectByPrimaryKey(cbob.getCboa01());
        if(new Byte("1").equals(cboa.getConfirmSkuStatus())){
            throw new SwException("??????????????????????????????????????????");
        }
        if (cbob == null) {
            throw new SwException("???????????????????????????");
        }
        if (updateGjQtyDto.getQty() > cbob.getCbob09()) {
            throw new SwException("??????????????????????????????????????????");
        }


        //???????????????????????????
        Date date = new Date();
        Double confirmQty = cbob.getConfirmQty();
        Cbba cbba = cbbaMapper.selectByPrimaryKey(cbob.getCbob17());
        if(cbba==null){
            throw new SwException("??????????????????????????????");
        }
        List<GsGoodsUse> list = gsGoodsUseMapper.selectLockByTotalOrderNo(cbob.getCbob18(),cbob.getCbob08());
        double lockQty = list.stream().mapToDouble(GsGoodsUse::getLockQty).sum();
        double canUse=cbba.getCbba13()-lockQty;


        if (confirmQty == null || confirmQty == 0.0) {
            if(updateGjQtyDto.getQty()>canUse){
                throw new SwException("??????????????????????????????????????????");
            }

            confirmQty = 0.0;

            //????????????
            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
            usex.createCriteria()
                    .andGoodsIdEqualTo(updateGjQtyDto.getGoodsId())
                    .andOrderNoEqualTo(updateGjQtyDto.getSaleOrderNo());
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);


            if (gsGoodsUses.size() > 0) {
                GsGoodsUse goodsUse = gsGoodsUses.get(0);
                goodsUse.setLockQty(goodsUse.getLockQty() + updateGjQtyDto.getQty());
                goodsUse.setUpdateTime(date);
                gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
            } else {
                GsGoodsUse goodsUse = new GsGoodsUse();
                goodsUse.setUpdateTime(date);
                goodsUse.setLockQty(updateGjQtyDto.getQty());
                goodsUse.setNoOutQty(0.0);
                goodsUse.setOrderQty(cbob.getCbob09());
                goodsUse.setCreateBy(updateGjQtyDto.getUserId());
                goodsUse.setCreateTime(date);
                goodsUse.setGoodsId(updateGjQtyDto.getGoodsId());
                goodsUse.setOrderNo(updateGjQtyDto.getSaleOrderNo());
                goodsUse.setOrderType(new Byte("2"));
                goodsUse.setWhId(WareHouseType.GQWWHID);
                goodsUse.setUpdateBy(updateGjQtyDto.getUserId());
                gsGoodsUseMapper.insert(goodsUse);
            }


        }
        else {

            //????????????
            Double qty = updateGjQtyDto.getQty();
            //????????????
            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
            usex.createCriteria()
                    .andGoodsIdEqualTo(updateGjQtyDto.getGoodsId())
                    .andOrderNoEqualTo(updateGjQtyDto.getSaleOrderNo());
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
            GsGoodsUse goodsUse = gsGoodsUses.get(0);
            Double mdfqty=0.0;
            if(qty>confirmQty){
                //????????????
                mdfqty=qty-confirmQty;
                goodsUse.setLockQty(goodsUse.getLockQty() +mdfqty);
                if(mdfqty>canUse){
                    throw new SwException("??????????????????????????????????????????");
                }


            }else {
                //????????????
                mdfqty=confirmQty-qty;
                goodsUse.setLockQty(goodsUse.getLockQty() -mdfqty);
            }



            goodsUse.setUpdateTime(date);
            if(goodsUse.getLockQty()==0){
                gsGoodsUseMapper.deleteByPrimaryKey(goodsUse.getId());
            }else {
                gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
            }

            GsOutStockAdivceCriteria adviceex=new GsOutStockAdivceCriteria();
            adviceex.createCriteria()
                    .andSaleOrderNoEqualTo(cboa.getCboa07())
                    .andGoodsIdEqualTo(updateGjQtyDto.getGoodsId());
            int i = gsOutStockAdivceMapper.deleteByExample(adviceex);

        }


        //cbob????????????????????????
        cbob.setConfirmQty(updateGjQtyDto.getQty());
        cbob.setCbob05(date);
        GsOutStockAdivce gsOutStockAdivce=new GsOutStockAdivce();
        gsOutStockAdivce.setCreateBy(updateGjQtyDto.getUserId());
        gsOutStockAdivce.setCreateTime(date);
        gsOutStockAdivce.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
        gsOutStockAdivce.setGoodsId(cbob.getCbob08());
        gsOutStockAdivce.setQty(updateGjQtyDto.getQty());
        gsOutStockAdivce.setSaleOrderNo(cboa.getCboa07());
        gsOutStockAdivce.setStatus(new Byte("3"));
        gsOutStockAdivce.setWhId(WareHouseType.GQWWHID);
        gsOutStockAdivceMapper.insert(gsOutStockAdivce);
        cbobMapper.updateByPrimaryKey(cbob);

    }

    @Override
    @Transactional
    public void confirmSkuSaleOrder(ConfirmSkuDto confirmSkuDto) {

        Cboa cboa = cboaMapper.selectByPrimaryKey(confirmSkuDto.getId());

        if(cboa==null){
            throw new SwException("???????????????????????????");
        }



        if(confirmSkuDto.getOpearte()==1){
            if(cboa.getConfirmSkuStatus()==1){
                throw new SwException("???????????????????????????");
            }
            cboa.setConfirmSkuStatus(new Byte("1"));

            //??????????????????
            CbobCriteria obex=new CbobCriteria();
            obex.createCriteria()
                    .andCboa01EqualTo(cboa.getCboa01());
            List<Cbob> cbobs = cbobMapper.selectByExample(obex);
            Date date = new Date();

            for (Cbob cbob : cbobs) {

                GsOutStockAdivce gsOutStockAdivce=new GsOutStockAdivce();


                gsOutStockAdivce.setCreateBy(confirmSkuDto.getUserId());
                gsOutStockAdivce.setCreateTime(date);
                gsOutStockAdivce.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
                gsOutStockAdivce.setGoodsId(cbob.getCbob08());
                gsOutStockAdivce.setQty(cbob.getConfirmQty());
                gsOutStockAdivce.setSaleOrderNo(cboa.getCboa07());
                gsOutStockAdivce.setStatus(new Byte("3"));
                gsOutStockAdivce.setWhId(WareHouseType.GQWWHID);
                gsOutStockAdivceMapper.insert(gsOutStockAdivce);
        }


        }else {
            //?????????????????????????????????
            Cbpk cbpk = cbpkMapper.selectLastBySaleOrderNo(cboa.getCboa07());
            if(cbpk!=null){
                throw new SwException("?????????????????????????????????????????????");

            }
            cboa.setConfirmSkuStatus(new Byte("2"));
            GsOutStockAdivceCriteria example=new GsOutStockAdivceCriteria();
            example.createCriteria()
                    .andSaleOrderNoEqualTo(cboa.getCboa07());
            int i = gsOutStockAdivceMapper.deleteByExample(example);


        }

        cboa.setCboa04(new Date());
        cboa.setCboa05(confirmSkuDto.getUserId());
        cboaMapper.updateByPrimaryKey(cboa);





    }

    @Override
    public List<BaseSelectVo> orderlistSelect(BaseSelectDto baseSelectDto) {
        return cboaMapper.orderlistSelect(baseSelectDto);
    }

    @Override
    public List<BaseSelectVo> orderChangeGoodsSelect(BaseSelectDto baseSelectDto) {
            List<OrderChangeGoodsVO> list= cbodMapper.orderChangeGoodsSelect(baseSelectDto);
            BaseSelectVo selectVo=new BaseSelectVo();;
            List<BaseSelectVo> res=new ArrayList<>();
        for (OrderChangeGoodsVO orderChangeGoodsVO : list) {
            selectVo= new BaseSelectVo();
            selectVo.setValue(orderChangeGoodsVO.getCbobId());
            if(orderChangeGoodsVO!=null){
                selectVo.setLabel(orderChangeGoodsVO.getBrand()+"-"+orderChangeGoodsVO.getModel()+"-"+orderChangeGoodsVO.getDescription());
            }
            res.add(selectVo);


        }

        return res;


    }

    @Override
    public GoodsPriceAndSkuVo goodsPriceAndSkuByOrderChange(GoodsPriceAndSkuDto goodsPriceAndSkuDto) {
        Cbob cbob = cbobMapper.selectByPrimaryKey(goodsPriceAndSkuDto.getCbobId());
        if(cbob==null){
            throw new SwException("????????????????????????????????????");
        }
        goodsPriceAndSkuDto.setGoodsId(cbob.getCbob08());

        GoodsPriceAndSkuVo goodsPriceAndSkuVo = goodsPriceAndSku(goodsPriceAndSkuDto);

        //??????
        if(cbob!=null){
            goodsPriceAndSkuVo.setPrice(cbob.getCbob11());
            goodsPriceAndSkuVo.setQty(cbob.getCbob09());
            goodsPriceAndSkuVo.setTotalPrice(cbob.getCbob12());
        }

        return goodsPriceAndSkuVo;
    }

    @Override
    public void insertgoodsShop(Integer goodsId) {

        baseCheckService.checkGoods(goodsId);

        Long userid = SecurityUtils.getUserId();

        GsSaleShoppingCriteria example=new GsSaleShoppingCriteria();
        example.createCriteria()
                .andGoodsIdEqualTo(goodsId)
                .andUserIdEqualTo(Math.toIntExact(userid));
        List<GsSaleShopping> gsGoodsShops = gsSaleShoppingMapper.selectByExample(example);
        if(gsGoodsShops.size()==0){
            GsSaleShopping gsGoodsShop=new GsSaleShopping();
            Date date=new Date();
            gsGoodsShop.setCreateTime(date);
            gsGoodsShop.setUpdateTime(date);
            gsGoodsShop.setCreateBy(Math.toIntExact(userid));
            gsGoodsShop.setUpdateBy(Math.toIntExact(userid));
            gsGoodsShop.setUserId(Math.toIntExact(userid));
            gsGoodsShop.setGoodsId(goodsId);
            gsGoodsShop.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());


            gsSaleShoppingMapper.insertSelective(gsGoodsShop);
        }
        return;

    }

    @Override
    public List<GoodsDetailAndSkuVo> goodsShopLists(List<GoodsPriceAndSkuDto> itemList) {
        List<GoodsDetailAndSkuVo> res=new ArrayList<>();
        if(itemList.size()==0){
            throw new SwException("???????????????????????????");
        }
        if(itemList.get(0).getCustomerId()==null){
            throw new SwException("???????????????");
        }
        if(itemList.get(0).getOrderClass()==null){
            throw new SwException("??????????????????????????????????????????");
        }
        Integer orderClass = itemList.get(0).getOrderClass();
        Integer customerId = itemList.get(0).getCustomerId();
        for (int i=0;i<itemList.size();i++) {
            itemList.get(i).setCustomerId(customerId);
            itemList.get(i).setOrderClass(orderClass);

            GoodsPriceAndSkuVo goodsPriceAndSkuVo = this.goodsPriceAndSku(itemList.get(i));
            if(goodsPriceAndSkuVo.getGoodsId()==null){
                throw new SwException("?????????????????????");
            }
            Integer goodsId = goodsPriceAndSkuVo.getGoodsId();
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsId);
            if(cbpb==null){
                throw new SwException("?????????????????????");
            }
            if(cbpb.getCbpb10()==null){
                throw new SwException("???????????????id??????");
            }
            Integer cbpb10 = cbpb.getCbpb10();

            Cala cala = calaMapper.selectByPrimaryKey(cbpb10);
            if(cala==null){
                throw new SwException("???????????????????????????");
            }
            if(goodsPriceAndSkuVo.getNormalPrice()==null){
                throw new SwException("??????????????????????????????");
            }
            if(goodsPriceAndSkuVo.getCkSku()==null){
                throw new SwException("??????????????????????????????");
            }
            if(goodsPriceAndSkuVo.getCanUseSku()==null){
                throw new SwException("????????????????????????????????????");
            }
            Double canUseSku = goodsPriceAndSkuVo.getCanUseSku();
            Double ckSku = goodsPriceAndSkuVo.getCkSku();
            Double normalPrice = goodsPriceAndSkuVo.getNormalPrice();
            GoodsDetailAndSkuVo goodsDetailAndSkuVo=new GoodsDetailAndSkuVo();
            goodsDetailAndSkuVo.setGoodsId(goodsId);
            goodsDetailAndSkuVo.setGoodsBrand(cala.getCala08());
            goodsDetailAndSkuVo.setGoodsModel(cbpb.getCbpb12());
            goodsDetailAndSkuVo.setGoodsdatail(cbpb.getCbpb08());
            goodsDetailAndSkuVo.setGoodsPrice(normalPrice);
            goodsDetailAndSkuVo.setCkgoodsStock(ckSku);
            goodsDetailAndSkuVo.setGoodsStock(canUseSku);
           res.add(goodsDetailAndSkuVo);
        }
        return res;
    }

    @Override
    public void delgoodsShop(DelSaleOrderDto delSaleOrderDto) {
        Long userid = SecurityUtils.getUserId();
        Date date=new Date();
        GsSaleShopping gsSaleShopping=new GsSaleShopping();
        gsSaleShopping.setId(delSaleOrderDto.getOrderId());
        gsSaleShopping.setUpdateBy(Math.toIntExact(userid));
        gsSaleShopping.setUpdateTime(date);
        gsSaleShopping.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());
        gsSaleShoppingMapper.updateByPrimaryKeySelective(gsSaleShopping);

        return;
    }

    @Override
    public void batchDelgoodsShop(List<Integer> ids,Integer userId) {
        GsSaleShoppingCriteria criteria = new GsSaleShoppingCriteria();
        criteria.createCriteria().andIdIn(ids);
        GsSaleShopping gsSaleShopping=new GsSaleShopping();
        Date date=new Date();
        gsSaleShopping.setUpdateBy(Math.toIntExact(userId));
        gsSaleShopping.setUpdateTime(date);
        gsSaleShopping.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());
        gsSaleShoppingMapper.updateByExampleSelective(gsSaleShopping,criteria);
    }


    @Override
    public void initSn() {
        CbdaCriteria exampl=new CbdaCriteria();
        List<Cbda> cbods = cbdaMapper.selectByExample(exampl);
        for (Cbda cbda : cbods) {

            GsGoodsSn gs=new GsGoodsSn();
            gs.setGoodsId(cbda.getCbda08());
            gs.setGroudStatus(new Byte("1"));
            gs.setSn(cbda.getCbda09());
            gs.setStatus(new Byte("1"));
            gs.setCreateTime(cbda.getCbda03());
            Cbla cbla = cblaMapper.selectByPrimaryKey(cbda.getCbda10());
            if(cbla!=null){
                gs.setWhId(cbla.getCbla10());
            }


            gs.setLocationId(cbda.getCbda10());
            gsGoodsSnMapper.insert(gs);
        }
    }

    @Override
    public Cbba auditTotalOrder(TotalOrderAddDto totalOrderAddDto) {
//        Cbba cbba = cbbaMapper.selectByPrimaryKey(totalOrderAddDto.getId());
//        if(cbba==null){
//            throw new SwException("???????????????????????????");
//        }
        return null;

    }

    @Override
    @Transactional
    public void pldelSaleOrder(DelSaleOrderDto delSaleOrderDto) {
//        List<Integer> orderIds = delSaleOrderDto.getOrderIds();
//        DelSaleOrderDto desSend=null;
//        for (Integer orderId : orderIds) {
//            desSend=new DelSaleOrderDto();
//            desSend.setOrderId(orderId);
//            desSend.setUserId(delSaleOrderDto.getUserId());
//            saleOrddelSaleOrder(desSend);
//        }

    }

    @Override
    public List<SaleOrderListVo> saleOrderListPj(SaleOrderListDto saleOrderListDto) {

        return cboaMapper.saleOrderListPj(saleOrderListDto);
    }

    @Override
    public List<SaleOrderListVo> saleOrderListGoods(SaleOrderListDto saleOrderListDto) {

        return cboaMapper.saleOrderListGoods(saleOrderListDto);
    }

    @Override
    public DelTotalOrderDo getPlTotalOrderDeleIds(List<Integer> ids) {
        DelTotalOrderDo delTotalOrderDo=new DelTotalOrderDo();
        List<Integer> plIds=new ArrayList<>();
        List<Integer> singids=new ArrayList<>();
        for (Integer id : ids) {

            Cbba cbba = cbbaMapper.selectByPrimaryKeyForUpdate(id);
            if (cbba == null) {
                throw new SwException("??????????????????????????????");
            }
            Double useNum = 0.0;
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByTotalOrderNo(cbba.getCbba08(),cbba.getCbba07());
            useNum = gsGoodsUses.stream().collect(Collectors.summingDouble(GsGoodsUse::getLockQty));


//            if (gsGoodsUses.size() > 0) {
//                useNum = gsGoodsUses.get(0).getLockQty();
//            }

            if (cbba.getCbba11() != 0.0) {
                throw new SwException("??????????????????????????????????????????,?????????:"+cbba.getCbba07());
            }

            if (useNum != 0.0) {
                throw new SwException("??????????????????????????????????????????,?????????:"+cbba.getCbba07());
            }
            CbbaCriteria examp=new CbbaCriteria();
            examp.createCriteria()
                    .andCbba08EqualTo(cbba.getCbba08())
                    .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                    .andCbba01NotEqualTo(id);
            List<Cbba> cbbas = cbbaMapper.selectByExample(examp);
            if(cbbas.size()==0 && cbba.getCbba13() == 0.0) {
                plIds.add(id);
            }else {
                singids.add(id);
            }
        }
    delTotalOrderDo.setPlids(plIds);
        delTotalOrderDo.setSingids(singids);
        return delTotalOrderDo;






    }

    @Override
    public List<Integer> plDelete(List<Integer> ids) {

        List<Cbba> list= cbbaMapper.selectByIds(ids);
        List<Integer> data=new ArrayList<>();
        List<Integer> returndata=new ArrayList<>();
        for (Cbba cbba : list) {
            if(cbba.getCbba13()==0 &&cbba.getCbba11()==0){
                   data.add(cbba.getCbba01());
            }else {
                returndata.add(cbba.getCbba01());
            }
        }
        if(data.size()!=0){
            cbbaMapper.plDeleByIds(data);
        }




        return returndata;

    }

    @Override
    public void getChongfuUse() {
        GsGoodsUseCriteria example=new GsGoodsUseCriteria();
        List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(example);

        Map<String,Integer> map=new HashMap<>();
        List<Integer> list=new ArrayList<>();
        for (GsGoodsUse use : gsGoodsUses) {
            if(map.containsKey(use.getGoodsId()+"??????"+use.getWhId())){
                list.add(use.getGoodsId());
            }else {
                map.put(use.getGoodsId()+"??????"+use.getWhId(),1);
            }

        }

        return;
    }

    @Override
    public void getDayuUse() {
//        cbobMapper.selectAll
    }

    @Override
    public List<TotalOrderListVo> totalOrderListss(TotalOrderListDto totalOrderListDto) {
        return  cbbaMapper.selectorderno(totalOrderListDto);
    }

    @Override
    public List<SaleOrderListVo> saleChangeListwithSaleOrder(SaleOrderListDto saleOrderListDto) {
        List<SaleOrderListVo> l1 = new ArrayList<>();
        List<SaleOrderListVo> saleOrderListVos = this.saleOrderList(saleOrderListDto);
        List<SaleOrderListVo> saleOrderListVos1 = this.saleChangeList(saleOrderListDto);
        for(int i=0;i<saleOrderListVos1.size();i++){
            saleOrderListVos1.get(i).setOrderTypeMsg("?????????????????????");
            l1.add(saleOrderListVos1.get(i));
        }
        l1.addAll(saleOrderListVos);
        return l1;
    }


//    @Override
//    public GsWorkInstance createTask() {
//
//        //??????????????????
//
//
//
//    }


}
