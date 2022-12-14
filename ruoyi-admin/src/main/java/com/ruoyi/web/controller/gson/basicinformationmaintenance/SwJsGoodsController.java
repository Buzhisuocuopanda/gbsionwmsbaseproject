package com.ruoyi.web.controller.gson.basicinformationmaintenance;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.PathConstant;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ErrCode;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ValidUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Cbca;
import com.ruoyi.system.domain.Cbpb;
import com.ruoyi.system.domain.Cbpf;
import com.ruoyi.system.domain.Do.CbpbDo;
import com.ruoyi.system.domain.Do.CbpfDo;
import com.ruoyi.system.domain.dto.CbpbDto;
import com.ruoyi.system.domain.dto.GoodsSelectDto;
import com.ruoyi.system.domain.vo.BaseSelectVo;
import com.ruoyi.system.domain.vo.CbpbVo;
import com.ruoyi.system.domain.vo.IdVo;
import com.ruoyi.system.domain.vo.SaleOrderDetailVo;
import com.ruoyi.system.service.ISwJsCustomerService;
import com.ruoyi.system.service.ISwJsGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.web.utils.FileCopyUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.List;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * 商品信息维护Controller
 *
 * @author lhy
 * &#064;date  2022-06-20
 */
@Api(
        tags = {"商品信息维护"}
)
@Slf4j
@RestController
@RequestMapping("/system/goods")
public class SwJsGoodsController extends BaseController {
    @Resource
    private ISwJsGoodsService swJsGoodsService;

