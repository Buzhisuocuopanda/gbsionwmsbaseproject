package com.ruoyi.system.service.gson.impl;

import com.ruoyi.common.constant.TakeOrderConstants;
import com.ruoyi.common.constant.WareHouseType;
import com.ruoyi.common.core.domain.entity.Cbpa;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.NumberToChineseUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.CbpmTakeOrderDo;
import com.ruoyi.system.domain.Do.NumberDo;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.gson.BaseCheckService;
import com.ruoyi.system.service.gson.TakeGoodsService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName TakeGoodsServiceImpl
 * Description
 * Create by gfy
 * Date 2022/8/10 17:51
 */
@Service
public class TakeGoodsServiceImpl implements TakeGoodsService {

    @Resource
    private CbpkMapper cbpkMapper;

    @Resource
    private CboaMapper cboaMapper;

    @Resource
    private CbplMapper cbplMapper;

    @Resource
    private GsGoodsUseMapper gsGoodsUseMapper;

    @Resource
    private NumberGenerate numberGenerate;

    @Resource
    private CalaMapper calaMapper;

    @Resource
    private CbcaMapper cbcaMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private CbwaMapper cbwaMapper;

    @Resource
    private CbpbMapper cbpbMapper;

    @Resource
    private CbobMapper cbobMapper;

    @Resource
    private CbpmMapper cbpmMapper;

    @Resource
    private CbpaMapper cbpaMapper;

    @Resource
    private CblaMapper cblaMapper;

    @Resource
    private GsGoodsSnMapper gsGoodsSnMapper;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private GsOutStockAdivceMapper gsOutStockAdivceMapper;

    @Resource
    private BaseCheckService baseCheckService;

    @Resource
    private GsGoodsSkuMapper gsGoodsSkuMapper;


    @Override
    public List<TakeGoodsOrderListVo> takeOrderList(TakeGoodsOrderListDto takeGoodsOrderListDto) {

        List<TakeGoodsOrderListVo> res = cbpkMapper.takeOrderList(takeGoodsOrderListDto);
        return res;
    }

    @Transactional
    public void addTakeGoodsOrderold(TakeGoodsOrderAddDto takeGoodsOrderAddDto) {

        //?????????????????? ???????????????????????????????????????
        CboaCriteria oaex = new CboaCriteria();
        oaex.createCriteria()
                .andCboa06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                .andCboa07EqualTo(takeGoodsOrderAddDto.getSaleOrderNo());
        List<Cboa> cboas = cboaMapper.selectByExample(oaex);
        if (cboas.size() == 0) {
            throw new SwException("?????????????????????????????????");
        }
        Cboa cboa = cboas.get(0);
        if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(cboa.getCboa11())) {
            throw new SwException("????????????????????????????????????");
        }

        //????????????????????????????????????????????????????????????????????????????????????
        Cbpk old = cbpkMapper.selectLastBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
        if (old != null && TakeOrderCheckStatus.NOCHECK.getCode().equals(old.getCheckStatus())) {
            throw new SwException("????????????????????????????????????????????????????????????????????????????????????");
        }

        Cbpk cbpk = new Cbpk();
        Date date = new Date();
        cbpk.setCbpk02(date);
        cbpk.setCbpk03(takeGoodsOrderAddDto.getUserId());
        cbpk.setCbpk04(date);
        cbpk.setCbpk05(takeGoodsOrderAddDto.getUserId());
        cbpk.setCbpk06(DeleteFlagEnum.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.TAKEORDER.getCode());
        cbpk.setCbpk07(numberGenerate.createOrderNo(numberDo).getOrderNo());
        cbpk.setCbpk08(takeGoodsOrderAddDto.getOrderDate());
        cbpk.setCbpk09(takeGoodsOrderAddDto.getCustomerId());
        cbpk.setCbpk10(takeGoodsOrderAddDto.getWhId());
        cbpk.setCbpk11(SaleOrderStatusEnums.YITIJIAO.getCode());
//        cbpk.setCbpk12();
//        cbpk.setCbpk13();
//        cbpk.setCbpk14();/
//        cbpk.setCbpk15();
        cbpk.setCbpk16(takeGoodsOrderAddDto.getCurrency());
        cbpk.setCbpk17(takeGoodsOrderAddDto.getSaleUserId());
        cbpk.setCbpk18(takeGoodsOrderAddDto.getContacts());
        cbpk.setCbpk19(takeGoodsOrderAddDto.getPhone());
//        cbpk.setCbpk20();
        cbpk.setCbpk21(takeGoodsOrderAddDto.getReceiveAdress());
//        cbpk.setCbpk22();
//        cbpk.setCbpk23();
//        cbpk.setCbpk24();
//        cbpk.setCbpk25();
//        cbpk.setCbpk26();
//        cbpk.setCbpk27();
//        cbpk.setCbpk28();
//        cbpk.setCbpk29();
        cbpk.setCbpk30(takeGoodsOrderAddDto.getCustomerNo());

        cbpk.setCbpk31(TakeOrderConstants.WEIWANCHENG);
        cbpk.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getCode().byteValue());
        cbpk.setSaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
        int insert = cbpkMapper.insertWithId(cbpk);
        Cbpl cbpl = null;
        for (TakeOrderGoodsDto good : takeGoodsOrderAddDto.getGoods()) {
            if (good.getQty() == 0) {
                continue;
            }

            if (good.getGoodsId() == null) {
                throw new SwException("????????????????????????");
            }

            //???????????????????????????
            CbobCriteria cbobex = new CbobCriteria();
            cbobex.createCriteria()
                    .andCboa01EqualTo(cboa.getCboa01())
                    .andCbob08EqualTo(good.getGoodsId());
            List<Cbob> cbobs = cbobMapper.selectByExample(cbobex);
            for (Cbob cbob : cbobs) {


//                if(cbob.getTakeQty()!=null && (cbob.getTakeQty()+good.getQty())>=cbob.getCbob09()){
//                    throw new SwException("????????????????????????????????????");
//                }
                List<Cbpl> cbpls = cbplMapper.selectBySaleOrderNoAndGoodsId(cboa.getCboa07(), good.getGoodsId());
                Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

                if (good.getQty() + collect > cbob.getCbob09()) {
                    throw new SwException("???????????????????????????????????????????????????");
                }
            }

            //?????????????????????????????? ?????????????????????????????????????????? ??????????????????????????????????????????
            GsGoodsUseCriteria guex = new GsGoodsUseCriteria();
            guex.createCriteria()
                    .andGoodsIdEqualTo(good.getGoodsId())
//                    .andOrderNoEqualTo(takeGoodsOrderAddDto.getSaleOrderNo())
                    .andWhIdEqualTo(takeGoodsOrderAddDto.getWhId());
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
            if (gsGoodsUses.size() == 0) {
                Cbpb cbpb = cbpbMapper.selectByPrimaryKey(good.getGoodsId());
                throw new SwException("????????????????????????????????????????????????:" + cbpb.getCbpb12());
            }

            GsGoodsUse goodsUse = gsGoodsUses.get(0);
            if (good.getQty() > goodsUse.getLockQty()) {
                throw new SwException("????????????????????????????????????????????????");

            }
            //???????????????
//            Double noOutQty = goodsUse.getNoOutQty();
//            List<Cbpl> list=cbplMapper.selectByGoodsAndSaleOrderNo(good.getGoodsId(),cboa.getCboa07());
//            Double takeQty=list.stream().mapToDouble(Cbpl::getGoodProductQty).sum();
//            Double lockQty = goodsUse.getLockQty();
//            if(good.getQty()+takeQty>lockQty){
//                throw new SwException("????????????????????????????????????????????????????????????");
//
//            }

//             Double lockQty1 = goodsUse.getLockQty();
//             Double lockQty1 = goodsUse.getLockQty();

            //??????????????????


            if (good.getQty() == 0) {
                throw new SwException("?????????????????????0");
            }

            //???????????????
            cbpl = new Cbpl();
            cbpl.setCbpk01(cbpk.getCbpk01());
            cbpl.setCbpl02(good.getNumber());
            cbpl.setCbpl03(date);
            cbpl.setCbpl04(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl05(date);
            cbpl.setCbpl06(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbpl.setCbpl08(good.getGoodsId());
            cbpl.setCbpl09(good.getQty());
            cbpl.setCbpl10(0.0);
            cbpl.setCbpl11(good.getPrice());
            cbpl.setCbpl12(good.getTotalPrice());
            cbpl.setGoodProductQty(good.getQty());
            cbpl.setCbpl16(cboa.getCboa27());
            cbpl.setCbobId(good.getCbob01());
//            cbpl.setCbpl13();
//            cbpl.setCbpl14();
//            cbpl.setCbpl15();
//            cbpl.setCbpl16();
//            cbpl.setGoodProductQty(0.0);
            cbplMapper.insert(cbpl);

//            List<Cbpl> list=cbplMapper.selectBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());


            //????????????
//            Double takeQty = list.stream().collect(Collectors.summingDouble(Cbpl::getCbpl09));
            //????????????
//            Double goodQty = list.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

//            Double lessQty=takeQty-goodQty;


        }

    }

    @Transactional
    @Override

    public void addTakeGoodsOrder(TakeGoodsOrderAddDto takeGoodsOrderAddDto) {

        //?????????????????? ???????????????????????????????????????
//        CboaCriteria oaex = new CboaCriteria();
//        oaex.createCriteria()
//                .andCboa06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCboa07EqualTo(takeGoodsOrderAddDto.getSaleOrderNo());
//        List<Cboa> cboas = cboaMapper.selectByExample(oaex);
//        if (cboas.size() == 0) {
//            throw new SwException("?????????????????????????????????");
//        }
//        Cboa cboa = cboas.get(0);
//        if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(cboa.getCboa11())) {
//            throw new SwException("????????????????????????????????????");
//        }

        //????????????????????????????????????????????????????????????????????????????????????
        Cbpk old = cbpkMapper.selectLastBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
        if (old != null && TakeOrderCheckStatus.NOCHECK.getCode().equals(old.getCheckStatus())) {
            throw new SwException("????????????????????????????????????????????????????????????????????????????????????");
        }

        Cbpk cbpk = new Cbpk();
        Date date = new Date();
        cbpk.setCbpk02(date);
        cbpk.setCbpk03(takeGoodsOrderAddDto.getUserId());
        cbpk.setCbpk04(date);
        cbpk.setCbpk05(takeGoodsOrderAddDto.getUserId());
        cbpk.setCbpk06(DeleteFlagEnum.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.TAKEORDER.getCode());
        cbpk.setCbpk07(numberGenerate.createOrderNo(numberDo).getOrderNo());
        cbpk.setCbpk08(takeGoodsOrderAddDto.getOrderDate());
        cbpk.setCbpk09(takeGoodsOrderAddDto.getCustomerId());
        cbpk.setCbpk10(takeGoodsOrderAddDto.getWhId());
        cbpk.setCbpk11(SaleOrderStatusEnums.YITIJIAO.getCode());
//        cbpk.setCbpk12();
//        cbpk.setCbpk13();
//        cbpk.setCbpk14();/
//        cbpk.setCbpk15();
        cbpk.setCbpk16(takeGoodsOrderAddDto.getCurrency());
        cbpk.setCbpk17(takeGoodsOrderAddDto.getSaleUserId());
        cbpk.setCbpk18(takeGoodsOrderAddDto.getContacts());
        cbpk.setCbpk19(takeGoodsOrderAddDto.getPhone());
//        cbpk.setCbpk20();
        cbpk.setCbpk21(takeGoodsOrderAddDto.getReceiveAdress());
//        cbpk.setCbpk22();
//        cbpk.setCbpk23();
//        cbpk.setCbpk24();
//        cbpk.setCbpk25();
//        cbpk.setCbpk26();
//        cbpk.setCbpk27();
//        cbpk.setCbpk28();
//        cbpk.setCbpk29();
        cbpk.setCbpk30(takeGoodsOrderAddDto.getCustomerNo());

        cbpk.setCbpk31(TakeOrderConstants.WEIWANCHENG);
        cbpk.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getCode().byteValue());
        cbpk.setSaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
        int insert = cbpkMapper.insertWithId(cbpk);
        Cbpl cbpl = null;
        for (TakeOrderGoodsDto good : takeGoodsOrderAddDto.getGoods()) {
            if (good.getQty() == 0) {
                continue;
            }

            if (good.getGoodsId() == null) {
                throw new SwException("????????????????????????");
            }

            //???????????????????????????
            Cbob cbob = cbobMapper.selectByPrimaryKey(good.getCbob01());
            if(cbob==null){
                throw new SwException("?????????????????????????????????");
            }
            Cboa cboa = cboaMapper.selectByPrimaryKey(cbob.getCboa01());
        if (cboa==null) {
            throw new SwException("?????????????????????????????????");
        }

        if (!SaleOrderStatusEnums.YIFUSHEN.getCode().equals(cboa.getCboa11())) {
            throw new SwException("????????????????????????????????????");
        }



//            for (Cbob cbob : cbobs) {


//                if(cbob.getTakeQty()!=null && (cbob.getTakeQty()+good.getQty())>=cbob.getCbob09()){
//                    throw new SwException("????????????????????????????????????");
//                }
//                List<Cbpl> cbpls = cbplMapper.selectBySaleOrderNoAndGoodsId(cboa.getCboa07(), good.getGoodsId());
//                Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));
            CbplCriteria plex=new CbplCriteria();
            plex.createCriteria()
                    .andCbobIdEqualTo(cbob.getCbob01())
                    .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
             Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

            if (good.getQty() + collect > cbob.getCbob09()) {
                    throw new SwException("???????????????????????????????????????????????????");
                }
//            }

            //?????????????????????????????? ?????????????????????????????????????????? ??????????????????????????????????????????
            GsGoodsUseCriteria guex = new GsGoodsUseCriteria();
            guex.createCriteria()
                    .andGoodsIdEqualTo(good.getGoodsId())
//                    .andOrderNoEqualTo(takeGoodsOrderAddDto.getSaleOrderNo())
                    .andWhIdEqualTo(takeGoodsOrderAddDto.getWhId());
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
            if (gsGoodsUses.size() == 0) {
                Cbpb cbpb = cbpbMapper.selectByPrimaryKey(good.getGoodsId());
                throw new SwException("????????????????????????????????????????????????:" + cbpb.getCbpb12());
            }

            GsGoodsUse goodsUse = gsGoodsUses.get(0);
            if (good.getQty() > goodsUse.getLockQty()) {
                throw new SwException("????????????????????????????????????????????????");

            }
            //???????????????
//            Double noOutQty = goodsUse.getNoOutQty();
//            List<Cbpl> list=cbplMapper.selectByGoodsAndSaleOrderNo(good.getGoodsId(),cboa.getCboa07());
//            Double takeQty=list.stream().mapToDouble(Cbpl::getGoodProductQty).sum();
//            Double lockQty = goodsUse.getLockQty();
//            if(good.getQty()+takeQty>lockQty){
//                throw new SwException("????????????????????????????????????????????????????????????");
//
//            }

//             Double lockQty1 = goodsUse.getLockQty();
//             Double lockQty1 = goodsUse.getLockQty();

            //??????????????????


            if (good.getQty() == 0) {
                throw new SwException("?????????????????????0");
            }

            //???????????????
            cbpl = new Cbpl();
            cbpl.setCbpk01(cbpk.getCbpk01());
            cbpl.setCbpl02(good.getNumber());
            cbpl.setCbpl03(date);
            cbpl.setCbpl04(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl05(date);
            cbpl.setCbpl06(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbpl.setCbpl08(good.getGoodsId());
            cbpl.setCbpl09(good.getQty());
            cbpl.setCbpl10(0.0);
            cbpl.setCbpl11(good.getPrice());
            cbpl.setCbpl12(good.getTotalPrice());
            cbpl.setGoodProductQty(good.getQty());
            cbpl.setCbpl16(cboa.getCboa27());
            cbpl.setCbobId(cbob.getCbob01());
//            cbpl.setCbpl13();
//            cbpl.setCbpl14();
//            cbpl.setCbpl15();
//            cbpl.setCbpl16();
//            cbpl.setGoodProductQty(0.0);
            cbplMapper.insert(cbpl);

//            List<Cbpl> list=cbplMapper.selectBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());


            //????????????
//            Double takeQty = list.stream().collect(Collectors.summingDouble(Cbpl::getCbpl09));
            //????????????
//            Double goodQty = list.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

//            Double lessQty=takeQty-goodQty;


        }

    }

    @Override
    public TakeGoodsOrderDetailVo takeOrderDetail(Integer id) {
        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(id);
        if (cbpk == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cbpk.getCbpk06())) {
            throw new SwException("????????????????????????");
        }

        TakeGoodsOrderDetailVo res = new TakeGoodsOrderDetailVo();
        res.setContacts(cbpk.getCbpk18());
        res.setCurrency(cbpk.getCbpk16());
        res.setOrderNo(cbpk.getCbpk07());
        if (CurrencyEnum.CNY.getCode().equals(cbpk.getCbpk16())) {
            res.setCurrencyMsg(CurrencyEnum.CNY.getMsg());
        } else {
            res.setCurrencyMsg(CurrencyEnum.USD.getMsg());
        }
        res.setCustomerId(cbpk.getCbpk09());
        Cbca cbca = cbcaMapper.selectByPrimaryKey(cbpk.getCbpk09());
        if (cbca != null) {
            res.setCustomerName(cbca.getCbca08());
            res.setCustomerLevel(cbca.getCbca28());
        }

        res.setCustomerNo(cbpk.getCbpk30());
        res.setOrderDate(cbpk.getCbpk08());
        res.setPhone(cbpk.getCbpk19());
        res.setReceiveAdress(cbpk.getCbpk21());
        res.setReceiver(cbpk.getCbpk18());
        res.setReceivPhone(cbpk.getCbpk19());
        Cboa cboa = null;
        if (!StringUtils.isBlank(cbpk.getSaleOrderNo())) {
            CboaCriteria oaex = new CboaCriteria();
            oaex.createCriteria()
                    .andCboa07EqualTo(cbpk.getSaleOrderNo());
            List<Cboa> cboas = cboaMapper.selectByExample(oaex);
            cboa = cboas.get(0);
            res.setSaleOrderId(cboas.get(0).getCboa01());

        }
        res.setSaleOrderNo(cbpk.getSaleOrderNo());
        SysUser user = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk03().longValue());
        if (user != null) {
            res.setUserId(user.getUserId().intValue());
            res.setUserName(user.getNickName());
        }
        if (cbpk.getCbpk12() != null) {
            SysUser auditUser = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk12().longValue());
            if (auditUser != null) {

                res.setAuditUserName(auditUser.getNickName());
            }
        }


        if (cbpk.getCbpk17() != null) {
            SysUser saleUser = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk17().longValue());
            if (saleUser != null) {
                res.setSaleUserId(saleUser.getUserId().intValue());
                res.setSaleUserName(saleUser.getNickName());

            }
        }


        Cbwa cbwa = cbwaMapper.selectByPrimaryKey(cbpk.getCbpk10());
        if (cbwa != null) {
            res.setWhName(cbwa.getCbwa09());
            res.setWhId(cbwa.getCbwa01());

        }
        if (cbpk.getCheckStatus() != null) {
            if (TakeOrderCheckStatus.NOCHECK.getCode().equals(cbpk.getCheckStatus())) {
                res.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getMsg());
            } else {
                res.setCheckStatus(TakeOrderCheckStatus.CHECK.getMsg());
            }


        }
        CbplCriteria plex = new CbplCriteria();
        plex.createCriteria()
                .andCbpk01EqualTo(cbpk.getCbpk01())
                .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
        plex.setOrderByClause("CBPL02 desc");
        List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
        List<TakeOrderGoodsVo> goods = res.getGoods();


        //zhaoGuoLiang???????????????????????????id
        CboaCriteria cboaCriteria = new CboaCriteria();
        cboaCriteria.createCriteria().andCboa07EqualTo(cbpk.getSaleOrderNo())
                .andCboa06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());

        List<Cboa> cboaList = cboaMapper.selectByExample(cboaCriteria);
        List<Cbob> cbobList = null;
        if (cboaList != null && cboaList.size() > 0) {
            CbobCriteria cbobCriteria = new CbobCriteria();
            cbobCriteria.createCriteria().andCboa01EqualTo(cboaList.get(0).getCboa01())
                    .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            cbobCriteria.setOrderByClause("CBOB03 desc");
            cbobList = cbobMapper.selectByExample(cbobCriteria);
        }

