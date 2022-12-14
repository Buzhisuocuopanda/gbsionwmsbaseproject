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
import com.ruoyi.common.utils.ValidUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Cbsa;
import com.ruoyi.system.domain.dto.CbsaDto;
import com.ruoyi.system.service.ISwJsSupplierService;
import com.ruoyi.web.utils.FileCopyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * 【供应商信息】Controller
 *
 * @author lhy
 * @date 2022-06-23
 */
@Api(
        tags = {"供应商信息"}
)
@Slf4j
@RestController
@RequestMapping("/system/supplier")
public class SwJsSupplierController extends BaseController {
    @Resource
    private ISwJsSupplierService swJsSupplierService;



    /**
     * 新增供应商信息
     */
    @ApiOperation(
            value ="新增供应商信息",
            notes = "新增供应商信息"
    )
    @PostMapping("/SwJsSupplieradd")
    @PreAuthorize("@ss.hasPermi('system:supplier:add')")
    public AjaxResult SwJsSupplieradd(@Valid @RequestBody CbsaDto cbsaDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(swJsSupplierService.insertSwJsSupplier(cbsaDto));


        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【新增供应商信息】接口出现异常,参数${},异常${}$", JSON.toJSON(cbsaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【新增供应商信息】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbsaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 修改供应商信息】
     */
    @ApiOperation(
            value ="修改供应商信息",
            notes = "修改供应商信息"
    )
    @PostMapping("/SwJsSupplieredit")
    @PreAuthorize("@ss.hasPermi('system:supplier:edit')")
    public AjaxResult SwJsSupplieredit(@RequestBody CbsaDto cbsaDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(swJsSupplierService.updateSwJsSupplier(cbsaDto));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【修改供应商信息】接口出现异常,参数${},异常${}$", JSON.toJSON(cbsaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【修改供应商信息】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbsaDto),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }



    /**
     * 删除商品分类
     */
    @ApiOperation(
            value ="删除供应商信息",
            notes = "删除供应商信息"
    )
    @PostMapping("/SwJsSupplierremove")
    @PreAuthorize("@ss.hasPermi('system:supplier:remove')")
    public AjaxResult SwJsSupplierremove(@RequestBody CbsaDto cbsaDto) {
        try {
            return toAjax(swJsSupplierService.deleteSwJsSupplierById(cbsaDto));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【删除供应商信息】接口出现异常,参数${},异常${}$", JSON.toJSON(cbsaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【删除供应商信息】接口出现异常,参数${}$,异常${}$",JSON.toJSON(cbsaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 查询供应商信息列表
     */
    @ApiOperation(
            value ="查询供应商信息列表",
            notes = "查询供应商信息列表"
    )
    @GetMapping("/SwJsSupplierlist")
    @PreAuthorize("@ss.hasPermi('system:supplier:list')")
    public AjaxResult<TableDataInfo> SwJsSupplierlist(Cbsa cbsa) {
        try{
            startPage();
            List<Cbsa> list = swJsSupplierService.selectSwJsSupplierList(cbsa);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【查询供应商信息列表】接口出现异常,参数${},异常${}$", JSON.toJSON(cbsa), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【查询供应商信息列表】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbsa),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 导出供应商信息
     */
    @ApiOperation(
            value ="导出供应商信息",
            notes = "导出供应商信息"
    )
    @PostMapping("/SwJsSupplierexport")
    @PreAuthorize("@ss.hasPermi('system:supplier:export')")
    public void SwJsSupplierexport(HttpServletResponse response, Cbsa cbsa) {
        List<Cbsa> list = swJsSupplierService.selectSwJsSupplierList(cbsa);
        ExcelUtil<Cbsa> util = new ExcelUtil<Cbsa>(Cbsa.class);
        util.exportExcel(response, list, "【导出供应商信息】数据");
    }


    /**
     * 导入供应商信息
     */
    @ApiOperation(
            value ="导入供应商信息",
            notes = "导入供应商信息"
    )
    @PostMapping("/importSwJsSupplier")
    @ResponseBody
    @PreAuthorize("@ss.hasPermi('system:supplier:import')")
    public AjaxResult importSwJsSupplier(MultipartFile file, boolean updateSupport) {
        try {
            ExcelUtil<CbsaDto> util = new ExcelUtil<>(CbsaDto.class);
            List<CbsaDto> swJsSupplierList = util.importExcel(file.getInputStream());
            String operName = getUsername();
            String message = swJsSupplierService.importSwJsGoodsClassify(swJsSupplierList, updateSupport, operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【导入供应商信息】接口出现异常,参数${}$,异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【导入供应商信息】接口出现异常,参数${}$,异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【导入供应商信息】接口出现异常,参数${}$,异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }
  /*  *//**
     * 导入供应商信息下载模板
     *//*
    @ApiOperation(
            value ="导入供应商信息下载模板",
            notes = "导入供应商信息下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CbsaDto> util = new ExcelUtil<CbsaDto>(CbsaDto.class);
         util.importTemplateExcel(response,"导入供应商信息");
    }*/


    /**
     * 导入供应商信息下载模板
     */
    @ApiOperation(
            value ="导入供应商信息下载模板",
            notes = "导入供应商信息下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        InputStream in = null;
        String excelPaht="";
        String excelPaht2="";
        String pdfPath="";
        XSSFWorkbook wb = null;
        try {
            long time = System.currentTimeMillis();


            excelPaht=   RuoYiConfig.getSwdataprofile()+"供应商信息导入模板_"+time+".xlsx";
            excelPaht2 = RuoYiConfig.getSwprofile() + "模板供应商信息导入_"  + time + ".xlsx";
            FileCopyUtils.copyFile(new File(RuoYiConfig.getSwprofile()+ PathConstant.SUPPIER_ORDER_SCANSER_EXCEL),new File(excelPaht2));

            File is = new File(excelPaht2);
            wb = new XSSFWorkbook(is);

            File file = new File("text.java");

            String filePath = file.getAbsolutePath();
            saveExcelToDisk(wb, excelPaht);

            //  saveExcelToDisk(wb, name);
            FileUtils.setAttachmentResponseHeader(response, "供应商信息导入模板_.xlsx");
            FileUtils.writeBytes(excelPaht, response.getOutputStream());
        } catch (SwException e) {
            log.error("【导入供应商信息下载模板】接口出现异常,参数${}$,异常${}$", ExceptionUtils.getStackTrace(e));

            // return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【导入供应商信息下载模板】接口出现异常,参数${}$,异常${}$",  ExceptionUtils.getStackTrace(e));

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
