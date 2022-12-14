package com.ruoyi.system.service.gson.impl;

import com.ruoyi.common.enums.NumberGenerateEnum;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Do.NumberDo;
import com.ruoyi.system.domain.vo.NumberVo;
import com.ruoyi.system.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ClassName NumberGenerate
 * Description
 * Create by gfy
 * Date 2022/8/6 11:18
 */
@Component
public class NumberGenerate {

    @Resource
    private CboaMapper cboaMapper;

    @Resource
    private CbocMapper cbocMapper;

    @Resource
    private CbpkMapper cbpkMapper;

    @Resource
    private CbpcMapper cbpcMapper;


    @Autowired
    private CbpgMapper cbpgMapper;

    @Resource
    private CbsbMapper cbsbMapper;

    @Resource
    private CbseMapper cbseMapper;

    @Resource
    private  CbqaMapper cbqaMapper;

    @Resource
    private CbshMapper cbshMapper;

    @Resource
    private CboeMapper cboeMapper;

    @Resource
    private  CbaaMapper cbaaMapper;

    @Resource
    private GsSalesOrdersChangeMapper gsSalesOrdersChangeMapper;

    @Resource
    private GsSalesChangeMapper gsSalesChangeMapper;

    @Resource
    private GsSalesOrdersMapper gsSalesOrdersMapper;

    @Resource
    private GsOrdersInMapper gsOrdersInMapper;
@Resource
private CbieMapper cbieMapper;


//    public static NumberGenerate getDeptIdUtil;

//    @PostConstruct
//    public void init() {
//        getDeptIdUtil  = this;
//        getDeptIdUtil.cbpgMapper = this.cbpgMapper;
//        getDeptIdUtil.cbsbMapper = this.cbsbMapper;
//
//    }



    public NumberVo createOrderNo(NumberDo numberDo){
        NumberVo res=new NumberVo();
        if(NumberGenerateEnum.SALEORDER.getCode().equals(numberDo.getType())){

            res.setOrderNo(getSaleOrderNo());
            return res;

        }else if(NumberGenerateEnum.SALEORDERCHANGE.getCode().equals(numberDo.getType())){
            res.setOrderNo(getSaleOrderChangeNo());
            return res;
        }else if(NumberGenerateEnum.TAKEORDER.getCode().equals(numberDo.getType())){
            res.setOrderNo(getTakeOrderNo());
            return res;
        }

        return res;
    }

