package com.ruoyi.system.service.gson.impl;

import com.ruoyi.common.constant.AuditStatusConstants;
import com.ruoyi.common.core.domain.entity.Cbpa;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.DeleteFlagEnum;
import com.ruoyi.common.enums.UseFlagEnum;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.system.domain.Cbba;
import com.ruoyi.system.domain.CbbaCriteria;
import com.ruoyi.system.domain.Cboa;
import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.Do.GsGoodsSnDo;
import com.ruoyi.system.domain.Do.SaleOrderCheckDo;
import com.ruoyi.system.mapper.CbbaMapper;
import com.ruoyi.system.mapper.CbpbMapper;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.vo.CbpdVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.gson.BaseCheckService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.List;

/**
 * ClassName BaseCheckServiceImpl
 * Description
 * Create by gfy
 * Date 2022/8/1 10:17
 */
@Service
public class BaseCheckServiceImpl implements BaseCheckService {
    @Resource
    private CbpcMapper cbpcMapper;
    @Resource
    private CbsaMapper cbsaMapper;
    @Resource
    private CbpbMapper cbpbMapper;

    @Resource
    private CbbaMapper cbbaMapper;

    @Resource
    private CbpdMapper cbpdMapper;

    @Resource
    private CbibMapper cbibMapper;

    @Resource
    private GsGoodsSkuMapper gsGoodsSkuMapper;

    @Resource
    private CbwaMapper cbwaMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private CbcaMapper cbcaMapper;

    @Resource
    private CboaMapper cboaMapper;

    @Resource
    private CalaMapper calaMapper;

    @Resource
    private CbpaMapper cbpaMapper;

    @Resource
    private CblaMapper cblaMapper;

    @Resource
    private GsGoodsSnMapper gsGoodsSnMapper;

    @Override
    public Cbpb checkGoodsForUpdate(Integer goodsId, String goodsName) {
        if(goodsId==null){
            throw new SwException("???????????????????????????");
        }


        Cbpb cbpb = cbpbMapper.selectByPrimaryKeyForUpdate(goodsId);
        if(cbpb==null || DeleteFlagEnum.DELETE.equals(cbpb.getCbpb06())){

            throw new SwException("???????????????");
        }

        if(UseFlagEnum.JINYONG.equals(cbpb.getCbpb07())){
            throw new SwException("??????????????????");
        }
        return cbpb;
    }

    @Override
    public Cbpb checkGoods(Integer goodsId, String goodsName) {
        if(goodsId==null){
            throw new SwException("???????????????????????????");
        }


        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsId);
        if(cbpb==null || DeleteFlagEnum.DELETE.getCode().equals(cbpb.getCbpb06())){
            if(goodsName!=null){
                throw new SwException("??????????????????"+goodsName);
            }else {
                throw new SwException("??????????????????");
            }

        }

        if(UseFlagEnum.JINYONG.equals(cbpb.getCbpb07())){
            throw new SwException("??????????????????"+goodsName==null?"":cbpb.getCbpb12());
        }
        return cbpb;
    }

    @Override
    public Cbba checkTotalExist(Integer goodsId, String orderNO) {

        CbbaCriteria example=new CbbaCriteria();
        example.createCriteria()
                .andCbba07EqualTo(orderNO)
                .andCbba08EqualTo(goodsId)
                .andCbba06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
        List<Cbba> cbbas = cbbaMapper.selectByExample(example);
        if(cbbas.size()>0){
            return cbbas.get(0);
        }else {
            return null;
        }


    }

    @Override
    public Boolean saleOrderStatusChekc(SaleOrderCheckDo saleOrderCheckDo) {
        //0????????????  1???????????? 2???????????? 4:????????? 6???????????????  5?????????

        //??????????????????????????????
        Cboa cboa = saleOrderCheckDo.getCboa();
        if(saleOrderCheckDo.getOperateType().equals(AuditStatusConstants.SO_COMMIT)){
            if(!cboa.getCboa11().equals(AuditStatusConstants.SO_NO_COMMIT)){
                throw new SwException("?????????????????????????????????????????????");
            }
        }else if(saleOrderCheckDo.getOperateType().equals(AuditStatusConstants.SO_REVIEWWD)){
            if(!cboa.getCboa11().equals(AuditStatusConstants.SO_COMMIT)){
                throw new SwException("?????????????????????????????????????????????");
            }
        }else if(saleOrderCheckDo.getOperateType().equals(AuditStatusConstants.SO_TWO_REV)){
            if(!cboa.getCboa11().equals(AuditStatusConstants.SO_COMMIT)){
                throw new SwException("?????????????????????????????????????????????");
            }
        }else if(saleOrderCheckDo.getOperateType().equals(AuditStatusConstants.SO_COMPLETED)){
            if(!cboa.getCboa11().equals(AuditStatusConstants.SO_TWO_REV)){
                throw new SwException("???????????????????????????????????????????????????");
            }
        }


        return true;
    }




    @Override
    public GsGoodsSku checkGoodsSkuForUpdate(Integer Id) {
        if(Id==null){
            throw new ServiceException("???????????????");
        }

        GsGoodsSku gsGoodsSku= gsGoodsSkuMapper.selectByPrimaryKeyForUpdate(Id);
        if(gsGoodsSku==null || DeleteFlagEnum1.DELETE.getCode().equals(gsGoodsSku.getDeleteFlag())){

            throw new ServiceException("???????????????");
        }
        return gsGoodsSku;
    }

    @Override
    public Cbsa checksupplier(Integer supplierid) {
        if(supplierid==null){
            throw new SwException("??????????????????");
        }

        Cbsa cbsa = cbsaMapper.selectByPrimaryKey(supplierid);
        if(cbsa==null || DeleteFlagEnum.DELETE.getCode().equals(cbsa.getCbsa06())){

            throw new SwException("??????????????????");
        }
        return cbsa;
    }

    @Override
    public Cbwa checkStore(Integer Storeid) {
        if(Storeid==null){
            throw new SwException("???????????????");
        }

        Cbwa cbwa = cbwaMapper.selectByPrimaryKey(Storeid);
        if(cbwa==null || DeleteFlagEnum.DELETE.getCode().equals(cbwa.getCbwa06())){

            throw new SwException("???????????????");
        }
        return cbwa;
    }

    @Override
    public Cbpb checkGoods(Integer goodsId) {
        if(goodsId==null){
            throw new SwException("???????????????????????????");
        }


        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(goodsId);
        if(cbpb==null || DeleteFlagEnum.DELETE.getCode().equals(cbpb.getCbpb06())){

            throw new SwException("??????????????????");
        }

        if(UseFlagEnum.JINYONG.getCode().equals(cbpb.getCbpb07())){
            throw new SwException("??????????????????:");
        }
        return cbpb;
    }

    @Override
    public Cbla checkStoresku(Integer Storeskuid) {
        if(Storeskuid==null){
            throw new SwException("???????????????");
        }
        Cbla cbla = cblaMapper.selectByPrimaryKey(Storeskuid);
        if(cbla==null || DeleteFlagEnum.DELETE.getCode().equals(cbla.getCbla06())){

            throw new SwException("???????????????");
        }
        return cbla;
    }

    @Override
    public SysUser checkUserTask(Long userId, String auditPerm) {
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(userId);
        String auditPerm1 = sysUser.getAuditPerm();
        if(auditPerm1==null){
            throw new SwException("?????????????????????");
        }
        String[] s1 = auditPerm.split(",");
        String[] split = auditPerm1.split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(split));
       for (String s : s1) {
                if(!set.contains(s)){
                    throw new SwException("?????????????????????");
                }
            }
