package com.ruoyi.framework.web.service.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.SalesScheduledOrdersService;
import com.ruoyi.system.service.gson.TaskService;
import com.ruoyi.system.service.gson.impl.NumberGenerate;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SalesScheduledOrdersServiceImpl implements SalesScheduledOrdersService {

    @Resource
    private NumberGenerate numberGenerate;

    @Resource
    private GsSalesOrdersMapper gsSalesOrdersMapper;

    @Resource
    private GsSalesOrdersDetailsMapper gsSalesOrdersDetailsMapper;


    @Resource
    private TaskService taskService;

    @Resource
    private CbsaMapper cbsaMapper;


    @Resource
    private GsSalesOrdersInMapper gsSalesOrdersInMapper;

    @Resource
    private GsSalesOrdersChangeMapper gsSalesOrdersChangeMapper;

    @Resource
    private CauaMapper cauamaMapper;

    @Resource
    private CbcaMapper cbcaMapper;

    @Resource
    private CbpbMapper cbpbMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private CbwaMapper cbwaMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private GsSalesChangeMapper gsSalesChangeMapper;

    @Resource
    private GsOrdersInMapper gsOrdersInMapper;



    @Resource
    private CalaMapper calaMapper;
    /**
     * ?????????????????????
     *
     * @param gsSalesOrdersDto
     */
    @Override
    @Transactional
    public void addSalesScheduledOrders(GsSalesOrdersDto gsSalesOrdersDto) {
        GsSalesOrdersDto gsSalesOrdersDto1 = new GsSalesOrdersDto();

        List<GsSalesOrdersDetailsDto> goods=gsSalesOrdersDto.getGoods();
        if (goods.size() == 0) {
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        GsSalesOrders gsSalesOrders = new GsSalesOrders();
        Date date = new Date();
        gsSalesOrders.setCreateTime(date);
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setCreateBy(userid);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
        String saleOrdersNo = numberGenerate.getSaleOrdersNo();
        gsSalesOrders.setOrderNo(saleOrdersNo);
        gsSalesOrders.setSupplierId(gsSalesOrdersDto.getSupplierId());
        gsSalesOrders.setSalerId(gsSalesOrdersDto.getSalerId());
        gsSalesOrders.setCustomerId(gsSalesOrdersDto.getCustomerId());
        gsSalesOrders.setOrderDate(date);
        gsSalesOrders.setStatus(TaskStatus.mr.getCode().byteValue());
        gsSalesOrders.setWhId(gsSalesOrdersDto.getWhId());
        gsSalesOrders.setUserId(userid.intValue());
        gsSalesOrders.setPonumber(gsSalesOrdersDto.getPonumber());
        gsSalesOrders.setFactory(gsSalesOrdersDto.getFactory());
        gsSalesOrdersMapper.insertSelective(gsSalesOrders);

        GsSalesOrdersCriteria gsSalesOrdersCriteria = new GsSalesOrdersCriteria();
        gsSalesOrdersCriteria.createCriteria().andOrderNoEqualTo(saleOrdersNo);
        List<GsSalesOrders> gsSalesOrders1 = gsSalesOrdersMapper.selectByExample(gsSalesOrdersCriteria);

        GsSalesOrdersDetails gsSalesOrdersDetails = null;
        for (GsSalesOrdersDetailsDto good : goods) {
            gsSalesOrdersDetails=new GsSalesOrdersDetails();
            gsSalesOrdersDetails.setCreateTime(date);
            gsSalesOrdersDetails.setUpdateTime(date);
            gsSalesOrdersDetails.setCreateBy(String.valueOf(userid));
            gsSalesOrdersDetails.setUpdateBy(String.valueOf(userid));

            gsSalesOrdersDetails.setDeleteFlag(String.valueOf(DeleteFlagEnum1.NOT_DELETE.getCode()));
            if(good.getGoodsId()==null){
                throw new SwException("???????????????????????????");
            }
            gsSalesOrdersDetails.setGoodsId(good.getGoodsId());
            if(good.getQty()==null){
                throw new SwException("????????????????????????");
            }
            gsSalesOrdersDetails.setQty(good.getQty());
            if(good.getPrice()==null){
                throw new SwException("????????????????????????");
            }
            gsSalesOrdersDetails.setPrice(good.getPrice());
            gsSalesOrdersDetails.setRemark(good.getRemark());
            gsSalesOrdersDetails.setGsSalesOrders(String.valueOf(gsSalesOrders1.get(0).getId()));
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            gsSalesOrdersDetails.setFactory(good.getFactory());
            gsSalesOrdersDetailsMapper.insertSelective(gsSalesOrdersDetails);
        }

return;
        }
    /**
     * ?????????????????????
     *
     * @param gsSalesOrdersDto
     */
    @Override
    @Transactional
    public void editSalesScheduledOrders(GsSalesOrdersDto gsSalesOrdersDto) {
        GsSalesOrders gsSalesOrders1 = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if(!gsSalesOrders1.getStatus().equals(TaskStatus.mr.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }

        if (gsSalesOrdersDto.getId() == null) {
            throw new SwException("id????????????");
        }


        List<GsSalesOrdersDetailsDto> goods = gsSalesOrdersDto.getGoods();
        if (goods.size() == 0) {
            throw new SwException("???????????????????????????");
        }

        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if (!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(gsSalesOrders.getStatus().intValue())) {
            throw new SwException("?????????????????????????????????????????????");
        }

        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setId(gsSalesOrdersDto.getId());
        gsSalesOrders.setSupplierId(gsSalesOrdersDto.getSupplierId());
        gsSalesOrders.setSalerId(gsSalesOrdersDto.getSalerId());
        gsSalesOrders.setCustomerId(gsSalesOrdersDto.getCustomerId());
        gsSalesOrders.setOrderDate(date);
        gsSalesOrders.setWhId(gsSalesOrdersDto.getWhId());
        gsSalesOrders.setUserId(userid.intValue());
        gsSalesOrders.setPonumber(gsSalesOrdersDto.getPonumber());
        gsSalesOrders.setFactory(gsSalesOrdersDto.getFactory());

        gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);

        GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
        gsSalesOrdersDetailsCriteria.createCriteria()
                .andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrders.getId()));
        List<GsSalesOrdersDetails> gsSalesOrdersDetails1 =
                gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
        if(gsSalesOrdersDetails1.size()==0){
            throw new SwException("????????????????????????????????????");
        }

        GsSalesOrdersDetailsCriteria shgui = new GsSalesOrdersDetailsCriteria();
        shgui.createCriteria()
                .andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrdersDto.getId()));
        int i = gsSalesOrdersDetailsMapper.deleteByExample(shgui);


        GsSalesOrdersDetails gsSalesOrdersDetails = null;
        for (GsSalesOrdersDetailsDto good : goods) {
            gsSalesOrdersDetails = new GsSalesOrdersDetails();
            if(good.getId()==null){
                throw new SwException("?????????????????????id????????????");
            }
            /*if(!uio.contains(good.getId())){
                throw new SwException("????????????????????????????????????");
            }*/
          //  gsSalesOrdersDetails.setId(good.getId());
            gsSalesOrdersDetails.setCreateTime(date);
            gsSalesOrdersDetails.setUpdateTime(date);
            gsSalesOrdersDetails.setCreateBy(String.valueOf(userid));
            gsSalesOrdersDetails.setUpdateBy(String.valueOf(userid));
            gsSalesOrdersDetails.setDeleteFlag(String.valueOf(DeleteFlagEnum1.NOT_DELETE.getCode()));
            if(good.getGoodsId()==null){
                throw new SwException("???????????????????????????");
            }
            gsSalesOrdersDetails.setGoodsId(good.getGoodsId());
            if(good.getQty()==null){
                throw new SwException("???????????????????????????");
            }
            gsSalesOrdersDetails.setQty(good.getQty());
            if(good.getPrice()==null){
                throw new SwException("????????????????????????");
            }
            gsSalesOrdersDetails.setPrice(good.getPrice());
            gsSalesOrdersDetails.setRemark(good.getRemark());

            gsSalesOrdersDetails.setGsSalesOrders(gsSalesOrdersDto.getId().toString());
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            gsSalesOrdersDetails.setFactory(good.getFactory());
            /*GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria1 = new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriteria1.createCriteria()
                    .andGsSalesOrdersEqualTo(gsSalesOrdersDto.getId().toString())
            .andGoodsIdEqualTo(good.getGoodsId());*/
            gsSalesOrdersDetailsMapper.insertSelective(gsSalesOrdersDetails);

        }
        return;

    }
    @Override
    @Transactional
    public void deleteSalesScheduledOrders(DeleteSaleOrderDto deleteSaleOrderDto) {
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(deleteSaleOrderDto.getOrderId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!SaleOrderStatusEnums.WEITIJIAO.getCode().equals(gsSalesOrders.getStatus().intValue())){
            throw new SwException("?????????????????????????????????????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setId(deleteSaleOrderDto.getOrderId());
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());
        int i = gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);
    }
    //??????
    @Override
    public List<GsSalesOrdersVo> saleOrderList(GsSalesOrdersDo gsSalesOrdersDo) {
        List<GsSalesOrdersVo> saleOrderListVos = gsSalesOrdersMapper.saleOrderList(gsSalesOrdersDo);


        return saleOrderListVos;
    }
   //??????
    @Override
    public List<GsSalesOrdersDetailsVo> saleOrderListdetail(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo) {

        List<GsSalesOrdersDetailsVo> saleOrderListVos = gsSalesOrdersMapper.saleOrderListdetail(gsSalesOrdersDetailsVo);

        return saleOrderListVos;
    }

    //??????????????????????????????
    @Override
    public List<GsSalesOrdersDetailsVo> saleOrderAdvance(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo) {

        List<GsSalesOrdersDetailsVo> salesOrdersChangeVos = gsSalesOrdersMapper.saleOrderListdetails(gsSalesOrdersDetailsVo);
        return salesOrdersChangeVos;
    }

    @Override
    @Transactional
    public void salesScheduledOrderssh(GsSalesOrdersDto gsSalesOrdersDto) {
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.mr.getCode().equals(gsSalesOrders.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setStatus(TaskStatus.sh.getCode().byteValue());
        int i = gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);

    }

    @Override
    @Transactional

    public void salesScheduledOrdersfs(GsSalesOrdersDto gsSalesOrdersDto) {
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrders.getStatus().intValue())){
            throw new SwException("????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setStatus(TaskStatus.mr.getCode().byteValue());
        int i = gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);
    }

    @Override
    @Transactional
    public void salesScheduledOrdersbjwc(GsSalesOrdersDto gsSalesOrdersDto) {
        if(gsSalesOrdersDto.getId()==null){
            throw new SwException("???????????????id????????????");
        }
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrders.getStatus().intValue())){
            throw new SwException("??????????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setId(gsSalesOrdersDto.getId());
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setStatus(TaskStatus.bjwc.getCode().byteValue());

        GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
        gsSalesOrdersDetailsCriteria.createCriteria().andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrdersDto.getId()));
        List<GsSalesOrdersDetails> gsSalesOrdersDetails = gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
        if(gsSalesOrdersDetails.size() == 0){
            throw new SwException("???????????????????????????");
        }
   /*     //??????id
        Integer whId = gsSalesOrders.getWhId();
        //??????
        String orderNo = gsSalesOrders.getOrderNo();
        //????????????
        Cbsa cbsa = cbsaMapper.selectByPrimaryKey(gsSalesOrders.getSupplierId());
        if(cbsa == null){
            throw new SwException("????????????????????????");
        }
        //?????????
        Integer supplierId = gsSalesOrders.getSupplierId();

        String vendername = cbsa.getCbsa08();
        for(int i=0;i<gsSalesOrdersDetails.size();i++){

            GsGoodsSkuDo goodsSkuDo = new GsGoodsSkuDo();
            goodsSkuDo.setGoodsId(gsSalesOrdersDetails.get(i).getGoodsId());
            goodsSkuDo.setWhId(whId);
            goodsSkuDo.setQty(gsSalesOrdersDetails.get(i).getQty());
            taskService.addGsGoodsSku(goodsSkuDo);


            CbibDo cbibDo = new CbibDo();
            cbibDo.setCbib02(whId);
            cbibDo.setCbib03(orderNo);
            cbibDo.setCbib05(String.valueOf(TaskType.xsydd.getCode()));
            cbibDo.setCbib06(vendername);
            cbibDo.setCbib07(gsSalesOrdersDto.getId());
            cbibDo.setCbib08(gsSalesOrdersDetails.get(i).getGoodsId());
            cbibDo.setCbib11(gsSalesOrdersDetails.get(i).getQty());
            cbibDo.setCbib12(gsSalesOrdersDetails.get(i).getPrice().doubleValue());
            cbibDo.setCbib13((double) 0);
            cbibDo.setCbib14((double) 0);
            cbibDo.setCbib17(TaskType.xsydd.getMsg());
            cbibDo.setCbib19(supplierId);
            taskService.InsertCBIB(cbibDo);
        }*/

        int i = gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);
    }

    @Override
    @Transactional

    public void salesScheduledOrdersqxwc(GsSalesOrdersDto gsSalesOrdersDto) {
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersDto.getId());
        if (gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.bjwc.getCode().equals(gsSalesOrders.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setStatus(TaskStatus.sh.getCode().byteValue());
        int i = gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);
    }

    @Transactional
    @Override
    public int addSubscribetotheinventoryslip(List<GsSalesOrdersIn>  itemList) {
        if(itemList.size() == 0){
            throw new SwException("???????????????????????????");
        }
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(itemList.get(0).getGsSalesOrders());
        gsSalesOrders.setId(itemList.get(0).getGsSalesOrders());
        gsSalesOrders.setStatuss(1);
        gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);


        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        GsSalesOrdersInMapper mapper = session.getMapper(GsSalesOrdersInMapper.class);
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setCreateTime(date);
            itemList.get(i).setUpdateTime(date);
            itemList.get(i).setCreateBy(userid);
            itemList.get(i).setUpdateBy(userid);
            itemList.get(i).setDeleteFlag(String.valueOf(DeleteFlagEnum.NOT_DELETE.getCode()));