    @Resource
    private ISwJsCustomerService swJsCustomerService;
    /**
     * 新增商品
     */
    @ApiOperation(
            value ="新增商品",
            notes = "新增商品"
    )
    @PostMapping("/SwJsGoodsadd")
    @PreAuthorize("@ss.hasPermi('system:goods:add')")
    public AjaxResult<IdVo> swJsGoodsadd(@Valid @RequestBody CbpbDo cbpbDo, BindingResult bindingResult) {
        IdVo res=null;
        try {
            ValidUtils.bindvaild(bindingResult);
            res=swJsGoodsService.insertSwJsGoodsClassify(cbpbDo);
            return AjaxResult.success(res);


        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【新增商品】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【新增商品】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }
    /**
     * 新增商品信息维护关联表
     */
    @ApiOperation(
            value ="新增商品信息维护关联表",
            notes = "新增商品信息维护关联表"
    )
    @PostMapping("/SwJsGoodsadds")
    public AjaxResult swJsGoodsadds(@Valid @RequestBody List<Cbpf> cbpf, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(swJsGoodsService.insertSwJsGoodsClassifys(cbpf));


        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【新增商品信息维护关联表】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpf), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【新增商品信息维护关联表】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpf), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 修改商品
     */
    @ApiOperation(
            value ="修改商品",
            notes = "修改商品"
    )
    @PostMapping("/SwJsGoodsedit")
    @PreAuthorize("@ss.hasPermi('system:goods:edit')")
    public AjaxResult swJsGoodsedit(@RequestBody CbpbDo cbpbDo) {
        try {

            return toAjax(swJsGoodsService.updateSwJsGoodsClassify(cbpbDo));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【修改商品】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【修改商品】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 删除商品
     */
    @ApiOperation(
            value ="删除商品",
            notes = "删除商品"
    )
    @PostMapping("/SwJsGoodsremove")
    @PreAuthorize("@ss.hasPermi('system:goods:remove')")
    public AjaxResult swJsGoodsremove(@RequestBody CbpbDo cbpbDo) {
        try {
            return toAjax(swJsGoodsService.deleteSwJsGoodsClassifyById(cbpbDo));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【删除商品】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【删除商品分类】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpbDo),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 查询商品列表
     */
    @ApiOperation(
            value ="查询商品列表",
            notes = "查询商品列表"
    )
    @GetMapping("/SwJsGoodslist")
    @PreAuthorize("@ss.hasPermi('system:goods:list')")
    public AjaxResult<TableDataInfo> swJsGoodslist(CbpbVo cbpbVo) {
        try {
            startPage();
            List<CbpbVo> list = swJsGoodsService.selectSwJsGoodsList(cbpbVo);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询商品列表】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpbVo),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 查询结算货币
     */
    @ApiOperation(
            value ="查询结算货币",
            notes = "查询结算货币"
    )
    @GetMapping("/SwJsGoodscny")
   // @PreAuthorize("@ss.hasPermi('system:goods:list')")
    public AjaxResult<TableDataInfo> swJsGoodslist(Cbpf cbpf) {
        try {
            startPage();
            List<Cbpf> list = swJsGoodsService.selectcbpfList(cbpf);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询结算货币】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpf),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 商品列表选择框
     */
    @ApiOperation(
            value ="商品列表选择框",
            notes = "商品列表选择框"
    )
    @GetMapping("/swJsGoodslistBySelect")
    public AjaxResult<BaseSelectVo> swJsGoodslistBySelect(GoodsSelectDto goodsSelectDto) {
        try {
            startPage();
            List<BaseSelectVo> list = swJsGoodsService.swJsGoodslistBySelect(goodsSelectDto);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询商品列表】接口出现异常,参数${}$,异常${}$", JSON.toJSON(goodsSelectDto),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 商品列表不分页选择框
     */
    @ApiOperation(
            value ="商品列表不分页选择框",
            notes = "商品列表不分页选择框"
    )
    @GetMapping("/swJsGoodslistBySelectAll")
    public AjaxResult<BaseSelectVo> swJsGoodslistBySelectAll(GoodsSelectDto goodsSelectDto) {
        try {
            List<BaseSelectVo> list = swJsGoodsService.swJsGoodslistBySelect(goodsSelectDto);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询商品列表】接口出现异常,参数${}$,异常${}$", JSON.toJSON(goodsSelectDto),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 导入商品
     */
    @ApiOperation(
            value ="导入商品",
            notes = "导入商品"
    )
    @PostMapping("/importSwJsGoods")
    @PreAuthorize("@ss.hasPermi('system:goods:import')")
    @ResponseBody
    public AjaxResult importSwJsGoods(MultipartFile file, boolean updateSupport) {
        try {
            ExcelUtil<CbpbDto> util = new ExcelUtil<>(CbpbDto.class);
            List<CbpbDto> swJsGoodsList = util.importExcel(file.getInputStream());
            //    LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String operName = SecurityUtils.getUsername();

            //String operName = loginUser.getUsername();
            String message = swJsGoodsService.importSwJsGoods(swJsGoodsList, updateSupport,operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【导入商品】接口出现异常,参数${},异常${}$", JSON.toJSON(message),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【导入商品】接口出现异常,参数${},异常${}$", JSON.toJSON(message),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【导入商品】接口出现异常,参数${},异常${}$", JSON.toJSON(message),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 结算货币导入
     */
    @ApiOperation(
            value ="结算货币导入",
            notes = "结算货币导入"
    )
    @PostMapping("/importcbpf")
    @ResponseBody
    public AjaxResult importSwJsCustomer(MultipartFile file, boolean updateSupport) {
        try {
           // WeakReference<String> wrf = new WeakReference<String>("123");
            ExcelUtil<Cbpf> util = new ExcelUtil<>(Cbpf.class);
            List<Cbpf> swJsCustomersList = util.importExcel(file.getInputStream());
            String operName = getUsername();
            String message = swJsGoodsService.importSwJsCustomer(swJsCustomersList, updateSupport, operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【结算货币导入】接口参数校验出现异常，参数${}$,异常${}$", JSON.toJSON(file),e.getMessage());
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【结算货币导入】接口出现异常,参数${}$,异常${}$", JSON.toJSON(file),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }
    /**
     * 查询商品列表导出
     */
    @ApiOperation(
            value ="查询商品列表导出",
            notes = "查询商品列表导出"
    )
    @GetMapping("/SwJsGoodslistout")
    @PreAuthorize("@ss.hasPermi('system:goods:list')")
    public AjaxResult<TableDataInfo> SwJsGoodslistout(CbpbVo cbpbVo) {
        try {
            startPage();
            List<CbpbVo> list = swJsGoodsService.selectSwJsGoodsListout(cbpbVo);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询商品列表导出】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpbVo),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }
    /**
     * 导出商品列表
     */
    @ApiOperation(
            value ="导出商品列表",
            notes = "导出商品列表"
    )
    @PostMapping("/SwJsGoodsexport")
    @PreAuthorize("@ss.hasPermi('system:goods:export')")
    public void swJsGoodsexport(HttpServletResponse response, CbpbVo cbpbVo) {
        List<CbpbVo> list = swJsGoodsService.selectSwJsGoodsListout(cbpbVo);
        ExcelUtil<CbpbVo> util = new ExcelUtil<>(CbpbVo.class);
        util.exportExcel(response, list, "商品数据");
    }

 /*   *//**
     * 导入商品下载模板
  *//*
    @ApiOperation(
            value ="导入商品下载模板",
            notes = "导入商品下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CbpbDto> util = new ExcelUtil<CbpbDto>(CbpbDto.class);

        util.importTemplateExcel(response,"导入商品下载模板");
    }*/


    /**
     * 导入商品下载模板
     */
    @ApiOperation(
            value ="导入商品下载模板",
            notes = "导入商品下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException, InvalidFormatException {
        InputStream in = null;
        String excelPaht="";
        String excelPaht2="";
        String pdfPath="";
        XSSFWorkbook wb = null;
        try {
        long time = System.currentTimeMillis();


        excelPaht=   RuoYiConfig.getSwdataprofile()+"商品信息维护导入模板_"+time+".xlsx";
        excelPaht2 = RuoYiConfig.getSwprofile() + "模板商品信息维护导入_"  + time + ".xlsx";
        FileCopyUtils.copyFile(new File(RuoYiConfig.getSwprofile()+ PathConstant.GOOD_ORDER_SCANSEWASTYY_EXCEL),new File(excelPaht2));

        File is = new File(excelPaht2);
        wb = new XSSFWorkbook(is);

        /*File file = new File("text.java");

        String filePath = file.getAbsolutePath();*/
        saveExcelToDisk(wb, excelPaht);

        //  saveExcelToDisk(wb, name);
        FileUtils.setAttachmentResponseHeader(response, "商品信息维护导入模板_.xlsx");
        FileUtils.writeBytes(excelPaht, response.getOutputStream());
        } catch (SwException e) {
            log.error("【导入商品下载模板】接口出现异常,参数${}$,异常${}$", ExceptionUtils.getStackTrace(e));

            // return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【导入商品下载模板】接口出现异常,参数${}$,异常${}$",  ExceptionUtils.getStackTrace(e));

            // return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }finally {

            if(in!=null){
                in.close();
            }
            if(wb!=null){
                wb.close();
            }

            if(excelPaht!=null){
                FileUtils.deleteFile(excelPaht);
            }
            if(excelPaht2!=null){
                FileUtils.deleteFile(excelPaht2);
            }
        }
        // return AjaxResult.success();
    }


    private static void saveExcelToDisk(XSSFWorkbook wb, String filePath){
        File file = new File(filePath);
        OutputStream os=null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {if(os!=null) {
                os.close();
            }
            } catch (IOException e) { log.error("error", e);}
        }
    }

}
