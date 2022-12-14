package com.ruoyi.framework.web.service.impl;

import com.ruoyi.common.enums.*;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.BeanCopyUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.*;
import com.ruoyi.system.domain.vo.GsPurchaseOrderVo;
import com.ruoyi.system.domain.vo.GsPurchaseOrdersVo;
import com.ruoyi.system.domain.vo.IdVo;
import com.ruoyi.system.domain.vo.NumberVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IPurchaseordertableService;
import com.ruoyi.system.service.gson.BaseCheckService;
import com.ruoyi.system.service.gson.TaskService;
import com.ruoyi.system.service.gson.impl.NumberGenerate;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PurchaseordertableServiceImpl implements IPurchaseordertableService {

    @Resource
    private GsPurchaseOrderMapper purchaseOrderMapper;

    @Resource
    private GsPurchaseOrderDetailMapper purchaseOrderDetailMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private GsGoodsSkuMapper gsGoodsSkuMapper;

    @Resource
    private BaseCheckService baseCheckService;

    @Resource
    private NumberGenerate numberGenerate;

    @Resource
    private CbsaMapper cbsaMapper;

    @Resource
    private TaskService taskService;
    @Resource
    private GsPurchaseOrderDetailMapper gsPurchaseOrderDetailMapper;
    @Transactional
    @Override
    public IdVo insertSwJsSkuBarcodes(GsPurchaseOrderDo gsPurchaseOrderDo) {
        NumberDo numberDo=new NumberDo();
        numberDo.setType(NumberGenerateEnum.SALEORDER.getCode());
        NumberVo orderNo = numberGenerate.createOrderNo(numberDo);

        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
        gsPurchaseOrder.setStatus(TaskStatus.mr.getCode().byteValue());
        gsPurchaseOrder.setOrderNo(orderNo.getOrderNo());
        gsPurchaseOrder.setOrderDate(date);
        purchaseOrderMapper.insertSelective(gsPurchaseOrder);
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
            example.createCriteria().andOrderNoEqualTo(orderNo.getOrderNo())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        List<GsPurchaseOrder> gsPurchaseOrders = purchaseOrderMapper.selectByExample(example);

        IdVo idVo = new IdVo();
        idVo.setId(Math.toIntExact(gsPurchaseOrders.get(0).getId()));
        return idVo;
    }
    @Transactional
    @Override
    public int insertSwJsSkuBarcodesm(List<GsPurchaseOrderDetail> itemList) {
        if(itemList.size()==0){
            throw new SwException("??????????????????");
        }
        if(itemList.get(0).getGoodsId()==null){
            throw new SwException("??????????????????");
        }
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        GsPurchaseOrderDetailMapper mapper = session.getMapper(GsPurchaseOrderDetailMapper.class);
        Date date = new Date();
        Long userid = SecurityUtils.getUserId();
        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setCreateTime(date);
            itemList.get(i).setCreateBy(userid);
            itemList.get(i).setUpdateTime(date);
            itemList.get(i).setUpdateBy(userid);
            itemList.get(i).setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
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
    @Transactional
    @Override
    public int deleteSwJsSkuBarcodsById(GsPurchaseOrderDo gsPurchaseOrderDo) {
        //????????????????????????
        GsPurchaseOrder gsPurchaseOrder1 = purchaseOrderMapper.selectByPrimaryKey(gsPurchaseOrderDo.getId());
        Byte status = gsPurchaseOrder1.getStatus();
        int statuss = status.intValue();
        if(statuss==(TaskStatus.bjwc.getCode())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setDeleteFlag(DeleteFlagEnum1.DELETE.getCode());
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
        example.createCriteria().andIdEqualTo(gsPurchaseOrderDo.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        return   purchaseOrderMapper.updateByExampleSelective(gsPurchaseOrder, example);
    }
    @Transactional
    @Override
    public int swJsPurchasereturnordersh(GsPurchaseOrderDo gsPurchaseOrderDo) {
        //????????????????????????
        GsPurchaseOrder gsPurchaseOrder1 = purchaseOrderMapper.selectByPrimaryKey(gsPurchaseOrderDo.getId());
        if(!gsPurchaseOrder1.getStatus().equals(TaskStatus.mr.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setStatus(TaskStatus.sh.getCode().byteValue());
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
        example.createCriteria().andIdEqualTo(gsPurchaseOrderDo.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        return   purchaseOrderMapper.updateByExampleSelective(gsPurchaseOrder, example);
    }
    @Transactional
    @Override
    public int swJsPurchasereturnorderfs(GsPurchaseOrderDo gsPurchaseOrderDo) {
        //????????????????????????
        GsPurchaseOrder gsPurchaseOrder1 = purchaseOrderMapper.selectByPrimaryKey(gsPurchaseOrderDo.getId());
        if(!gsPurchaseOrder1.getStatus().equals(TaskStatus.sh.getCode().byteValue())){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setStatus(TaskStatus.mr.getCode().byteValue());
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
        example.createCriteria().andIdEqualTo(gsPurchaseOrderDo.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        return   purchaseOrderMapper.updateByExampleSelective(gsPurchaseOrder, example);
    }
    @Transactional
    @Override
    public int swJsPurchasereturnorderbjwc(GsPurchaseOrderDo gsPurchaseOrderDo) throws InterruptedException {
        //????????????????????????
        GsPurchaseOrder gsPurchaseOrder1 = purchaseOrderMapper.selectByPrimaryKey(gsPurchaseOrderDo.getId());
        if (!gsPurchaseOrder1.getStatus().equals(TaskStatus.sh.getCode().byteValue())) {
            throw new SwException("?????????????????????????????????");
        }
        Integer whId = gsPurchaseOrder1.getWhId();
        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setStatus(TaskStatus.bjwc.getCode().byteValue());
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
        example.createCriteria().andIdEqualTo(gsPurchaseOrderDo.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());


        GsPurchaseOrderDetailCriteria example1 = new GsPurchaseOrderDetailCriteria();
        example1.createCriteria()
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode())
                .andPurchaseOrderIdEqualTo(Math.toIntExact(gsPurchaseOrderDo.getId()));
        List<GsPurchaseOrderDetail> gsPurchaseOrderDetails = purchaseOrderDetailMapper.selectByExample(example1);

        for (int i = 0; i < gsPurchaseOrderDetails.size(); i++) {
            int goodsid = gsPurchaseOrderDetails.get(i).getGoodsId();

            Double num = gsPurchaseOrderDetails.get(i).getQty();
            GsGoodsSkuCriteria example2 = new GsGoodsSkuCriteria();
            example2.createCriteria()
                    .andGoodsIdEqualTo(goodsid)
                    .andWhIdEqualTo(whId);
            List<GsGoodsSku> gsGoodsSkus = gsGoodsSkuMapper.selectByExample(example2);
            if (gsGoodsSkus.size() == 0) {
                //????????????
                GsGoodsSku gsGoodsSku = new GsGoodsSku();
                gsGoodsSku.setCreateTime(date);
                gsGoodsSku.setUpdateTime(date);
                gsGoodsSku.setCreateBy(Math.toIntExact(userid));
                gsGoodsSku.setUpdateBy(Math.toIntExact(userid));
                gsGoodsSku.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());
                gsGoodsSku.setGoodsId(goodsid);
                gsGoodsSku.setWhId(whId);
                gsGoodsSku.setQty(num);
                gsGoodsSkuMapper.insertSelective(gsGoodsSku);

            } else {
                //????????????
                List<Integer> collect1 = gsGoodsSkus.stream().map(GsGoodsSku::getGoodsId).collect(Collectors.toList());
                int[] ints1 = collect1.stream().mapToInt(Integer::intValue).toArray();
                int id = ints1[0];
                //  Integer id1 = gsGoodsSkus.get(0).getId();
                GsGoodsSku gsGoodsSku = baseCheckService.checkGoodsSkuForUpdate(id);
                gsGoodsSku.setQty(gsGoodsSku.getQty() + num);
                gsGoodsSku.setUpdateBy(Math.toIntExact(userid));
                gsGoodsSku.setUpdateTime(date);
                gsGoodsSkuMapper.updateByPrimaryKeySelective(gsGoodsSku);
            }
            //??????????????????????????????
            GsPurchaseOrderDetail gsPurchaseOrderDetail = new GsPurchaseOrderDetail();
            //??????????????????
            gsPurchaseOrderDetail.setInQty(num);
            gsPurchaseOrderDetail.setChangeQty(num);
            GsPurchaseOrderDetailCriteria example3 = new GsPurchaseOrderDetailCriteria();
            example3.createCriteria()
                    .andGoodsIdEqualTo(goodsid)
                    .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());

            gsPurchaseOrderDetailMapper.updateByExampleSelective(gsPurchaseOrderDetails.get(i), example3);

            CbibDo cbibDo = new CbibDo();
            cbibDo.setCbib02(gsPurchaseOrder1.getWhId());
            cbibDo.setCbib03(gsPurchaseOrder1.getOrderNo());
            cbibDo.setCbib05(String.valueOf(TaskType.cgdd.getCode()));
            Cbsa cbsa = cbsaMapper.selectByPrimaryKey(gsPurchaseOrder1.getSupplierId());

            cbibDo.setCbib06(cbsa.getCbsa08());
            cbibDo.setCbib07(Math.toIntExact(gsPurchaseOrderDetails.get(i).getId()));
            cbibDo.setCbib08(gsPurchaseOrderDetails.get(i).getGoodsId());
            //??????????????????
            cbibDo.setCbib11(gsPurchaseOrderDetails.get(i).getQty());

            double v = gsPurchaseOrderDetails.get(i).getPrice().doubleValue();
            cbibDo.setCbib12(v);
            cbibDo.setCbib13((double) 0);
            cbibDo.setCbib14((double) 0);

            cbibDo.setCbib17(TaskType.cgdd.getMsg());
            cbibDo.setCbib19(gsPurchaseOrder1.getSupplierId());
            taskService.InsertCBIB(cbibDo);
        }
        //?????????



        return   purchaseOrderMapper.updateByExampleSelective(gsPurchaseOrder, example);
    }
    @Transactional
    @Override
    public int swJsPurchasereturnorderqxwc(GsPurchaseOrderDo gsPurchaseOrderDo) {
        //????????????????????????
        GsPurchaseOrder gsPurchaseOrder1 = purchaseOrderMapper.selectByPrimaryKey(gsPurchaseOrderDo.getId());
        if(!gsPurchaseOrder1.getStatus().equals(TaskStatus.bjwc.getCode().byteValue())){
            throw new SwException("????????????????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();
        GsPurchaseOrder gsPurchaseOrder = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        Date date = new Date();
        gsPurchaseOrder.setCreateTime(date);
        gsPurchaseOrder.setUpdateTime(date);
        gsPurchaseOrder.setCreateBy(userid);
        gsPurchaseOrder.setUpdateBy(userid);
        gsPurchaseOrder.setStatus(TaskStatus.sh.getCode().byteValue());
        GsPurchaseOrderCriteria example = new GsPurchaseOrderCriteria();
        example.createCriteria().andIdEqualTo(gsPurchaseOrderDo.getId())
                .andDeleteFlagEqualTo(DeleteFlagEnum1.NOT_DELETE.getCode());
        return   purchaseOrderMapper.updateByExampleSelective(gsPurchaseOrder, example);
    }

    @Override
    public List<GsPurchaseOrderVo> selectSwJsTaskGoodsRelLists(GsPurchaseOrderVo gsPurchaseOrderVo) {
        return purchaseOrderMapper.selectSwJsTaskGoodsRelLists(gsPurchaseOrderVo);
    }

    @Override
    public List<GsPurchaseOrdersVo> SwJsSkuBarcodelists(GsPurchaseOrdersVo gsPurchaseOrdersVo) {

        return purchaseOrderMapper.SwJsSkuBarcodelists(gsPurchaseOrdersVo);
    }

    @Override
    public void SwJsPurchasereturnordersedit(GsPurchaseOrderDo gsPurchaseOrderDo) {
        Date date = new Date();
     if(gsPurchaseOrderDo.getId()==null){
         throw new SwException("id????????????");
     }
        List<GsPurchaseOrderDetail> goods = gsPurchaseOrderDo.getGoods();
        if(goods==null||goods.size()==0){
            throw new SwException("???????????????????????????");
        }
        Long userid = SecurityUtils.getUserId();

        GsPurchaseOrder cboe = BeanCopyUtils.coypToClass(gsPurchaseOrderDo, GsPurchaseOrder.class, null);
        cboe.setId(gsPurchaseOrderDo.getId());
        cboe.setUpdateBy(userid);
        cboe.setUpdateTime(new Date());
        purchaseOrderMapper.updateByPrimaryKeySelective(cboe);

        GsPurchaseOrderDetailCriteria sddf=new GsPurchaseOrderDetailCriteria();
        sddf.createCriteria().andPurchaseOrderIdEqualTo(Math.toIntExact(gsPurchaseOrderDo.getId()));
        List<GsPurchaseOrderDetail> gsPurchaseOrderDetails = gsPurchaseOrderDetailMapper.selectByExample(sddf);
        if(gsPurchaseOrderDetails.size()==0){
            throw new SwException("????????????????????????");
        }
        GsPurchaseOrderDetailCriteria example = new GsPurchaseOrderDetailCriteria();
        example.createCriteria()
                .andPurchaseOrderIdEqualTo(Math.toIntExact(gsPurchaseOrderDo.getId()));
        int i = gsPurchaseOrderDetailMapper.deleteByExample(example);


        GsPurchaseOrderDetail gsPurchaseOrderDetail = null;
        for(GsPurchaseOrderDetail gsPurchaseOrderDetail1:goods){
            gsPurchaseOrderDetail = new GsPurchaseOrderDetail();
          /*  if(gsPurchaseOrderDetail1.getId()==null){
                throw new SwException("??????????????????????????????");
            }*/
         /*   if(!uio.contains(gsPurchaseOrderDetail1.getId())){
                throw new SwException("????????????????????????????????????");
            }*/
            //gsPurchaseOrderDetail.setId(gsPurchaseOrderDetail1.getId());
            gsPurchaseOrderDetail.setCreateTime(date);
            gsPurchaseOrderDetail.setUpdateTime(date);
            gsPurchaseOrderDetail.setCreateBy(userid);
            gsPurchaseOrderDetail.setUpdateBy(userid);
            gsPurchaseOrderDetail.setDeleteFlag(DeleteFlagEnum1.NOT_DELETE.getCode());

            gsPurchaseOrderDetail.setGoodsId(gsPurchaseOrderDetail1.getGoodsId());
            gsPurchaseOrderDetail.setQty(gsPurchaseOrderDetail1.getQty());
            gsPurchaseOrderDetail.setPrice(gsPurchaseOrderDetail1.getPrice());
            gsPurchaseOrderDetail.setRemark(gsPurchaseOrderDetail1.getRemark());
            gsPurchaseOrderDetail.setInQty(gsPurchaseOrderDetail1.getInQty());
            gsPurchaseOrderDetail.setChangeQty(gsPurchaseOrderDetail1.getChangeQty());
            gsPurchaseOrderDetail.setSurplusQty(gsPurchaseOrderDetail1.getSurplusQty());
            gsPurchaseOrderDetail.setPurchaseOrderId(gsPurchaseOrderDo.getId().intValue());
            gsPurchaseOrderDetail.setUpdateBy(userid);
            gsPurchaseOrderDetail.setUpdateTime(new Date());
            gsPurchaseOrderDetailMapper.insertSelective(gsPurchaseOrderDetail);
        }
    }
}