//            gsSalesOrdersInDto.get(i).setPonumber(gsSalesOrdersInDto.getPonumber());
//            gsSalesOrdersInDto.setGoodsId(gsSalesOrdersInDto.getGoodsId());
//            gsSalesOrdersInDto.setInQty(gsSalesOrdersInDto.getInQty());
//            gsSalesOrdersInDto.setGsSalesOrders(gsSalesOrdersInDto.getGsSalesOrders());
            itemList.get(i).setStatus(TaskStatus.mr.getCode().byteValue());
            itemList.get(i).setFactory(itemList.get(i).getFactory());
            mapper.insertSelective(itemList.get(i));
            if (i % 10 == 9) {//???10???????????????
                session.commit();
                session.clearCache();
            }
        }
        session.commit();
        session.clearCache();
        return 1;
    }

    @Override
    @Transactional

    public void editSubscribetotheinventoryslip(GsSalesOrdersInDto gsSalesOrdersInDto) {
        /*GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
        // int i = Integer.parseInt(gsSalesOrdersIn.getDeleteFlag());
        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.mr.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }
        List<GsSalesOrdersIn> goods = gsSalesOrdersInDto.getGoods();

        gsSalesOrdersInDto.getGsSalesOrders();*/
        List<GsSalesOrdersIn> goods = gsSalesOrdersInDto.getGoods();
        GsSalesOrdersInCriteria gsSalesOrdersInCriteria = new GsSalesOrdersInCriteria();
        gsSalesOrdersInCriteria.createCriteria().andInidEqualTo(gsSalesOrdersInDto.getId());
        int i = gsSalesOrdersInMapper.deleteByExample(gsSalesOrdersInCriteria);


        Long userid = SecurityUtils.getUserId();
        Date date = new Date();


        GsSalesOrdersIn gsSalesOrdersDetails = null;
        for (GsSalesOrdersIn good : goods) {
            gsSalesOrdersDetails = new GsSalesOrdersIn();
            gsSalesOrdersDetails.setCreateTime(date);
            gsSalesOrdersDetails.setCreateBy(userid);
            gsSalesOrdersDetails.setUpdateTime(date);
            gsSalesOrdersDetails.setUpdateBy(userid);
            gsSalesOrdersDetails.setDeleteFlag(String.valueOf(DeleteFlagEnum.NOT_DELETE.getCode()));
            gsSalesOrdersDetails.setPonumber(gsSalesOrdersInDto.getPonumber());
            gsSalesOrdersDetails.setGoodsId(gsSalesOrdersInDto.getGoodsId());
            gsSalesOrdersDetails.setInQty(gsSalesOrdersInDto.getInQty());
            gsSalesOrdersDetails.setGsSalesOrders(gsSalesOrdersInDto.getGsSalesOrders());
            gsSalesOrdersDetails.setStatus(TaskStatus.mr.getCode().byteValue());
            gsSalesOrdersDetails.setFactory(gsSalesOrdersInDto.getFactory());

            gsSalesOrdersInMapper.insertSelective(gsSalesOrdersDetails);

        }

        }

    @Override
    @Transactional
    public void deleteSubscribetotheinventoryslip(GsSalesOrdersInDto gsSalesOrdersInDto) {
        GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.mr.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersIn.setUpdateTime(date);
        gsSalesOrdersIn.setUpdateBy(userid);
        gsSalesOrdersIn.setDeleteFlag(String.valueOf(DeleteFlagEnum.DELETE.getCode()));
        gsSalesOrdersInMapper.updateByPrimaryKeySelective(gsSalesOrdersIn);
    }

    @Override
    public List<GsSalesOrdersInVo> seleteSubscribetotheinventoryslip(GsSalesOrdersInVo gsSalesOrdersInVo) {
        List<GsSalesOrdersInVo> gsSalesOrdersInVos = gsSalesOrdersInMapper.seleteSubscribetotheinventoryslip(gsSalesOrdersInVo);
        return gsSalesOrdersInVos;
    }

    //??????????????????????????????
    @Override
    public List<GsSalesOrdersInVo> selectSalesReceiptList(GsSalesOrdersInVo gsSalesOrdersInVo) {
        List<GsSalesOrdersInVo> gsSalesOrdersInVos = gsSalesOrdersInMapper.selectSalesReceiptList(gsSalesOrdersInVo);
        return gsSalesOrdersInVos;
    }

    @Override
    @Transactional

    public void subscribetotheinventoryslipsh(GsSalesOrdersInDto gsSalesOrdersInDto) {
Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());

        // GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