//        CalaCriteria laexample = new CalaCriteria();
//        laexample.createCriteria()
//                .andCala10EqualTo("????????????");
//        List<Cala> calas = calaMapper.selectByExample(laexample);
//        Map<Integer, String> brandMap = new HashMap<>();
//        for (Cala cala : calas) {
//            brandMap.put(cala.getCala01(), cala.getCala08());
//        }
        Map<Integer, String> brandMap = baseCheckService.brandMap();

        TakeOrderGoodsVo good = null;
        Double sumQty = 0.0;
        Double sunPrice = 0.0;

        Map<Integer, TakeOrderGoodsVo> goodsMap = new HashMap<>();
        int i = 0;
        Map<Integer,Integer> goodsScanQty=new HashMap<>();
        for (Cbpl cbpl : cbpls) {
            good = new TakeOrderGoodsVo();
//            CbpmCriteria pmex=new CbpmCriteria();
//            pmex.createCriteria()
//                    .andCbpk01EqualTo(cbpk.getCbpk01())
//                    .andCb
//            List<Cbpm> cbpms = cbpmMapper.selectByExample(pmex);

//            CbpmCriteria pmex=new CbpmCriteria();
//                 pmex.createCriteria()
//                .andCbpm07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCbpm08EqualTo(good.getGoodsId())
//                .andCbpk01EqualTo(cbpk.getCbpk01());
//                List<Cbpm> cbpms = cbpmMapper.selectByExample(pmex);
//                good.setGoodsNum(Double.valueOf(cbpms.size()));


            //zhaoGuoLiang???????????????????????????id

            if(cbpl.getCbobId()!=null){
                Cbob cbob = cbobMapper.selectByPrimaryKey(cbpl.getCbobId());
                if(cbob!=null){
                    good.setCbob01(cbob.getCbob01());
                }


            }else {
                if (cbobList != null && cbobList.size() > i) {
                    good.setCbob01(cbobList.get(i).getCbob01());
                }
            }

            good.setCbplId(cbpl.getCbpl01());
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbpl.getCbpl08());
            if (cbpb != null) {

//                good.setGoodsNum();
                good.setUpc(cbpb.getCbpb15());
                good.setBrand(brandMap.get(cbpb.getCbpb10()));
                good.setDescription(cbpb.getCbpb08());
                good.setModel(cbpb.getCbpb12());
                Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
                if (cbpa != null) {
                    good.setGoodClass(cbpa.getCbpa07());
                }

            }


            good.setGoodsId(cbpl.getCbpl08());
            if (OrderTypeEnum.GUOJIDINGDAN.getCode().equals(cbpl.getCbpl16())) {
                good.setOrderClass(OrderTypeEnum.GUOJIDINGDAN.getMsg());
            } else {
                good.setOrderClass(OrderTypeEnum.GUONEIDINGDAN.getMsg());
            }
            if (cboa != null) {
                GsGoodsUseCriteria usexample = new GsGoodsUseCriteria();
                usexample.createCriteria()
                        .andOrderNoEqualTo(cboa.getCboa07())
                        .andGoodsIdEqualTo(good.getGoodsId());
                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usexample);

                if (gsGoodsUses.size() > 0) {
                    GsGoodsUse goodsUse = gsGoodsUses.get(0);
                    good.setUseQty(goodsUse.getLockQty());


                }

//                CbobCriteria obex = new CbobCriteria();
//                obex.createCriteria()
//                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                        .andCbob08EqualTo(good.getGoodsId())
//                        .andCboa01EqualTo(cboa.getCboa01());
               Cbob cbob = cbobMapper.selectByPrimaryKey(cbpl.getCbobId());
                if (cbob!=null) {
//                    Cbob cbob = cbobs.get(0);
                    good.setNoSendQty(cbob.getCbob09() - cbob.getCbob10());
                    good.setOrderQty(cbob.getCbob09());
                }


            }

            //????????????




            good.setPrice(cbpl.getCbpl11());

            CbpmCriteria pmex = new CbpmCriteria();
            pmex.createCriteria()
                    .andCbpm08EqualTo(good.getGoodsId())
                    .andCbpm11EqualTo(1)
                    .andCbpm07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                    .andCbpk01EqualTo(cbpk.getCbpk01());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(pmex);
            Integer usqty = goodsScanQty.get(good.getGoodsId());
            if(usqty==null){
                usqty=0;
            }
            Integer scanqty=cbpms.size()-usqty;
            if(cbpl.getCbpl09()>scanqty){
                good.setScanQty(scanqty);
                goodsScanQty.put(good.getGoodsId(),usqty+scanqty);
            }else {
                good.setScanQty(cbpl.getCbpl09().intValue());
                goodsScanQty.put(good.getGoodsId(),usqty+cbpl.getCbpl09().intValue());
            }




//            good.setGoodsNum(Double.valueOf(cbpms.size()));
            //todo
//            good.setRemark();
            good.setQty(cbpl.getCbpl09());
            good.setGoodsNum(good.getScanQty().doubleValue());
            //TODO
//            good.setSupplierId();
            good.setTotalPrice(cbpl.getCbpl12());

            sumQty = sumQty + cbpl.getCbpl09();
            sunPrice = sunPrice + cbpl.getCbpl11();


            res.getGoods().add(good);

            goodsMap.put(good.getGoodsId(), good);
            i++;

        }
        res.setSumPrice(sunPrice);
        res.setSumQty(sumQty);
        res.setCapPrice(NumberToChineseUtil.moneyToChinese(res.getSumPrice()));

        res.setSumQty(sumQty);
