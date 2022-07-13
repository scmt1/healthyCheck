package com.scmt.generator.utils;





import java.util.List;

import com.scmt.generator.domain.ColumnModel;
import com.scmt.generator.domain.TableModel;

/**
 * @author linzf
 * @since 2019-08-05
 * 类描述：mybatis配置文件生成功能
 */
public class MyBatisUtil {
    /**
     * 从表结构中去生成mybatis配置
     *
     * @param table     表名
     * @param namespace 命名空间
     * @param beanName  类全名
     * @param beanShortName 类的名称
     * @return 返回组装的字符串
     */
    public static String genMapperConfig(TableModel table, String namespace, String beanName, String beanShortName) {
        StringBuffer search = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        sb.append("<mapper namespace=\"" + namespace + "\">\n");
        //生成resultMap
        //String resultMap = beanName.split("\\.")[(beanName.split("\\.").length - 1)] + "Map";
       // sb.append(genResultMap(beanName, resultMap, table));
        //sb.append("\n");
//        sb.append("\t<select id=\"query" + beanShortName + "List\" resultMap=\"" + resultMap + "\">\n");
//        sb.append("\t\tselect * from " + table.getTableName() + " where 1=1\n");
//        sb.append("\t\t<if test=\"search!=null and search!=''\"> \n");
//        sb.append("\t\t\tand (\n");
//        for(ColumnModel c:table.getColumns()){
//            if(c.getFieldType().equals("String")){
//                search.append("\t\t\t\t" + c.getColumnName() + " like concat('%',#{search},'%') or\n");
//            }
//        }
//        sb.append(search.substring(0,search.length()-3)).append("\n");
//        sb.append("\t\t\t)\n");
//        sb.append("\t\t</if>\n");
//        sb.append("\t</select>\n");
        //sb.append("\n");
        sb.append("</mapper>");
        return sb.toString();
    }

    /**
     * 功能描述：生成dao的map
     *
     * @param beanName  类的名称
     * @param resultMap map的dao的名称
     * @param table     表的名称
     * @return 返回组装的字符串
     */
    private static String genResultMap(String beanName, String resultMap, TableModel table) {
        List<ColumnModel> columnModelList = table.getColumns();
        List<ColumnModel> primaryKeys = table.getPrimaryKeyColumns();
        StringBuffer sb = new StringBuffer();
        sb.append("\t<resultMap type=\"" + beanName + "\" id=\"" + resultMap + "\">\n");
        if (primaryKeys.size() == 1) {
            ColumnModel primaryKey = primaryKeys.get(0);
            sb.append("\t\t<id property=\"" + primaryKey.getFieldName() + "\" column=\"" + primaryKey.getColumnName() + "\"/>\n");
            for (ColumnModel cm : columnModelList) {
                if (!cm.isPrimaryKey()) {
                    sb.append("\t\t<result property=\"" + cm.getFieldName() + "\" column=\"" + cm.getColumnName() + "\"/>\n");
                }
            }
        } else {
            for (ColumnModel cm : columnModelList) {
                sb.append("\t\t<result property=\"" + cm.getFieldName() + "\" column=\"" + cm.getColumnName() + "\"/>\n");
            }
        }
        sb.append("\t</resultMap>\n\n");
        return sb.toString();
    }


}