//        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
//            throw new SwException("?????????????????????");
//        }

        GsSalesOrdersIn gsSalesOrdersIn=new GsSalesOrdersIn();
/*
        if(!TaskStatus.mr.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }*/

        GsSalesOrdersInCriteria gsSalesOrdersInCriteria = new GsSalesOrdersInCriteria();
        gsSalesOrdersInCriteria.createCriteria()
                .andGsSalesOrdersEqualTo(gsSalesOrdersInDto.getId());
        List<GsSalesOrdersIn> gsSalesOrdersIns = gsSalesOrdersInMapper.selectByExample(gsSalesOrdersInCriteria);
        double sum = gsSalesOrdersIns.stream().mapToDouble(GsSalesOrdersIn::getInQty).sum();

        //???????????????
        GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
        gsSalesOrdersDetailsCriteria.createCriteria()
                .andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrdersInDto.getId()));
        List<GsSalesOrdersDetails> gsSalesOrdersDetails = gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
        double sum1 = gsSalesOrdersDetails.stream().mapToDouble(GsSalesOrdersDetails::getQty).sum();

         if(sum>sum1){
            throw new SwException("???????????????????????????????????????");
        }
        gsSalesOrders.setId(gsSalesOrdersInDto.getId());

        gsSalesOrders.setCas(TaskStatus.sh.getCode());
        gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders);