//        CbpmCriteria pmex=new CbpmCriteria();
//        pmex.createCriteria()
//                .andCbpm07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCbpm08EqualTo(good.getGoodsId())
//                .andCbpk01EqualTo(cbpk.getCbpk01());
//        pmex.setOrderByClause("CBPM11 ASC");
        List<CbpmTakeOrderDo> cbpms = cbpmMapper.selectByTakeId(cbpk.getCbpk01());
        //??????????????????
        TakeOrderSugestVo sugest = null;
        Map<Integer, Integer> scanMap = new HashMap<>();
        for (int x = 1; x <= cbpms.size(); x++) {
            CbpmTakeOrderDo cbpm = cbpms.get(x - 1);
            sugest = new TakeOrderSugestVo();
            TakeOrderGoodsVo takeOrderGoodsVo = goodsMap.get(cbpm.getCbpm08());
            if (takeOrderGoodsVo != null) {
                sugest.setBrand(takeOrderGoodsVo.getBrand());
                sugest.setDescription(takeOrderGoodsVo.getDescription());
                sugest.setModel(takeOrderGoodsVo.getModel());
                sugest.setGoodClass(takeOrderGoodsVo.getGoodClass());
                sugest.setUpc(takeOrderGoodsVo.getUpc());

//                sugest.setCbpm01(takeOrderGoodsVo.getCbplId());

            }
            sugest.setGoodsId(cbpm.getCbpm08());
            sugest.setNumber(x);
            sugest.setCbpm01(cbpm.getCbpm01());
            sugest.setScanStatus(ScanStatusEnum.findByKey(cbpm.getCbpm11()).getMsg());
            sugest.setBfSn(cbpm.getCbpm12());
            if (cbpm.getCbpm10() != null) {
                Cbla cbla = cblaMapper.selectByPrimaryKey(cbpm.getCbpm10());
                if (cbla != null) {
                    sugest.setCbla09(cbla.getCbla09());
                }
            }

            sugest.setSku(cbpm.getSku());
            sugest.setSn(cbpm.getCbpm09());
            res.getSugests().add(sugest);
            if (ScanStatusEnum.YISAOMA.getCode().equals(cbpm.getCbpm11())) {
                res.getScans().add(sugest);
                Integer integer = scanMap.get(takeOrderGoodsVo.getGoodsId());
                if (integer == null) {
                    scanMap.put(takeOrderGoodsVo.getGoodsId(), 1);
                } else {
                    scanMap.put(takeOrderGoodsVo.getGoodsId(), integer + 1);
                }
            }
//            for (TakeOrderGoodsVo resGood : res.getGoods()) {
//                if (scanMap.get(resGood.getGoodsId()) == null) {
//                    resGood.setScanQty(0);
//                } else {
//                    resGood.setScanQty(scanMap.get(resGood.getGoodsId()));
//                }
//
//
//            }


        }
        List<TakeOrderGoodsVo> goods1 = res.getGoods();
//        for (TakeOrderGoodsVo takeOrderGoodsVo : goods1) {
//            if (scanMap.get(takeOrderGoodsVo.getGoodsId()) != null) {
//                takeOrderGoodsVo.setGoodsNum(scanMap.get(takeOrderGoodsVo.getGoodsId()).doubleValue());
//
//            }
//        }


        if (res.getOrderDate() != null) {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            String format = sd.format(res.getOrderDate());
            res.setOrderDateMsg(format);
        }

        if(res.getScans()==null){
            res.setScannum(0.0);
            res.setNoscannum(res.getSumQty());
        }else {
            res.setScannum((double) res.getScans().size());
            res.setNoscannum(res.getSumQty() - res.getScannum());
        }

        return res;
    }

    //    @Override
//    public TakeGoodsOrderDetailVo takeOrderDetailBySaleId(Integer saleOrderId,Integer whId) {
//        Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderId);
//        TakeGoodsOrderDetailVo res=new TakeGoodsOrderDetailVo();
//        res.setContacts(cboa.getCboa17());
//        res.setCurrency(cboa.getCboa16());
//        if(CurrencyEnum.CNY.getCode().equals(cboa.getCboa16())){
//            res.setCurrencyMsg(CurrencyEnum.CNY.getMsg());
//        }else {
//            res.setCurrencyMsg(CurrencyEnum.USD.getMsg());
//        }
//        res.setCustomerId(cboa.getCboa09());
//        Cbca cbca = cbcaMapper.selectByPrimaryKey(res.getCustomerId());
//        if(cbca!=null){
//            res.setCustomerName(cbca.getCbca08());
//            res.setCustomerLevel(cbca.getCbca28());
//        }
//
//        res.setCustomerNo(cboa.getCboa25());
//        res.setOrderDate(cboa.getCboa08());
//        res.setPhone(cboa.getCboa19());
//        res.setReceiveAdress(cboa.getCboa18());
//
//        res.setReceiver(cboa.getCboa17());
//        res.setReceivPhone(cboa.getCboa19());
//        res.setSaleOrderId(cboa.getCboa01());
//
//        res.setSaleOrderNo(cboa.getCboa07());
////        SysUser user = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk03().longValue());
////        if(user!=null){
////            res.setUserId(user.getUserId().intValue());
////            res.setUserName(user.getNickName());
////        }
//
//        SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa10().longValue());
//        if(saleUser!=null){
//            res.setSaleUserId(saleUser.getUserId().intValue());
//            res.setSaleUserName(saleUser.getNickName());
//
//        }
//
//        Cbwa cbwa = cbwaMapper.selectByPrimaryKey(whId);
//        if(cbwa!=null){
//            res.setWhName(cbwa.getCbwa09());
//            res.setWhId(cbwa.getCbwa01());
//
//        }
////        if(cbpk.getCheckStatus()!=null){
////            if(TakeOrderCheckStatus.NOCHECK.getCode().equals(cbpk.getCheckStatus())){
////                res.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getMsg());
////            }else {
////                res.setCheckStatus(TakeOrderCheckStatus.CHECK.getMsg());
////            }
////
////
////        }
//
//        CalaCriteria laexample = new CalaCriteria();
//        laexample.createCriteria()
//                .andCala10EqualTo("????????????");
//        List<Cala> calas = calaMapper.selectByExample(laexample);
//        Map<Integer, String> brandMap = new HashMap<>();
//        for (Cala cala : calas) {
//            brandMap.put(cala.getCala01(), cala.getCala08());
//        }
////        CbobCriteria obex=new CbobCriteria();
////        obex.createCriteria()
////                .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
////                .andCboa01EqualTo(cboa.getCboa01());
////        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
//        TakeOrderGoodsVo good=null;
//        //????????????
//        GsGoodsUseCriteria usex=new GsGoodsUseCriteria();
//        usex.createCriteria().andOrderNoEqualTo(cboa.getCboa07())
//                .andWhIdEqualTo(whId);
//        List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//        for (GsGoodsUse goodsUse : gsGoodsUses) {
//            good=new TakeOrderGoodsVo();
//            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsUse.getGoodsId());
//            if (cbpb != null) {
//                good.setGoodsId(cbpb.getCbpb01());
//                good.setBrand(brandMap.get(cbpb.getCbpb10()));
//                good.setDescription(cbpb.getCbpb08());
//                good.setModel(cbpb.getCbpb12());
//                Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
//                if(cbpa!=null){
//                    good.setGoodClass(cbpa.getCbpa07());
//                }
//            }
//                CbobCriteria obex=new CbobCriteria();
//                obex.createCriteria()
//                        .andCbob08EqualTo(goodsUse.getGoodsId())
//                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                        .andCboa01EqualTo(cboa.getCboa01());
//                List<Cbob> cbobs = cbobMapper.selectByExample(obex);
//                if(cbobs.size()>0){
//                    Cbob cbob=cbobs.get(0);
//                    good.setNoSendQty(cbob.getCbob09()-cbob.getCbob10());
//                    good.setPrice(cbob.getCbob11());
//                    //todo
////            good.setRemark();
//                    good.setQty(goodsUse.getLockQty());
//                    good.setTotalPrice(cbob.getCbob12());
//                    //TODO
////            good.setSupplierId();
//
//                }
//
//                good.setUseQty(goodsUse.getLockQty());
//
//
//                //????????????
//                good.setGoodsNum(0.0);
//
//                res.getGoods().add(good);
//
//            }
//        return res;
//
//    }
//
//    @Transactional
//    @Override
//    public void mdfTakeGoodsOrder(TakeGoodsOrderAddDto takeGoodsOrderAddDto) {
//
//        //???????????????????????????????????????
//        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(takeGoodsOrderAddDto.getId());
//        if(!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cbpk.getCbpk11())){
//            throw new SwException("?????????????????????????????????????????????");
//        }
//
//        Date date = new Date();
//
//
//        cbpk.setCbpk04(date);
//        cbpk.setCbpk05(takeGoodsOrderAddDto.getUserId());
//        cbpk.setCbpk08(takeGoodsOrderAddDto.getOrderDate());
//        cbpk.setCbpk09(takeGoodsOrderAddDto.getCustomerId());
//        cbpk.setCbpk10(takeGoodsOrderAddDto.getWhId());
//
////        cbpk.setCbpk12();
////        cbpk.setCbpk13();
////        cbpk.setCbpk14();/
////        cbpk.setCbpk15();
//        cbpk.setCbpk16(takeGoodsOrderAddDto.getCurrency());
//        cbpk.setCbpk17(takeGoodsOrderAddDto.getSaleUserId());
//        cbpk.setCbpk18(takeGoodsOrderAddDto.getContacts());
//        cbpk.setCbpk19(takeGoodsOrderAddDto.getPhone());
////        cbpk.setCbpk20();
//        cbpk.setCbpk21(takeGoodsOrderAddDto.getReceiveAdress());
////        cbpk.setCbpk22();
////        cbpk.setCbpk23();
////        cbpk.setCbpk24();
////        cbpk.setCbpk25();
////        cbpk.setCbpk26();
////        cbpk.setCbpk27();
////        cbpk.setCbpk28();
////        cbpk.setCbpk29();
//        cbpk.setCbpk30(takeGoodsOrderAddDto.getCustomerNo());
//        cbpk.setCbpk31(TakeOrderConstants.WEIWANCHENG);
//        cbpk.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getCode().byteValue());
//        cbpk.setSaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
//        cbpk.setCbpk11(SaleOrderStatusEnums.YITIJIAO.getCode());
//        int update = cbpkMapper.updateByPrimaryKey(cbpk);
//        Cbpl cbpl=null;
//
//        //??????cbpl
//
//        CbplCriteria plex=new CbplCriteria();
//        plex.createCriteria()
//                .andCbpk01EqualTo(cbpk.getCbpk01());
//        int i = cbplMapper.deleteByExample(plex);
//
//
//        for (TakeOrderGoodsDto good : takeGoodsOrderAddDto.getGoods()) {
//            if(good.getGoodsId()==null){
//                throw new SwException("????????????????????????");
//            }
//            //?????????????????????????????? ?????????????????????????????????????????? ??????????????????????????????????????????
//            GsGoodsUseCriteria guex=new GsGoodsUseCriteria();
//            guex.createCriteria()
//                    .andGoodsIdEqualTo(good.getGoodsId())
//                    .andOrderNoEqualTo(takeGoodsOrderAddDto.getSaleOrderNo())
//                    .andWhIdEqualTo(takeGoodsOrderAddDto.getWhId());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
//            if(gsGoodsUses.size()==0){
//                throw new SwException("????????????????????????????????????????????????:"+good.getGoodsMsg());
//            }
//
//            GsGoodsUse goodsUse = gsGoodsUses.get(0);
//            if(good.getQty()>goodsUse.getLockQty()){
//                throw new SwException("????????????????????????????????????????????????"+good.getGoodsMsg());
//
//            }
//            //???????????????
//            Double noOutQty = goodsUse.getNoOutQty();
//            Double lockQty = goodsUse.getLockQty();
//            if(good.getQty()+noOutQty>lockQty){
//                throw new SwException("????????????????????????????????????????????????????????????"+good.getGoodsMsg());
//
//            }
//
//            //???????????????
//            cbpl=new Cbpl();
//            cbpl.setCbpk01(cbpk.getCbpk01());
//            cbpl.setCbpl02(good.getNumber());
//            cbpl.setCbpl03(date);
//            cbpl.setCbpl04(takeGoodsOrderAddDto.getUserId());
//            cbpl.setCbpl05(date);
//            cbpl.setCbpl06(takeGoodsOrderAddDto.getUserId());
//            cbpl.setCbpl07(DeleteFlagEnum.NOT_DELETE.getCode());
//            cbpl.setCbpl08(good.getGoodsId());
//            cbpl.setCbpl09(good.getQty());
//            cbpl.setCbpl10(0.0);
//            cbpl.setCbpl11(good.getPrice());
//            cbpl.setCbpl12(good.getTotalPrice());
////            cbpl.setCbpl13();
////            cbpl.setCbpl14();
////            cbpl.setCbpl15();
////            cbpl.setCbpl16();
//            cbpl.setGoodProductQty(good.getQty());
//            cbplMapper.insert(cbpl);
//
//
//
////            List<Cbpl> list=cbplMapper.selectBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
//
//
//            //????????????
////            Double takeQty = list.stream().collect(Collectors.summingDouble(Cbpl::getCbpl09));
//            //????????????
////            Double goodQty = list.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));
//
////            Double lessQty=takeQty-goodQty;
//
//
//        }
//
//    }
    @Override
    public TakeGoodsOrderDetailVo takeOrderDetailBySaleId(Integer saleOrderId, Integer whId) {
        Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderId);
        TakeGoodsOrderDetailVo res = new TakeGoodsOrderDetailVo();
        res.setContacts(cboa.getCboa17());
        res.setCurrency(cboa.getCboa16());
        if (CurrencyEnum.CNY.getCode().equals(cboa.getCboa16())) {
            res.setCurrencyMsg(CurrencyEnum.CNY.getMsg());
        } else {
            res.setCurrencyMsg(CurrencyEnum.USD.getMsg());
        }
        res.setCustomerId(cboa.getCboa09());
        Cbca cbca = cbcaMapper.selectByPrimaryKey(res.getCustomerId());
        if (cbca != null) {
            res.setCustomerName(cbca.getCbca08());
            res.setCustomerLevel(cbca.getCbca28());
        }

        res.setCustomerNo(cboa.getCboa25());
        res.setOrderDate(cboa.getCboa08());
        res.setPhone(cboa.getCboa19());
        res.setReceiveAdress(cboa.getCboa18());

        res.setReceiver(cboa.getCboa17());
        res.setReceivPhone(cboa.getCboa19());
        res.setSaleOrderId(cboa.getCboa01());

        res.setSaleOrderNo(cboa.getCboa07());
//        SysUser user = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk03().longValue());
//        if(user!=null){
//            res.setUserId(user.getUserId().intValue());
//            res.setUserName(user.getNickName());
//        }

        SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa10().longValue());
        if (saleUser != null) {
            res.setSaleUserId(saleUser.getUserId().intValue());
            res.setSaleUserName(saleUser.getNickName());

        }

        Cbwa cbwa = cbwaMapper.selectByPrimaryKey(whId);
        if (cbwa != null) {
            res.setWhName(cbwa.getCbwa09());
            res.setWhId(cbwa.getCbwa01());

        }
