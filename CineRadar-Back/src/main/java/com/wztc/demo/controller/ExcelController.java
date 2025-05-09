package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.Response;
import com.wztc.demo.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 导出与导入
 */
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    /**
     * 导出电影excel
     */
    @GetMapping("/exportGoodsExcel")
    public Response exportGoodsExcel(HttpServletRequest request, HttpServletResponse response){
        List<String> fields = Arrays.asList("*");
        String tableName = "goods";
        String sheetName = "电影表";
        String fileName = excelService.exportExcel(fields, tableName, sheetName);
        return Response.success(fileName);
    }


    /**
     * 文件上传
     */
    @NoTokenAccess
    @PostMapping("/importGoodsExcel")
    public Response importGoodsExcel(HttpServletRequest request,@RequestPart("file") MultipartFile multipartFile) {
        String tableName = "goods";
        excelService.importExcel(multipartFile, tableName);
        return Response.success();
    }
}