//        Long userid = SecurityUtils.getUserId();
//        Date date = new Date();
//        gsSalesOrdersIn.setUpdateTime(date);
//        gsSalesOrdersIn.setUpdateBy(userid);
//        gsSalesOrdersIn.setStatus(TaskStatus.sh.getCode().byteValue());
//        gsSalesOrdersInMapper.updateByPrimaryKeySelective(gsSalesOrdersIn);
    }

    @Override
    @Transactional

    public void subscribetotheinventoryslipfs(GsSalesOrdersInDto gsSalesOrdersInDto) {
        GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersIn.setUpdateTime(date);
        gsSalesOrdersIn.setUpdateBy(userid);
        gsSalesOrdersIn.setStatus(TaskStatus.mr.getCode().byteValue());
        gsSalesOrdersInMapper.updateByPrimaryKeySelective(gsSalesOrdersIn);
    }

    @Override
    @Transactional
    public void subscribetotheinventoryslipbjwc(GsSalesOrdersInDto gsSalesOrdersInDto) throws InterruptedException {
GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("??????????????????????????????");
        }

        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersIn.getGsSalesOrders());
        if(gsSalesOrders == null ){
            throw new SwException("?????????????????????");
        }
        Integer whId = gsSalesOrders.getWhId();
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersIn.setUpdateTime(date);
        gsSalesOrdersIn.setUpdateBy(userid);
        gsSalesOrdersIn.setStatus(TaskStatus.bjwc.getCode().byteValue());

        GsGoodsSkuDo goodsSkuDo = new GsGoodsSkuDo();
        goodsSkuDo.setGoodsId(gsSalesOrdersIn.getGoodsId());
        goodsSkuDo.setWhId(whId);
        goodsSkuDo.setQty(gsSalesOrdersIn.getInQty());
        taskService.addGsGoodsSku(goodsSkuDo);


        //??????
        String orderNo = gsSalesOrders.getOrderNo();
        //????????????
        Cbsa cbsa = cbsaMapper.selectByPrimaryKey(gsSalesOrders.getSupplierId());
        if(cbsa == null){
            throw new SwException("?????????????????????");
        }
        String vendername = cbsa.getCbsa08();
        //?????????
        Integer supplierId = gsSalesOrders.getSupplierId();


        CbibDo cbibDo = new CbibDo();
        cbibDo.setCbib02(whId);
        cbibDo.setCbib03(orderNo);
        cbibDo.setCbib05(String.valueOf(TaskType.xsydd.getCode()));
        cbibDo.setCbib06(vendername);
        cbibDo.setCbib07(gsSalesOrdersIn.getId());
        cbibDo.setCbib08(gsSalesOrdersIn.getGoodsId());
        cbibDo.setCbib11(gsSalesOrdersIn.getInQty());
        cbibDo.setCbib12((double) 0);
        cbibDo.setCbib13((double) 0);
        cbibDo.setCbib14((double) 0);
        cbibDo.setCbib17(TaskType.yydrkd.getMsg());
        cbibDo.setCbib19(supplierId);
        taskService.InsertCBIB(cbibDo);

        GsSalesOrders gsSalesOrders1 = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersIn.getGsSalesOrders());
        gsSalesOrders1.setStatus(TaskStatus.bjwc.getCode().byteValue());
        gsSalesOrders1.setId(gsSalesOrdersIn.getGsSalesOrders());
        gsSalesOrdersMapper.updateByPrimaryKeySelective(gsSalesOrders1);



        gsSalesOrdersInMapper.updateByPrimaryKeySelective(gsSalesOrdersIn);
    }

    @Override
    @Transactional
    public void subscribetotheinventoryslipqxwc(GsSalesOrdersInDto gsSalesOrdersInDto) {
        GsSalesOrdersIn gsSalesOrdersIn = gsSalesOrdersInMapper.selectByPrimaryKey(gsSalesOrdersInDto.getId());
        if (gsSalesOrdersIn == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(Integer.parseInt(gsSalesOrdersIn.getDeleteFlag()))) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.bjwc.getCode().equals(gsSalesOrdersIn.getStatus().intValue())){
            throw new SwException("????????????????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersIn.setUpdateTime(date);
        gsSalesOrdersIn.setUpdateBy(userid);
        gsSalesOrdersIn.setStatus(TaskStatus.sh.getCode().byteValue());
        gsSalesOrdersInMapper.updateByPrimaryKeySelective(gsSalesOrdersIn);
    }

    @Transactional
    @Override
    public int addGsSalesOrdersChange(List<GsSalesOrdersChange>  gsSalesOrdersChangeDto) {

        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        GsSalesOrdersChangeMapper mapper = session.getMapper(GsSalesOrdersChangeMapper.class);
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
//        GsSalesOrdersChange gsSalesOrdersChange = new GsSalesOrdersChange();
//        BeanUtils.copyProperties(gsSalesOrdersChangeDto, gsSalesOrdersChange);



        for (int i = 0; i < gsSalesOrdersChangeDto.size(); i++) {
            gsSalesOrdersChangeDto.get(i).setCreateTime(date);
            gsSalesOrdersChangeDto.get(i).setCreateBy(userid);
            gsSalesOrdersChangeDto.get(i).setUpdateTime(date);
            gsSalesOrdersChangeDto.get(i).setUpdateBy(userid);
            gsSalesOrdersChangeDto.get(i).setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
            String qualityinspectionlistNos = numberGenerate.getQualityinspectionlistNos();
            gsSalesOrdersChangeDto.get(i).setOrderNo(qualityinspectionlistNos);
            gsSalesOrdersChangeDto.get(i).setOrderDate(date);


            gsSalesOrdersChangeDto.get(i).setStatus(TaskStatus.mr.getCode().byteValue());
            mapper.insertSelective(gsSalesOrdersChangeDto.get(i));
            if (i % 10 == 9) {//???10???????????????
                session.commit();
                session.clearCache();
            }
        }

        if(gsSalesOrdersChangeDto.get(0).getGsSalesOrders()==null){
            throw new SwException("??????????????????");
        }
        GsSalesOrders gsSalesOrders = new GsSalesOrders();
        gsSalesOrders.setStatuss((int) TaskStatus.mr.getCode().byteValue());
        GsSalesOrdersCriteria gsSalesOrdersCriteria = new GsSalesOrdersCriteria();
        gsSalesOrdersCriteria.createCriteria()
                .andIdEqualTo(gsSalesOrdersChangeDto.get(0).getGsSalesOrders());
        gsSalesOrdersMapper.updateByExampleSelective(gsSalesOrders,gsSalesOrdersCriteria);


        session.commit();
        session.clearCache();
        return 1;
    }

    @Override
    @Transactional
    public void editGsSalesOrdersChange(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesOrdersChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrdersChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersChange.setUpdateTime(date);
        gsSalesOrdersChange.setUpdateBy(userid);
        gsSalesOrdersChange.setSalerId(gsSalesOrdersChangeDto.getSalerId());
        gsSalesOrdersChange.setSupplierId(gsSalesOrdersChangeDto.getSupplierId());
        gsSalesOrdersChange.setGoodsclassify(gsSalesOrdersChangeDto.getGoodsclassify());
        gsSalesOrdersChange.setQty(gsSalesOrdersChangeDto.getQty());
        gsSalesOrdersChange.setGsSalesOrders(gsSalesOrdersChangeDto.getGsSalesOrders());
        gsSalesOrdersChangeMapper.updateByPrimaryKeySelective(gsSalesOrdersChange);
    }

    @Override
    @Transactional
    public void deleteGsSalesOrdersChange(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        GsSalesChange gsSalesChange = gsSalesChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesChange.setId(gsSalesOrdersChangeDto.getId());
        gsSalesChange.setUpdateTime(date);
        gsSalesChange.setUpdateBy(userid);
        gsSalesChange.setDeleteFlag(DeleteFlagEnum.DELETE.getCode().byteValue());
        gsSalesChangeMapper.updateByPrimaryKeySelective(gsSalesChange);
    }

    @Override
    public List<GsSalesOrdersChangeVo> seleteGsSalesOrdersChange(GsSalesOrdersChangeVo gsSalesOrdersChangeVo) {
        List<GsSalesOrdersChangeVo> gsSalesOrdersChangeVos = gsSalesOrdersChangeMapper.seleteGsSalesOrdersChange(gsSalesOrdersChangeVo);
        return gsSalesOrdersChangeVos;
    }

    @Override
    @Transactional
    public void gsSalesOrdersChangesh(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {

//id?????????id
        GsSalesChange gsSalesChange = gsSalesChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        gsSalesChange.setId(gsSalesOrdersChangeDto.getId());
        gsSalesChange.setStatus(TaskStatus.sh.getCode().byteValue());





        GsSalesOrdersDetailsCriteria  ssm= new GsSalesOrdersDetailsCriteria();
        ssm.createCriteria()
                .andGsSalesOrdersEqualTo(String.valueOf(gsSalesChange.getGsid()));
        //.andGoodsIdEqualTo(gsSalesOrdersChangeDto.getGoodsId());
        List<GsSalesOrdersDetails> gsSalesOrdersDetailss = gsSalesOrdersDetailsMapper.selectByExample(ssm);
        if(gsSalesOrdersDetailss.size() == 0){
            throw new SwException("?????????????????????");
        }
        if(gsSalesOrdersDetailss.get(0).getQty()==null){
            throw new SwException("???????????????????????????");
        }

        for(int j=0;j<gsSalesOrdersDetailss.size();j++){


            GsSalesOrdersChangeCriteria gsSalesOrdersChangeCriteria = new GsSalesOrdersChangeCriteria();
            gsSalesOrdersChangeCriteria.createCriteria()
                    .andChangeidEqualTo(gsSalesOrdersChangeDto.getId())
                    .andGoodsIdEqualTo(gsSalesOrdersDetailss.get(j).getGoodsId());
            List<GsSalesOrdersChange> gsSalesOrdersChanges = gsSalesOrdersChangeMapper.selectByExample(gsSalesOrdersChangeCriteria);
            if(gsSalesOrdersChanges.size()==0){
                throw new SwException("????????????????????????");
            }
            Double qty = gsSalesOrdersDetailss.get(j).getQty();

            if(qty < gsSalesOrdersChanges.get(0).getQty()){
                throw new SwException("?????????????????????????????????");
            }


            GsSalesOrdersDetails gsSalesOrdersDetails = new GsSalesOrdersDetails();
            gsSalesOrdersDetails.setQty(gsSalesOrdersChanges.get(0).getQty());


            GsSalesOrdersDetailsCriteria  sm= new GsSalesOrdersDetailsCriteria();
            sm.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(gsSalesChange.getGsid()))
                    .andGoodsIdEqualTo(gsSalesOrdersDetailss.get(j).getGoodsId());
            gsSalesOrdersDetailsMapper.updateByExampleSelective(gsSalesOrdersDetails,sm);


           /* if(!TaskStatus.mr.getCode().equals(gsSalesOrdersChangeDto.getStatus().intValue())){
                throw new SwException("???????????????????????????");
            }

            Long userid = SecurityUtils.getUserId();
            Date date = new Date();
            GsSalesOrdersChange gsSalesOrdersChange = new GsSalesOrdersChange();
            gsSalesOrdersChange.setUpdateTime(date);
            gsSalesOrdersChange.setUpdateBy(userid);
            gsSalesOrdersChange.setStatus(TaskStatus.sh.getCode().byteValue());

            GsSalesOrdersChangeCriteria gsSalesOrdersChangeCriteria1 = new GsSalesOrdersChangeCriteria();
            gsSalesOrdersChangeCriteria1.createCriteria()
                    .andChangeidEqualTo(gsSalesOrdersChangeDto.getId())
                    .andGoodsIdEqualTo(gsSalesOrdersDetailss.get(j).getGoodsId());
            gsSalesOrdersChangeMapper.updateByExampleSelective(gsSalesOrdersChange,gsSalesOrdersChangeCriteria1);*/

        }




  /*      GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesOrdersChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrdersChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }*/
        gsSalesChangeMapper.updateByPrimaryKeySelective(gsSalesChange);


     /*   Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersChange.setUpdateTime(date);
        gsSalesOrdersChange.setUpdateBy(userid);
        gsSalesOrdersChange.setStatus(TaskStatus.sh.getCode().byteValue());


        gsSalesOrdersChangeMapper.updateByPrimaryKeySelective(gsSalesOrdersChange);*/
    }

    @Override
    @Transactional
    public void gsSalesOrdersChangefs(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesOrdersChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrdersChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrdersChange.getStatus().intValue())){
            throw new SwException("????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersChange.setUpdateTime(date);
        gsSalesOrdersChange.setUpdateBy(userid);
        gsSalesOrdersChange.setStatus(TaskStatus.mr.getCode().byteValue());
        gsSalesOrdersChangeMapper.updateByPrimaryKeySelective(gsSalesOrdersChange);
    }

    @Override
    @Transactional
    public void gsSalesOrdersChangebjwc(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) throws InterruptedException {
        GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesOrdersChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrdersChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.sh.getCode().equals(gsSalesOrdersChange.getStatus().intValue())){
            throw new SwException("????????????????????????");
        }
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesOrdersChange.getGsSalesOrders());

        if(gsSalesOrders == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrders.getDeleteFlag().intValue())){
            throw new SwException("????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersChange.setUpdateTime(date);
        gsSalesOrdersChange.setUpdateBy(userid);
        gsSalesOrdersChange.setStatus(TaskStatus.bjwc.getCode().byteValue());

        GsGoodsSkuDo goodsSkuDo = new GsGoodsSkuDo();
       // goodsSkuDo.setGoodsId(gsSalesOrders.());
        goodsSkuDo.setWhId(gsSalesOrders.getWhId());
        goodsSkuDo.setQty(gsSalesOrdersChange.getQty());
        taskService.addGsGoodsSku(goodsSkuDo);


        //??????
        String orderNo = gsSalesOrders.getOrderNo();
        //????????????
        Cbsa cbsa = cbsaMapper.selectByPrimaryKey(gsSalesOrders.getSupplierId());
        if(cbsa == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(cbsa.getCbsa06())){
            throw new SwException("????????????????????????");
        }
        String vendername = cbsa.getCbsa08();
        //?????????
        Integer supplierId = gsSalesOrders.getSupplierId();


        CbibDo cbibDo = new CbibDo();
        cbibDo.setCbib02(gsSalesOrders.getWhId());
        cbibDo.setCbib03(orderNo);
        cbibDo.setCbib05(String.valueOf(TaskType.xsydd.getCode()));
        cbibDo.setCbib06(vendername);
        cbibDo.setCbib07(gsSalesOrdersChange.getId());
        cbibDo.setCbib08(gsSalesOrdersChange.getGoodsId());
        cbibDo.setCbib11((double) 0);
        cbibDo.setCbib12((double) 0);
        cbibDo.setCbib13((double) 0);
        cbibDo.setCbib14((double) 0);
        cbibDo.setCbib15(gsSalesOrdersChange.getQty());
        cbibDo.setCbib17(TaskType.yydrkd.getMsg());
        cbibDo.setCbib19(supplierId);
        taskService.InsertCBIB(cbibDo);




        gsSalesOrdersChangeMapper.updateByPrimaryKeySelective(gsSalesOrdersChange);
    }

    @Override
    @Transactional
    public void gsSalesOrdersChangeqxwc(GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(gsSalesOrdersChangeDto.getId());
        if (gsSalesOrdersChange == null || !DeleteFlagEnum.NOT_DELETE.getCode().equals(gsSalesOrdersChange.getDeleteFlag().intValue())) {
            throw new SwException("?????????????????????");
        }

        if(!TaskStatus.bjwc.getCode().equals(gsSalesOrdersChange.getStatus().intValue())){
            throw new SwException("????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        gsSalesOrdersChange.setUpdateTime(date);
        gsSalesOrdersChange.setUpdateBy(userid);
        gsSalesOrdersChange.setStatus(TaskStatus.sh.getCode().byteValue());
        gsSalesOrdersChangeMapper.updateByPrimaryKeySelective(gsSalesOrdersChange);
    }

    @Override
    public List<GsSalesOrderssVo> seleteSalesbookingsummary(GsSalesOrderssVo gsSalesOrderssVo) {
        return gsSalesOrdersMapper.seleteSalesbookingsummary(gsSalesOrderssVo);
    }

    @Override
    @Transactional
    public int insertSwJsStores(List<GsSalesOrdersdrDto> itemList) {
        if(itemList.size() == 0){
            throw new SwException("????????????");
        }
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        if("".equals(itemList.get(0).getSupperilername())){
            throw new SwException("????????????????????????");
        }
        String supperilername = itemList.get(0).getSupperilername();
        CbsaCriteria cbsaCriteria = new CbsaCriteria();
        cbsaCriteria.createCriteria().andCbsa08EqualTo(supperilername);
        List<Cbsa> cbsas = cbsaMapper.selectByExample(cbsaCriteria);
        if(cbsas.size() == 0){
            throw new SwException("?????????????????????");
        }
        //????????????id
        Integer supplierId = cbsas.get(0).getCbsa01();


        if ("".equals(itemList.get(0).getSalaername())) {
            throw new SwException("????????????????????????");
        }
        String salaername = itemList.get(0).getSalaername();
         CauaCriteria cauaCriteria = new CauaCriteria();
        cauaCriteria.createCriteria().andCaua17EqualTo(salaername);
        List<Caua> cauas = cauamaMapper.selectByExample(cauaCriteria);
        if(cauas.size() == 0){
            throw new SwException("?????????????????????");
        }
        //????????????id
        Integer caua01 = cauas.get(0).getCaua01();


        if(itemList.get(0).getClientname().equals("")){
            throw new SwException("????????????????????????");
        }
        String clientname = itemList.get(0).getClientname();
        CbcaCriteria cbcaCriteria = new CbcaCriteria();
        cbcaCriteria.createCriteria().andCbca08EqualTo(clientname);
        List<Cbca> cbcas = cbcaMapper.selectByExample(cbcaCriteria);
        if(cbcas.size() == 0){
            throw new SwException("?????????????????????");
        }
        //????????????id
        Integer cbca01 = cbcas.get(0).getCbca01();



        GsSalesOrders gsSalesOrders = new GsSalesOrders();
        gsSalesOrders.setCreateTime(date);
        gsSalesOrders.setCreateBy(userid);
        gsSalesOrders.setUpdateTime(date);
        gsSalesOrders.setUpdateBy(userid);
        gsSalesOrders.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getCode().byteValue());
        NumberDo numberDo = new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
        String orderNo = numberGenerate.createOrderNo(numberDo).getOrderNo();
        gsSalesOrders.setOrderNo(orderNo);
        gsSalesOrders.setSupplierId(supplierId);
        gsSalesOrders.setSalerId(caua01);
        gsSalesOrders.setCustomerId(cbca01);
        gsSalesOrders.setOrderDate(date);
        gsSalesOrders.setStatus(TaskStatus.mr.getCode().byteValue());
        gsSalesOrdersMapper.insertSelective(gsSalesOrders);

        GsSalesOrdersCriteria gsSalesOrdersCriteria = new GsSalesOrdersCriteria();
        gsSalesOrdersCriteria.createCriteria().andOrderNoEqualTo(orderNo);
        List<GsSalesOrders> gsSalesOrderss = gsSalesOrdersMapper.selectByExample(gsSalesOrdersCriteria);
        Integer id = gsSalesOrderss.get(0).getId();
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        GsSalesOrdersDetailsMapper mapper = session.getMapper(GsSalesOrdersDetailsMapper.class);

        for (int i = 0; i < itemList.size(); i++) {

            if(itemList.get(i).getNum()== 0){
                throw new SwException("????????????????????????");
            }
            if(itemList.get(i).getPrices()== 0){
                throw new SwException("????????????????????????");
            }

            itemList.get(i).setQty(itemList.get(i).getNum());
            if("".equals(itemList.get(i).getGoodstype())){
                throw new SwException("????????????????????????");
            }
            CbpbCriteria cbpbCriteria = new CbpbCriteria();
            cbpbCriteria.createCriteria().andCbpb12EqualTo(itemList.get(i).getGoodstype());
            List<Cbpb> cbpbs = cbpbMapper.selectByExample(cbpbCriteria);
            if(cbpbs.size() == 0){
                throw new SwException("?????????????????????");
            }
            //??????id
            Integer goodsid = cbpbs.get(0).getCbpb01();
            itemList.get(i).setCreateTime(date);
            itemList.get(i).setCreateBy(String.valueOf(userid));
            itemList.get(i).setUpdateTime(date);
            itemList.get(i).setUpdateBy(String.valueOf(userid));
            itemList.get(i).setDeleteFlag(String.valueOf(DeleteFlagEnum.NOT_DELETE.getCode().byteValue()));
            itemList.get(i).setQty(itemList.get(i).getNum());
            itemList.get(i).setGoodsId(goodsid);
            itemList.get(i).setPrice(BigDecimal.valueOf(itemList.get(i).getPrices()));
            itemList.get(i).setRemark(itemList.get(i).getRemark());
            itemList.get(i).setGsSalesOrders(id.toString());
            mapper.insertSelective(itemList.get(i));
            if (i % 10 == 9) {//???10???????????????
                session.commit();
                session.clearCache();
            }
        }
        session.commit();
        session.clearCache();
        return 1;
    }

    @Override
    @Transactional
    public String importSwJsGoods(List<GsSalesOrdersdrDto> swJsGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(swJsGoodsList) || swJsGoodsList.size() == 0)
        {
            throw new ServiceException("?????????????????????????????????");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        this.insertSwJsStores(swJsGoodsList);


        if (failureNum > 0)
        {
            failureMsg.insert(0, "?????????????????????????????? " + failureNum + " ??????????????????????????????????????????");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "????????????????????????????????????????????? " + successNum + " ?????????????????????");
        }
        return successMsg.toString();
    }

    @Override
    @Transactional
    public FgkVo seleteSaleFgkVomary(FgkVo fgkVo) {
        if(fgkVo.getId() == null){
            throw new SwException("id????????????");
        }
        GsSalesOrdersChange gsSalesOrdersChange = gsSalesOrdersChangeMapper.selectByPrimaryKey(fgkVo.getId());
        fgkVo.setId(gsSalesOrdersChange.getId());
        fgkVo.setOrderNo(gsSalesOrdersChange.getOrderNo());
        fgkVo.setOrderDate(gsSalesOrdersChange.getOrderDate());
        fgkVo.setSupplierId(gsSalesOrdersChange.getSupplierId());
        fgkVo.setSalerId(gsSalesOrdersChange.getSalerId());
        fgkVo.setGoodsclassify(gsSalesOrdersChange.getGoodsclassify());
        fgkVo.setQty(gsSalesOrdersChange.getQty());
        fgkVo.setGsSalesOrders(gsSalesOrdersChange.getGsSalesOrders());
        fgkVo.setStatus(gsSalesOrdersChange.getStatus());
        fgkVo.setGoodsId(gsSalesOrdersChange.getGoodsId());
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(Long.valueOf(gsSalesOrdersChange.getSalerId()));
        if(sysUser != null){
            fgkVo.setSaleruser(sysUser.getUserName());
        }
        Cbsa cbsa = cbsaMapper.selectByPrimaryKey(gsSalesOrdersChange.getSupplierId());
        if(cbsa != null){
            fgkVo.setSaleruser(cbsa.getCbsa08());
        }
        Cbpb cbpb = cbpbMapper.selectByPrimaryKey(gsSalesOrdersChange.getGoodsId());
        if(cbpb != null){
            fgkVo.setGoodsclassify(cbpb.getCbpb12());
        }

        return fgkVo;
    }

    @Override
    @Transactional
    public int editGsSalesOrdersChanges(List<GsSalesOrdersChange> gsSalesOrdersChangeDto) {


        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        GsSalesOrdersChangeMapper mapper = session.getMapper(GsSalesOrdersChangeMapper.class);
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
//        GsSalesOrdersChange gsSalesOrdersChange = new GsSalesOrdersChange();
//        BeanUtils.copyProperties(gsSalesOrdersChangeDto, gsSalesOrdersChange);
        for (int i = 0; i < gsSalesOrdersChangeDto.size(); i++) {
            if(gsSalesOrdersChangeDto.get(i).getId()==null){
                throw new SwException("?????????id????????????");

            }


            gsSalesOrdersChangeDto.get(i).setUpdateTime(date);
            gsSalesOrdersChangeDto.get(i).setUpdateBy(userid);
            gsSalesOrdersChangeDto.get(i).setOrderDate(date);
        /*    GsSalesOrdersDetailsCriteria  ssm= new GsSalesOrdersDetailsCriteria();
            ssm.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrdersChangeDto.get(i).getGsSalesOrders()))
                    .andGoodsIdEqualTo(gsSalesOrdersChangeDto.get(i).getGoodsId());
            List<GsSalesOrdersDetails> gsSalesOrdersDetailss = gsSalesOrdersDetailsMapper.selectByExample(ssm);
            if(gsSalesOrdersDetailss.size() == 0){
                throw new SwException("?????????????????????");
            }
            if(gsSalesOrdersDetailss.get(0).getQty()==null){
                throw new SwException("???????????????????????????");
            }
            Double qty = gsSalesOrdersDetailss.get(0).getQty();
            if(qty < gsSalesOrdersChangeDto.get(i).getQty()){
                throw new SwException("?????????????????????????????????");
            }

            GsSalesOrdersDetails gsSalesOrdersDetails = new GsSalesOrdersDetails();
            gsSalesOrdersDetails.setQty(gsSalesOrdersChangeDto.get(i).getQty());


            GsSalesOrdersDetailsCriteria  sm= new GsSalesOrdersDetailsCriteria();
            sm.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrdersChangeDto.get(i).getGsSalesOrders()))
                    .andGoodsIdEqualTo(gsSalesOrdersChangeDto.get(i).getGoodsId());
            gsSalesOrdersDetailsMapper.updateByExampleSelective(gsSalesOrdersDetails,sm);*/




            //gsSalesOrdersChangeDto.get(i).setStatus(TaskStatus.mr.getCode().byteValue());
            mapper.updateByPrimaryKeySelective(gsSalesOrdersChangeDto.get(i));
            if (i % 10 == 9) {//???10???????????????
                session.commit();
                session.clearCache();
            }
        }
        session.commit();
        session.clearCache();
        return 1;    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundeditone(GsSalesChangeDo cbpdDto) {

        List<GsSalesOrdersChange> goods = cbpdDto.getGoods();
        if(goods==null||goods.size()==0){
            throw new SwException("???????????????????????????");
        }

        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsSalesChange cbpc = BeanCopyUtils.coypToClass(cbpdDto, GsSalesChange.class, null);
        cbpc.setCreateTime(date);
        cbpc.setCreateBy(userid);
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);

        String purchaseinboundNo = numberGenerate.getTakeOrderNoss();

        cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        cbpc.setStatus(TaskStatus.mr.getCode().byteValue());
        cbpc.setOrderNo(purchaseinboundNo);
        cbpc.setOrderDate(date);
       if(cbpc.getGsid()==null) {
           throw new SwException("???????????????id????????????");
       }

        gsSalesChangeMapper.insertSelective(cbpc);
        GsSalesChangeCriteria gsSalesChangeCriteria = new GsSalesChangeCriteria();
        gsSalesChangeCriteria.createCriteria().andOrderNoEqualTo(purchaseinboundNo);
        List<GsSalesChange> gsSalesChanges = gsSalesChangeMapper.selectByExample(gsSalesChangeCriteria);

        GsSalesOrdersChange cbpd = null;
        for(GsSalesOrdersChange good:goods){
            cbpd = new GsSalesOrdersChange();
            if(gsSalesChanges.size()>0) {

                cbpd.setChangeid(gsSalesChanges.get(0).getId());
            }
            cbpd.setGoodsId(good.getGoodsId());
            cbpd.setCreateTime(date);
            cbpd.setCreateBy(userid);
            cbpd.setUpdateTime(date);
            cbpd.setUpdateBy(userid);
            cbpd.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
            if(good.getPrice()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getGoodsId()==null){
                throw new SwException("??????id????????????");
            }
            if(good.getQty()==null){
                throw new SwException("????????????????????????");
            }
            cbpd.setQty(good.getQty());


            cbpd.setFactory(good.getFactory());

            cbpd.setSupplierId(good.getSupplierId());
            cbpd.setGsSalesOrders(good.getGsSalesOrders());
            cbpd.setSalerId(good.getSalerId());
            cbpd.setOrderDate(date);
            cbpd.setOrderNo(cbpdDto.getOrderNo());
            cbpd.setGsSalesOrders(cbpc.getGsid());
            cbpd.setPrice(good.getPrice());
            gsSalesOrdersChangeMapper.insertSelective(cbpd);
        }



    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundedibgdxg(GsSalesChangeDo cbpdDto) {

        GsSalesChange gsSalesChange = gsSalesChangeMapper.selectByPrimaryKey(cbpdDto.getId());

        if(gsSalesChange==null){
            throw new SwException("??????????????????");
        }
        if(!gsSalesChange.getStatus().equals(TaskStatus.mr.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }


        if(gsSalesChange.getGsid()==null){
            throw new SwException("?????????id??????");
        }
        List<GsSalesOrdersChange> goods = cbpdDto.getGoods();
            if(goods==null||goods.size()==0){
                throw new SwException("???????????????????????????");
            }

            Long userid = SecurityUtils.getUserId();
            Date date = new Date();
            GsSalesChange cbpc = BeanCopyUtils.coypToClass(cbpdDto, GsSalesChange.class, null);
        cbpc.setId(cbpdDto.getId());
            cbpc.setUpdateTime(date);
            cbpc.setUpdateBy(userid);
            cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
            /*if(cbpc.getGsid()==null) {
                throw new SwException("???????????????id????????????");
            }*/

            gsSalesChangeMapper.updateByPrimaryKeySelective(cbpc);

        GsSalesOrdersChangeCriteria gsSalesOrdersChangeCriteria = new GsSalesOrdersChangeCriteria();
        gsSalesOrdersChangeCriteria.createCriteria().andChangeidEqualTo(cbpdDto.getId());
        int i = gsSalesOrdersChangeMapper.deleteByExample(gsSalesOrdersChangeCriteria);

    /*    GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
        gsSalesOrdersDetailsCriteria.createCriteria().andGsSalesOrdersEqualTo(String.valueOf(gsSalesChange.getGsid()));
        int i1 = gsSalesOrdersDetailsMapper.deleteByExample(gsSalesOrdersDetailsCriteria);*/

        GsSalesOrdersChange cbpd = null;
        GsSalesOrdersDetails gsSalesOrdersDetails = null;

        for(GsSalesOrdersChange good:goods){
            if(good.getPrice()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getGoodsId()==null){
                throw new SwException("??????id????????????");
            }
            if(good.getQty()==null){
                throw new SwException("????????????????????????");
            }
                cbpd = new GsSalesOrdersChange();
                cbpd.setCreateTime(date);
                cbpd.setCreateBy(userid);
                cbpd.setUpdateTime(date);
                cbpd.setUpdateBy(userid);
                cbpd.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                cbpd.setGoodsId(good.getGoodsId());
                cbpd.setQty(good.getQty());
                cbpd.setChangeid(cbpdDto.getId());
                cbpd.setFactory(good.getFactory());
                cbpd.setChangeid(cbpdDto.getId());
            cbpd.setFactory(good.getFactory());
            cbpd.setPrice(good.getPrice());

                gsSalesOrdersChangeMapper.insertSelective(cbpd);

       /*     gsSalesOrdersDetails.setUpdateTime(date);
            gsSalesOrdersDetails.setUpdateBy(String.valueOf(userid));
            gsSalesOrdersDetails.setQty(good.getQty());
            GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriteria.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(gsSalesChange.getGsid()))
                    .andGoodsIdEqualTo(good.getGoodsId());

            gsSalesOrdersDetailsMapper.updateByExampleSelective(gsSalesOrdersDetails,gsSalesOrdersDetailsCriteria);*/
            }
    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundedgdxgsh(GsSalesChangeDo cbpdDto) {


        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsSalesChange cbpc = BeanCopyUtils.coypToClass(cbpdDto, GsSalesChange.class, null);
        cbpc.setId(cbpdDto.getId());
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);
        cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
      /*  if(cbpc.getGsid()==null) {
            throw new SwException("???????????????id????????????");
        }*/
        cbpc.setStatus(TaskStatus.sh.getCode().byteValue());
        if(cbpdDto.getId()==null){
            throw new SwException("id????????????");
        }

        GsSalesChange gsSalesChange = gsSalesChangeMapper.selectByPrimaryKey(cbpdDto.getId());
if(gsSalesChange.getGsid()==null){
    throw new SwException("?????????id????????????");
}
        GsSalesOrders gsSalesOrders = gsSalesOrdersMapper.selectByPrimaryKey(gsSalesChange.getGsid());
if(gsSalesOrders==null){
    throw new SwException("?????????id????????????");
}
        GsSalesOrdersChangeDto gsSalesOrdersChangeDto = new GsSalesOrdersChangeDto();
        gsSalesOrdersChangeDto.setId(gsSalesOrders.getId());
      //  gsSalesOrdersChangesh( gsSalesOrdersChangeDto);


        gsSalesChangeMapper.updateByPrimaryKeySelective(cbpc);

            GsSalesOrdersChangeCriteria gsSalesOrdersChangeCriteria = new GsSalesOrdersChangeCriteria();
            gsSalesOrdersChangeCriteria.createCriteria().andChangeidEqualTo(cbpdDto.getId());
List<GsSalesOrdersChange> gsSalesOrdersChanges = gsSalesOrdersChangeMapper.selectByExample(gsSalesOrdersChangeCriteria);

            GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria = new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriteria.createCriteria().andGsSalesOrdersEqualTo(String.valueOf(gsSalesChange.getGsid()));
            gsSalesOrdersDetailsMapper.deleteByExample(gsSalesOrdersDetailsCriteria);

    for(int i=0;i<gsSalesOrdersChanges.size();i++){
        GsSalesOrdersDetails gsSalesOrdersDetails = new GsSalesOrdersDetails();
        gsSalesOrdersDetails.setCreateTime(date);
        gsSalesOrdersDetails.setCreateBy(String.valueOf(userid));
        gsSalesOrdersDetails.setUpdateTime(date);
        gsSalesOrdersDetails.setUpdateBy(String.valueOf(userid));
        gsSalesOrdersDetails.setDeleteFlag(String.valueOf(DeleteFlagEnum.NOT_DELETE.getCode()));
        gsSalesOrdersDetails.setGoodsId(gsSalesOrdersChanges.get(i).getGoodsId());
        gsSalesOrdersDetails.setQty(gsSalesOrdersChanges.get(i).getQty());
        gsSalesOrdersDetails.setGsSalesOrders(String.valueOf(gsSalesOrders.getId()));
        gsSalesOrdersDetails.setFactory(gsSalesOrdersChanges.get(i).getFactory());
        gsSalesOrdersDetails.setPrice(BigDecimal.valueOf(gsSalesOrdersChanges.get(i).getPrice()));
        gsSalesOrdersDetailsMapper.insertSelective(gsSalesOrdersDetails);
    }


return ;
    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundbgdxgdelete(GsSalesChangeDo cbpdDto) {

        if(cbpdDto.getId()==null){
            throw new SwException("id????????????");
        }
        GsSalesChange gsSalesChange = gsSalesChangeMapper.selectByPrimaryKey(cbpdDto.getId());
        if(gsSalesChange==null){
            throw new SwException("?????????????????????");
        }
        if(gsSalesChange.getStatus()!=TaskStatus.mr.getCode().byteValue()){
            throw new SwException("?????????????????????????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsSalesChange cbpc = BeanCopyUtils.coypToClass(cbpdDto, GsSalesChange.class, null);
        cbpc.setId(cbpdDto.getId());
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);
        cbpc.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());
        /*if(cbpc.getGsid()==null) {
            throw new SwException("???????????????id????????????");
        }*/
        gsSalesChangeMapper.updateByPrimaryKeySelective(cbpc);

    }

    @Override
    public List<GsSalesOrdersVo> saleOrderLists(GsSalesOrdersDo gsSalesOrdersDo) {
        List<GsSalesOrdersVo> saleOrderListVos = gsSalesOrdersMapper.saleOrderLists(gsSalesOrdersDo);


        return saleOrderListVos;

    }

    @Override
    public List<GsSalesOrdersDetailsVo> saleOrderListdetails(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo) {
        return null;
    }

    @Override
    @Transactional
    public String importSwJsGoodss(List<GsSalesOrdersInDo> swJsGoodsList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(swJsGoodsList) || swJsGoodsList.size() == 0)
        {
            throw new ServiceException("?????????????????????????????????");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        this.insertSwJsStoress(swJsGoodsList);


        if (failureNum > 0)
        {
            failureMsg.insert(0, "?????????????????????????????? " + failureNum + " ??????????????????????????????????????????");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "????????????????????????????????????????????? " + swJsGoodsList.size() + " ?????????????????????");
        }
        return successMsg.toString();    }


    private void insertSwJsStoress(List<GsSalesOrdersInDo> itemList) {
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        if(CollectionUtils.isEmpty(itemList)){
            throw new SwException("??????????????????");
        }
      if(itemList.get(0).getPonumber()==null){
          throw new SwException("getPonumber????????????");}

        if(itemList.get(0).getInQty()==null){
            throw new SwException("??????????????????");}

        GsSalesOrdersCriteria salesOrdersCriteria=new GsSalesOrdersCriteria();
         salesOrdersCriteria.createCriteria().andPonumberEqualTo(itemList.get(0).getPonumber());
List<GsSalesOrders> gsSalesOrders = gsSalesOrdersMapper.selectByExample(salesOrdersCriteria);
if(gsSalesOrders.size()==0){
    throw new SwException("?????????????????????");}
if(gsSalesOrders.get(0).getId()==null){
    throw new SwException("???????????????id??????");}

        GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria=new GsSalesOrdersDetailsCriteria();
        gsSalesOrdersDetailsCriteria.createCriteria().andGsSalesOrdersEqualTo(String.valueOf(gsSalesOrders.get(0).getId()));
        List<GsSalesOrdersDetails> gsSalesOrdersDetails = gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
        if(gsSalesOrdersDetails.size()==0){
            throw new SwException("????????????????????????????????????");}

        double sum = gsSalesOrdersDetails.stream().mapToDouble(GsSalesOrdersDetails::getQty).sum();
        double sum1 = itemList.stream().mapToDouble(GsSalesOrdersInDo::getInQty).sum();
        if(sum1>sum){
            throw new SwException("?????????????????????????????????");}



        for (int i = 0; i < itemList.size(); i++) {
            //??????sku????????????

        if(itemList.get(i).getSku()==null){
            throw new SwException("sku????????????");}

        CbpbCriteria cbpbCriteria = new CbpbCriteria();
        cbpbCriteria.createCriteria().andCbpb12EqualTo(itemList.get(i).getSku());
        List<Cbpb> cbpbs = cbpbMapper.selectByExample(cbpbCriteria);
        if(cbpbs.size()==0){
            throw new SwException("???????????????sku");}

        //??????
            GsSalesOrdersIn gsSalesOrdersIn = new GsSalesOrdersIn();
            gsSalesOrdersIn.setCreateTime(date);
            gsSalesOrdersIn.setCreateBy(userid);
            gsSalesOrdersIn.setUpdateTime(date);
            gsSalesOrdersIn.setUpdateBy(userid);
            gsSalesOrdersIn.setDeleteFlag(String.valueOf(DeleteFlagEnum1.NOT_DELETE.getCode()));
            gsSalesOrdersIn.setGsSalesOrders(gsSalesOrders.get(0).getId());
            if(itemList.get(i).getInQty()==null){
                throw new SwException("??????????????????");}
            gsSalesOrdersIn.setInQty(itemList.get(i).getInQty());
            gsSalesOrdersIn.setPonumber(itemList.get(i).getPonumber());
            if(cbpbs.get(0).getCbpb01()==null){
                throw new SwException("??????id????????????");}
            gsSalesOrdersIn.setGoodsId(cbpbs.get(0).getCbpb01());
            gsSalesOrdersIn.setStatus(TaskStatus.mr.getCode().byteValue());

            gsSalesOrdersInMapper.insertSelective(gsSalesOrdersIn);


    }



    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundrkdxz(GsOrdersInDo cbpdDto) {
        List<GsSalesOrdersIn> goods = cbpdDto.getGoods();
        if(goods==null||goods.size()==0){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsOrdersIn cbpc = BeanCopyUtils.coypToClass(cbpdDto, GsOrdersIn.class, null);
        cbpc.setCreateTime(date);
        cbpc.setCreateBy(userid);
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);
        cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        cbpc.setStatus(TaskStatus.mr.getCode().byteValue());
        String purchaseinboundNo = numberGenerate.getTakeOrderNosss();

        cbpc.setOrderNo(purchaseinboundNo);
        cbpc.setOrderDate(date);
        cbpc.setPonumber(cbpdDto.getPonumber());
        if(cbpc.getGsid()==null) {
            throw new SwException("???????????????id????????????");
        }
        cbpc.setGsid(cbpdDto.getGsid());

        gsOrdersInMapper.insertSelective(cbpc);

        GsOrdersInCriteria gsSalesChangeCriteria = new GsOrdersInCriteria();
        gsSalesChangeCriteria.createCriteria().andOrderNoEqualTo(purchaseinboundNo);
        List<GsOrdersIn> gsOrdersIns = gsOrdersInMapper.selectByExample(gsSalesChangeCriteria);



        Integer id = gsOrdersIns.get(0).getId();

        GsSalesOrdersIn cbpd = null;
        for(GsSalesOrdersIn good:goods){

            GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria=new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriteria.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(cbpdDto.getGsid()))
                    .andGoodsIdEqualTo(good.getGoodsId());
            List<GsSalesOrdersDetails> gsSalesOrdersDetails = gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
            double sum = gsSalesOrdersDetails.stream().mapToDouble(GsSalesOrdersDetails::getQty).sum();
            if(good.getInQty()>sum){
                throw new SwException("?????????????????????????????????");
            }


            cbpd = new GsSalesOrdersIn();

            if(good.getGoodsId()==null){
                throw new SwException("??????id????????????");
            }
            if(good.getInQty()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            cbpd.setGoodsId(good.getGoodsId());
            cbpd.setCreateTime(date);
            cbpd.setCreateBy(userid);
            cbpd.setUpdateTime(date);
            cbpd.setUpdateBy(userid);
            cbpd.setDeleteFlag(String.valueOf(DeleteFlagEnum1.NOT_DELETE.getCode()));
            cbpd.setGoodsId(good.getGoodsId());
            cbpd.setPonumber(good.getPonumber());
            cbpd.setFactory(good.getFactory());
            cbpd.setInid(id);
            cbpd.setInQty(good.getInQty());
            cbpd.setPrice(good.getPrice());
            BigDecimal multiply = good.getPrice().multiply(BigDecimal.valueOf(good.getInQty()));
            cbpd.setTotalprice(multiply);

            gsSalesOrdersInMapper.insertSelective(cbpd);
        }



    }

    @Override
    @Transactional
    public void SwJsPurchaseinboundedirkdxg(GsOrdersInDo cbpdDto) {
        GsOrdersIn gsSalesChange = gsOrdersInMapper.selectByPrimaryKey(cbpdDto.getId());
        if(!gsSalesChange.getStatus().equals(TaskStatus.mr.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }

        if(gsSalesChange==null){
            throw new SwException("??????????????????");
        }
        if(gsSalesChange.getGsid()==null){
            throw new SwException("?????????id??????");
        }
        List<GsSalesOrdersIn> goods = cbpdDto.getGoods();
        if(goods==null||goods.size()==0){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsOrdersIn cbpc = new GsOrdersIn();
        cbpc.setId(cbpdDto.getId());
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);
        cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        cbpc.setStatus(TaskStatus.mr.getCode().byteValue());
        cbpc.setCustomerId(cbpdDto.getCustomerId());
        cbpc.setSupplierId(cbpdDto.getSupplierId());
        cbpc.setSalerId(cbpdDto.getSalerId());

        gsOrdersInMapper.updateByPrimaryKeySelective(cbpc);

        GsSalesOrdersInCriteria gsSalesChangeCriteria = new GsSalesOrdersInCriteria();
        gsSalesChangeCriteria.createCriteria().andInidEqualTo(cbpdDto.getId());
        int i = gsSalesOrdersInMapper.deleteByExample(gsSalesChangeCriteria);

        GsSalesOrdersIn cbpd = null;
        for(GsSalesOrdersIn good:goods){

            GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriteria=new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriteria.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(cbpdDto.getGsid()))
                    .andGoodsIdEqualTo(good.getGoodsId());
            List<GsSalesOrdersDetails> gsSalesOrdersDetails =
                    gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
           if(gsSalesOrdersDetails.size()>0) {
               double sum = gsSalesOrdersDetails.stream().mapToDouble(GsSalesOrdersDetails::getQty).sum();
               if (good.getInQty() > sum) {
                   throw new SwException("?????????????????????????????????");
               }
           }
            GsSalesOrdersDetailsCriteria gsSalesOrdersDetailsCriterias=new GsSalesOrdersDetailsCriteria();
            gsSalesOrdersDetailsCriterias.createCriteria()
                    .andGsSalesOrdersEqualTo(String.valueOf(cbpdDto.getGsid()));
            List<GsSalesOrdersDetails> gsSalesOrdersDetailss =
                    gsSalesOrdersDetailsMapper.selectByExample(gsSalesOrdersDetailsCriteria);
            double sums = gsSalesOrdersDetailss.stream().mapToDouble(GsSalesOrdersDetails::getQty).sum();
            if (good.getInQty() > sums) {
                throw new SwException("?????????????????????????????????");
            }




            cbpd = new GsSalesOrdersIn();

            if(good.getGoodsId()==null){
                throw new SwException("??????id????????????");
            }
            if(good.getInQty()==null){
                throw new SwException("????????????????????????");
            }
            if(good.getFactory()==null){
                throw new SwException("????????????????????????");
            }
            String purchaseinboundNo = numberGenerate.getTakeOrderNosss();

            cbpd.setCreateTime(date);
            cbpd.setCreateBy(userid);
            cbpd.setUpdateTime(date);
            cbpd.setUpdateBy(userid);
            cbpd.setDeleteFlag(String.valueOf(DeleteFlagEnum1.NOT_DELETE.getCode()));
            cbpd.setGoodsId(good.getGoodsId());
            cbpd.setPonumber(good.getPonumber());
            cbpd.setFactory(good.getFactory());
            cbpd.setInid(cbpdDto.getId());
            cbpd.setInQty(good.getInQty());
            cbpd.setPrice(good.getPrice());
            BigDecimal multiply = good.getPrice().multiply(BigDecimal.valueOf(good.getInQty()));
            cbpd.setTotalprice(multiply);
            gsSalesOrdersInMapper.insertSelective(cbpd);
        }




    }

    @Override
    @Transactional
    public void SwJsPurchaseinbounderkdsh(GsSalesChangeDo cbpdDto) {
        GsOrdersIn gsSalesChange = gsOrdersInMapper.selectByPrimaryKey(cbpdDto.getId());
        if(!gsSalesChange.getStatus().equals(TaskStatus.mr.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }

        Long userid = SecurityUtils.getUserId();
        Date date = new Date();
        GsOrdersIn cbpc = new GsOrdersIn();
        cbpc.setId(cbpdDto.getId());
        cbpc.setCreateTime(date);
        cbpc.setUpdateTime(date);
        cbpc.setUpdateBy(userid);
        cbpc.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        cbpc.setStatus(TaskStatus.sh.getCode().byteValue());
        gsOrdersInMapper.updateByPrimaryKey(cbpc);
    }

    @Override
    public GsOrdersInDo SwJsPurchaseinboundrrkdxq(GsOrdersInDo cbpdDto) {
        if(cbpdDto.getId()==null){
            throw new SwException("?????????id????????????");
        }
        GsOrdersIn gsSalesChange = gsOrdersInMapper.selectByPrimaryKey(cbpdDto.getId());
        if(gsSalesChange==null){
            throw new SwException("?????????????????????");
        }
        cbpdDto = BeanCopyUtils.coypToClass(gsSalesChange, GsOrdersInDo.class, null);
if(cbpdDto.getCustomerId()!=null){
    Cbca cbca = cbcaMapper.selectByPrimaryKey(cbpdDto.getCustomerId());
    if (cbca != null) {
        cbpdDto.setCustomerName(cbca.getCbca08());
    }

}
if(cbpdDto.getSalerId()!=null){
    SysUser sysUser = sysUserMapper.selectByPrimaryKey(Long.valueOf(cbpdDto.getSalerId()));
    if (sysUser != null) {
        cbpdDto.setSalerName(sysUser.getNickName());
    }
}


if(cbpdDto.getSupplierId()!=null){
    Cbsa cbsa = cbsaMapper.selectByPrimaryKey(cbpdDto.getSupplierId());
    if(cbsa!=null){
        cbpdDto.setSupplierName(cbsa.getCbsa08());
    }
}

        GsSalesOrdersInCriteria gsSalesChangeCriteria = new GsSalesOrdersInCriteria();
        gsSalesChangeCriteria.createCriteria().andInidEqualTo(cbpdDto.getId());
        List<GsSalesOrdersIn> gsSalesChanges = gsSalesOrdersInMapper.selectByExample(gsSalesChangeCriteria);
        if(gsSalesChanges==null||gsSalesChanges.size()==0){
            throw new SwException("???????????????????????????");
        }
        List<GsSalesOrdersIn> goods = cbpdDto.getGoods();
        for (GsSalesOrdersIn gsSalesChange1:gsSalesChanges){
            goods.add(gsSalesChange1);
        }
      if(goods!=null&&goods.size()>0){
         for(GsSalesOrdersIn good:goods){
             if(good.getGoodsId()!=null){
               Cbpb cbpb = cbpbMapper.selectByPrimaryKey(good.getGoodsId());
                 if(cbpb!=null){
                good.setGoodsName(cbpb.getCbpb08());
                good.setBrand(cbpb.getCbpb12());
                    // good.setInQty();
                if(cbpb.getCbpb10()!=null){
                    Cala cala = calaMapper.selectByPrimaryKey(cbpb.getCbpb10());
                    if(cala!=null){
                        good.setPinpai(cala.getCala08());
                    }
                }
            }

        }

    }
}

        return cbpdDto;


    }


}



