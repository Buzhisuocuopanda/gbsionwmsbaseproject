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
import com.ruoyi.system.domain.Cbla;
import com.ruoyi.system.domain.dto.CblaDto;
import com.ruoyi.system.domain.vo.CblaVo;
import com.ruoyi.system.service.ISwJsStoreService;
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
 * 库位Controller
 *
 * @author lhy
 * &#064;date  2022-06-16
 */
@Api(
        tags = {"库位信息维护"}
)
@Slf4j
@RestController
@RequestMapping("/system/store")
public class SwJsStoreController extends BaseController {
    @Resource
    private ISwJsStoreService swJsStoreService;

    @ApiOperation(
            value ="新增库位信息维护",
            notes = "新增库位信息维护"
    )
    @PostMapping("/SwJsStoreadd")
    @PreAuthorize("@ss.hasPermi('system:store:add')")
    public AjaxResult swJsStoreadd(@Valid @RequestBody CblaDto cblaDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(swJsStoreService.insertSwJsStore(cblaDto));
        }catch (SwException e) {
            log.error("【新增库位信息维护】接口出现异常,参数${},异常${}$", JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【新增库位信息维护】接口出现异常,参数${},异常${}$", JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【新增库位信息维护】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 修改库位信息维护
     */
    @ApiOperation(
            value ="修改库位信息维护",
            notes = "修改库位信息维护"
    )
    @PostMapping("/SwJsStoreedit")
    @PreAuthorize("@ss.hasPermi('system:store:edit')")
    public AjaxResult swJsStoreedit(@RequestBody CblaDto cblaDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(swJsStoreService.updateSwJsStore(cblaDto));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【修改库位信息维护】接口出现异常,参数${},异常${}$", JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【修改库位信息维护】接口出现异常,参数${}$,异常${}$",JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 删除库位信息维护
     */
    @ApiOperation(
            value ="删除库位信息维护",
            notes = "删除库位信息维护"
    )
    @PostMapping("/SwJsStoreremove")
    @PreAuthorize("@ss.hasPermi('system:store:remove')")
    public AjaxResult swJsStoreremove(@RequestBody CblaDto cblaDto) {
        try {
            return toAjax(swJsStoreService.deleteSwJsStoreById(cblaDto));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }
        catch (ServiceException e) {
            log.error("【删除库位信息维护】接口出现异常,参数${}$,异常${}$",JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
        catch (Exception e) {
            log.error("【删除库位信息维护】接口出现异常,参数${}$,异常${}$",JSON.toJSON(cblaDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 查询库位列表
     */
    @ApiOperation(
            value ="条件查询查询库位信息维护",
            notes = "条件查询查询库位信息维护"
    )
    @GetMapping("/SwJsStorelist")
    @PreAuthorize("@ss.hasPermi('system:store:list')")
    public AjaxResult<TableDataInfo> swJsStorelist(CblaVo CblaVo) {
        try {
            startPage();
            List<CblaVo> list = swJsStoreService.selectSwJsStoreList(CblaVo);
            return AjaxResult.success(getDataTable(list));
        }catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【条件查询查询库位信息维护】接口出现异常,参数${}$,异常${}$", JSON.toJSON(CblaVo),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }

    /**
     * 导出库位信息维护列表
     */

    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermi('system:store:export')")
    public void export(HttpServletResponse response, CblaVo cblaVo) {
        List<CblaVo> list = swJsStoreService.selectSwJsStoreList(cblaVo);
        ExcelUtil<CblaVo> util = new ExcelUtil<CblaVo>(CblaVo.class);
        util.exportExcel(response, list, "仓位信息维护数据");
    }

    /**
     * 导入库位信息
     */
    @ApiOperation(
            value ="导入库位信息",
            notes = "导入库位信息"
    )
    @PostMapping("/importSwJsStorelist")
    @PreAuthorize("@ss.hasPermi('system:store:import')")
    @ResponseBody
    public AjaxResult importSwJsGoods(MultipartFile file, boolean updateSupport) {
        try {
            ExcelUtil<CblaDto> util = new ExcelUtil<>(CblaDto.class);
            List<CblaDto> swJsGoodsList = util.importExcel(file.getInputStream());
            //    LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String operName = SecurityUtils.getUsername();

            //String operName = loginUser.getUsername();
            String message = swJsStoreService.importSwJsGoods(swJsGoodsList, updateSupport,operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【导入库位信息】接口出现异常,参数${},异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }
        catch (ServiceException e) {
            log.error("【导入库位信息】接口出现异常,参数${},异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【导入库位信息】接口出现异常,参数${},异常${}$", JSON.toJSON(message),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }



/*    *//**
     * 导入库位下载模板
     *//*
    @ApiOperation(
            value ="导入库位下载模板",
            notes = "导入库位下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CblaDto> util = new ExcelUtil<CblaDto>(CblaDto.class);
        util.importTemplateExcel(response,"导入库位下载模板");
    }*/


    /**
     * 导入库位下载模板
     */
    @ApiOperation(
            value ="导入库位下载模板",
            notes = "导入库位下载模板"
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


            excelPaht=   RuoYiConfig.getSwdataprofile()+"库位信息导入模板_"+time+".xlsx";
            excelPaht2 = RuoYiConfig.getSwprofile() + "模板库位信息导入_"  + time + ".xlsx";
            FileCopyUtils.copyFile(new File(RuoYiConfig.getSwprofile()+ PathConstant.STORESKU_ORDER_SCANSER_EXCEL),new File(excelPaht2));

            File is = new File(excelPaht2);
            wb = new XSSFWorkbook(is);

            File file = new File("text.java");

            String filePath = file.getAbsolutePath();
            saveExcelToDisk(wb, excelPaht);

            //  saveExcelToDisk(wb, name);
            FileUtils.setAttachmentResponseHeader(response, "库位信息导入模板_.xlsx");
            FileUtils.writeBytes(excelPaht, response.getOutputStream());
        } catch (SwException e) {
            log.error("【导入库位下载模板】接口出现异常,参数${}$,异常${}$", ExceptionUtils.getStackTrace(e));

            // return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【导入库位下载模板】接口出现异常,参数${}$,异常${}$",  ExceptionUtils.getStackTrace(e));

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
