package com.scmt.generator.utils;



import com.scmt.generator.domain.ColumnModel;
import com.scmt.generator.domain.TableModel;

import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * @author linzf
 * @since 2019-08-05
 * 类描述：数据库的工具类
 */
public class JdbcUtil {

    /**
     * 功能描述：获取主键的名称
     *
     * @param table 表对象
     * @return 返回主键名称
     */
    public static String getPrimaryKeyType(TableModel table) {
        for (ColumnModel columnModel : table.getColumns()) {
            if (columnModel.isPrimaryKey()) {
                return columnModel.getFieldType();
            }
        }
        return "";
    }

    /**
     * 功能描述：获取主键的名称
     *
     * @param table 表对象
     * @return 返回主键名称
     */
    public static String getPrimaryKeyName(TableModel table) {
        for (ColumnModel columnModel : table.getColumns()) {
            if (columnModel.isPrimaryKey()) {
                return columnModel.getColumnName();
            }
        }
        return "";
    }

    /**
     * 获取连接
     * @param driverClassName 驱动名称
     * @param username        账号
     * @param password        密码
     * @param url             地址
     * @return 返回数据库连接
     */
    public static Connection getConnection(String driverClassName, String username, String password, String url) {
        try {
            Class.forName(driverClassName);
            Properties properties = new Properties();
            properties.put("user", username);
            properties.put("password", password);
            //想要获取数据库结构中的注释，这个值是重点
            properties.put("remarksReporting", "true");
            return DriverManager.getConnection(url, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取表结构
     *
     * @param driverClassName 驱动名称
     * @param username        账号
     * @param password        密码
     * @param url             地址
     * @param tableName       表名
     * @return 返回表的实体类型
     */
    public static TableModel getTableStructure(String driverClassName, String username, String password, String url, String tableName) {
        List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();
        List<ColumnModel> primaryKeyColumns = new ArrayList<ColumnModel>();
        Set<String> imports = new HashSet<String>();
        try {

            DatabaseMetaData dbMeta = getConnection(driverClassName, username, password, url).getMetaData();
            List<String> primaryKeys = getPrimaryKeys(dbMeta, tableName);
            ResultSet columnSet = dbMeta.getColumns(null, "%", tableName, "%");
            ColumnModel columnModel = null;
            while (columnSet.next()) {
                columnModel = new ColumnModel();
                columnModel.setColumnName(columnSet.getString("COLUMN_NAME"));
                columnModel.setColumnSize(columnSet.getInt("COLUMN_SIZE"));
                columnModel.setDataType(columnSet.getString("DATA_TYPE"));
                columnModel.setRemarks(columnSet.getString("REMARKS"));
                columnModel.setTypeName(columnSet.getString("TYPE_NAME"));
                try {
                    columnModel.setAutoIncrement(columnSet.getBoolean("IS_AUTOINCREMENT"));
                } catch (Exception e) {
                    if ("YES".equals(columnSet.getString("IS_AUTOINCREMENT"))) {
                        columnModel.setAutoIncrement(true);
                    } else {
                        columnModel.setAutoIncrement(false);
                    }
                }
                columnModel.setPrimaryKey(justicPrimaryKey(columnModel.getColumnName(), primaryKeys));
                String columnClassName = sqlType2JavaType(columnModel.getTypeName());
                String imp = getImportByJavaType(columnClassName);
                if (StringUtils.isNotEmpty(imp)) {
                    imports.add(imp);
                }
                String fieldName = getFieldName(columnModel.getColumnName());
                String fieldType = null;
                try {
                    if (StringUtils.isNotEmpty(columnClassName)) {
                        fieldType = Class.forName(columnClassName).getSimpleName();
                    } else {
                        throw new RuntimeException();
                    }
                } catch (ClassNotFoundException e) {
                    fieldType = columnClassName;
                }
                columnModel.setFieldName(fieldName);
                columnModel.setColumnClassName(columnClassName);
                columnModel.setFieldType(fieldType);
                System.out.println(columnSet.getString("COLUMN_NAME") + "---------" + fieldType);
                columnModelList.add(columnModel);
                if (columnModel.isPrimaryKey()) {
                    primaryKeyColumns.add(columnModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TableModel table = new TableModel();
        table.setColumns(columnModelList);
        table.setPrimaryKeyColumns(primaryKeyColumns);
        table.setImports(imports);
        table.setTableName(tableName);
        return table;
    }

    /**
     * 将数据库字段转换成bean属性
     *
     * @param columnName 字段名称
     * @return 返回字段属性
     */
    private static String getFieldName(String columnName) {
        char[] columnCharArr = columnName.toCharArray();
        StringBuffer sb = new StringBuffer();
        int ad = -1;
        for (int i = 0; i < columnCharArr.length; i++) {
            char cur = columnCharArr[i];
            if (cur == '_') {
                ad = i;
            } else {
                if ((ad + 1) == i && ad != -1) {
                    sb.append(Character.toUpperCase(cur));
                } else {
                    sb.append(cur);
                }
                ad = -1;
            }
        }
        return sb.toString();
    }

    /**
     * 获取表主键
     *
     * @throws SQLException
     */
    private static List<String> getPrimaryKeys(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        ResultSet pkRSet = dbMeta.getPrimaryKeys(null, null, tableName);
        List<String> primaryKyes = new ArrayList<String>();
        while (pkRSet.next()) {
            primaryKyes.add(pkRSet.getObject("COLUMN_NAME").toString());
        }
        return primaryKyes;
    }

    /**
     * 判断列是否为主键列
     */
    private static boolean justicPrimaryKey(String columnName, List<String> primaryKyes) {
        for (String key : primaryKyes) {
            if (key.equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private static String sqlType2JavaType(String sqlType) {
        if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "Byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "Short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        } else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "Timestamp";
        }
        return "String";
    }

    /**
     * 根据数据类型获取需要引入的类
     */
    private static String getImportByJavaType(String javaType) {
        switch (javaType) {
            case "Date":
                return "java.util.Date";
            case "Timestamp":
                return "java.sql.Timestamp";
            case "Blod":
                return "java.sql.Blod";
        }
        return null;
    }
}