//        if(cbpk.getCheckStatus()!=null){
//            if(TakeOrderCheckStatus.NOCHECK.getCode().equals(cbpk.getCheckStatus())){
//                res.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getMsg());
//            }else {
//                res.setCheckStatus(TakeOrderCheckStatus.CHECK.getMsg());
//            }
//
//
//        }

        CalaCriteria laexample = new CalaCriteria();
        laexample.createCriteria()
                .andCala10EqualTo("????????????");
        List<Cala> calas = calaMapper.selectByExample(laexample);
        Map<Integer, String> brandMap = new HashMap<>();
        for (Cala cala : calas) {
            brandMap.put(cala.getCala01(), cala.getCala08());
        }
//        CbobCriteria obex=new CbobCriteria();
//        obex.createCriteria()
//                .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCboa01EqualTo(cboa.getCboa01());
//        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
        TakeOrderGoodsVo good = null;
        Date date = new Date();
        //????????????
        CbobCriteria obex = new CbobCriteria();
        obex.createCriteria()
                .andCboa01EqualTo(cboa.getCboa01());
        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
        for (Cbob cbob : cbobs) {
            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
            usex.createCriteria().andGoodsIdEqualTo(cbob.getCbob08())
                    .andWhIdEqualTo(whId);
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
            Double canTakeQty = 0.0;
            Double cbobQty = cbob.getCbob09();
            for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                if (gsGoodsUs.getLockQty() > cbobQty) {
                    canTakeQty = canTakeQty + cbobQty;
                    cbobQty = 0.0;
                    gsGoodsUs.setLockQty(gsGoodsUs.getLockQty() - cbobQty);
                    gsGoodsUs.setUpdateTime(date);
//                gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
                } else {
                    canTakeQty = canTakeQty + gsGoodsUs.getLockQty();
                    cbobQty = cbobQty - gsGoodsUs.getLockQty();
//                gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());

                }

            }
            good = new TakeOrderGoodsVo();
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
            if (cbpb != null) {
                good.setGoodsId(cbpb.getCbpb01());
                good.setBrand(brandMap.get(cbpb.getCbpb10()));
                good.setDescription(cbpb.getCbpb08());
                good.setModel(cbpb.getCbpb12());
                Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
                if (cbpa != null) {
                    good.setGoodClass(cbpa.getCbpa07());
                }
            }


            CbplCriteria plex=new CbplCriteria();
            plex.createCriteria()
                    .andCbobIdEqualTo(cbob.getCbob01())
                    .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
            Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

            good.setNoSendQty(cbob.getCbob09() -collect);
            good.setPrice(cbob.getCbob11());
            good.setCbob01(cbob.getCbob01());
            //todo
//            good.setRemark();
            good.setQty(canTakeQty);
            good.setTotalPrice(cbob.getCbob12());
            good.setGoodsNum(canTakeQty);
            res.getGoods().add(good);

        }

//    GsGoodsUseCriteria usex=new GsGoodsUseCriteria();
//    usex.createCriteria().andOrderNoEqualTo(cboa.getCboa07())
//            .andWhIdEqualTo(whId);
//    List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//    for (GsGoodsUse goodsUse : gsGoodsUses) {
//        good=new TakeOrderGoodsVo();
//        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsUse.getGoodsId());
//        if (cbpb != null) {
//            good.setGoodsId(cbpb.getCbpb01());
//            good.setBrand(brandMap.get(cbpb.getCbpb10()));
//            good.setDescription(cbpb.getCbpb08());
//            good.setModel(cbpb.getCbpb12());
//            Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
//            if(cbpa!=null){
//                good.setGoodClass(cbpa.getCbpa07());
//            }
//        }
//        CbobCriteria obex=new CbobCriteria();
//        obex.createCriteria()
//                .andCbob08EqualTo(goodsUse.getGoodsId())
//                .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCboa01EqualTo(cboa.getCboa01());
//        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
//        if(cbobs.size()>0){
//            Cbob cbob=cbobs.get(0);
//            good.setNoSendQty(cbob.getCbob09()-cbob.getCbob10());
//            good.setPrice(cbob.getCbob11());
//            //todo
////            good.setRemark();
//            good.setQty(goodsUse.getLockQty());
//            good.setTotalPrice(cbob.getCbob12());
//            //TODO
////            good.setSupplierId();
//
//        }
//
//        good.setUseQty(goodsUse.getLockQty());
//
//
//        //????????????
//        good.setGoodsNum(0.0);
//
//        res.getGoods().add(good);

//    }
        return res;

    }

    @Override
    public TakeGoodsOrderDetailVo takeOrderDetailBySaleIdIds(List<Integer> saleOrderIds, Integer whId) {
        if(saleOrderIds.size()==0){
            throw new SwException("?????????????????????????????????");
        }
        TakeGoodsOrderDetailVo res = new TakeGoodsOrderDetailVo();
        for (Integer saleOrderId : saleOrderIds) {


        Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderId);

        res.setContacts(cboa.getCboa17());
        res.setCurrency(cboa.getCboa16());
        if (CurrencyEnum.CNY.getCode().equals(cboa.getCboa16())) {
            res.setCurrencyMsg(CurrencyEnum.CNY.getMsg());
        } else {
            res.setCurrencyMsg(CurrencyEnum.USD.getMsg());
        }
        res.setCustomerId(cboa.getCboa09());
        Cbca cbca = cbcaMapper.selectByPrimaryKey(res.getCustomerId());
        if (cbca != null) {
            res.setCustomerName(cbca.getCbca08());
            res.setCustomerLevel(cbca.getCbca28());
        }

        res.setCustomerNo(cboa.getCboa25());
        res.setOrderDate(cboa.getCboa08());
        res.setPhone(cboa.getCboa19());
        res.setReceiveAdress(cboa.getCboa18());

        res.setReceiver(cboa.getCboa17());
        res.setReceivPhone(cboa.getCboa19());
        res.setSaleOrderId(cboa.getCboa01());

        res.setSaleOrderNo(cboa.getCboa07());
//        SysUser user = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk03().longValue());
//        if(user!=null){
//            res.setUserId(user.getUserId().intValue());
//            res.setUserName(user.getNickName());
//        }

        SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa10().longValue());
        if (saleUser != null) {
            res.setSaleUserId(saleUser.getUserId().intValue());
            res.setSaleUserName(saleUser.getNickName());

        }

        Cbwa cbwa = cbwaMapper.selectByPrimaryKey(whId);
        if (cbwa != null) {
            res.setWhName(cbwa.getCbwa09());
            res.setWhId(cbwa.getCbwa01());

        }
//        if(cbpk.getCheckStatus()!=null){
//            if(TakeOrderCheckStatus.NOCHECK.getCode().equals(cbpk.getCheckStatus())){
//                res.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getMsg());
//            }else {
//                res.setCheckStatus(TakeOrderCheckStatus.CHECK.getMsg());
//            }
//
//
//        }

//        CalaCriteria laexample = new CalaCriteria();
//        laexample.createCriteria()
//                .andCala10EqualTo("????????????");
//        List<Cala> calas = calaMapper.selectByExample(laexample);
//        Map<Integer, String> brandMap = new HashMap<>();
//        for (Cala cala : calas) {
//            brandMap.put(cala.getCala01(), cala.getCala08());
//        }
        Map<Integer, String> brandMap = baseCheckService.brandMap();
//        CbobCriteria obex=new CbobCriteria();
//        obex.createCriteria()
//                .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                .andCboa01EqualTo(cboa.getCboa01());
//        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
        TakeOrderGoodsVo good = null;
        Date date = new Date();


        //????????????
        CbobCriteria obex = new CbobCriteria();
        obex.createCriteria()
                .andCboa01EqualTo(cboa.getCboa01());
        List<Cbob> cbobs = cbobMapper.selectByExample(obex);
        for (Cbob cbob : cbobs) {
            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
            usex.createCriteria().andGoodsIdEqualTo(cbob.getCbob08())
                    .andWhIdEqualTo(whId);
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
            Double canTakeQty = 0.0;
            Double cbobQty = cbob.getCbob09();
            for (GsGoodsUse gsGoodsUs : gsGoodsUses) {
                if (gsGoodsUs.getLockQty() > cbobQty) {
                    canTakeQty = canTakeQty + cbobQty;
                    cbobQty = 0.0;
                    gsGoodsUs.setLockQty(gsGoodsUs.getLockQty() - cbobQty);
                    gsGoodsUs.setUpdateTime(date);
//                gsGoodsUseMapper.updateByPrimaryKey(gsGoodsUs);
                } else {
                    canTakeQty = canTakeQty + gsGoodsUs.getLockQty();
                    cbobQty = cbobQty - gsGoodsUs.getLockQty();
//                gsGoodsUseMapper.deleteByPrimaryKey(gsGoodsUs.getId());

                }

            }
            good = new TakeOrderGoodsVo();
            good.setCbob01(cbob.getCbob01());
            Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
            if (cbpb != null) {
                good.setGoodsId(cbpb.getCbpb01());
                good.setBrand(brandMap.get(cbpb.getCbpb10()));
                good.setDescription(cbpb.getCbpb08());
                good.setModel(cbpb.getCbpb12());
                Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
                if (cbpa != null) {
                    good.setGoodClass(cbpa.getCbpa07());
                }
            }
            CbplCriteria plex=new CbplCriteria();
            plex.createCriteria()
                    .andCbobIdEqualTo(cbob.getCbob01())
                    .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
            Double collect = cbpls.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

            good.setNoSendQty(cbob.getCbob09() -collect);
//            good.setNoSendQty(cbob.getCbob09() - cbob.getCbob10());
            good.setPrice(cbob.getCbob11());
            good.setCbob01(cbob.getCbob01());
            //todo
//            good.setRemark();
            good.setQty(canTakeQty);
            good.setTotalPrice(cbob.getCbob12());
            good.setGoodsNum(canTakeQty);
            res.getGoods().add(good);

        }

        }
        return res;
    }

    @Transactional
    @Override
    public void mdfTakeGoodsOrder(TakeGoodsOrderAddDto takeGoodsOrderAddDto) {

        //???????????????????????????????????????
        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(takeGoodsOrderAddDto.getId());
        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cbpk.getCbpk11())) {
            throw new SwException("?????????????????????????????????????????????");
        }

        Date date = new Date();


        cbpk.setCbpk04(date);
        cbpk.setCbpk05(takeGoodsOrderAddDto.getUserId());
        cbpk.setCbpk08(takeGoodsOrderAddDto.getOrderDate());
        cbpk.setCbpk09(takeGoodsOrderAddDto.getCustomerId());
        cbpk.setCbpk10(takeGoodsOrderAddDto.getWhId());

//        cbpk.setCbpk12();
//        cbpk.setCbpk13();
//        cbpk.setCbpk14();/
//        cbpk.setCbpk15();
        cbpk.setCbpk16(takeGoodsOrderAddDto.getCurrency());
        cbpk.setCbpk17(takeGoodsOrderAddDto.getSaleUserId());
        cbpk.setCbpk18(takeGoodsOrderAddDto.getContacts());
        cbpk.setCbpk19(takeGoodsOrderAddDto.getPhone());
//        cbpk.setCbpk20();
        cbpk.setCbpk21(takeGoodsOrderAddDto.getReceiveAdress());