    private synchronized String getSaleOrderChangeNo() {
        //拼接规则 PO202208040017 PO +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="OC"+format;
        CbocCriteria example=new CbocCriteria();
        example.createCriteria()
                .andCboc07Like(orderNo+"%");
        List<Cboc> cbocs = cbocMapper.selectByExample(example);
        if(cbocs.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (Cboc cboc : cbocs) {
                Integer no = getNum(cboc.getCboc07(),10);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    private synchronized String getSaleOrderNo() {
        //拼接规则 PO202208040017 OC +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="PO"+format;
        CboaCriteria example=new CboaCriteria();
        example.createCriteria()
                .andCboa07Like(orderNo+"%");
        List<Cboa> cboas = cboaMapper.selectByExample(example);
        if(cboas.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (Cboa cboa : cboas) {
                Integer no = getNum(cboa.getCboa07(),10);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    private synchronized String getTakeOrderNo() {
        //拼接规则 OC202208040017 SP05 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="OC"+format;
        CbpkCriteria example=new CbpkCriteria();
        example.createCriteria()
                .andCbpk07Like(orderNo+"%");
        List<Cbpk> cbpks = cbpkMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (Cbpk res : cbpks) {
                Integer no = getNum(res.getCbpk07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
    //入库单新增
    public synchronized String getTakeOrderNosss() {
        //拼接规则 OC202208040017 SP05 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="SP"+format;
        GsOrdersInCriteria example=new GsOrdersInCriteria();
        example.createCriteria()
                .andOrderNoLike("%"+format+"%");
        List<  GsOrdersIn> cbpks =  gsOrdersInMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for ( GsOrdersIn res : cbpks) {
                Integer no = getNum(res.getOrderNo(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    public synchronized String getTakeOrderNoss() {
        //拼接规则 OC202208040017 SP05 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="SC"+format;
        GsSalesChangeCriteria example=new GsSalesChangeCriteria();
        example.createCriteria()
                .andOrderNoLike("%"+format+"%");
        List< GsSalesChange> cbpks = gsSalesChangeMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (GsSalesChange res : cbpks) {
                Integer no = getNum(res.getOrderNo(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    //采购入库编号
    public synchronized String getPurchaseinboundNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
      //  String orderNo = " ";

        String orderNo = null;
        if (storeId / 10 == 0) {
            String s = "PI0" + storeId + format;
           orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "PI" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "PI0" + i + format;
            orderNo=s;
        }
        CbpcCriteria example=new CbpcCriteria();
        example.createCriteria()
                .andCbpc07Like("%"+format+"%");
        List<Cbpc> cbpks = cbpcMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbpc res : cbpks) {
                Integer no = getNum(res.getCbpc07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
     //采购退库编号
     public synchronized String getPurchasereturnNo(int storeId) {
         //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
         SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
         String format = sd.format(new Date());
         String orderNo = " ";

         if (storeId / 10 == 0) {
             String s = "PO0" + storeId + format;
             orderNo=s;
         }
         else if(storeId/10>0&&storeId/10<10){
             String s = "PO" + storeId + format;
             orderNo=s;
         }else {
             int i = storeId % 3;
             String s = "PO0" + i + format;
             orderNo=s;
         }
         CbpgCriteria example=new CbpgCriteria();
         example.createCriteria()
                 .andCbpg07Like("%"+format+"%");
         List<Cbpg> cbpks = cbpgMapper.selectByExample(example);
         if(cbpks.size()==0){
             return orderNo+"0001";
         }
         else {

             Integer num=0;
             for (Cbpg res : cbpks) {
                 Integer no = getNum(res.getCbpg07(),12);
                 if(num<no){
                     num=no;
                 }

             }

             return  createOrderNo(orderNo,num);

         }

     }
    //销售出库编号
    public synchronized String getSellofwarehouseNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "SO0" + storeId + format;
            orderNo = s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "SO" + storeId + format;
            orderNo = s;
        }else {
            int i = storeId % 3;
            String s = "SO0" + i + format;
            orderNo = s;
        }
        CbsbCriteria example=new CbsbCriteria();
        example.createCriteria()
                .andCbsb07Like("%"+format+"%");
        List<Cbsb> cbpks = cbsbMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbsb res : cbpks) {
                Integer no = getNum(res.getCbsb07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
    //销售退库单编号

    public synchronized String getSalesreturnordersNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "SI0" + storeId + format;
            orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "SI" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "SI0" + i + format;
            orderNo=s;
        }
        CbseCriteria example=new CbseCriteria();
        example.createCriteria()
                .andCbse07Like("%"+format+"%");
        List<Cbse> cbpks = cbseMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbse res : cbpks) {
                Integer no = getNum(res.getCbse07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
    //销售提货单
    public synchronized String getSalesbillofladingNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "SP0" + storeId + format;
            s=orderNo;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "SP" + storeId + format;
            s=orderNo;
        }else {
            int i = storeId % 3;
            String s = "SP0" + i + format;
            s=orderNo;
        }
        CbpkCriteria example=new CbpkCriteria();
        example.createCriteria()
                .andCbpk07Like("%"+format+"%");
        List<Cbpk> cbpks = cbpkMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbpk res : cbpks) {
                Integer no = getNum(res.getCbpk07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
    //质检单编号
    public synchronized String getQualityinspectionlistNo() {
        //拼接规则 PO202208040017 PO +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="QC"+format;
        CbqaCriteria example=new CbqaCriteria();
        example.createCriteria()
                .andCbqa07Like(orderNo+"%");
        List<Cbqa> cbpks = cbqaMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbqa res : cbpks) {
                Integer no = getNum(res.getCbqa07(),10);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    //预订单变更单编号
    public synchronized String getQualityinspectionlistNos() {
        //拼接规则 PO202208040017 PO +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="SK"+format;

        GsSalesChangeCriteria example=new GsSalesChangeCriteria();
        example.createCriteria()
                .andOrderNoLike(orderNo+"%");

        List<   GsSalesChange> cbpks = gsSalesChangeMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (GsSalesChange res : cbpks) {
                Integer no = getNum(res.getOrderNo(),10);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }


     //仓库初始化单编号
    public synchronized String getWarehouseinitializationNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "ST0" + storeId + format;
            orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "ST" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "ST0" + i + format;
            orderNo=s;
        }
        CbieCriteria example=new CbieCriteria();
        example.createCriteria()
                .andCbie07Like("%"+format+"%");
        List<Cbie> cbpks = cbieMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbie res : cbpks) {
                Integer no = getNum(res.getCbie07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }


    //仓库盘点单编号
    public synchronized String getWarehouseinitializationNoss(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "ST0" + storeId + format;
            orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "ST" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "ST0" + i + format;
            orderNo=s;
        }
        CbshCriteria example=new CbshCriteria();
        example.createCriteria()
                .andCbsh07Like("%"+format+"%");
        List<Cbsh> cbpks = cbshMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbsh res : cbpks) {
                Integer no = getNum(res.getCbsh07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    //调拨单
    public synchronized String getWarehouseinitializationNos(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "ST0" + storeId + format;
            orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "ST" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "ST0" + i + format;
            orderNo=s;
        }
        CbaaCriteria example=new CbaaCriteria();
        example.createCriteria()
                .andCbaa07Like("%"+format+"%");
        List<Cbaa> cbpks = cbaaMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbaa res : cbpks) {
                Integer no = getNum(res.getCbaa07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }




    //库存初始化单编号
    public synchronized String getBinitinitializationNo(int storeId) {
        //拼接规则 PI01 20220717 0001 PI01 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo = "";

        if (storeId / 10 == 0) {
            String s = "II0" + storeId + format;
            orderNo=s;
        }
        else if(storeId/10>0&&storeId/10<10){
            String s = "II" + storeId + format;
            orderNo=s;
        }else {
            int i = storeId % 3;
            String s = "II0" + i + format;
            orderNo=s;
        }
        CbieCriteria example=new CbieCriteria();
        example.createCriteria()
                .andCbie07Like("%"+format+"%");
        List<Cbie> cbpks = cbieMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {

            Integer num=0;
            for (Cbie res : cbpks) {
                Integer no = getNum(res.getCbie07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }
    public synchronized String getSaleOrdersNo() {
        //拼接规则 PO202208040017 OC +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="PO"+format;
        GsSalesOrdersCriteria example=new GsSalesOrdersCriteria();
        example.createCriteria()
                .andOrderNoLike(orderNo+"%");
        List<GsSalesOrders> gsSalesOrders = gsSalesOrdersMapper.selectByExample(example);
        if(gsSalesOrders.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (GsSalesOrders cboa : gsSalesOrders) {
                Integer no = getNum(cboa.getOrderNo(),10);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }

    private Integer getNum(String orderNo,int index){
        String substring = orderNo.substring(index);
        if(substring.startsWith("000")){
            return Integer.parseInt(substring.substring(3))+1;
        }else if(substring.startsWith("00")){
            return Integer.parseInt(substring.substring(2))+1;
        }else if(substring.startsWith("0")) {
            return Integer.parseInt(substring.substring(1))+1;
        }else {
            return Integer.parseInt(substring.substring(0))+1;
        }
    }


    private String createOrderNo(String orderNo,Integer num){
        String no=String.valueOf(num);
        if(no.length()==4){
            return orderNo+no;
        }else if(no.length()==3){
            return orderNo+"0"+no;
        }else if(no.length()==2){
            return orderNo+"00"+no;
        }else {
            return orderNo+"000"+no;
        }

    }
    public synchronized String getTakeOrderNos() {
        //拼接规则 OC202208040017 SP05 +年月日 +四位数数量自增
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        String orderNo="OS"+format;
        CboeCriteria example=new CboeCriteria();
        example.createCriteria()
                .andCboe07Like("%"+orderNo+"%");
        List<Cboe> cbpks = cboeMapper.selectByExample(example);
        if(cbpks.size()==0){
            return orderNo+"0001";
        }else {
            Integer num=0;
            for (Cboe res : cbpks) {
                Integer no = getNum(res.getCboe07(),12);
                if(num<no){
                    num=no;
                }

            }

            return  createOrderNo(orderNo,num);

        }

    }


}
