package com.wztc.demo.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.wztc.demo.controller.FileController;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.ExcelMapper;
import com.wztc.demo.util.DateUtils;
import com.wztc.demo.util.ExcelUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelService {
    @Autowired
    private ExcelMapper excelMapper;

    @Autowired
    private SqlSession sqlSession;

    /**
     * 导出 excel
     */
    public String exportExcel(List<String> fields, String tableName, String outFileName){
        StringBuilder sqlBuilder = new StringBuilder();
        //构建查询 sql语句
        String queryFields = String.join(",", fields);
        sqlBuilder.append("select ")
                .append(queryFields)
                .append(" from " + tableName + " order by id asc");

        //数据List<Map>
        List<Map> lstData = excelMapper.dynamicSqlOperDbData(sqlBuilder.toString());

        //构建excel标题头
        Map<String, String> headerMap = fields.stream()
                .collect(Collectors.toMap(
                        field -> field,  //kyeMapper
                        field -> field,  //valueMapper
                        //如果有重复的键, 则抛出异常(这里不会发生，因为假设表表中没有重复的元素)
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        //导出exel
        ExcelWriter excelWriter = exportExcelImplByHuTools(headerMap, lstData, outFileName);
        //保存excel数据到excel文件
        return saveExcel(excelWriter, outFileName);
    }


    /**
     * 使用hutool保存
     */
    private <T> ExcelWriter exportExcelImplByHuTools(Map<String, String> headers, List<T> data, String sheetName){
        final BigExcelWriter bigWriter = ExcelUtil.getBigWriter();
        //设置Sheet名称
        bigWriter.renameSheet(0, sheetName);
        //excel标题
        headers.forEach(bigWriter::addHeaderAlias);
        //写数据
        bigWriter.write(data);
        return bigWriter;
    }

    /**
     *  excel保存文件
     */
    private String saveExcel(ExcelWriter excelWriter, String fileNamePrefix){
        String subPath = "excel/";
        String fileName = fileNamePrefix + "_" + DateUtils.getStringId() + ".xlsx";
        try {
            FileUtil.mkdir(new File(FileController.filePath + subPath));
            // 文件存储形式：excel/fileName
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            excelWriter.flush(outputStream, true);
            byte[] fileByte = outputStream.toByteArray();
            FileUtil.writeBytes(fileByte, new File(FileController.filePath + subPath + fileName)); // ***/manager/files/1697438073596-avatar.png
            System.out.println(fileName + "--上传成功");
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        return subPath + fileName;
    }


    /**
     * 导入excel
     */
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void importExcel(MultipartFile excelFile, String tableName) {
        // 先删除原来数据
        deleteAllData(tableName);
        // 使用try-with-resources语句来处理文件流，确保资源被正确关闭
        try(InputStream inputStream = excelFile.getInputStream()) {
            Workbook workbook = null;
            // 判断文件类型
            if (excelFile.getOriginalFilename().endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream); // 处理 .xls 格式的文件
            } else if (excelFile.getOriginalFilename().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream); // 处理 .xlsx 格式的文件
            } else {
                // 处理文件类型不支持的情况，这里可以根据实际需求进行处理
                System.out.println("不支持的文件类型");
            }
            int numberOfSheets = workbook.getNumberOfSheets();
            // 遍历所有sheet, 注意这里只有一个sheet
            for (int i = 0; i < numberOfSheets; i++) {
                if (i == 0) {
                    Sheet sheetAt = workbook.getSheetAt(i);
                    int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
                    if (physicalNumberOfRows >= 2) {
                        parseExcelToDbData(sheetAt,tableName);
                    }else{
                        throw new ServiceException("excel中没有数据");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除数据
     */
    private void deleteAllData(String tableName) {
        String sql = "delete from " + tableName;
        excelMapper.dynamicSqlOperDbData(sql);
    }

    /**
     * 解析excel文件，获取表头和数据
     * 然后 保存数据库中
     */
    private boolean parseExcelToDbData(Sheet sheetAt, String tableName) {
        // 解析插入数据结果
        Map<String,String> result = new HashMap<>();
        DataFormatter dataFormatter = new DataFormatter();
        List<String> fields = new ArrayList<>();
        List<Map<String,String>> allData = new ArrayList<>();
        for (Row row : sheetAt) {
            if (row.getRowNum() == 0) {
                // 第一行是表头，获取表头字段名称
                for (Cell cell : row) {
                    String cellContent = ExcelUtils.getSafeCellContent(cell, dataFormatter);
                    fields.add(cellContent.trim());
                }
            }else {
                Map<String,String> mapData = new HashMap<>();
                for (Cell cell : row) {
                    String cellContent = ExcelUtils.getSafeCellContent(cell, dataFormatter);
                    mapData.put(fields.get(cell.getColumnIndex()),cellContent);
                }
                allData.add(mapData);
            }
        }

        // 这里我们将空字符串id视为最小值 ( 使用Stream API和Comparator对allData按照id排序)
        List<Map<String, String>> sortedData = allData.stream()
                .sorted(Comparator.comparing(
                        map -> {
                            String id = map.getOrDefault("id", "").isEmpty() ? "" : map.get("id");
                            return StrUtil.isEmpty(id) ? Integer.MAX_VALUE : Integer.parseInt(id);
                        },
                        Comparator.nullsLast(Integer::compareTo) // 处理可能的空值
                ))
                .collect(Collectors.toList());

        //循环遍历排序的列表数据
        sortedData.stream().filter(map -> !emptyMap(map)).forEach(map -> {
            StringBuilder columnNames = new StringBuilder();
            List<Object> values = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (StrUtil.isNotEmpty(entry.getValue())) {
                    if (columnNames.length() > 0) {
                        columnNames.append(", ");
                    }
                    columnNames.append(entry.getKey());
                    values.add(entry.getValue()); // 确保这里的值类型与数据库列类型匹配
                }
            }
            String sql = "INSERT INTO " + tableName + " (" + columnNames.toString() + ") VALUES (" +
                    String.join(", ", Collections.nCopies(values.size(), "?")) + ")"; // 使用问号作为参数占位符
            System.out.println(sql); // 仅用于调试，实际生产环境中建议移除或注释掉
            Connection connection = sqlSession.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // 设置参数
                int index = 1;
                for (Object value : values) {
                    preparedStatement.setObject(index++, value);
                }
                // 执行SQL语句
                preparedStatement.executeUpdate();
                System.out.println("SQL executed: " + sql);
                // 注意：实际项目中，不建议打印SQL语句到日志或控制台，特别是包含敏感信息时。
            } catch (SQLException e) {
                e.printStackTrace();
                // 处理异常，如回滚事务等
                throw new RuntimeException(e);
            }
        });

        return true;
    }

    /**
     * 空map, 所有的值为空
     * @param map
     * @return
     */
    public static boolean emptyMap(Map<String, String> map) {
        for (String value : map.values()) {
            if (value != null && !value.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
