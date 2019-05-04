package com.wdy.module.serviceUtil;

import com.csvreader.CsvReader;
import com.wdy.module.common.constant.SqlConstant;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.service.Service;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PoiUtil {
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件行分隔符
     */
    private static final String CSV_RN = "\r\n";

    public static HSSFWorkbook exportData2Excel(List dataList, List columns, String tableName) {
        //1.创建Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建Excel表单
        HSSFSheet sheet = workbook.createSheet(tableName + "信息表");
        //创建标题的显示样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        // 设置表头
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            HSSFCell cell0 = headerRow.createCell(i);
            cell0.setCellValue(columns.get(i).toString());
            cell0.setCellStyle(headerStyle);
        }
        // 设置数据
        for (int i = 0; i < dataList.size(); i++) {
            Object data = dataList.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < columns.size(); j++) {
                try {
                    Cell cell = row.createCell(j);
                    String column = columns.get(j).toString();
                    boolean flag = false;
                    // 列名以id结尾且不是主键
                    if (column.length() > 2 && column.charAt(column.length() - 2) == 'i' && column.charAt(column.length() - 1) == 'd' && column.length() > 2) {
                        column = column.substring(0, column.length() - 2);
                        flag = true;
                    }
                    Field field = data.getClass().getDeclaredField(column);
                    //设置对象的访问权限，保证对private的属性的访问
                    field.setAccessible(true);
                    if (field.get(data) != null && flag) {
                        Object o = field.get(data);
                        Field fieldItem = o.getClass().getDeclaredField("id");
                        fieldItem.setAccessible(true);
                        // id数据
                        cell.setCellValue(fieldItem.get(o).toString());
                        continue;
                    }
                    cell.setCellValue(field.get(data) != null ? field.get(data).toString() : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        setSizeColumn(sheet, columns.size());
        return workbook;
    }

    public static HSSFWorkbook exportData2ExcelBatch() {
        String sql = SqlConstant.QUERY_ALL_TABLE;
        Service service = (Service) SpringContextUtil.getBean("BaseService");
        // 获取所有数据表
        List<String> results = service.findBySql(sql);
        //1.创建Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (String table : results) {
            try {
                List dataList = service.findBySql("select * from " + table, Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(table)));
                List columns = service.findBySql(SqlConstant.QUERY_TABLIE_COLUMN + "\'" + table + "\'");
                //创建Excel表单
                HSSFSheet sheet = workbook.createSheet(table + "信息表");
                //创建标题的显示样式
                HSSFCellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
                // 设置表头
                HSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.size(); i++) {
                    HSSFCell cell0 = headerRow.createCell(i);
                    cell0.setCellValue(columns.get(i).toString());
                    cell0.setCellStyle(headerStyle);
                }
                // 设置数据
                for (int i = 0; i < dataList.size(); i++) {
                    Object data = dataList.get(i);
                    HSSFRow row = sheet.createRow(i + 1);
                    for (int j = 0; j < columns.size(); j++) {
                        Cell cell = row.createCell(j);
                        String column = columns.get(j).toString();
                        boolean flag = false;
                        // 列名以id结尾且不是主键
                        if (column.length() > 2 && column.charAt(column.length() - 2) == 'i' && column.charAt(column.length() - 1) == 'd' && column.length() > 2) {
                            column = column.substring(0, column.length() - 2);
                            flag = true;
                        }
                        Field field = data.getClass().getDeclaredField(column);
                        //设置对象的访问权限，保证对private的属性的访问
                        field.setAccessible(true);
                        if (field.get(data) != null && flag) {
                            Object o = field.get(data);
                            Field fieldItem = o.getClass().getDeclaredField("id");
                            fieldItem.setAccessible(true);
                            // id数据
                            cell.setCellValue(fieldItem.get(o).toString());
                            continue;
                        }
                        cell.setCellValue(field.get(data) != null ? field.get(data).toString() : "");
                    }
                }
                setSizeColumn(sheet, columns.size());
            } catch (Exception e) {
            }
        }
        return workbook;
    }

    public static void exportData2Csv(List dataList, List columns, OutputStream os) {
        StringBuffer buf = new StringBuffer();
        for (Object item : columns) {
            buf.append(item.toString()).append(CSV_COLUMN_SEPARATOR);
        }
        buf.append(CSV_RN);
        try {
            // 设置数据
            for (int i = 0; i < dataList.size(); i++) {
                Object data = dataList.get(i);
                for (int j = 0; j < columns.size(); j++) {
                    String column = columns.get(j).toString();
                    boolean flag = false;
                    // 列名以id结尾且不是主键
                    if (column.charAt(column.length() - 2) == 'i' && column.charAt(column.length() - 1) == 'd' && column.length() > 2) {
                        column = column.substring(0, column.length() - 2);
                        flag = true;
                    }
                    Field field = data.getClass().getDeclaredField(column);
                    //设置对象的访问权限，保证对private的属性的访问
                    field.setAccessible(true);
                    if (field.get(data) != null && flag) {
                        Object o = field.get(data);
                        Field fieldItem = o.getClass().getDeclaredField("id");
                        fieldItem.setAccessible(true);
                        buf.append(fieldItem.get(o).toString()).append(CSV_COLUMN_SEPARATOR);
                        continue;
                    }
                    buf.append(field.get(data) != null ? field.get(data).toString() : "").append(CSV_COLUMN_SEPARATOR);
                }
                buf.append(CSV_RN);
            }
            // 写出响应
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(buf.toString());
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void importExcelDataFile(MultipartFile file, String tableName) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow columnNames = sheet.getRow(0);
            List dataColumnList = new ArrayList();
            for (int j = 0; j < columnNames.getLastCellNum(); j++)
                dataColumnList.add(columnNames.getCell(j));
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                try {
                    saveEntity(dataColumnList, tableName, row);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.FILE_ERROR);
        }
    }

    public static void importExcelDataFileBatch(MultipartFile file) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                String tableName = sheetName.substring(0, sheetName.length() - 3);
                List dataColumnList = new ArrayList();
                HSSFRow columnNames = sheet.getRow(0);
                for (int j = 0; j < columnNames.getLastCellNum(); j++)
                    dataColumnList.add(columnNames.getCell(j));
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;
                    try {
                        saveEntity(dataColumnList, tableName, row);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.FILE_ERROR);
        }
    }

    public static void importCsvDataFile(InputStream fileInputStream, List dataColumnList, String tableName, Integer mode) {
        try {
            Reader reader = new InputStreamReader(fileInputStream);
            CsvReader csvReader = new CsvReader(reader);
            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                // 读一整行 System.out.println(csvReader.getRawRecord());
                // 读这行的某一列  System.out.println(csvReader.get("id"));
                saveEntity(dataColumnList, tableName, csvReader, mode);
            }
        } catch (Exception e) {
            throw new ServiceException(ResultEnum.FILE_ERROR);
        }
    }

    public static void saveEntity(List dataColumnList, String tableName, CsvReader csvReader, Integer mode) {
        try {
            // 根据类名实例化实体类
            Class clazz = Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(tableName));
            Object o = clazz.newInstance();
            //属性赋值
            for (int j = 0; j < dataColumnList.size(); j++) {
                String column = dataColumnList.get(j).toString();
                String objectColumn = column;
                boolean flag = false;
                // 列名以id结尾且不是主键
                if (column.length() > 2 && column.charAt(column.length() - 2) == 'i' && column.charAt(column.length() - 1) == 'd' && column.length() > 2) {
                    objectColumn = column.substring(0, column.length() - 2);
                    flag = true;
                }
                Field field = clazz.getDeclaredField(objectColumn);
                field.setAccessible(true);
                // 为字节数组类型 跳过无法处理
                if (field.getType().getName().contains("[B"))
                    continue;
                // 是对象里面的对象
                if (flag && !StringUtil.isEmpty(csvReader.get(column))) {
                    Class goodClazz = Class.forName(field.getType().getName());
                    Object good = goodClazz.newInstance();
                    Field fieldItem = good.getClass().getDeclaredField("id");
                    fieldItem.setAccessible(true);
                    fieldItem.set(good, converAttributeValue(fieldItem.getType().getName(), csvReader.get(column)));
                    field.set(o, good);
                }
                // 不是对象的对象
                else if (!flag)
                    field.set(o, converAttributeValue(field.getType().getName(), csvReader.get(column)));
            }
            Object serviceObj = SpringContextUtil.getBean(SqlConstant.EntityToSqlMap.get(tableName) + "Service");
            Method saveOne;
            if (mode == 0) {
                saveOne = serviceObj.getClass().getMethod("saveOne", clazz);
            }
            else
                saveOne = serviceObj.getClass().getMethod("updateGood", clazz);
            saveOne.invoke(serviceObj, o);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static void setSizeColumn(Sheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                Row currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
                if (currentRow.getCell(columnNum) != null) {
                    Cell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            // Excel的长度为字节码长度*256,*1.3为了处理数字格式
            columnWidth = (int) Math.floor(columnWidth * 256 * 1.3);
            //单元格长度大于20000的话也不美观,设置个最大长度
            columnWidth = columnWidth >= 20000 ? 20000 : columnWidth;
            //设置每列长度
            sheet.setColumnWidth(columnNum, columnWidth);
        }
    }

    public static void writeToResponse(HSSFWorkbook workbook, HttpServletRequest request,
                                       HttpServletResponse response, String fileName) {
        try {
            String userAgent = request.getHeader("User-Agent");
            // 解决中文乱码问题
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String fileName1 = fileName + sdf.format(new Date()) + ".xlsx";
            String newFilename = URLEncoder.encode(fileName1, "UTF8");
            // 如果没有userAgent，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
            String rtn = "filename=\"" + newFilename + "\"";
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase();
                // IE浏览器，只能采用URLEncoder编码
                if (userAgent.indexOf("IE") != -1) {
                    rtn = "filename=\"" + newFilename + "\"";
                }
                // Opera浏览器只能采用filename*
                else if (userAgent.indexOf("OPERA") != -1) {
                    rtn = "filename*=UTF-8''" + newFilename;
                }
                // Safari浏览器，只能采用ISO编码的中文输出
                else if (userAgent.indexOf("SAFARI") != -1) {
                    rtn = "filename=\"" + new String(fileName1.getBytes("UTF-8"), "ISO8859-1")
                            + "\"";
                }
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                else if (userAgent.indexOf("FIREFOX") != -1) {
                    rtn = "filename*=UTF-8''" + newFilename;
                }
            }
            String headStr = "attachment;  " + rtn;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/msexcel;charset=UTF-8");
            response.setHeader("Content-Disposition", headStr);
            // 响应到客户端
            if (workbook != null) {
                OutputStream os = response.getOutputStream();
                workbook.write(os);
                os.flush();
                os.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveEntity(List dataColumnList, String tableName, Row row) throws Exception {
        // 根据类名实例化实体类
        Class clazz = Class.forName("com.wdy.module.entity." + SqlConstant.EntityToSqlMap.get(tableName));
        Object o = clazz.newInstance();
        Object serviceObj = SpringContextUtil.getBean(SqlConstant.EntityToSqlMap.get(tableName) + "Service");
        //属性赋值
        for (int j = 0; j < dataColumnList.size(); j++) {
            if (j == 0 && "T_User".equals(tableName)) continue;
            if (j == 0 && "role".equals(tableName)) continue;
            String column = dataColumnList.get(j).toString();
            String objectColumn = column;
            boolean flag = false;
            // 列名以id结尾且不是主键
            if (column.length() > 2 && column.charAt(column.length() - 2) == 'i' && column.charAt(column.length() - 1) == 'd' && column.length() > 2) {
                objectColumn = column.substring(0, column.length() - 2);
                flag = true;
            }
            Field field = clazz.getDeclaredField(objectColumn);
            field.setAccessible(true);
            if (field.getType().getName().contains("[B"))
                continue;
            // 是对象里面的对象
            if (flag && !StringUtil.isEmpty(row.getCell(j).getStringCellValue())) {
                Class goodClazz = Class.forName(field.getType().getName());
                Object good = goodClazz.newInstance();
                Field fieldItem = good.getClass().getDeclaredField("id");
                fieldItem.setAccessible(true);
                fieldItem.set(good, converAttributeValue(fieldItem.getType().getName(), String.valueOf(getCellValueByType(row.getCell(j)))));
                field.set(o, good);
            }
            // 不是对象的对象
            else if (!flag) {
                field.set(o, converAttributeValue(field.getType().getName(), String.valueOf(getCellValueByType(row.getCell(j)))));
            }
        }
        // 得到方法对象,有参的方法需要指定参数类型
        Method saveOne = serviceObj.getClass().getMethod("saveOne", clazz);
        // 执行存储方法，有参传参 结果为返回值
        saveOne.invoke(serviceObj, o);
    }

    public static void responseSetProperties(String fileName, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("User-Agent");
        // 设置文件后缀
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String fn = fileName + sdf.format(new Date()) + ".csv";
        // 如果没有userAgent，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
        String rtn = "filename=\"" + fn + "\"";
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("IE") != -1) {
                rtn = "filename=\"" + fn + "\"";
            }
            // Opera浏览器只能采用filename*
            else if (userAgent.indexOf("OPERA") != -1) {
                rtn = "filename*=UTF-8''" + fn;
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            else if (userAgent.indexOf("SAFARI") != -1) {
                rtn = "filename=\"" + new String(fn.getBytes("UTF-8"), "ISO8859-1")
                        + "\"";
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            else if (userAgent.indexOf("FIREFOX") != -1) {
                rtn = "filename*=UTF-8''" + fn;
            }
        }
        String headStr = "attachment;  " + rtn;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", headStr);
    }

    // 自动匹配类型
    public static Object converAttributeValue(String type, String value) {
        if ("long".equals(type) || Long.class.getTypeName().equals(type)) {
            if (!StringUtil.isEmpty(value) && value.contains(".")) {
                value = value.substring(0, value.indexOf("."));
            }
            return Long.parseLong(StringUtil.isEmpty(value) ? "0" : value);
        } else if ("double".equals(type) || Double.class.getTypeName().equals(type)) {
            return Double.parseDouble(StringUtil.isEmpty(value) ? "0" : value);
        } else if (Timestamp.class.getTypeName().equals(type)) {
            if (StringUtil.isEmpty(value) || !value.contains("-"))
                return new Timestamp(System.currentTimeMillis());
            return Timestamp.valueOf(value);
        } else if ("int".equals(type) || Integer.class.getTypeName().equals(type)) {
            return Integer.valueOf(StringUtil.isEmpty(value) ? "0" : value);
        } else if ("byte".equals(type) || Byte.class.getTypeName().equals(type)) {
            if (!StringUtil.isEmpty(value) && value.contains("."))
                value = value.substring(0, value.indexOf("."));
            return Byte.valueOf(value);
        } else {
            return value;
        }
    }

    private static Object getCellValueByType(Cell cell) {
        Object cellValue = "";
        if (null != cell) {
            switch (cell.getCellType()) {
                // 数字
                case HSSFCell.CELL_TYPE_NUMERIC:
                    //判断单元格的类型是否则NUMERIC类型
                    if (0 == cell.getCellType()) {
                        // 判断是否为日期类型
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            DateFormat formater = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm");
                            cellValue = formater.format(date);
                        } else {
                            cellValue = Double.toString(cell.getNumericCellValue());
                        }
                    }
                    break;
                // 字符串
                case HSSFCell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    if ("null".equals(cellValue))
                        cellValue = "";
                    break;
                // Boolean
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    cellValue = cell.getBooleanCellValue() + "";
                    break;
                // 公式
                case HSSFCell.CELL_TYPE_FORMULA:
                    cellValue = cell.getCellFormula() + "";
                    break;
                // 空值
                case HSSFCell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                // 故障
                case HSSFCell.CELL_TYPE_ERROR:
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
                    break;
            }
        }
        return cellValue;
    }
}

