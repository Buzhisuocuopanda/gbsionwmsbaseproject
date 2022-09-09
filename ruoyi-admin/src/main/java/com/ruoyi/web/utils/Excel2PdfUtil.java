package com.ruoyi.web.utils;

/**
 * ClassName Excel2PdfUtil
 * Description
 * Create by gfy
 * Date 2022/9/9 8:35
 */
import com.aspose.cells.License;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


public class Excel2PdfUtil {
    /**
     * excel文件导出为PDF文件
     * @param Address 需要转化的Excel文件地址，
     *
     */
    public static void excel2pdf(String Address,String saveAddress) {
        if (!getLicense()) {   // 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        try {
//            File pdfFile = new File("src/main/resources/template/肩关节功能评定量表.pdf"); // 输出路径
            File pdfFile = new File(saveAddress); // 输出路径
            FileInputStream excelstream = new FileInputStream(Address);
            Workbook wb = new Workbook(excelstream);// excel路径，这里是先把数据放进缓存表里，然后把缓存表转化成PDF
            FileOutputStream fileOS = new FileOutputStream(pdfFile);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);//参数true把内容放在一张PDF页面上；
            wb.save(fileOS, pdfSaveOptions);
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取认证，去除水印
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = Excel2PdfUtil.class.getClassLoader().getResourceAsStream("license.xml");//这个文件应该是类似于密码验证(证书？)，用于获得去除水印的权限
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

