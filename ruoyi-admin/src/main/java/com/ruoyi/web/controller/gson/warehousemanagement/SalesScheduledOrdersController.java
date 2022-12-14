package com.ruoyi.web.controller.gson.warehousemanagement;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.ErrCode;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.SwException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ValidUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Cbpd;
import com.ruoyi.system.domain.Do.GsOrdersInDo;
import com.ruoyi.system.domain.Do.GsSalesChangeDo;
import com.ruoyi.system.domain.Do.GsSalesOrdersDo;
import com.ruoyi.system.domain.Do.GsSalesOrdersInDo;
import com.ruoyi.system.domain.GsSalesOrdersChange;
import com.ruoyi.system.domain.GsSalesOrdersIn;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.service.SalesScheduledOrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

/**
 * 销售预订单Controller
 *
 * @author lhy
 * &#064;date  2022-06-26
 */
@Api(
        tags = {"销售预订单"}
)
@Slf4j
@RestController
@RequestMapping("/system/SalesScheduledOrders")
public class SalesScheduledOrdersController extends BaseController {

    @Resource
    private SalesScheduledOrdersService salesScheduledOrdersService;


    /**
     * 添加销售预订单
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="添加销售预订单",
            notes = "添加销售预订单"
    )
    @PostMapping("/addSalesScheduledOrders")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:add')")
    public AjaxResult addSalesScheduledOrders(@Valid @RequestBody GsSalesOrdersDto gsSalesOrdersDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.addSalesScheduledOrders(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【添加销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【添加销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【添加销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 修改销售预订单
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="修改销售预订单",
            notes = "修改销售预订单"
    )
    @PostMapping("/editSalesScheduledOrders")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:edit')")
    public AjaxResult editSalesScheduledOrders(@Valid @RequestBody GsSalesOrdersDto gsSalesOrdersDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.editSalesScheduledOrders(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【修改销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【修改销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【修改销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 删除销售预订单
     *
     * @param deleteSaleOrderDto
     * @return
     */
    @ApiOperation(
            value ="删除销售预订单",
            notes = "删除销售预订单"
    )
    @PostMapping("/deleteSalesScheduledOrders")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:remove')")
    public AjaxResult deleteSalesScheduledOrders(@Valid @RequestBody DeleteSaleOrderDto deleteSaleOrderDto, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            if (deleteSaleOrderDto.getOrderId() == null) {
                throw new SwException("请选择要删除的销售预订单");
            }
            deleteSaleOrderDto.setUserId(getUserId().intValue());

            salesScheduledOrdersService.deleteSalesScheduledOrders(deleteSaleOrderDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【删除销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(deleteSaleOrderDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【删除销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(deleteSaleOrderDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【删除销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(deleteSaleOrderDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 查询销售预订单
     *
     * @param gsSalesOrdersDo
     * @return
     */
    @ApiOperation(
            value ="查询销售预订单",
            notes = "查询销售预订单"
    )
    @GetMapping("/saleOrderList")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:list')")
    public AjaxResult<List<TableDataInfo>> saleOrderList( GsSalesOrdersDo gsSalesOrdersDo) {
        try {
            startPage();
            List<GsSalesOrdersVo> list = salesScheduledOrdersService.saleOrderList(gsSalesOrdersDo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 销售预订单详情
     *
     * @param gsSalesOrdersDetailsVo
     * @return
     */
    @ApiOperation(
            value ="销售预订单详情",
            notes = "销售预订单详情"
    )
    @PostMapping("/saleOrderListdetail")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:detail')")
    public AjaxResult<List<TableDataInfo>> saleOrderListdetail( GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo) {
        try {
           // startPage();
            List<GsSalesOrdersDetailsVo> list = salesScheduledOrdersService.saleOrderListdetail(gsSalesOrdersDetailsVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【销售预订单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDetailsVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 审核销售预订单
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="审核销售预订单",
            notes = "审核销售预订单"
    )
    @PostMapping("/SalesScheduledOrderssh")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:sh')")
    public AjaxResult salesScheduledOrderssh(@RequestBody GsSalesOrdersDto gsSalesOrdersDto) {
        try {
            salesScheduledOrdersService.salesScheduledOrderssh(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【审核销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【审核销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【审核销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 反审销售预订单
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="反审销售预订单",
            notes = "反审销售预订单"
    )
    @PostMapping("/SalesScheduledOrdersfs")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:fs')")
    public AjaxResult salesScheduledOrdersfs( @RequestBody GsSalesOrdersDto gsSalesOrdersDto) {
        try {
            salesScheduledOrdersService.salesScheduledOrdersfs(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【反审销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【反审销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【反审销售预订单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 销售预订单标记完成
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="销售预订单标记完成",
            notes = "销售预订单标记完成"
    )
    @PostMapping("/SalesScheduledOrdersbjwc")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:bjwc')")
    public AjaxResult SalesScheduledOrdersbjwc( @RequestBody GsSalesOrdersDto gsSalesOrdersDto) {
        try {
            salesScheduledOrdersService.salesScheduledOrdersbjwc(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【销售预订单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【销售预订单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【销售预订单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 销售预订单取消完成
     *
     * @param gsSalesOrdersDto
     * @return
     */
    @ApiOperation(
            value ="销售预订单取消完成",
            notes = "销售预订单取消完成"
    )
    @PostMapping("/SalesScheduledOrdersqxwc")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:qxwc')")
    public AjaxResult SalesScheduledOrdersqxwc( @RequestBody GsSalesOrdersDto gsSalesOrdersDto) {
        try {
            salesScheduledOrdersService.salesScheduledOrdersqxwc(gsSalesOrdersDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【销售预订单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【销售预订单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【销售预订单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    //销售预订单入库单

    /**
     * 添加预订单入库单
     *
     * @param gsSalesOrdersIn
     * @return
     */
    @ApiOperation(
            value ="添加预订单入库单",
            notes = "添加预订单入库单"
    )
    @PostMapping("/addSubscribetotheinventoryslip")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:add')")
    public AjaxResult addSubscribetotheinventoryslip(@Valid @RequestBody List<GsSalesOrdersIn> gsSalesOrdersIn, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(salesScheduledOrdersService.addSubscribetotheinventoryslip(gsSalesOrdersIn));
//            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【添加预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersIn), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【添加预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersIn), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【添加预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersIn), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 修改预订单入库单
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="修改预订单入库单",
            notes = "修改预订单入库单"
    )
    @PostMapping("/editSubscribetotheinventoryslip")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:edit')")
    public AjaxResult editSubscribetotheinventoryslip(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.editSubscribetotheinventoryslip(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【修改预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【修改预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【修改预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 删除预订单入库单
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="删除预订单入库单",
            notes = "删除预订单入库单"
    )
    @PostMapping("/deleteSubscribetotheinventoryslip")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:remove')")
    public AjaxResult deleteSubscribetotheinventoryslip(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.deleteSubscribetotheinventoryslip(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【删除预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【删除预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【删除预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 查询预订单入库单
     *
     * @param gsSalesOrdersInVo
     * @return
     */
    @ApiOperation(
            value ="查询预订单入库单",
            notes = "查询预订单入库单"
    )
    @GetMapping("/seleteSubscribetotheinventoryslip")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:list')")
    public AjaxResult<List<TableDataInfo>> seleteSubscribetotheinventoryslip( GsSalesOrdersInVo gsSalesOrdersInVo) {
        try {
            startPage();
            List<GsSalesOrdersInVo> list = salesScheduledOrdersService.seleteSubscribetotheinventoryslip(gsSalesOrdersInVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            log.error("【查询预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 查询预订单入库单
     *
     * @param gsSalesOrdersInVo
     * @return
     */
    @ApiOperation(
            value ="销售预订单入库单详情",
            notes = "销售预订单入库单详情"
    )
    @PostMapping("/selectSalesReceiptList")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:list')")
    public AjaxResult<List<TableDataInfo>> selectSalesReceiptList( GsSalesOrdersInVo gsSalesOrdersInVo) {
        try {
            startPage();
            List<GsSalesOrdersInVo> list = salesScheduledOrdersService.selectSalesReceiptList(gsSalesOrdersInVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            log.error("【销售预订单入库单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【销售预订单入库单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 审核预订单入库单
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="审核预订单入库单",
            notes = "审核预订单入库单"
    )
    @PostMapping("/Subscribetotheinventoryslipsh")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:sh')")
    public AjaxResult Subscribetotheinventoryslipsh(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.subscribetotheinventoryslipsh(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【审核预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【审核预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【审核预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 反审预订单入库单
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="反审预订单入库单",
            notes = "反审预订单入库单"
    )
    @PostMapping("/Subscribetotheinventoryslipfs")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:fs')")
    public AjaxResult Subscribetotheinventoryslipfs(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.subscribetotheinventoryslipfs(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【反审预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【反审预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【反审预订单入库单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 预订单入库单标记完成
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="预订单入库单标记完成",
            notes = "预订单入库单标记完成"
    )
    @PostMapping("/Subscribetotheinventoryslipbjwc")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:bjwc')")
    public AjaxResult Subscribetotheinventoryslipbjwc(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.subscribetotheinventoryslipbjwc(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【预订单入库单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【预订单入库单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【预订单入库单标记完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 预订单入库单取消完成
     *
     * @param gsSalesOrdersInDto
     * @return
     */
    @ApiOperation(
            value ="预订单入库单取消完成",
            notes = "预订单入库单取消完成"
    )
    @PostMapping("/Subscribetotheinventoryslipqxwc")
    @PreAuthorize("@ss.hasPermi('system:salesReceipt:qxwc')")
    public AjaxResult Subscribetotheinventoryslipqxwc(  @RequestBody GsSalesOrdersInDto gsSalesOrdersInDto) {
        try {

            salesScheduledOrdersService.subscribetotheinventoryslipqxwc(gsSalesOrdersInDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【预订单入库单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【预订单入库单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【预订单入库单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersInDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 销售预订单变更单详情
     *
     * @param gsSalesOrdersDetailsVo
     * @return
     */
    @ApiOperation(
            value ="销售预订单变更单详情",
            notes = "销售预订单变更单详情"
    )
    @PostMapping("/saleOrderAdvance")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:advance')")
    public AjaxResult<List<TableDataInfo>> saleOrderAdvance(GsSalesOrdersDetailsVo gsSalesOrdersDetailsVo) {
        try {
            //startPage();
            List<GsSalesOrdersDetailsVo > list = salesScheduledOrdersService.saleOrderAdvance(gsSalesOrdersDetailsVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【销售预订单变更单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDetailsVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    // 销售预订单变更单

    /**
     * 添加预订单变更单
     *
     * @param gsSalesOrdersChange
     * @return
     */
    @ApiOperation(
            value ="添加预订单变更单",
            notes = "添加预订单变更单"
    )
    @PostMapping("/addGsSalesOrdersChange")
    @PreAuthorize("@ss.hasPermi('system:saleChange:add')")
    public AjaxResult addGsSalesOrdersChange(@Valid @RequestBody List<GsSalesOrdersChange> gsSalesOrdersChange, BindingResult bindingResult) {
        try {
            ValidUtils.bindvaild(bindingResult);
            return toAjax(salesScheduledOrdersService.addGsSalesOrdersChange(gsSalesOrdersChange));
//            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【添加预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChange), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【添加预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChange), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【添加预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChange), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 修改预订单变更单
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="修改预订单变更单",
            notes = "修改预订单变更单"
    )
    @PostMapping("/editGsSalesOrdersChange")
    @PreAuthorize("@ss.hasPermi('system:saleChange:edit')")
    public AjaxResult editGsSalesOrdersChange(@RequestBody  List<GsSalesOrdersChange>  gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.editGsSalesOrdersChanges(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【修改预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【修改预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【修改预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 删除预订单变更单
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="删除预订单变更单",
            notes = "删除预订单变更单"
    )
    @PostMapping("/deleteGsSalesOrdersChange")
    @PreAuthorize("@ss.hasPermi('system:saleChange:remove')")
    public AjaxResult deleteGsSalesOrdersChange( @RequestBody GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.deleteGsSalesOrdersChange(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【删除预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【删除预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【删除预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 查询预订单变更单
     *
     * @param gsSalesOrdersChangeVo
     * @return
     */
    @ApiOperation(
            value ="查询预订单变更单",
            notes = "查询预订单变更单"
    )
    @GetMapping("/seleteGsSalesOrdersChange")
    @PreAuthorize("@ss.hasPermi('system:saleChange:list')")
    public AjaxResult<List<TableDataInfo>> seleteGsSalesOrdersChange(GsSalesOrdersChangeVo gsSalesOrdersChangeVo) {
        try {
            startPage();
            List<GsSalesOrdersChangeVo> list = salesScheduledOrdersService.seleteGsSalesOrdersChange(gsSalesOrdersChangeVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            log.error("【查询预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 审核预订单变更单
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="审核预订单变更单",
            notes = "审核预订单变更单"
    )
    @PostMapping("/GsSalesOrdersChangesh")
    @PreAuthorize("@ss.hasPermi('system:saleChange:sh')")
    public AjaxResult GsSalesOrdersChangesh( @RequestBody GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.gsSalesOrdersChangesh(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【审核预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【审核预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【审核预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 反审预订单变更单
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="反审预订单变更单",
            notes = "反审预订单变更单"
    )
    @PostMapping("/GsSalesOrdersChangefs")
    @PreAuthorize("@ss.hasPermi('system:saleChange:fs')")
    public AjaxResult GsSalesOrdersChangefs( @RequestBody GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.gsSalesOrdersChangefs(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【反审预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【反审预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【反审预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 标记完成预订单变更单
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="标记完成预订单变更单",
            notes = "标记完成预订单变更单"
    )
    @PostMapping("/GsSalesOrdersChangebjwc")
    @PreAuthorize("@ss.hasPermi('system:saleChange:bjwc')")
    public AjaxResult GsSalesOrdersChangebjwc( @RequestBody GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.gsSalesOrdersChangebjwc(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【标记完成预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【标记完成预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【标记完成预订单变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 预订单变更单取消完成
     *
     * @param gsSalesOrdersChangeDto
     * @return
     */
    @ApiOperation(
            value ="预订单变更单取消完成",
            notes = "预订单变更单取消完成"
    )
    @PostMapping("/GsSalesOrdersChangeqxwc")
    @PreAuthorize("@ss.hasPermi('system:saleChange:qxwc')")
    public AjaxResult GsSalesOrdersChangeqxwc( @RequestBody GsSalesOrdersChangeDto gsSalesOrdersChangeDto) {
        try {
            salesScheduledOrdersService.gsSalesOrdersChangeqxwc(gsSalesOrdersChangeDto);
            return AjaxResult.success();
        } catch (SwException e) {
            log.error("【预订单变更单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【预订单变更单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【预订单变更单取消完成】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersChangeDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 查询预订单详情
     *
     * @param gsSalesOrderssVo
     * @return
     */
    @ApiOperation(
            value ="查询预订单详情",
            notes = "查询预订单详情"
    )
    @GetMapping("/seleteSalesbookingsummary")
    public AjaxResult<List<TableDataInfo>> seleteSalesbookingsummary( GsSalesOrderssVo gsSalesOrderssVo) {
        try {
            startPage();
            List<GsSalesOrderssVo> list = salesScheduledOrdersService.seleteSalesbookingsummary(gsSalesOrderssVo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            log.error("【查询预订单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrderssVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询预订单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrderssVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 查询预订单变更单详情
     *
     * @param fgkVo
     * @return
     */
    @ApiOperation(
            value ="查询预订单变更单详情",
            notes = "查询预订单变更单详情"
    )
    @GetMapping("/seleteSaleFgkVomary")
    public AjaxResult <FgkVo> seleteSaleFgkVomary( FgkVo fgkVo) {
        try {
            startPage();
         FgkVo res = salesScheduledOrdersService.seleteSaleFgkVomary(fgkVo);
            return AjaxResult.success(res);
        } catch (SwException e) {
            log.error("【查询预订单变更单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(fgkVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询预订单变更单详情】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(fgkVo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }

    /**
     * 导入销售预购单
     */
    @ApiOperation(
            value ="导入销售预购单",
            notes = "导入销售预购单"
    )
    @PostMapping("/importSwJsGoods")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @ResponseBody
    public AjaxResult importSwJsGoods(MultipartFile file, boolean updateSupport) {
        try {
            ExcelUtil<GsSalesOrdersdrDto> util = new ExcelUtil<>(GsSalesOrdersdrDto.class);
            List<GsSalesOrdersdrDto> swJsGoodsList = util.importExcel(file.getInputStream());
            //    LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String operName = SecurityUtils.getUsername();

            //String operName = loginUser.getUsername();
            String message = salesScheduledOrdersService.importSwJsGoods(swJsGoodsList, updateSupport,operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【导入销售预购单】接口出现异常,参数${},异常${}$", JSON.toJSON(file), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【导入销售预购单】接口出现异常,参数${},异常${}$", JSON.toJSON(file), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【导入销售预购单】接口出现异常,参数${},异常${}$", JSON.toJSON(file),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 变更单新增
     */
    @ApiOperation(
            value ="变更单新增",
            notes = "变更单新增"
    )
    @PostMapping("/bgdxz")
    public AjaxResult SwJsPurchaseinboundeditone(@Valid @RequestBody GsSalesChangeDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundeditone(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【变更单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【变更单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【变更单新增】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }




    /**
     * 变更单修改
     */
    @ApiOperation(
            value ="变更单修改",
            notes = "变更单修改"
    )
    @PostMapping("/bgdxg")
    public AjaxResult SwJsPurchaseinboundedibgdxg(@Valid @RequestBody GsSalesChangeDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundedibgdxg(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【变更单修改】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【变更单修改】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【变更单修改】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }



    /**
     * 变更单审核
     */
    @ApiOperation(
            value ="变更单审核",
            notes = "变更单审核"
    )
    @PostMapping("/bgdxgsh")
    public AjaxResult SwJsPurchaseinboundebgdxgsh(@Valid @RequestBody GsSalesChangeDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundedgdxgsh(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【变更单审核】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【变更单审核】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【变更单审核】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 变更单删除
     */
    @ApiOperation(
            value ="变更单删除",
            notes = "变更单删除"
    )
    @PostMapping("/bgdxgdelete")
    public AjaxResult SwJsPurchaseinboundbgdxgdelete(@Valid @RequestBody GsSalesChangeDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundbgdxgdelete(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【变更单删除】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【变更单删除】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【变更单删除】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 查询销售变更单
     *
     * @param gsSalesOrdersDo
     * @return
     */
    @ApiOperation(
            value ="查询销售变更单",
            notes = "查询销售变更单"
    )
    @GetMapping("/saleOrderLists")
    @PreAuthorize("@ss.hasPermi('system:saleOrder:list')")
    public AjaxResult<List<TableDataInfo>> saleOrderLists( GsSalesOrdersDo gsSalesOrdersDo) {
        try {
            startPage();
            List<GsSalesOrdersVo> list = salesScheduledOrdersService.saleOrderLists(gsSalesOrdersDo);
            return AjaxResult.success(getDataTable(list));
        } catch (SwException e) {
            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【查询销售变更单】接口出现异常,参数${}$,异常${}$",  JSON.toJSON(gsSalesOrdersDo), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }

    }


    /**
     * 导入预订单入库单下载模板
     */
    @ApiOperation(
            value ="导入预订单入库单下载模板",
            notes = "导入预订单入库单下载模板"
    )
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<GsSalesOrdersInDo> util = new ExcelUtil<GsSalesOrdersInDo>(GsSalesOrdersInDo.class);
        util.importTemplateExcel(response,"导入预订单入库单下载模板");
    }


    /**
     * 导入预订单入库单
     */
    @ApiOperation(
            value ="导入预订单入库单",
            notes = "导入预订单入库单"
    )
    @PostMapping("/importSwJsGoodssss")
    @PreAuthorize("@ss.hasPermi('system:purchaseinbound:import')")
    @ResponseBody
    public AjaxResult importSwJsGoodss(MultipartFile file, boolean updateSupport) {
        try {
            ExcelUtil<GsSalesOrdersInDo> util = new ExcelUtil<>(GsSalesOrdersInDo.class);
            List<GsSalesOrdersInDo> swJsGoodsList = util.importExcel(file.getInputStream());
            //    LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String operName = SecurityUtils.getUsername();

            //String operName = loginUser.getUsername();
            String message = salesScheduledOrdersService.importSwJsGoodss(swJsGoodsList, updateSupport,operName);
            return AjaxResult.success(message);
        }catch (SwException e) {
            log.error("【导入预订单入库单】接口出现异常,参数${},异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (ServiceException e) {
            log.error("【导入预订单入库单】接口出现异常,参数${},异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }
        catch (JSONException e) {
            log.error("【导入预订单入库单】接口出现异常,参数${},异常${}$", JSON.toJSON(message), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (Exception e) {
            log.error("【导入预订单入库单】接口出现异常,参数${},异常${}$", JSON.toJSON(message),ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    /**
     * 入库单新增
     */
    @ApiOperation(
            value ="入库单新增",
            notes = "入库单新增"
    )
    @PostMapping("/rkdxz")
    public AjaxResult SwJsPurchaseinboundrkdxz(@Valid @RequestBody GsOrdersInDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundrkdxz(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【入库单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【入库单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【入库单新增】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }





    /**
     * 入库单修改
     */
    @ApiOperation(
            value ="入库单修改",
            notes = "入库单修改"
    )
    @PostMapping("/rkdxg")
    public AjaxResult SwJsPurchaseinboundedirkdxg(@Valid @RequestBody GsOrdersInDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinboundedirkdxg(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【入库单修改】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【入库单修改】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【入库单修改】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }



    /**
     * 入库单审核
     */
    @ApiOperation(
            value ="入库单审核",
            notes = "入库单审核"
    )
    @PostMapping("/rkdsh")
    public AjaxResult SwJsPurchaseinbounderkdsh(@Valid @RequestBody GsSalesChangeDo cbpdDto, BindingResult bindingResult) {


        try {
            ValidUtils.bindvaild(bindingResult);
            salesScheduledOrdersService.SwJsPurchaseinbounderkdsh(cbpdDto);
            return AjaxResult.success();


        }catch (SwException e) {
            log.error("【入库单审核】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【入库单审核】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【入库单审核】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }


    @ApiOperation(
            value ="入库单详情",
            notes = "入库单详情"
    )
    @PostMapping("/rkdxq")
    public AjaxResult SwJsPurchaseinboundrrkdxq( GsOrdersInDo cbpdDto) {


        try {
            GsOrdersInDo gsOrdersInDo = salesScheduledOrdersService.SwJsPurchaseinboundrrkdxq(cbpdDto);
            return AjaxResult.success(gsOrdersInDo);


        }catch (SwException e) {
            log.error("【入库单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        }catch (ServiceException e) {
            log.error("【入库单新增】接口出现异常,参数${},异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.SYS_PARAMETER_ERROR.getErrCode(), e.getMessage());

        } catch (Exception e) {
            log.error("【入库单新增】接口出现异常,参数${}$,异常${}$", JSON.toJSON(cbpdDto), ExceptionUtils.getStackTrace(e));

            return AjaxResult.error((int) ErrCode.UNKNOW_ERROR.getErrCode(), "操作失败");
        }
    }



}