//        cbpk.setCbpk22();
//        cbpk.setCbpk23();
//        cbpk.setCbpk24();
//        cbpk.setCbpk25();
//        cbpk.setCbpk26();
//        cbpk.setCbpk27();
//        cbpk.setCbpk28();
//        cbpk.setCbpk29();
        cbpk.setCbpk30(takeGoodsOrderAddDto.getCustomerNo());
        cbpk.setCbpk31(TakeOrderConstants.WEIWANCHENG);
        cbpk.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getCode().byteValue());
        cbpk.setSaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());
        cbpk.setCbpk11(SaleOrderStatusEnums.YITIJIAO.getCode());
        int update = cbpkMapper.updateByPrimaryKey(cbpk);
        Cbpl cbpl = null;

        //??????cbpl

        CbplCriteria plex = new CbplCriteria();
        plex.createCriteria()
                .andCbpk01EqualTo(cbpk.getCbpk01());
        int i = cbplMapper.deleteByExample(plex);


        for (TakeOrderGoodsDto good : takeGoodsOrderAddDto.getGoods()) {
            if (good.getGoodsId() == null) {
                throw new SwException("????????????????????????");
            }
            //?????????????????????????????? ?????????????????????????????????????????? ??????????????????????????????????????????
            GsGoodsUseCriteria guex = new GsGoodsUseCriteria();
            guex.createCriteria()
                    .andGoodsIdEqualTo(good.getGoodsId())
                    .andOrderNoEqualTo(takeGoodsOrderAddDto.getSaleOrderNo())
                    .andWhIdEqualTo(takeGoodsOrderAddDto.getWhId());
            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(guex);
            if (gsGoodsUses.size() == 0) {
                throw new SwException("????????????????????????????????????????????????:" + good.getGoodsMsg());
            }

            GsGoodsUse goodsUse = gsGoodsUses.get(0);
            if (good.getQty() > goodsUse.getLockQty()) {
                throw new SwException("????????????????????????????????????????????????" + good.getGoodsMsg());

            }
            //???????????????
            Double noOutQty = goodsUse.getNoOutQty();
            Double lockQty = goodsUse.getLockQty();
            if (good.getQty() + noOutQty > lockQty) {
                throw new SwException("????????????????????????????????????????????????????????????" + good.getGoodsMsg());

            }

            //???????????????
            cbpl = new Cbpl();
            cbpl.setCbpk01(cbpk.getCbpk01());
            cbpl.setCbpl02(good.getNumber());
            cbpl.setCbpl03(date);
            cbpl.setCbpl04(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl05(date);
            cbpl.setCbpl06(takeGoodsOrderAddDto.getUserId());
            cbpl.setCbpl07(DeleteFlagEnum.NOT_DELETE.getCode());
            cbpl.setCbpl08(good.getGoodsId());
            cbpl.setCbpl09(good.getQty());
            cbpl.setCbpl10(0.0);
            cbpl.setCbpl11(good.getPrice());
            cbpl.setCbpl12(good.getTotalPrice());
            cbpl.setCbobId(good.getCbob01());
//            cbpl.setCbpl13();
//            cbpl.setCbpl14();
//            cbpl.setCbpl15();
//            cbpl.setCbpl16();
            cbpl.setGoodProductQty(good.getQty());
            cbplMapper.insert(cbpl);


//            List<Cbpl> list=cbplMapper.selectBySaleOrderNo(takeGoodsOrderAddDto.getSaleOrderNo());


            //????????????
//            Double takeQty = list.stream().collect(Collectors.summingDouble(Cbpl::getCbpl09));
            //????????????
//            Double goodQty = list.stream().collect(Collectors.summingDouble(Cbpl::getGoodProductQty));

//            Double lessQty=takeQty-goodQty;


        }

    }

    @Transactional
    @Override
    public void delTakeGoodsOrder(Integer id, Long userId) {
        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(id);
        if (cbpk == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cbpk.getCbpk06())) {
            throw new SwException("????????????????????????");
        }
        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(cbpk.getCbpk11())) {
            throw new SwException("???????????????????????????????????????");
        }

        cbpk.setCbpk06(DeleteFlagEnum.DELETE.getCode());
        cbpk.setCbpk05(userId.intValue());
        cbpk.setCbpk04(new Date());
        cbpkMapper.updateByPrimaryKey(cbpk);


        CbplCriteria plex=new CbplCriteria();
        plex.createCriteria()
                .andCbpk01EqualTo(cbpk.getCbpk01());

        Cbpl cbpl=new Cbpl();
        cbpl.setCbpl07(DeleteFlagEnum.DELETE.getCode());
        int i = cbplMapper.updateByExampleSelective(cbpl,plex);

        CbpmCriteria pmex=new CbpmCriteria();
        pmex.createCriteria()
                .andCbpk01EqualTo(cbpk.getCbpk01());
        int i1 = cbpmMapper.deleteByExample(pmex);


    }

    @Transactional
    @Override
    public void auditTakeOrder(AuditTakeOrderDto auditTakeOrderDto) {

        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(auditTakeOrderDto.getTakeOrderId());
        //???????????? ????????????????????? ??????????????????????????????????????????
        if (cbpk == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cbpk.getCbpk06())) {
            throw new SwException("????????????????????????");
        }

        Date date = new Date();
        if (auditTakeOrderDto.getOpType().equals(1)) {
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cbpk.getCbpk11())) {
                throw new SwException("???????????????????????????????????????");
            }
            cbpk.setCbpk11(SaleOrderStatusEnums.YISHENHE.getCode());
            cbpk.setCbpk12(auditTakeOrderDto.getUserId());

            //?????????????????????

            CbplCriteria plex = new CbplCriteria();
            plex.createCriteria()
                    .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                    .andCbpk01EqualTo(cbpk.getCbpk01());
            List<Cbpl> cbpls = cbplMapper.selectByExample(plex);

            Cbpm cbpm = null;
            for (Cbpl cbpl : cbpls) {
                //???????????? ???????????????

                //??????????????????????????????
                GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
                usex.createCriteria()
                        .andGoodsIdEqualTo(cbpl.getCbpl08())
                        .andWhIdEqualTo(cbpk.getCbpk10())

                ;

                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);

                if (gsGoodsUses.size() > 0) {
                    List<GsGoodsSn> list = gsGoodsSnMapper.selectOutByWhIdAndGoodsId(cbpk.getCbpk10(), cbpl.getCbpl08(), cbpl.getCbpl09().intValue());

                    for (int i = 0; i < list.size(); i++) {
                        GsGoodsSn gsGoodsSn = list.get(i);

                        cbpm = new Cbpm();
                        cbpm.setCbpk01(cbpk.getCbpk01());
                        cbpm.setCbpm02(i + 1);
                        cbpm.setCbpm03(date);
                        cbpm.setCbpm04(auditTakeOrderDto.getUserId());
                        cbpm.setCbpm05(date);
                        cbpm.setCbpm06(auditTakeOrderDto.getUserId());
                        cbpm.setCbpm07(DeleteFlagEnum.NOT_DELETE.getCode());
                        cbpm.setCbpm08(cbpl.getCbpl08());
                        cbpm.setCbpm09(gsGoodsSn.getSn());
                        cbpm.setCbpm10(gsGoodsSn.getLocationId());
                        cbpm.setCbpm11(0);

                        cbpmMapper.insert(cbpm);
                        gsGoodsSn.setStatus(new Byte("2"));
                        gsGoodsSn.setUpdateTime(date);

                        gsGoodsSnMapper.updateByPrimaryKeySelective(gsGoodsSn);


                    }


                }


            }


        }
        else if (auditTakeOrderDto.getOpType().equals(2)) {
            //?????? ?????????????????????????????????
            if (!SaleOrderStatusEnums.YITIJIAO.getCode().equals(cbpk.getCbpk11())) {
                throw new SwException("???????????????????????????????????????");
            }
            CbpmCriteria plex = new CbpmCriteria();
            plex.createCriteria()
                    .andCbpk01EqualTo(auditTakeOrderDto.getTakeOrderId());

            int i = cbpmMapper.deleteByExample(plex);
            cbpk.setCbpk11(SaleOrderStatusEnums.WEITIJIAO.getCode());

        }
        else if (auditTakeOrderDto.getOpType().equals(3)) {
            //?????? ?????????????????????????????????

            if (!SaleOrderStatusEnums.YISHENHE.getCode().equals(cbpk.getCbpk11())) {
                throw new SwException("???????????????????????????????????????");
            }
            CbpmCriteria scex = new CbpmCriteria();
            scex.createCriteria()
                    .andCbpk01EqualTo(auditTakeOrderDto.getTakeOrderId());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(scex);
//            if(cbpms.size()>0){
//                throw new SwException("????????????????????????????????????");
//            }
            cbpk.setCbpk11(SaleOrderStatusEnums.YITIJIAO.getCode());
            for (Cbpm cbpm : cbpms) {
                if (cbpm.getCbpm11() == 1) {
                    throw new SwException("????????????????????????????????????");

                }
                cbpmMapper.deleteByPrimaryKey(cbpm.getCbpm01());

                GsGoodsSn gs = new GsGoodsSn();
                gs.setStatus(new Byte("1"));

                GsGoodsSnCriteria example = new GsGoodsSnCriteria();
                example.createCriteria()
                        .andSnEqualTo(cbpm.getCbpm09());
                int i = gsGoodsSnMapper.updateByExampleSelective(gs, example);
            }


        }
        else if (auditTakeOrderDto.getOpType().equals(4)) {
            //????????????

            if (!SaleOrderStatusEnums.YISHENHE.getCode().equals(cbpk.getCbpk11())) {
                throw new SwException("?????????????????????????????????????????????");
            }

            if (cbpk.getCheckStatus() != 1) {
                throw new SwException("????????????????????????????????????????????????");
            }


            cbpk.setCbpk11(SaleOrderStatusEnums.YIWANCHENG.getCode());
            CboaCriteria orderex = new CboaCriteria();
            orderex.createCriteria()
                    .andCboa07EqualTo(cbpk.getSaleOrderNo());
            List<Cboa> cboas = cboaMapper.selectByExample(orderex);

            //???????????????????????????
            CbpmCriteria pmex = new CbpmCriteria();
            pmex.createCriteria()
                    .andCbpk01EqualTo(cbpk.getCbpk01())
                    .andCbpm07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(pmex);
            for (Cbpm cbpm : cbpms) {
                if (cbpm.getCbpm11() == 0) {
                    throw new SwException("?????????????????????????????????");
                }

            }


            if (cboas.size() > 0) {
                Cboa cboa = cboas.get(0);
                CbobCriteria obex = new CbobCriteria();
                obex.createCriteria()
                        .andCboa01EqualTo(cboa.getCboa01());
                List<Cbob> cbobs = cbobMapper.selectByExample(obex);

                for (Cbob cbob : cbobs) {
                    CbplCriteria plex = new CbplCriteria();
                    plex.createCriteria()
                            .andCbpk01EqualTo(cbpk.getCbpk01())
                            .andCbpl08EqualTo(cbob.getCbob08())
                            .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                    List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
                    if (cbpls.size() > 0) {
                        if (cbob.getTakeQty() == null) {
                            cbob.setTakeQty(cbpls.get(0).getGoodProductQty());

                        } else {
                            cbob.setTakeQty(cbob.getTakeQty() + cbpls.get(0).getGoodProductQty());

                        }
                    }

                }

                double sum = cbobs.stream().mapToDouble(Cbob::getCbob09).sum();
                if(sum ==cbpms.size()){
                    cboa.setCboa23(1);
                    cboaMapper.updateByPrimaryKeySelective(cboa);
                }
            }

            //???????????????????????????????????????????????????????????????????????????????????????????????????


        }
        else if (auditTakeOrderDto.getOpType().equals(5)) {
            //????????????

            if (!SaleOrderStatusEnums.YIWANCHENG.getCode().equals(cbpk.getCbpk11())) {
                throw new SwException("?????????????????????????????????????????????");
            }

            cbpk.setCbpk11(SaleOrderStatusEnums.YISHENHE.getCode());
            CboaCriteria orderex = new CboaCriteria();
            orderex.createCriteria()
                    .andCboa07EqualTo(cbpk.getSaleOrderNo());
            List<Cboa> cboas = cboaMapper.selectByExample(orderex);
            if (cboas.size() > 0) {
                Cboa cboa = cboas.get(0);
                CbobCriteria obex = new CbobCriteria();
                obex.createCriteria()
                        .andCboa01EqualTo(cboa.getCboa01());
                List<Cbob> cbobs = cbobMapper.selectByExample(obex);

                for (Cbob cbob : cbobs) {
                    CbplCriteria plex = new CbplCriteria();
                    plex.createCriteria()
                            .andCbpk01EqualTo(cbpk.getCbpk01())
                            .andCbpl08EqualTo(cbob.getCbob08())
                            .andCbpl07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
                    List<Cbpl> cbpls = cbplMapper.selectByExample(plex);
                    if (cbpls.size() > 0) {
                        cbob.setTakeQty(cbob.getTakeQty() - cbpls.get(0).getGoodProductQty());
                    }

                }


            }

        }
        else if (auditTakeOrderDto.getOpType().equals(6)) {
            if (cbpk.getCheckStatus().equals(new Byte("1"))) {
                throw new SwException("???????????????????????????");
            }
            cbpk.setCheckStatus(new Byte("1"));
            List<GoodsDto> goods = auditTakeOrderDto.getGoods();

            //???????????????????????????
            CbpmCriteria pmex = new CbpmCriteria();
            pmex.createCriteria()
                    .andCbpk01EqualTo(cbpk.getCbpk01())
                    .andCbpm07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(pmex);
            if (cbpms.size() == 0) {
                throw new SwException("???????????????????????????????????????");
            }

            for (Cbpm cbpm : cbpms) {
                if (cbpm.getCbpm11() == 0) {
                    throw new SwException("?????????????????????????????????");
                }

            }

            for (GoodsDto good : goods) {
                Cbpl cbpl = cbplMapper.selectByPrimaryKey(good.getPlId());
                if (good.getGoodQty() > cbpl.getCbpl09()) {
                    throw new SwException("????????????????????????????????????");
                }

                cbpl.setCbpl01(good.getPlId());
                cbpl.setGoodProductQty(Double.valueOf(cbpms.size()));
                cbpl.setGoodProductQty(good.getGoodQty());
                cbplMapper.updateByPrimaryKeySelective(cbpl);
            }

        }


        cbpk.setCbpk12(auditTakeOrderDto.getUserId());
        cbpk.setCbpk05(auditTakeOrderDto.getUserId());
        cbpk.setCbpk04(new Date());
        cbpkMapper.updateByPrimaryKey(cbpk);
        return;
    }

    @Override
    @Transactional
    public void mdfTakeSuggest(ChangeSuggestDto changeSuggestDto) {
        Date date = new Date();
        List<ChangeSuggestModel> list = changeSuggestDto.getList();
        for (ChangeSuggestModel changeSuggestModel : list) {
            //??????????????????????????????????????????????????????

            Cbpm cbpm = cbpmMapper.selectByPrimaryKey(changeSuggestModel.getCbpm01());
            if (cbpm == null) {
                throw new SwException("???????????????????????????");
            }
            if (!cbpm.getCbpm09().equals(changeSuggestModel.getCbpm09())) {
                GsGoodsSnCriteria snex = new GsGoodsSnCriteria();
                snex.createCriteria()
                        .andSnEqualTo(changeSuggestModel.getCbpm09())
                        .andDeleteFlagEqualTo(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
                List<GsGoodsSn> gsGoodsSns = gsGoodsSnMapper.selectByExample(snex);
                if (gsGoodsSns.size() == 0) {
                    throw new SwException("???SN?????????");
                }
                GsGoodsSn gsGoodsSn = gsGoodsSns.get(0);
                if (gsGoodsSn.getStatus() != 1) {
                    throw new SwException("???Sn??????????????????");
                }

                gsGoodsSn.setStatus(new Byte("2"));
                gsGoodsSn.setUpdateTime(date);
                gsGoodsSnMapper.updateByPrimaryKeySelective(gsGoodsSn);

                GsGoodsSnCriteria snex2 = new GsGoodsSnCriteria();
                snex2.createCriteria()
                        .andSnEqualTo(cbpm.getCbpm09())
                        .andDeleteFlagEqualTo(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
                List<GsGoodsSn> gsGoodsSns2 = gsGoodsSnMapper.selectByExample(snex2);
                if (gsGoodsSns2.size() == 0) {
                    throw new SwException("???SN?????????");
                }

                GsGoodsSn gsGoodsSn2 = gsGoodsSns2.get(0);


                gsGoodsSn2.setStatus(new Byte("1"));
                gsGoodsSn2.setUpdateTime(date);
                gsGoodsSnMapper.updateByPrimaryKeySelective(gsGoodsSn2);
//                gsGoodsSn.set
//
                cbpm.setCbpm01(changeSuggestModel.getCbpm01());
                cbpm.setCbpm07(changeSuggestModel.getCbpm07());
                cbpm.setCbpm08(changeSuggestModel.getCbpm08());
                cbpm.setCbpm12(cbpm.getCbpm09());
                cbpm.setCbpm09(changeSuggestModel.getCbpm09());

                cbpm.setCbpm05(date);
                cbpm.setCbpm06(changeSuggestDto.getUserId());
                cbpm.setCbpm09(changeSuggestModel.getCbpm09());
                cbpm.setCbpm08(changeSuggestModel.getCbpm08());
                cbpm.setCbpm10(changeSuggestModel.getCbpm10());
                cbpmMapper.updateByPrimaryKeySelective(cbpm);
            }
//            CbpmCriteria example=new CbpmCriteria();
//            example.createCriteria()
//                    .andCbpm09EqualTo(changeSuggestModel.getCbpm09());
//            List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
//            if(cbpms.size()>0 && !cbpms.get(0).getCbpm01().equals(changeSuggestModel.getCbpm01())){
//                throw new SwException("????????????Sn???????????????????????????????????????:" + changeSuggestModel.getCbpm09());
//            }


//            gsGoodsSnMapper.selectByExample()

        }

    }

    @Override
    public int TakeGoodsOrdersm(Cbpm itemList) {
        if (itemList == null) {
            throw new SwException("???????????????????????????");
        }


        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(itemList.getCbpk01());
        if (!cbpk.getCbpk11().equals(2)) {
            throw new SwException("????????????????????????");
        }
        CbpmCriteria example = new CbpmCriteria();
        example.createCriteria()
                .andCbpk01EqualTo(cbpk.getCbpk01())
                .andCbpm09EqualTo(itemList.getCbpm09());
        List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
        if (cbpms.size() == 0) {
            throw new SwException("????????????Sn??????????????????????????????");
        }


/*GsGoodsSnCriteria example0=new GsGoodsSnCriteria();
            example0.createCriteria()
                    .andSnEqualTo(itemList.get(i).getCbpm09());
            List<GsGoodsSn> gsGoodsSnss = gsGoodsSnMapper.selectByExample(example0);
            if(gsGoodsSnss.size()>0){
                throw new SwException("?????????sn?????????" );
            }*/
        CbpmCriteria sfgu = new CbpmCriteria();
        sfgu.createCriteria()
                .andCbpm09EqualTo(itemList.getCbpm09())
                .andCbpm11EqualTo(ScanStatusEnum.YISAOMA.getCode())
                .andCbpk01EqualTo(itemList.getCbpk01());
        List<Cbpm> cbpmss = cbpmMapper.selectByExample(sfgu);
        if (cbpmss.size() > 0) {
            throw new SwException("sn?????????"+itemList.getCbpm09());
        }


        itemList.setCbpm05(date);
        itemList.setCbpm06(Math.toIntExact(userid));
        itemList.setCbpm11(ScanStatusEnum.YISAOMA.getCode());


        GsGoodsSnCriteria example1 = new GsGoodsSnCriteria();
        example1.createCriteria()
                .andSnEqualTo(itemList.getCbpm09());
        List<GsGoodsSn> gsGoodsSns = gsGoodsSnMapper.selectByExample(example1);
        if (gsGoodsSns.size() == 0) {
            throw new SwException("????????????Sn??????????????????SN??????");
        }

        GsGoodsSnCriteria example6 = new GsGoodsSnCriteria();
        example6.createCriteria()
                .andSnEqualTo(itemList.getCbpm09())
                .andStatusEqualTo(GoodsType.yck.getCode());
        List<GsGoodsSn> gsGoodsSnss = gsGoodsSnMapper.selectByExample(example6);
        if (gsGoodsSnss.size() > 0) {
            throw new SwException("????????????Sn??????????????????");
        }


        if(gsGoodsSns.get(0).getGoodsId()==null){
            throw new SwException("????????????Sn????????????????????????");
        }

        if(gsGoodsSns.get(0).getWhId()==null){
            throw new SwException("????????????Sn????????????????????????");
        }

//        if(gsGoodsSns.get(0).getLocationId()==null){
//            throw new SwException("????????????Sn????????????????????????");
//        }
        if(gsGoodsSns.get(0).getLocationId()!=null){

        //????????????,
        GsGoodsSkuCriteria tuiw = new GsGoodsSkuCriteria();
        tuiw.createCriteria()
                .andGoodsIdEqualTo(gsGoodsSns.get(0).getGoodsId())
                .andLocationIdEqualTo(gsGoodsSns.get(0).getLocationId())
                .andWhIdEqualTo(gsGoodsSns.get(0).getWhId());
        List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(tuiw);
        if (gsGoodsSkus.size()> 0) {
            //????????????????????????1
            if(gsGoodsSkus.get(0).getQty()==1) {

                List<GsGoodsSku> gsGoodsSkus1 = gsGoodsSkuMapper.selectByGoodsIdAndWhId(gsGoodsSns.get(0).getGoodsId(), gsGoodsSns.get(0).getWhId());
                if (gsGoodsSkus1.size() > 0) {
                    GsGoodsSku gsGoodsSku = new GsGoodsSku();
                    gsGoodsSku.setId(gsGoodsSkus1.get(0).getId());
                    gsGoodsSku.setUpdateTime(date);
                    gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty() + gsGoodsSkus1.get(0).getQty());
                    gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSku);

                    gsGoodsSkuMapper.deleteByPrimaryKey(gsGoodsSkus.get(0).getId());
                }
                else {
                    GsGoodsSku gsGoodsSku = new GsGoodsSku();
                    gsGoodsSku.setId(gsGoodsSkus.get(0).getId());
                    gsGoodsSku.setCreateTime(date);
                    gsGoodsSku.setUpdateTime(date);
                    gsGoodsSku.setCreateBy(Math.toIntExact(userid));
                    gsGoodsSku.setUpdateBy(Math.toIntExact(userid));
                    gsGoodsSku.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                    gsGoodsSku.setGoodsId(gsGoodsSns.get(0).getGoodsId());
                    gsGoodsSku.setWhId(gsGoodsSns.get(0).getWhId());
                    gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty());
                    gsGoodsSku.setLocationId(null);
                    gsGoodsSkuMapper.updateByExample(gsGoodsSku, tuiw);
                }
            }
            //??????????????????1
            else{
                //????????????????????????????????????
                GsGoodsSku gsGoodsSku = new GsGoodsSku();
                gsGoodsSku.setId(gsGoodsSkus.get(0).getId());
                gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty()-1);
                gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSku);

                List<GsGoodsSku> gsGoodsSkus1 = gsGoodsSkuMapper.selectByGoodsIdAndWhId(gsGoodsSns.get(0).getGoodsId(), gsGoodsSns.get(0).getWhId());
                if (gsGoodsSkus1.size() > 0) {
                    GsGoodsSku gsGoodsSkuc = new GsGoodsSku();
                    gsGoodsSkuc.setId(gsGoodsSkus1.get(0).getId());
                    gsGoodsSkuc.setUpdateTime(date);
                    gsGoodsSkuc.setQty(gsGoodsSkus1.get(0).getQty() + 1);
                    gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSkuc);
                }else{
                    GsGoodsSku gsGoodsSkud = new GsGoodsSku();
                   // gsGoodsSkud.setId(gsGoodsSkus.get(0).getId());
                    gsGoodsSkud.setCreateTime(date);
                    gsGoodsSkud.setUpdateTime(date);
                    gsGoodsSkud.setCreateBy(Math.toIntExact(userid));
                    gsGoodsSkud.setUpdateBy(Math.toIntExact(userid));
                    gsGoodsSkud.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                    gsGoodsSkud.setGoodsId(gsGoodsSns.get(0).getGoodsId());
                    gsGoodsSkud.setWhId(gsGoodsSns.get(0).getWhId());
                    gsGoodsSkud.setQty(1.0);
                    gsGoodsSkud.setLocationId(null);
                    gsGoodsSkuMapper.insert(gsGoodsSkud);
                }
            }

        }







        GsGoodsSn goodsSn = new GsGoodsSn();
        goodsSn = gsGoodsSns.get(0);
        goodsSn.setId(gsGoodsSns.get(0).getId());
        goodsSn.setCreateTime(gsGoodsSns.get(0).getCreateTime());
        goodsSn.setCreateBy(gsGoodsSns.get(0).getCreateBy());
        goodsSn.setUpdateTime(date);
        goodsSn.setUpdateBy(Math.toIntExact(userid));
        goodsSn.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        goodsSn.setWhId(gsGoodsSns.get(0).getWhId());
        goodsSn.setGoodsId(gsGoodsSns.get(0).getGoodsId());
        goodsSn.setSn(itemList.getCbpm09());
        goodsSn.setGroudStatus(Groudstatus.XJ.getCode());
        goodsSn.setStatus(GoodsType.ckz.getCode());
        goodsSn.setLocationId(null);
        if (gsGoodsSns.get(0).getInTime() != null) {
            goodsSn.setInTime(gsGoodsSns.get(0).getInTime());
        }


        goodsSn.setInTime(gsGoodsSns.get(0).getInTime());


        GsGoodsSnCriteria example2 = new GsGoodsSnCriteria();
        example2.createCriteria()
                .andSnEqualTo(itemList.getCbpm09());
        gsGoodsSnMapper.updateByExample(goodsSn, example2);
        }
        cbpmMapper.updateByExampleSelective(itemList, example);


        return 1;
    }

    @Override
    public List<GsOutStockAdivceVo> saleOrderSuggest(GsOutStockAdivceDto gsOutStockAdivceDto) {
        List<GsOutStockAdivceVo> list = gsOutStockAdivceMapper.saleOrderSuggest(gsOutStockAdivceDto);
        Map<Integer, String> brandMap = baseCheckService.brandMap();

        for (GsOutStockAdivceVo gsOutStockAdivceVo : list) {
            if (gsOutStockAdivceVo != null && gsOutStockAdivceVo.getBrand() != null) {
                if (brandMap.get(Integer.parseInt(gsOutStockAdivceVo.getBrand())) != null) {
//                gsOutStockAdivceVo.setGoodsMsg((brandMap.get(gsOutStockAdivceVo.getBrand()) + "-" + gsOutStockAdivceVo.getModel() + "-" + gsOutStockAdivceVo.getDescription()));
                    gsOutStockAdivceVo.setBrand(brandMap.get(Integer.parseInt(gsOutStockAdivceVo.getBrand())));
                }
            }

        }

        return list;
    }

    @Override
    public void auditOutStockEnd(GsOutStockAdivceDto gsOutStockAdivceDto) {

        GsOutStockAdivce gsOutStockAdivce = gsOutStockAdivceMapper.selectByPrimaryKey(gsOutStockAdivceDto.getId());
        if (gsOutStockAdivce == null) {
            throw new SwException("???????????????????????????");
        }

        if (gsOutStockAdivce.getStatus() != 2) {
            throw new SwException("??????????????????????????????????????????");
        }


        Date date = new Date();
        gsOutStockAdivce.setUpdateTime(date);
        gsOutStockAdivce.setUpdateBy(gsOutStockAdivceDto.getUserId());
        gsOutStockAdivce.setStatus(new Byte("3"));
        gsOutStockAdivceMapper.updateByPrimaryKey(gsOutStockAdivce);
        if (gsOutStockAdivce.getWhId().equals(WareHouseType.GQWWHID)) {
            return;
        }
        //????????????
        GsGoodsUseCriteria exeample = new GsGoodsUseCriteria();
        exeample.createCriteria()
                //cdc??????id
                .andWhIdEqualTo(WareHouseType.CDCWHID)
                .andOrderNoEqualTo(gsOutStockAdivce.getSaleOrderNo())
                .andGoodsIdEqualTo(gsOutStockAdivce.getGoodsId());
        List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(exeample);
        if (gsGoodsUses.size() > 0) {
            GsGoodsUse goodsUse = gsGoodsUses.get(0);
            goodsUse.setLockQty(goodsUse.getLockQty() + gsOutStockAdivce.getQty());
            goodsUse.setUpdateTime(date);
            gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
        } else {
            GsGoodsUse gsGoodsUse = new GsGoodsUse();
            gsGoodsUse.setLockQty(gsOutStockAdivce.getQty());
            gsGoodsUse.setUpdateTime(date);
            gsGoodsUse.setUpdateBy(gsOutStockAdivceDto.getUserId());
            gsGoodsUse.setWhId(WareHouseType.CDCWHID);
//            gsGoodsUse.setOrderType(new Byte("1"));
            gsGoodsUse.setOrderNo(gsOutStockAdivce.getSaleOrderNo());
            gsGoodsUse.setGoodsId(gsOutStockAdivce.getGoodsId());
            gsGoodsUse.setCreateTime(date);
            gsGoodsUse.setCreateBy(gsOutStockAdivceDto.getUserId());
            gsGoodsUse.setOrderQty(0.0);
            gsGoodsUseMapper.insert(gsGoodsUse);
        }


        //???CDC???GQW ???????????????????????????
        GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
        usex.createCriteria()
                .andWhIdEqualTo(gsOutStockAdivce.getWhId())
                .andGoodsIdEqualTo(gsOutStockAdivce.getGoodsId())
                .andOrderNoEqualTo(gsOutStockAdivce.getSaleOrderNo());
        List<GsGoodsUse> gsGoodsUsessub = gsGoodsUseMapper.selectByExample(usex);
        if (gsGoodsUsessub.size() > 0) {
            GsGoodsUse goodsUse = gsGoodsUsessub.get(0);
            if ((goodsUse.getLockQty() - gsOutStockAdivce.getQty()) > 0) {
                goodsUse.setLockQty(goodsUse.getLockQty() - gsOutStockAdivce.getQty());
                goodsUse.setUpdateTime(date);
                gsGoodsUseMapper.updateByPrimaryKey(goodsUse);
            } else {
                gsGoodsUseMapper.deleteByPrimaryKey(goodsUse.getId());
            }


        }
    }

    /**
     * @author: zhaoguoliang
     * @date: Create in 2022/9/29 17:31
     * ????????????id?????????id??????????????????sn??????
     */
    @Override
    public List<GsGoodsSnVo> selectGoodsSnByWhIdAndGoodsId(Integer whId, Integer goodsId) {
        List<GsGoodsSnVo> gsGoodsSnVos = gsGoodsSnMapper.selectGoodsSnByWhIdAndGoodsId(whId, goodsId);
        Map<Integer, String> integerStringMap = baseCheckService.brandMap();
        for (GsGoodsSnVo gsGoodsSnVo : gsGoodsSnVos) {
            if (gsGoodsSnVo.getCbpb10() != null) {
                gsGoodsSnVo.setCbpb10(integerStringMap.get(Integer.parseInt(gsGoodsSnVo.getCbpb10())));
            }
            gsGoodsSnVo.setGoodsMsg(gsGoodsSnVo.getSn() + " - " + gsGoodsSnVo.getCbla09() + " - " + gsGoodsSnVo.getCbpb10() + " - " + gsGoodsSnVo.getCbpb12() + " - " + gsGoodsSnVo.getCbpb08());
        }

        return gsGoodsSnVos;
    }

    /**
     * @author: zhaoguoliang
     * @date: Create in 2022/9/29 17:31
     * ??????????????????sn??????
     */
    @Override
    public List<GsGoodsSnVo> selectGoodsSnByStatus(GsGoodsSnVo gsGoodsSnVo2) {
        List<GsGoodsSnVo> gsGoodsSnVos = gsGoodsSnMapper.selectGoodsSnByStatus(gsGoodsSnVo2);
        Map<Integer, String> integerStringMap = baseCheckService.brandMap();
        for (GsGoodsSnVo gsGoodsSnVo : gsGoodsSnVos) {
            if (gsGoodsSnVo.getCbpb10() != null) {
                gsGoodsSnVo.setCbpb10(integerStringMap.get(Integer.parseInt(gsGoodsSnVo.getCbpb10())));
            }
            gsGoodsSnVo.setGoodsMsg(gsGoodsSnVo.getSn() + " - " + gsGoodsSnVo.getCbla09() + " - " + gsGoodsSnVo.getCbpb10() + " - " + gsGoodsSnVo.getCbpb12() + " - " + gsGoodsSnVo.getCbpb08());
        }

        return gsGoodsSnVos;
    }

    @Override
    @Transactional
    public void mdfTakeSuggest2(CbpmDto cbpmDto) {

        CbpmCriteria cbpmCriteria = new CbpmCriteria();
        cbpmCriteria.createCriteria().andCbpk01EqualTo(cbpmDto.getCbpk01());
        //???????????????????????????
        List<Cbpm> cbpmList = cbpmMapper.selectByExample(cbpmCriteria);
        //????????????????????????
        List<CbpmDto.CbpmDtoItem> canList = cbpmDto.getGoodsSnList();
        //??????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????
        for (Cbpm cbpm : cbpmList) {
            int index = 0;
            //???????????????????????????
            for (CbpmDto.CbpmDtoItem cbpmDtoItem : canList) {
                //??????????????????????????????????????????
                if (cbpm.getCbpm09().equals(cbpmDtoItem.getSn())) {
                    index = 1;
                }
            }
            //index???1???????????????????????????????????????index???0????????????????????????????????????????????????????????????????????????
            if (index == 0) {
                //???????????????????????????
                if (cbpm.getCbpm11() == 1) {
                    throw new SwException("??????????????????Sn???????????????:" + cbpm.getCbpm09());
                }
           /*     CbpmCriteria cbpmCriteria2 =new CbpmCriteria();
                cbpmCriteria2.createCriteria()
                        .andCbpm09EqualTo(cbpm.getCbpm09());*/
                //??????CBPM???????????????
                cbpmMapper.deleteByPrimaryKey(cbpm.getCbpm01());

                GsGoodsSn gsGoodsSn3 = new GsGoodsSn();
                gsGoodsSn3.setStatus((byte) 1L);
                gsGoodsSn3.setGroudStatus((byte) 1L);
                GsGoodsSnCriteria gsGoodsSnCriteria = new GsGoodsSnCriteria();
                gsGoodsSnCriteria.createCriteria().andSnEqualTo(cbpm.getCbpm09());
                gsGoodsSnMapper.updateByExampleSelective(gsGoodsSn3, gsGoodsSnCriteria);
            }

        }

        for (CbpmDto.CbpmDtoItem cbpmDtoItem : canList) {
            int index = 0;
            for (Cbpm cbpm : cbpmList) {
                if (cbpm.getCbpm09().equals(cbpmDtoItem.getSn())) {
                    index = 1;
                }
            }
            if (index == 0) {
                //??????????????????????????????????????????????????????
                CbpmCriteria example = new CbpmCriteria();
                example.createCriteria()
                        .andCbpm09EqualTo(cbpmDtoItem.getSn());
                List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
                if (cbpms.size() > 0) {
                    throw new SwException("????????????Sn???????????????????????????????????????:" + cbpmDtoItem.getSn());
                }
                Cbpm cbpm = new Cbpm();
                cbpm.setCbpm07(0);
                cbpm.setCbpm08(cbpmDtoItem.getGoodsId());
                cbpm.setCbpm09(cbpmDtoItem.getSn());
                cbpm.setCbpm10(cbpmDtoItem.getLocationId());
                cbpm.setCbpm05(new Date());
                cbpm.setCbpk01(cbpmDto.getCbpk01());
                cbpm.setCbpm06(Integer.parseInt(SecurityUtils.getUserId() + ""));
                cbpmMapper.insertSelective(cbpm);

                GsGoodsSn gsGoodsSn2 = new GsGoodsSn();
                gsGoodsSn2.setId(cbpmDtoItem.getId());
                gsGoodsSn2.setStatus((byte) 2L);
                gsGoodsSn2.setGroudStatus((byte) 2L);
                gsGoodsSnMapper.updateByPrimaryKeySelective(gsGoodsSn2);
            }

           /* //??????????????????????????????????????????????????????
            CbpmCriteria example=new CbpmCriteria();
            example.createCriteria()
                    .andCbpm09EqualTo(changeSuggestModel.getCbpm09());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
            if(cbpms.size()>0 && !cbpms.get(0).getCbpm01().equals(changeSuggestModel.getCbpm01())){
                throw new SwException("????????????Sn???????????????????????????????????????:" + changeSuggestModel.getCbpm09());
            }

            Cbpm cbpm=new Cbpm();
            cbpm.setCbpm01(changeSuggestModel.getCbpm01());
            cbpm.setCbpm07(changeSuggestModel.getCbpm07());
            cbpm.setCbpm08(changeSuggestModel.getCbpm08());
            cbpm.setCbpm09(changeSuggestModel.getCbpm09());
            cbpm.setCbpm10(changeSuggestModel.getCbpm10());
            cbpm.setCbpm05(date);
            cbpm.setCbpm06(changeSuggestDto.getUserId());
            cbpmMapper.updateByPrimaryKey(cbpm);*/
        }

    }

    @Override
    public List<TakeGoodsOrderListVo> takeOrderListCk(TakeGoodsOrderListDto takeGoodsOrderListDto) {


        return cbpkMapper.takeOrderListCk(takeGoodsOrderListDto);
    }

    @Override
    public TakeGoodsOrderDetailVo takeOrderDetailIds(List<Integer> ids) {

        TakeGoodsOrderDetailVo takeGoodsOrderDetailVo = null;
        String saleOrderNo = "";
        List<TakeOrderGoodsVo> goods = new ArrayList<>();
        for (Integer id : ids) {
            takeGoodsOrderDetailVo = takeOrderDetail(id);
            if (takeGoodsOrderDetailVo.getSaleOrderNo() != null) {
                saleOrderNo = saleOrderNo + takeGoodsOrderDetailVo.getSaleOrderNo();
            }
            for (TakeOrderGoodsVo good : takeGoodsOrderDetailVo.getGoods()) {

                good.setTakegoodsid(id);
                good.setQty(good.getGoodsNum());
                if (good.getQty() != 0.0) {
                    goods.add(good);
                }
            }
//            goods.addAll(takeGoodsOrderDetailVo.getGoods());
        }
        takeGoodsOrderDetailVo.setGoods(goods);
        return takeGoodsOrderDetailVo;
    }

    @Override
    public TakeGoodsOrderDetailVo saleExitDetailByIds(SaleExitDetailByIdsDto saleExitDetailByIdsDto) {
        List<Integer> saleOrderIds = saleExitDetailByIdsDto.getSaleOrderIds();
        TakeGoodsOrderDetailVo res = new TakeGoodsOrderDetailVo();
        List<TakeOrderGoodsVo> goods = new ArrayList<>();
        for (Integer saleOrderId : saleOrderIds) {
            Cboa cboa = cboaMapper.selectByPrimaryKey(saleOrderId);

            res.setContacts(cboa.getCboa17());
            res.setCurrency(cboa.getCboa16());
            if (CurrencyEnum.CNY.getCode().equals(cboa.getCboa16())) {
                res.setCurrencyMsg(CurrencyEnum.CNY.getMsg());
            } else {
                res.setCurrencyMsg(CurrencyEnum.USD.getMsg());
            }
            res.setCustomerId(cboa.getCboa09());
            Cbca cbca = cbcaMapper.selectByPrimaryKey(res.getCustomerId());
            if (cbca != null) {
                res.setCustomerName(cbca.getCbca08());
                res.setCustomerLevel(cbca.getCbca28());
            }

            res.setCustomerNo(cboa.getCboa25());
            res.setOrderDate(cboa.getCboa08());
            res.setPhone(cboa.getCboa19());
            res.setReceiveAdress(cboa.getCboa18());

            res.setReceiver(cboa.getCboa17());
            res.setReceivPhone(cboa.getCboa19());
            res.setSaleOrderId(cboa.getCboa01());

            res.setSaleOrderNo(cboa.getCboa07());
//        SysUser user = sysUserMapper.selectByPrimaryKey(cbpk.getCbpk03().longValue());
//        if(user!=null){
//            res.setUserId(user.getUserId().intValue());
//            res.setUserName(user.getNickName());
//        }
            if (cboa.getCboa10() != null) {
                SysUser saleUser = sysUserMapper.selectByPrimaryKey(cboa.getCboa10().longValue());
                if (saleUser != null) {
                    res.setSaleUserId(saleUser.getUserId().intValue());
                    res.setSaleUserName(saleUser.getNickName());

                }
            }


            Cbwa cbwa = cbwaMapper.selectByPrimaryKey(saleExitDetailByIdsDto.getWhId());
            if (cbwa != null) {
                res.setWhName(cbwa.getCbwa09());
                res.setWhId(cbwa.getCbwa01());

            }
//        if(cbpk.getCheckStatus()!=null){
//            if(TakeOrderCheckStatus.NOCHECK.getCode().equals(cbpk.getCheckStatus())){
//                res.setCheckStatus(TakeOrderCheckStatus.NOCHECK.getMsg());
//            }else {
//                res.setCheckStatus(TakeOrderCheckStatus.CHECK.getMsg());
//            }
//
//
//        }

            CalaCriteria laexample = new CalaCriteria();
            laexample.createCriteria()
                    .andCala10EqualTo("????????????");
            List<Cala> calas = calaMapper.selectByExample(laexample);
            Map<Integer, String> brandMap = new HashMap<>();
            for (Cala cala : calas) {
                brandMap.put(cala.getCala01(), cala.getCala08());
            }
            CbobCriteria obex = new CbobCriteria();
            obex.createCriteria()
                    .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
                    .andCboa01EqualTo(cboa.getCboa01());
            List<Cbob> cbobs = cbobMapper.selectByExample(obex);
            TakeOrderGoodsVo good = null;
            for (Cbob cbob : cbobs) {
                //????????????
                GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
                usex.createCriteria()
                        .andGoodsIdEqualTo(cbob.getCbob08())
                        .andWhIdEqualTo(saleExitDetailByIdsDto.getWhId());
                List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
                good = new TakeOrderGoodsVo();
                Cbpb cbpb = cbpbMapper.selectByPrimaryKey(cbob.getCbob08());
                if (cbpb != null) {
                    good.setGoodsId(cbpb.getCbpb01());
                    good.setBrand(brandMap.get(cbpb.getCbpb10()));
                    good.setDescription(cbpb.getCbpb08());
                    good.setModel(cbpb.getCbpb12());
                    Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
                    if (cbpa != null) {
                        good.setGoodClass(cbpa.getCbpa07());
                    }
                }
                if (gsGoodsUses.size() == 0) {
                    throw new SwException("??????????????????????????????" + good.getModel());
                }

                GsGoodsUse goodsUse = gsGoodsUses.get(0);
                if (goodsUse.getLockQty() < cbob.getCbob09()) {
                    throw new SwException("????????????????????????" + good.getModel());

                }


                good.setNoSendQty(cbob.getCbob09() - cbob.getCbob10());
                good.setPrice(cbob.getCbob11());
                //todo
//            good.setRemark();
                good.setQty(cbob.getCbob09());
                good.setTotalPrice(cbob.getCbob12());
                good.setCbob01(cbob.getCbob01());
                //TODO
//            good.setSupplierId();


                good.setUseQty(cbob.getCbob09());


                //????????????
                good.setGoodsNum(0.0);


//                res.getGoods().add(good);
                goods.add(good);

            }

        }

            //????????????
//            GsGoodsUseCriteria usex = new GsGoodsUseCriteria();
//            usex.createCriteria()
//                    .andGoodsIdEqualTo()
//                    .andWhIdEqualTo(saleExitDetailByIdsDto.getWhId());
//            List<GsGoodsUse> gsGoodsUses = gsGoodsUseMapper.selectByExample(usex);
//            for (GsGoodsUse goodsUse : gsGoodsUses) {
//                good = new TakeOrderGoodsVo();
//                Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsUse.getGoodsId());
//                if (cbpb != null) {
//                    good.setGoodsId(cbpb.getCbpb01());
//                    good.setBrand(brandMap.get(cbpb.getCbpb10()));
//                    good.setDescription(cbpb.getCbpb08());
//                    good.setModel(cbpb.getCbpb12());
//                    Cbpa cbpa = cbpaMapper.selectByPrimaryKey(cbpb.getCbpb14());
//                    if (cbpa != null) {
//                        good.setGoodClass(cbpa.getCbpa07());
//                    }
//                }
//                CbobCriteria obex = new CbobCriteria();
//                obex.createCriteria()
//                        .andCbob08EqualTo(goodsUse.getGoodsId())
//                        .andCbob07EqualTo(DeleteFlagEnum.NOT_DELETE.getCode())
//                        .andCboa01EqualTo(cboa.getCboa01());
//                List<Cbob> cbobs = cbobMapper.selectByExample(obex);
//                if (cbobs.size() > 0) {
//                    Cbob cbob = cbobs.get(0);
//                    good.setNoSendQty(cbob.getCbob09() - cbob.getCbob10());
//                    good.setPrice(cbob.getCbob11());
//                    //todo
////            good.setRemark();
//                    good.setQty(goodsUse.getLockQty());
//                    good.setTotalPrice(cbob.getCbob12());
//                    good.setCbob01(cbob.getCbob01());
//                    //TODO
////            good.setSupplierId();
//
//                }
//
//                good.setUseQty(goodsUse.getLockQty());
//
//
//                //????????????
//                good.setGoodsNum(0.0);
//
//
////                res.getGoods().add(good);
//                goods.add(good);
//
//            }
//
//        }
        res.setGoods(goods);
        return res;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int TakeGoodsOrdersmtotal(List<Cbpm> itemLists) {
        if (itemLists.size() == 0) {
            throw new SwException("???????????????????????????");
        }


        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        Cbpk cbpk = cbpkMapper.selectByPrimaryKey(itemLists.get(0).getCbpk01());
        if (!cbpk.getCbpk11().equals(2)) {
            throw new SwException("????????????????????????");
        }

        for (Cbpm itemList : itemLists) {
            CbpmCriteria example = new CbpmCriteria();
            example.createCriteria()
                    .andCbpk01EqualTo(cbpk.getCbpk01())
                    .andCbpm09EqualTo(itemList.getCbpm09());
            List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
            if (cbpms.size() == 0) {
                throw new SwException("????????????Sn??????????????????????????????");
            }


/*GsGoodsSnCriteria example0=new GsGoodsSnCriteria();
            example0.createCriteria()
                    .andSnEqualTo(itemList.get(i).getCbpm09());
            List<GsGoodsSn> gsGoodsSnss = gsGoodsSnMapper.selectByExample(example0);
            if(gsGoodsSnss.size()>0){
                throw new SwException("?????????sn?????????" );
            }*/
            CbpmCriteria sfgu = new CbpmCriteria();
            sfgu.createCriteria()
                    .andCbpm09EqualTo(itemList.getCbpm09())
                    .andCbpm11EqualTo(ScanStatusEnum.YISAOMA.getCode())
                    .andCbpk01EqualTo(itemList.getCbpk01());
            List<Cbpm> cbpmss = cbpmMapper.selectByExample(sfgu);
            if (cbpmss.size() > 0) {
                throw new SwException("sn?????????"+itemList.getCbpm09());
            }


            itemList.setCbpm05(date);
            itemList.setCbpm06(Math.toIntExact(userid));
            itemList.setCbpm11(ScanStatusEnum.YISAOMA.getCode());


            GsGoodsSnCriteria example1 = new GsGoodsSnCriteria();
            example1.createCriteria()
                    .andSnEqualTo(itemList.getCbpm09());
            List<GsGoodsSn> gsGoodsSns = gsGoodsSnMapper.selectByExample(example1);
            if (gsGoodsSns.size() == 0) {
                throw new SwException("????????????Sn??????????????????SN??????");
            }

            GsGoodsSnCriteria example6 = new GsGoodsSnCriteria();
            example6.createCriteria()
                    .andSnEqualTo(itemList.getCbpm09())
                    .andStatusEqualTo(GoodsType.yck.getCode());
            List<GsGoodsSn> gsGoodsSnss = gsGoodsSnMapper.selectByExample(example6);
            if (gsGoodsSnss.size() > 0) {
                throw new SwException("????????????Sn??????????????????");
            }


            if(gsGoodsSns.get(0).getGoodsId()==null){
                throw new SwException("????????????Sn????????????????????????");
            }
            if(gsGoodsSns.get(0).getLocationId()==null){
                throw new SwException("????????????Sn????????????????????????");
            }
            if(gsGoodsSns.get(0).getWhId()==null){
                throw new SwException("????????????Sn????????????????????????");
            }
            //????????????,
            GsGoodsSkuCriteria tuiw = new GsGoodsSkuCriteria();
            tuiw.createCriteria()
                    .andGoodsIdEqualTo(gsGoodsSns.get(0).getGoodsId())
                    .andLocationIdEqualTo(gsGoodsSns.get(0).getLocationId())
                    .andWhIdEqualTo(gsGoodsSns.get(0).getWhId());
            List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(tuiw);
            if (gsGoodsSkus.size()> 0) {
                //????????????????????????1
                if(gsGoodsSkus.get(0).getQty()==1) {

                    List<GsGoodsSku> gsGoodsSkus1 = gsGoodsSkuMapper.selectByGoodsIdAndWhId(gsGoodsSns.get(0).getGoodsId(), gsGoodsSns.get(0).getWhId());
                    if (gsGoodsSkus1.size() > 0) {
                        GsGoodsSku gsGoodsSku = new GsGoodsSku();
                        gsGoodsSku.setId(gsGoodsSkus1.get(0).getId());
                        gsGoodsSku.setUpdateTime(date);
                        gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty() + gsGoodsSkus1.get(0).getQty());
                        gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSku);

                        gsGoodsSkuMapper.deleteByPrimaryKey(gsGoodsSkus.get(0).getId());
                    }
                    else {
                        GsGoodsSku gsGoodsSku = new GsGoodsSku();
                        gsGoodsSku.setId(gsGoodsSkus.get(0).getId());
                        gsGoodsSku.setCreateTime(date);
                        gsGoodsSku.setUpdateTime(date);
                        gsGoodsSku.setCreateBy(Math.toIntExact(userid));
                        gsGoodsSku.setUpdateBy(Math.toIntExact(userid));
                        gsGoodsSku.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                        gsGoodsSku.setGoodsId(gsGoodsSns.get(0).getGoodsId());
                        gsGoodsSku.setWhId(gsGoodsSns.get(0).getWhId());
                        gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty());
                        gsGoodsSku.setLocationId(null);
                        gsGoodsSkuMapper.updateByExample(gsGoodsSku, tuiw);
                    }
                }
                //??????????????????1
                else{
                    //????????????????????????????????????
                    GsGoodsSku gsGoodsSku = new GsGoodsSku();
                    gsGoodsSku.setId(gsGoodsSkus.get(0).getId());
                    gsGoodsSku.setQty(gsGoodsSkus.get(0).getQty()-1);
                    gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSku);

                    List<GsGoodsSku> gsGoodsSkus1 = gsGoodsSkuMapper.selectByGoodsIdAndWhId(gsGoodsSns.get(0).getGoodsId(), gsGoodsSns.get(0).getWhId());
                    if (gsGoodsSkus1.size() > 0) {
                        GsGoodsSku gsGoodsSkuc = new GsGoodsSku();
                        gsGoodsSkuc.setId(gsGoodsSkus1.get(0).getId());
                        gsGoodsSkuc.setUpdateTime(date);
                        gsGoodsSkuc.setQty(gsGoodsSkus1.get(0).getQty() + 1);
                        gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSkuc);
                    }else{
                        GsGoodsSku gsGoodsSkud = new GsGoodsSku();
                        // gsGoodsSkud.setId(gsGoodsSkus.get(0).getId());
                        gsGoodsSkud.setCreateTime(date);
                        gsGoodsSkud.setUpdateTime(date);
                        gsGoodsSkud.setCreateBy(Math.toIntExact(userid));
                        gsGoodsSkud.setUpdateBy(Math.toIntExact(userid));
                        gsGoodsSkud.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                        gsGoodsSkud.setGoodsId(gsGoodsSns.get(0).getGoodsId());
                        gsGoodsSkud.setWhId(gsGoodsSns.get(0).getWhId());
                        gsGoodsSkud.setQty(1.0);
                        gsGoodsSkud.setLocationId(null);
                        gsGoodsSkuMapper.insert(gsGoodsSkud);
                    }
                }

            }


            GsGoodsSn goodsSn = new GsGoodsSn();
            goodsSn = gsGoodsSns.get(0);
            goodsSn.setId(gsGoodsSns.get(0).getId());
            goodsSn.setCreateTime(gsGoodsSns.get(0).getCreateTime());
            goodsSn.setCreateBy(gsGoodsSns.get(0).getCreateBy());
            goodsSn.setUpdateTime(date);
            goodsSn.setUpdateBy(Math.toIntExact(userid));
            goodsSn.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
            goodsSn.setWhId(gsGoodsSns.get(0).getWhId());
            goodsSn.setGoodsId(gsGoodsSns.get(0).getGoodsId());
            goodsSn.setSn(itemList.getCbpm09());
            goodsSn.setGroudStatus(Groudstatus.XJ.getCode());
            goodsSn.setStatus(GoodsType.ckz.getCode());
            goodsSn.setLocationId(null);
            if (gsGoodsSns.get(0).getInTime() != null) {
                goodsSn.setInTime(gsGoodsSns.get(0).getInTime());
            }


            goodsSn.setInTime(gsGoodsSns.get(0).getInTime());


            GsGoodsSnCriteria example2 = new GsGoodsSnCriteria();
            example2.createCriteria()
                    .andSnEqualTo(itemList.getCbpm09());
            gsGoodsSnMapper.updateByExample(goodsSn, example2);

            cbpmMapper.updateByExampleSelective(itemList, example);
        }


        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editTakeGoodsOrdersn(TakeGoodsOrderAddDto takeGoodsOrderAddDto) {

        //?????????sn??????????????????
        GsGoodsSnCriteria oldsnexample = new GsGoodsSnCriteria();
        oldsnexample.createCriteria()
                .andSnEqualTo(takeGoodsOrderAddDto.getOldsn());
        List<GsGoodsSn> oldgsGoodsSns = gsGoodsSnMapper.selectByExample(oldsnexample);
        if(oldgsGoodsSns.size()>0){
            GsGoodsSn oldgoodsSn = new GsGoodsSn();
            oldgoodsSn.setStatus(GoodsType.yrk.getCode());
            gsGoodsSnMapper.updateByPrimaryKeySelective(oldgoodsSn);
        }
        else {
            throw new SwException("???sn?????????");
        }

       //???sn?????????
        GsGoodsSnCriteria newsnexample = new GsGoodsSnCriteria();
        newsnexample.createCriteria()
                .andSnEqualTo(takeGoodsOrderAddDto.getNewsn());
        List<GsGoodsSn> newgsGoodsSns = gsGoodsSnMapper.selectByExample(newsnexample);
        if(newgsGoodsSns.size()>0){
            GsGoodsSn newgoodsSn = new GsGoodsSn();
            newgoodsSn.setStatus(GoodsType.ckz.getCode());
            gsGoodsSnMapper.updateByPrimaryKeySelective(newgoodsSn);
        }else {
            throw new SwException("??????sn?????????");
        }



        //??????sn
        CbpmCriteria example = new CbpmCriteria();
        example.createCriteria().andCbpm09EqualTo(takeGoodsOrderAddDto.getOldsn())
                .andCbpk01EqualTo(takeGoodsOrderAddDto.getId());
        List<Cbpm> cbpms = cbpmMapper.selectByExample(example);
        if(cbpms.size() > 0){
            Cbpm cbpm = new Cbpm();
            cbpm.setCbpm09(takeGoodsOrderAddDto.getNewsn());
            if(newgsGoodsSns.size()>0) {
               cbpm.setCbpm10(newgsGoodsSns.get(0).getLocationId());

            }
            cbpmMapper.updateByExampleSelective(cbpm, example);
        }


        return 1;
    }

}