//        if(!set.contains(s1)){
//            throw new SwException("?????????????????????");
//        }
        return sysUser;
    }


    @Override
    public GsGoodsSku checkGoodsSku(Integer goodsId, Integer storeId) {
        if(goodsId==null ||storeId==null){
            throw new SwException("??????id????????????");
        }
        GsGoodsSkuCriteria example = new GsGoodsSkuCriteria();
        example.createCriteria().andGoodsIdEqualTo(goodsId)
                                .andWhIdEqualTo(storeId);
        List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(example);
        if(gsGoodsSkus.size()>0){
            throw new SwException("???????????????????????????");

        }
        return null;
    }
    //??????????????????????????????
    @Override
    public CbpdVo selectgoodsinfo(CbpdVo cbpdVo) {
        return cbpdMapper.selectgoodsinfo(cbpdVo);
    }

    @Override
    public Map<Integer, String> brandMap() {

        CalaCriteria laex=new CalaCriteria();
        laex.createCriteria()
                .andCala10EqualTo("????????????");
        List<Cala> calas = calaMapper.selectByExample(laex);
        Map<Integer,String> map=new HashMap<>();
        for (Cala cala : calas) {
            map.put(cala.getCala01(),cala.getCala08());
        }
        return map;



    }

    @Override
    public Map<Integer, Cbpa> classMap() {
        Map<Integer, Cbpa> map=new HashMap<>();

        CbpaCriteria paex=new CbpaCriteria();
        paex.createCriteria()
                .andCbpa06EqualTo(DeleteFlagEnum.NOT_DELETE.getCode());
        List<Cbpa> cbpas = cbpaMapper.selectByExample(paex);
        for (Cbpa cbpa : cbpas) {
            map.put(cbpa.getCbpa01(),cbpa);
        }
        return map;
    }

    @Override
    public Cbca checkCustomer(Integer customerId) {
        Cbca cbca = cbcaMapper.selectByPrimaryKey(customerId);
        if(cbca==null || DeleteFlagEnum.DELETE.getCode().equals(cbca.getCbca06())){
            throw new SwException("?????????????????????");

        }

        if(!"??????".equals(cbca.getCbca07())){
            throw new SwException("??????????????????");
        }

        return cbca;
    }

    @Override
    public Cboa checkSaleOrder(Integer orderId) {

        Cboa cboa = cboaMapper.selectByPrimaryKeyForUpdate(orderId);
        if(cboa==null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cboa.getCboa06())){
            throw new SwException("?????????????????????????????????");
        }

        return cboa;
    }

    @Override
    public GsGoodsSn checkGsGoodsSn(GsGoodsSnDo gsGoodsSnDo) {
        if(gsGoodsSnDo.getSn()==null){
            throw new SwException("sn????????????");
        }
        GsGoodsSnCriteria example = new GsGoodsSnCriteria();
        example.createCriteria().andSnEqualTo(gsGoodsSnDo.getSn());
        List<GsGoodsSn> gsGoodsSns = gsGoodsSnMapper.selectByExample(example);
        if(gsGoodsSns.size()==0){
            throw new SwException("sn????????????sn??????");
        }
        GsGoodsSn gsGoodsSn = gsGoodsSns.get(0);
        return gsGoodsSn;
    }


}



