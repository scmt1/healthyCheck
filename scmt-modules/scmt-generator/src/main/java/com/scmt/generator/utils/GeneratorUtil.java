package com.scmt.generator.utils;

import com.scmt.generator.domain.ColumnInfo;
import com.scmt.generator.domain.ColumnModel;
import com.scmt.generator.domain.GenConfig;
import com.scmt.generator.domain.TableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 类描述：生成后端代码的实现类
 *
 * @author linzf
 * @since 2019-08-05
 */
@RestController
public class GeneratorUtil {
    public static final char UNDERLINE = '_';

    /**
     * 从表结构中去生成javabean
     *
     * @param author
     * @param table
     * @param beanName
     * @param packagePath
     * @return
     */
    private static String genJavaBeanFromTableStructure(String author, TableModel table, String beanName, String packagePath) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n");
        }
        for (String imp : table.getImports()) {
            sb.append("import " + imp + ";\n");
        }
        sb.append("import javax.persistence.*;\n");
        sb.append("import org.springframework.format.annotation.DateTimeFormat;\n");
        sb.append("import tk.mybatis.mapper.annotation.KeySql;");
        String businessPackage = packagePath.substring(0, packagePath.lastIndexOf("."));
        sb.append("import " + businessPackage + ".util.UuidGenId;\n");
        sb.append("\n");
        sb.append("/**\n *@author " + author + "\n **/\n");
        List<ColumnModel> columnModelList = table.getColumns();
        try {
            sb.append("@Table(name = \"" + table.getTableName() + "\") \r\n");
            sb.append("public class " + toFirstCharUpCase(beanName) + " {\r\n");
            for (ColumnModel columnModel : columnModelList) {
                if (StringUtils.isNotBlank(columnModel.getRemarks())) {
                    sb.append("	/** \r\n");
                    sb.append("	* " + columnModel.getRemarks() + " \r\n");
                    sb.append("	*/\r\n");
                }
                if (columnModel.isPrimaryKey()) {
                    sb.append("    @Id \r\n");
                    // 判断当前的主键是否是自增的，若不是自增的则使用默认的UUID的规则生成
                    if (!columnModel.isAutoIncrement()) {
                        sb.append("    @KeySql(genId = UuidGenId.class) \r\n");
                    }
                }
                sb.append("    @Column(name = \"" + columnModel.getFieldName() + "\") \r\n");
                if (columnModel.getFieldType().equals("Date")) {
                    sb.append("    @DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\") \r\n");
                }
                sb.append("	private " + columnModel.getFieldType() + " " + columnModel.getFieldName() + ";\r\n");

            }
            sb.append("\r\n");
            //get set
            for (ColumnModel columnModel : columnModelList) {
                sb.append(
                        "\tpublic " + columnModel.getColumnClassName() + " get" + toFirstCharUpCase((String) columnModel.getFieldName()) + "() {\r\n" +
                                "\t\treturn " + columnModel.getFieldName() + ";\r\n" +
                                "\t}\r\n" +
                                "\r\n" +
                                "\tpublic void set" + toFirstCharUpCase((String) columnModel.getFieldName()) + "(" + columnModel.getColumnClassName() + " " + columnModel.getFieldName() + ") {\r\n" +
                                "\t\tthis." + columnModel.getFieldName() + " = " + columnModel.getFieldName() + ";\r\n" +
                                "\t}\r\n\r\n");
            }
            sb.append("}\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 从表结构中去生成查询实体类
     *
     * @param author
     * @param table
     * @param beanName
     * @param extendsBasePackage
     * @param packagePath
     * @return
     */
    private static String genQueryModelFromTableStructure(String author, TableModel table, String beanName, String extendsBasePackage, String packagePath) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n\n");
        }
        sb.append("import " + extendsBasePackage + ".entity.QueryBase;\n\n");
        sb.append("/**\n *@author " + author + "\n **/\n");
        try {
            sb.append("public class " + toFirstCharUpCase(beanName) + " extends QueryBase {\r\n");
            List<ColumnModel> columns = getQueryFields(table);
            for (ColumnModel columnModel : columns) {
                if (StringUtils.isNotBlank(columnModel.getRemarks())) {
                    sb.append("	//" + columnModel.getRemarks() + " \r\n");
                }
                String qFieldType = getQueryModelFieldType(columnModel.getFieldType());
                sb.append("	private " + qFieldType + " " + columnModel.getFieldName() + ";\r\n");
            }
            sb.append("\r\n");
            //get set
            for (ColumnModel columnModel : columns) {
                String qFieldType = getQueryModelFieldType(columnModel.getFieldType());
                sb.append(
                        "\tpublic " + qFieldType + " get" + toFirstCharUpCase((String) columnModel.getFieldName()) + "() {\r\n" +
                                "\t\treturn " + columnModel.getFieldName() + ";\r\n" +
                                "\t}\r\n" +
                                "\r\n" +
                                "\tpublic void set" + toFirstCharUpCase((String) columnModel.getFieldName()) + "(" + qFieldType + " " + columnModel.getFieldName() + ") {\r\n" +
                                "\t\tthis." + columnModel.getFieldName() + " = " + columnModel.getFieldName() + ";\r\n" +
                                "\t}\r\n\r\n");
            }
            sb.append("}\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 生成Dao
     */
    private static String genDao(String author, String packagePath, String beanName) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n\n");
        }
        String businessPackage = packagePath.substring(0, packagePath.lastIndexOf("."));
        sb.append("import tk.mybatis.mapper.common.Mapper;\n");
        sb.append("import " + businessPackage + ".entity." + beanName + ";\n");
        sb.append("import org.apache.ibatis.annotations.Param;\n");
        sb.append("import java.util.List;\n");
        sb.append("/**\n *@author " + author + "\n **/\n");
        sb.append("public interface " + beanName + "Dao extends Mapper<" + beanName + "> {\r\n");
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：获取列表数据\n");
        sb.append("\t* @param search 查询的实体\n");
        sb.append("\t* @return 返回查询结果\n");
        sb.append("\t*/\n");
        sb.append("\t List<" + beanName + "> query" + beanName + "List(@Param(\"search\") String search);");
        sb.append("\n");
        sb.append("\n\t\n}");
        return sb.toString();
    }

    /**
     * 生成Service
     */
    private static String genService(String author, String packagePath, String beanName, List<ColumnInfo> columns) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n\n");
        }
        String businessPackage = packagePath.substring(0, packagePath.substring(0, packagePath.lastIndexOf(".")).lastIndexOf("."));
        sb.append("import com.baomidou.mybatisplus.extension.service.IService;\n");
        sb.append("import com.baomidou.mybatisplus.core.metadata.IPage;\n");
        sb.append("import com.scmt.core.common.vo.Result;\n");
        sb.append("import com.scmt.core.common.vo.PageVo;\n");
        sb.append("import com.scmt.core.common.vo.SearchVo;\n");
        sb.append("import " + businessPackage + ".entity." + beanName + ";\n");
        sb.append("import javax.servlet.http.HttpServletResponse;\n");
        sb.append("import java.util.List;\n");
        sb.append("/**\n *@author " + author + "\n **/\n");
        sb.append("public interface I" + beanName + "Service extends IService<" + beanName + "> {\r\n");

        // 分页查询
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：实现分页查询\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 需要模糊查询的信息\n");
        sb.append("\t* @param searchVo 排序参数\n");
        sb.append("\t* @param pageVo 分页参数\n");
        sb.append("\t* @return 返回获取结果\n");
        sb.append("\t*/\n");
        sb.append("\tpublic IPage<" + beanName + "> query" + beanName + "ListByPage(" + beanName + "  " + toFirstCharLowerCase(beanName) + ", SearchVo searchVo, PageVo pageVo);\n");

        // 导出
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述： 导出\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 查询参数\n");
        sb.append("\t* @param response response参数\n");
        sb.append("\t*/\n");
        sb.append("\tpublic void download(" + beanName + "  " + toFirstCharLowerCase(beanName) + ", HttpServletResponse response) ;\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 生成ServiceImpl
     */
    private static String genServiceImpl(String author, String packagePath, String beanName, List<ColumnInfo> columns) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n\n");
        }
        String businessPackage = packagePath.substring(0, packagePath.substring(0, packagePath.substring(0, packagePath.lastIndexOf(".")).lastIndexOf(".")).lastIndexOf("."));
        sb.append("import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;\n");
        sb.append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n");
        sb.append("import com.baomidou.mybatisplus.core.metadata.IPage;\n");
        sb.append("import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n");
        sb.append("import com.baomidou.mybatisplus.core.toolkit.StringUtils;\n");
        sb.append("import " + businessPackage + ".entity." + beanName + ";\n");
        sb.append("import " + businessPackage + ".service.I" + beanName + "Service;\n");
        sb.append("import com.scmt.core.common.vo.PageVo;\n");
        sb.append("import com.scmt.core.common.vo.SearchVo;\n");
        sb.append("import " + businessPackage + ".mapper." + beanName + "Mapper;\n");
        sb.append("import com.scmt.core.utis.FileUtil;\n");
        sb.append("import javax.servlet.http.HttpServletResponse;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import java.util.LinkedHashMap;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.text.SimpleDateFormat;\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.Map;\n");

        sb.append("/**\n *@author " + author + "\n **/\n");
        sb.append("@Service\n");
        sb.append("public class " + beanName + "ServiceImpl extends ServiceImpl<" + beanName + "Mapper, " + beanName + "> implements I" + beanName + "Service {\r\n");
        sb.append("\t@Autowired\n");
        sb.append("\t@SuppressWarnings(\"SpringJavaAutowiringInspection\")\n");
        sb.append("\tprivate " + beanName + "Mapper " + toFirstCharLowerCase(beanName) + "Mapper;\n");

        // 分页查询
        sb.append("\n");
        sb.append("\t@Override\n");
        sb.append("\tpublic IPage<" + beanName + "> query" + beanName + "ListByPage(" + beanName + "  " + toFirstCharLowerCase(beanName) + ", SearchVo searchVo, PageVo pageVo){\n");
        sb.append("\t\tint page = 1;\n");
        sb.append("\t\tint limit = 10;\n");
        sb.append("\t\tif (pageVo != null) {\n");
        sb.append("\t\t\tif (pageVo.getPageNumber() != 0) {\n");
        sb.append("\t\t\t\tpage = pageVo.getPageNumber();\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t\tif (pageVo.getPageSize() != 0) {\n");
        sb.append("\t\t\t\tlimit = pageVo.getPageSize();\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t}\n");
        sb.append("\t\tPage<" + beanName + "> pageData = new Page<>(page, limit);\n");
        sb.append("\t\tQueryWrapper<" + beanName + "> queryWrapper = new QueryWrapper<>();\n");
        sb.append("\t\tif (" + toFirstCharLowerCase(beanName) + " !=null) {\n");
        sb.append("\t\t\tqueryWrapper = LikeAllField(" + toFirstCharLowerCase(beanName) + ",searchVo);\n");
        sb.append("\t\t}\n");
        sb.append("\t\tIPage<" + beanName + "> result = " + toFirstCharLowerCase(beanName) + "Mapper.selectPage(pageData, queryWrapper);\n");

        sb.append("\t\treturn  result;\n");
        sb.append("\t}\n");
        //导出
        sb.append("\t@Override\n");
        sb.append("\tpublic void download(" + beanName + " " + toFirstCharLowerCase(beanName) + ", HttpServletResponse response) {\n");
        sb.append("\t\tList<Map<String, Object>> mapList = new ArrayList<>();\n");
        sb.append("\t\tSimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n");
        sb.append("\t\tQueryWrapper<" + beanName + "> queryWrapper = new QueryWrapper<>();\n");
        sb.append("\t\tif (" + toFirstCharLowerCase(beanName) + " !=null) {\n");
        sb.append("\t\t\tqueryWrapper = LikeAllField(" + toFirstCharLowerCase(beanName) + ",null);\n");
        sb.append("\t\t}\n");
        sb.append("\t\tList<" + beanName + "> list = " + toFirstCharLowerCase(beanName) + "Mapper.selectList(queryWrapper);\n");
        sb.append("\t\tfor (" + beanName + " re : list) {\n");
        sb.append("\t\t\tMap<String, Object> map = new LinkedHashMap<>();\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if (c.getColumnType().equals("Date")) {
                    sb.append("\t\t\tmap.put(\"" + c.getRemark() + "\",simpleDateFormat.format( re.get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "()));\n");
                } else {
                    sb.append("\t\t\tmap.put(\"" + c.getRemark() + "\", re.get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "());\n");
                }

            }
        }
        sb.append("\t\t\tmapList.add(map);\n");
        sb.append("\t\t}\n");
        sb.append("\t\tFileUtil.createExcel(mapList, \"exel.xlsx\", response);\n");
        sb.append("\t}\n");

        //构建模糊查询
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：构建模糊查询\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 需要模糊查询的信息\n");
        sb.append("\t* @return 返回查询\n");
        sb.append("\t*/\n");
        sb.append("\tpublic QueryWrapper<" + beanName + ">  LikeAllField(" + beanName + "  " + toFirstCharLowerCase(beanName) + ", SearchVo searchVo) {\n");

        sb.append("\t\tQueryWrapper<" + beanName + "> queryWrapper = new QueryWrapper<>();\n");
        for (ColumnInfo c : columns) {
//            if(c.getColumnType().equals("varchar")){
//                sb.append("\t\tif(StringUtils.isNotBlank("+toFirstCharLowerCase(beanName)+".get" +toFirstCharUpCase(underlineToCamel( c.getColumnName()))+"())){\n");
//            }
//            else{
//                sb.append("\t\tif("+toFirstCharLowerCase(beanName)+".get" +toFirstCharUpCase(underlineToCamel( c.getColumnName()))+"() != null){\n");
//            }
//            sb.append("\t\t\tqueryWrapper.lambda().and(i -> i.like(" + beanName + "::get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + ", " + toFirstCharLowerCase(beanName) + ".get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "()));\n");
//            sb.append("\t\t}\n");
            if(c.getColumnType().equals("varchar")){
                sb.append("\t\tif(StringUtils.isNotBlank("+toFirstCharLowerCase(beanName)+".get" +toFirstCharUpCase(underlineToCamel( c.getColumnName()))+"())){\n");
            }
            else{
                sb.append("\t\tif("+toFirstCharLowerCase(beanName)+".get" +toFirstCharUpCase(underlineToCamel( c.getColumnName()))+"() != null){\n");
            }
            sb.append("\t\t\tqueryWrapper.and(i -> i.like(\"" + c.getTableName() + "."+c.getColumnName()+"\", " + toFirstCharLowerCase(beanName) + ".get" +toFirstCharUpCase(underlineToCamel( c.getColumnName()))  + "()));\n");
            sb.append("\t\t}\n");
        }
        sb.append("\t\tif(searchVo!=null){\n");
        sb.append("\t\t\tif(searchVo.getStartDate()!=null && searchVo.getEndDate()!=null){\n");
        sb.append("\t\t\t\tqueryWrapper.lambda().and(i -> i.between(" + beanName + "::getCreateTime, searchVo.getStartDate(),searchVo.getEndDate()));\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t}\n");
        sb.append("\t\tqueryWrapper.lambda().and(i -> i.eq(" + beanName + "::getDelFlag, 0));\n");

        sb.append("\t\treturn queryWrapper;\n");
        sb.append("\t\n}");
        sb.append("\n}");
        return sb.toString();
    }


    /**
     * 生成controller
     *
     * @param genConfig
     */
    private static String genController(String author, String packagePath, String beanName, List<ColumnInfo> columns, GenConfig genConfig) {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(packagePath)) {
            sb.append("package " + packagePath + ";\n\n");
        }
        String businessPackage = packagePath.substring(0, packagePath.lastIndexOf("."));
        sb.append("import java.util.Arrays;\n");
        sb.append("import java.util.Date;\n");
        sb.append("import " + businessPackage + ".service.I" + beanName + "Service;\n");
        sb.append("import javax.servlet.http.HttpServletResponse;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.GetMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.PostMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestParam;\n");
        sb.append("import org.springframework.web.bind.annotation.RestController;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import com.scmt.core.common.utils.ResultUtil;\n");
        sb.append("import com.scmt.core.common.vo.PageVo;\n");
        sb.append("import com.scmt.core.common.vo.Result;\n");
        sb.append("import com.scmt.core.common.vo.SearchVo;\n");
        sb.append("import com.scmt.core.common.utils.SecurityUtil;\n");
        sb.append("import " + businessPackage + ".entity." + beanName + ";\n");
        sb.append("import com.baomidou.mybatisplus.core.toolkit.StringUtils;\n");
        sb.append("import com.baomidou.mybatisplus.core.metadata.IPage;\n");
        sb.append("import com.scmt.core.common.enums.LogType;\n");
        sb.append("import io.swagger.annotations.Api;\n");
        sb.append("import io.swagger.annotations.ApiOperation;\n");
        sb.append("/**\n *@author " + author + "\n **/\n");
        sb.append("@RestController\n");
        sb.append("@Api(tags =\" " + genConfig.getApiAlias() + "数据接口\")\n");

        sb.append("@RequestMapping(\"/scmt/" + toFirstCharLowerCase(beanName) + "\")\n");
        sb.append("public class " + beanName + "Controller{\r\n");
        sb.append("\t@Autowired\n");
        sb.append("\tprivate I" + beanName + "Service " + toFirstCharLowerCase(beanName) + "Service;\n");

        sb.append("\t@Autowired\n");
        sb.append("\tprivate SecurityUtil securityUtil;\n");

        // 新增数据的方法
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：新增" + genConfig.getApiAlias() + "数据\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 实体\n");
        sb.append("\t* @return 返回新增结果\n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"新增" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@PostMapping(\"add" + beanName + "\")\n");
        sb.append("\tpublic Result<Object> add" + beanName + "(@RequestBody " + beanName + " " + toFirstCharLowerCase(beanName) + "){\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + ".setDelFlag(0);\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + ".setCreateId(securityUtil.getCurrUser().getId());\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + ".setCreateTime(new Date());\n");
        sb.append("\t\t\tboolean res = " + toFirstCharLowerCase(beanName) + "Service.save(" + toFirstCharLowerCase(beanName) + ");\n");
        sb.append("\t\t\tif (res) {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"保存成功\");\n");
        sb.append("\t\t\t} else {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"保存失败\");\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t\treturn ResultUtil.error(\"保存异常:\" + e.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");

        // 根据主键来更新数据
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：更新数据\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 实体\n");
        sb.append("\t* @return 返回更新结果\n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"更新" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@PostMapping(\"update" + beanName + "\")\n");
        sb.append("\tpublic Result<Object> update" + beanName + "(@RequestBody " + beanName + " " + toFirstCharLowerCase(beanName) + "){\n");
        sb.append("\t\tif (StringUtils.isBlank(" + toFirstCharLowerCase(beanName) + ".getId())) {\n");
        sb.append("\t\t\treturn ResultUtil.error(\"参数为空，请联系管理员！！\");\n");
        sb.append("\t\t}\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + ".setUpdateId(securityUtil.getCurrUser().getId());\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + ".setUpdateTime(new Date());\n");
        sb.append("\t\t\tboolean res = " + toFirstCharLowerCase(beanName) + "Service.updateById(" + toFirstCharLowerCase(beanName) + ");\n");
        sb.append("\t\t\tif (res) {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"修改成功\");\n");
        sb.append("\t\t\t} else {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"修改失败\");\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t\treturn ResultUtil.error(\"保存异常:\" + e.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");

        // 根据主键删除数据的方法
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：根据主键来删除数据\n");
        sb.append("\t* @param ids 主键集合\n");
        sb.append("\t* @return 返回删除结果\n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"根据主键来删除" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@PostMapping(\"delete" + beanName + "\")\n");
        sb.append("\tpublic Result<Object> delete" + beanName + "(@RequestParam String[] ids){\n");
        sb.append("\t\tif (ids == null || ids.length == 0) {\n");
        sb.append("\t\t\treturn ResultUtil.error(\"参数为空，请联系管理员！！\");\n");
        sb.append("\t\t}\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\tboolean res = " + toFirstCharLowerCase(beanName) + "Service.removeByIds(Arrays.asList(ids));\n");
        sb.append("\t\t\tif (res) {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"删除成功\");\n");
        sb.append("\t\t\t} else {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"删除失败\");\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t\treturn ResultUtil.error(\"删除异常:\" + e.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");

        // 根据主键来获取数据
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：根据主键来获取数据\n");
        sb.append("\t* @param id 主键\n");
        sb.append("\t* @return 返回获取结果\n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"根据主键来获取" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@GetMapping(\"get" + beanName + "\")\n");
        sb.append("\tpublic Result<Object> get" + beanName + "(@RequestParam(name = \"id\")String id){\n");
        sb.append("\t\tif (StringUtils.isBlank(id)) {\n");
        sb.append("\t\t\treturn ResultUtil.error(\"参数为空，请联系管理员！！\");\n");
        sb.append("\t\t}\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\t" + beanName + " res = " + toFirstCharLowerCase(beanName) + "Service.getById(id);\n");
        sb.append("\t\t\tif (res != null) {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"查询成功\");\n");
        sb.append("\t\t\t} else {\n");
        sb.append("\t\t\t\treturn ResultUtil.data(res, \"查询失败\");\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t\treturn ResultUtil.error(\"查询异常:\" + e.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        // 分页查询
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：实现分页查询\n");
        sb.append("\t* @param searchVo 需要模糊查询的信息\n");
        sb.append("\t* @param pageVo 分页参数\n");
        sb.append("\t* @return 返回获取结果\n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"分页查询" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@GetMapping(\"query" + beanName + "List\")\n");
        sb.append("\tpublic Result<Object> query" + beanName + "List(" + toFirstCharUpCase(beanName) + "  " + toFirstCharLowerCase(beanName) + ", SearchVo searchVo, PageVo pageVo){\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\t IPage<" + beanName + "> result =" + toFirstCharLowerCase(beanName) + "Service.query" + toFirstCharUpCase(beanName) + "ListByPage(" + toFirstCharLowerCase(beanName) + ", searchVo, pageVo);\n");
        sb.append("\t\t\t return ResultUtil.data(result);\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t\t return ResultUtil.error(\"查询异常:\" + e.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}");


        // 导出数据
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* 功能描述：导出数据\n");
        sb.append("\t* @param response 请求参数\n");
        sb.append("\t* @param " + toFirstCharLowerCase(beanName) + " 查询参数\n");
        sb.append("\t* @return \n");
        sb.append("\t*/\n");
        sb.append("\t@ApiOperation(\"导出" + genConfig.getApiAlias() + "数据\")\n");
        sb.append("\t@PostMapping(\"/download\")\n");
        sb.append("\tpublic void download(HttpServletResponse response," + beanName + "  " + toFirstCharLowerCase(beanName) + "){\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\t" + toFirstCharLowerCase(beanName) + "Service.download( " + toFirstCharLowerCase(beanName) + ",response);\n");
        sb.append("\t\t} catch (Exception e) {\n");
        sb.append("\t\t\t e.printStackTrace();\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 将首字母变大写
     *
     * @param str 字符串
     * @return 返回大写字符串
     */
    private static String toFirstCharUpCase(String str) {
        char[] columnCharArr = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columnCharArr.length; i++) {
            char cur = columnCharArr[i];
            if (i == 0) {
                sb.append(Character.toUpperCase(cur));
            } else {
                sb.append(cur);
            }
        }
        return sb.toString();
    }

    /**
     * 将首字母变小写
     *
     * @param str 字符串
     * @return 返回小写字符串
     */
    public static String toFirstCharLowerCase(String str) {
        char[] columnCharArr = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < columnCharArr.length; i++) {
            char cur = columnCharArr[i];
            if (i == 0) {
                sb.append(Character.toLowerCase(cur));
            } else {
                sb.append(cur);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param param
     * @param charType
     * @return
     */
    public static String camelToUnderline(String param, Integer charType) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
            }
            if (charType == 2) {
                sb.append(Character.toUpperCase(c));  //统一都转大写
            } else {
                sb.append(Character.toLowerCase(c));  //统一都转小写
            }


        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        Boolean flag = false; // "_" 后转大写标志,默认字符前面没有"_"
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                flag = true;
                continue;   //标志设置为true,跳过
            } else {
                if (flag == true) {
                    //表示当前字符前面是"_" ,当前字符转大写
                    sb.append(Character.toUpperCase(param.charAt(i)));
                    flag = false;  //重置标识
                } else {
                    sb.append(Character.toLowerCase(param.charAt(i)));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取查询实体类的字段类型
     *
     * @param javaType java类型
     * @return 返回对应的值
     */
    private static String getQueryModelFieldType(String javaType) {
        switch (javaType) {
            case "byte":
                return "Byte";
            case "short":
                return "Short";
            case "int":
                return "Integer";
            case "float":
                return "Float";
            case "double":
                return "Double";
            case "long":
                return "Long";
            case "datetime":
            case "date":
                return "Date";
            case "timestamp":
                return "LocalDateTime";
        }
        return "String";
    }

    /**
     * @param table 表格参数
     * @return 返回查询的字符串
     */
    public static List<ColumnModel> getQueryFields(TableModel table) {
        if (table.getPrimaryKeyColumns().size() == 1 && table.getPrimaryKeyColumns().get(0).isAutoIncrement()) {
            List<ColumnModel> columns = new ArrayList<ColumnModel>();
            for (ColumnModel cm : table.getColumns()) {
                if (!cm.isPrimaryKey()) {
                    columns.add(cm);
                }
            }
            return columns;
        }
        return table.getColumns();
    }

    /**
     * 创建文件夹，防止文件路径不存在
     */
    private static String createFloder(String src, String packagePath) throws IOException {
        File file = new File("");
        String path = file.getCanonicalPath();
        File pf = new File(path);
        pf = new File(pf.getAbsolutePath() + "/" + src);
        String[] subF = packagePath.split("/");
        for (String sf : subF) {
            pf = new File(pf.getPath() + "/" + sf);
            if (!pf.exists()) {
                pf.mkdirs();
            }
        }
        return pf.getAbsolutePath();
    }

    /**
     * 创建文件夹，防止文件路径不存在
     */
    private static String createFloder(String basePath) throws IOException {
        File file = new File("");
        String path = file.getCanonicalPath();
        File pf = new File(path);
        String[] subF = basePath.split("/");
        for (String sf : subF) {
            if (StringUtils.isNotEmpty(sf)) {
                pf = new File(pf.getPath() + "/" + sf);
                if (!pf.exists()) {
                    pf.mkdirs();
                }
            }
        }
        return pf.getAbsolutePath();
    }

    /**
     * 功能描述：创建前端的api
     *
     * @param webPath  前端工程路径
     * @param beanName 实体名称
     */
    private static void genApi(String webPath, String beanName) throws IOException {
        // api文件目录不存在则创建api文件目录
        String apiPath = webPath + "/src/api/" + toFirstCharLowerCase(beanName);
        File file = new File(apiPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //生成api相关的文件
        File fEntity = new File(apiPath + "/" + toFirstCharLowerCase(beanName) + ".js");
        if (fEntity.exists()) {
            fEntity.delete();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("import { getRequest, postRequest, putRequest, postBodyRequest, getNoAuthRequest, postNoAuthRequest } from '@/libs/axios';\n");
        // 添加API
        sb.append("\texport const add" + beanName + " = params => {\n");
        sb.append("\t\treturn postBodyRequest('/" + toFirstCharLowerCase(beanName) + "/add" + beanName + "',params);\n");
        sb.append("\t};\n");
        // 删除API
        sb.append("\texport const delete" + beanName + " = params => {\n");
        sb.append("\t\treturn postRequest('/" + toFirstCharLowerCase(beanName) + "/delete" + beanName + "',params);\n");
        sb.append("\t};\n");
        // 更新API
        sb.append("\texport const update" + beanName + " = params => {\n");
        sb.append("\t\treturn postBodyRequest('/" + toFirstCharLowerCase(beanName) + "/update" + beanName + "',params);\n");
        sb.append("\t};\n");
        // 获取API列表
        sb.append("\texport const query" + beanName + "List = params => {\n");
        sb.append("\t\treturn getRequest('/" + toFirstCharLowerCase(beanName) + "/query" + beanName + "List',params);\n");
        sb.append("\t};\n");
        // 获取API
        sb.append("\texport const get" + beanName + " = params => {\n");
        sb.append("\t\treturn getRequest('/" + toFirstCharLowerCase(beanName) + "/get" + beanName + "',params);\n");
        sb.append("\t};\n");
        FileOutputStream fos = new FileOutputStream(fEntity);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    /**
     * 功能描述：生成路由信息
     *
     * @param webPath    工程路径
     * @param beanName   实体类名称
     * @param routerNode 当前创建的节点需要挂载到那个节点底下
     */
    private static String genRouter(String webPath, String beanName, String routerNode) throws IOException {
        String parentTreeCode = "";
        boolean isAdd = false;
        boolean hasRouter = false;
        boolean hasParentCode = false;
        StringBuilder sb = new StringBuilder("");
        String routerPath = webPath + "/src/router/router.js";
        //File fEntity = new File(routerPath);
        //FileInputStream fis = new FileInputStream(routerPath);
        //InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        //BufferedReader br = new BufferedReader(isr);
        String line;
        List<String> routerStr = new ArrayList<>();
//        while ((line = br.readLine()) != null) {
//            routerStr.add(line);
//            // 表示在我们的路由中已经存在该配置信息了，我们就不再新增该配置信息
//            if (line.replaceAll(" ", "").equals("name:'" + toFirstCharLowerCase(beanName) + "List',")) {
//                hasRouter = true;
//            }
//
//            if (line.replaceAll(" ", "").equals("name:'" + routerNode + "',")) {
//                hasParentCode = true;
//            }
//            if (hasParentCode) {
//                System.out.println("====>"+line.replaceAll(" ", ""));
//                if (line.replaceAll(" ", "").indexOf("code:") != -1) {
//                    parentTreeCode = line.replaceAll(" ", "").replace("code:", "").replaceAll("'", "").replace(",", "");
//                    hasParentCode = false;
//                }
//            }
//        }
//        for (String s : routerStr) {
//            if (hasRouter) {
//                sb.append(s + "\n");
//            } else {
//                // 表示已经定位到了我们需要增加路由节点的父节点的位置了
//                if (s.replaceAll(" ", "").equals("name:'" + routerNode + "',")) {
//                    isAdd = true;
//                }
//                // 进入此处做子节点增加逻辑的处理
//                if (isAdd) {
//                    // 表示已经到了我们需要加入节点的位置了
//                    if (s.replaceAll(" ", "").equals("children:[")) {
//                        sb.append(s);
//                        sb.append("\t{\n");
//                        sb.append("\t\tpath: '" + toFirstCharLowerCase(beanName) + "List',\n");
//                        sb.append("\t\tname: '" + toFirstCharLowerCase(beanName) + "List',\n");
//                        sb.append("\t\tmeta: {\n");
//                        sb.append("\t\t\ticon: 'ios-paper',\n");
//                        sb.append("\t\t\ttitle: '" + beanName + "',\n");
//                        sb.append("\t\t\tcode:'system-manage-" + toFirstCharLowerCase(beanName) + "',\n");
//                        sb.append("\t\t\trequireAuth: true\n");
//                        sb.append("\t\t},\n");
//                        sb.append("\t\tcomponent: resolve => {\n");
//                        sb.append("\t\t\trequire(['../view/sys/" + toFirstCharLowerCase(beanName) + "/" + toFirstCharLowerCase(beanName) + "List.vue'], resolve);\n");
//                        sb.append("\t\t}\n");
//                        sb.append("\t},\n");
//                        isAdd = false;
//                    } else {
//                        sb.append(s + "\n");
//                    }
//                } else {
//                    sb.append(s + "\n");
//                }
//            }
//        }
        // br.close();
        //isr.close();
        //fis.close();
//        if (fEntity.exists()) {
//            fEntity.delete();
//        }
//        FileOutputStream fos = new FileOutputStream(fEntity);
//        fos.write(sb.toString().getBytes());
//        fos.close();
        return parentTreeCode;
    }

    /**
     * 功能描述：生成updateVue的相关文件
     *
     * @param webPath  工程路径
     * @param beanName 实体类的名称
     * @param columns  表信息
     */
    private static void genUpdateVue(String webPath, String beanName, List<ColumnInfo> columns, GenConfig genConfig) throws IOException {
        // vue文件目录不存在则创建api文件目录
        String vuePath = webPath + "/src/view/" + toFirstCharLowerCase(beanName);
        File file = new File(vuePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //生成vue列表的文件
        File fEntity = new File(vuePath + "/update" + beanName + "" + ".vue");
        if (fEntity.exists()) {
            fEntity.delete();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<style lang=\"less\">\n");
        sb.append("\t." + toFirstCharLowerCase(beanName) + "{\n");
        sb.append("\t\t.ivu-modal {\n");
        sb.append("\t\t\twidth: 660px !important;\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("</style>\n");

        sb.append("<template>\n");
        sb.append("\t<Modal v-model=\"show\" :title=\"editTitle\" class=\"" + toFirstCharLowerCase(beanName) + "\"  :mask-closable=\"false\">\n");
        sb.append("\t\t<Form ref=\"" + toFirstCharLowerCase(beanName) + "Form\" :model=\"" + toFirstCharLowerCase(beanName) + "Form\" :rules=\"" + toFirstCharLowerCase(beanName) + "FormRule\" :label-width=\"100\">\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow() ) {
                if(StringUtils.isNotBlank(c.getFormType())){
                    if (c.getFormType().equals("Input") ) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t<Input v-bind:disabled=\"disabled\"  type=\"text\" :maxlength=50 v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请输入" + c.getRemark() + "\"/>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else if (c.getFormType().equals("Textarea")) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t<Input v-bind:disabled=\"disabled\"  :rows=\"3\" type=\"textarea\"  v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请输入" + c.getRemark() + "\"/>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else if (c.getFormType().equals("Date")) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "Time\">\n");
                        sb.append("\t\t\t\t\t\t<DatePicker v-bind:disabled=\"disabled\"  style=\"width:100%;\" type=\"datetime\" placeholder=\"请输入" + c.getRemark() + "\"  v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "Time \" @on-change=\"set" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "\" format=\"yyyy-MM-dd HH:mm:ss\" ></DatePicker>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else if (c.getFormType().equals("Radio")) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t<RadioGroup v-bind:disabled=\"disabled\"  v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t <Radio v-for=\"item in " + underlineToCamel(c.getColumnName()) + "Radio\" :label=\"item.value\">{{item.title}}</Radio>\n");
                        sb.append("\t\t\t\t\t\t</RadioGroup>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else if (c.getFormType().equals("Select")) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t<Select  v-bind:disabled=\"disabled\"  v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请选择\" clearable >\n");
                        sb.append("\t\t\t\t\t\t\t<Option v-for=\"(item, i) in " + underlineToCamel(c.getColumnName()) + "Priority\" :key=\"item.value\" :value=\"item.value\">{{item.title}}</Option>\n");
                        sb.append("\t\t\t\t\t\t</Select>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else if (c.getColumnType().equals("int") || c.getColumnType().equals("bigint") || c.getColumnType().equals("bigint")) {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + underlineToCamel(c.getColumnName()) + "\">\n");
                        sb.append("\t\t\t\t\t\t<InputNumber v-bind:disabled=\"disabled\"  style=\"width:100%;\"  :min=\"1\" v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\"></InputNumber>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    } else {
                        sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + c.getColumnName() + "\">\n");
                        sb.append("\t\t\t\t\t\t<Input v-bind:disabled=\"disabled\"  type=\"text\" :maxlength=50 v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请输入" + c.getRemark() + "\"/>\n");
                        sb.append("\t\t\t\t\t</FormItem>\n");
                    }
                }
                //默认input
                else {
                    sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\" prop=\"" + c.getColumnName() + "\">\n");
                    sb.append("\t\t\t\t\t\t<Input v-bind:disabled=\"disabled\"  type=\"text\" :maxlength=50 v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请输入" + c.getRemark() + "\"/>\n");
                    sb.append("\t\t\t\t\t</FormItem>\n");
                }
            }
        }
        sb.append("\t\t</Form>\n");
        sb.append("\t\t<div slot=\"footer\">\n");
        sb.append("\t\t\t<Button type=\"text\" @click=\"show=false\">取消</Button>\n");
        sb.append("\t\t\t<Button type=\"primary\" :loading=\"loading\" @click=\"handSubmit\" v-if=\"!disabled\">提交</Button>\n");
        sb.append("\t\t</div>\n");
        sb.append("\t</Modal>\n");
        sb.append("</template>\n");
        sb.append("<script>\n");
        sb.append("\timport {add" + beanName + ",update" + beanName + ",get" + beanName + "} from '@/api/" + toFirstCharLowerCase(beanName) + "/" + toFirstCharLowerCase(beanName) + "'\n");
        sb.append("\timport { getDictDataByType } from '@/api/index';\n");
        sb.append("\texport default {\n");
        sb.append("\t\tname: \"update" + beanName + "\",\n");
        sb.append("\t\tprops: {\n");
        sb.append("\t\t\tvalue: {\n");
        sb.append("\t\t\t\ttype: Boolean,\n");
        sb.append("\t\t\t\tdefault: false\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t" + beanName + "Id: {\n");
        sb.append("\t\t\t\ttype: String\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tmodalTitle: {\n");
        sb.append("\t\t\t\ttype: String\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\tdata() {\n");
        sb.append("\t\t\treturn {\n");
        sb.append("\t\t\t\tshow: this.value,\n");
        sb.append("\t\t\t\teditTitle: this.modalTitle,\n");
        sb.append("\t\t\t\tloading: true,\n");
        sb.append("\t\t\t\tdisabled:false,\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if ("Radio".equals(c.getFormType())) {
                    sb.append("\t\t\t\t" + underlineToCamel(c.getColumnName()) + "Radio :[],\n");
                }
                if ("Select".equals(c.getFormType())) {
                    sb.append("\t\t\t\t" + underlineToCamel(c.getColumnName()) + "Priority :[],\n");
                }
            }
        }
        sb.append("\t\t\t\t" + toFirstCharLowerCase(beanName) + "Form: {\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if ("Date".equals(c.getFormType())) {
                    sb.append("\t\t\t\t\t").append(underlineToCamel(c.getColumnName()) + "Time").append(":'',\n");

                }
                sb.append("\t\t\t\t\t").append(underlineToCamel(c.getColumnName())).append(":'',\n");
            }
        }
        sb.append("\t\t\t\t},\n");
        sb.append("\t\t\t\t" + toFirstCharLowerCase(beanName) + "FormRule: this.get" + beanName + "FormRule()\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\tmethods: {\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if ("Radio".equals(c.getFormType()) && StringUtils.isNotBlank(c.getDictName()) && !c.getDictName().trim().equals("null")) {
                    sb.append("\t\t\tget" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "Radio(){\n");
                    sb.append("\t\t\t\tgetDictDataByType('" + c.getDictName() + "').then(res => {\n");
                    sb.append("\t\t\t\t\tif (res.success) {\n");
                    sb.append("\t\t\t\t\t\t this." + underlineToCamel(c.getColumnName()) + "Radio = res.result;\n");
                    sb.append("\t\t\t\t\t}\n");
                    sb.append("\t\t\t\t});\n");
                    sb.append("\t\t\t},\n");

                }
                if ("Select".equals(c.getFormType()) && StringUtils.isNotBlank(c.getDictName()) && !c.getDictName().trim().equals("null")) {
                    sb.append("\t\t\tget" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "Priority(){\n");
                    sb.append("\t\t\t\tgetDictDataByType('" + c.getDictName() + "').then(res => {\n");
                    sb.append("\t\t\t\t\tif (res.success) {\n");
                    sb.append("\t\t\t\t\t\t this." + underlineToCamel(c.getColumnName()) + "Priority = res.result;\n");
                    sb.append("\t\t\t\t\t}\n");
                    sb.append("\t\t\t\t});\n");
                    sb.append("\t\t\t},\n");

                }
                if ("Date".equals(c.getFormType())) {
                    sb.append("\t\t\tset" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "(e){\n");
                    sb.append("\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + " = e;\n");
                    sb.append("\t\t\t},\n");
                }
            }
        }
        sb.append("\t\t\thandSubmit() {\n");
        sb.append("\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].validate((valid) => {\n");
        sb.append("\t\t\t\t\tthis.loading = true;\n");
        sb.append("\t\t\t\t\tif (valid) {\n");
        sb.append("\t\t\t\t\t\tif(this." + beanName + "Id!=null&&this." + beanName + "Id.trim().length>0){\n");
        sb.append("\t\t\t\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form.id=this." + beanName + "Id;\n");
        sb.append("\t\t\t\t\t\t\tupdate" + beanName + "(this." + toFirstCharLowerCase(beanName) + "Form).then(res => {\n");
        sb.append("\t\t\t\t\t\t\t\tif (res.success) {\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.closeModal(false);\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$emit('handSearch');\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$Message.success('保存成功');\n");
        sb.append("\t\t\t\t\t\t\t\t}else{\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$Message.error(res.msg);\n");
        sb.append("\t\t\t\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\t\t\t}).finally(() => {\n");
        sb.append("\t\t\t\t\t\t\t\tthis.loading = false;\n");
        sb.append("\t\t\t\t\t\t\t});\n");
        sb.append("\t\t\t\t\t\t}else{\n");
        sb.append("\t\t\t\t\t\t\tadd" + beanName + "(this." + toFirstCharLowerCase(beanName) + "Form).then(res => {\n");
        sb.append("\t\t\t\t\t\t\t\tif (res.success) {\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.closeModal(false);\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$emit('handSearch');\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$Message.success('保存成功');\n");
        sb.append("\t\t\t\t\t\t\t\t}else{\n");
        sb.append("\t\t\t\t\t\t\t\t\tthis.$Message.error(res.msg);\n");
        sb.append("\t\t\t\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\t\t\t}).finally(() => {\n");
        sb.append("\t\t\t\t\t\t\t\tthis.loading = false;\n");
        sb.append("\t\t\t\t\t\t\t});\n");
        sb.append("\t\t\t\t\t\t}\n");

        sb.append("\t\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\t\t\tthis.loading = false;\n");
        sb.append("\t\t\t\t\t\tthis.$Message.error('表单验证不通过！');\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tcloseModal(val) {\n");
        sb.append("\t\t\t\tthis.$emit('input', val);\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\tinitForm(){\n");
        sb.append("\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form= {\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if ("Date".equals(c.getFormType())) {
                    sb.append("\t\t\t\t\t").append(underlineToCamel(c.getColumnName()) + "Time").append(":'',\n");
                }
                sb.append("\t\t\t\t\t").append(underlineToCamel(c.getColumnName())).append(":'',\n");
            }
        }
        sb.append("\t\t\t\t};\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\tget" + beanName + "FormRule() {\n");
        sb.append("\t\t\t\treturn {\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow()) {
                if ( "Input".equals(c.getFormType())) {
                    sb.append("\t\t\t\t\t" + underlineToCamel(c.getColumnName()) + ": [\n");
                    sb.append("\t\t\t\t\t\t{required: true, message: '" + c.getRemark() + "不能为空！', trigger: 'blur'},\n");
                    sb.append("\t\t\t\t\t\t{type: 'string', max: " + 50 + ", message: '数据的最大长度为" + 50 + "！', trigger: 'blur'}\n");
                    sb.append("\t\t\t\t\t],\n");
                } else if ("Date".equals(c.getFormType())) {
                    sb.append("\t\t\t\t\t" + underlineToCamel(c.getColumnName()) + "Time: [\n");
                    sb.append("\t\t\t\t\t\t{required: true, message: '" + c.getRemark() + "不能为空！', trigger: 'blur',pattern: /.+/ },\n");
                    sb.append("\t\t\t\t\t],\n");
                } else if ( "int".equals(c.getFormType()) ||"bigint".equals(c.getFormType())) {
                    sb.append("\t\t\t\t\t" + underlineToCamel(c.getColumnName()) + ": [\n");
                    sb.append("\t\t\t\t\t\t{required: true,pattern:/^[0-9]+$/, message: '" + c.getRemark() + "不能为空！', trigger: 'blur' },\n");
                    sb.append("\t\t\t\t\t],\n");
                } else {
                    sb.append("\t\t\t\t\t" + underlineToCamel(c.getColumnName()) + ": [\n");
                    sb.append("\t\t\t\t\t\t{required: true, message: '" + c.getRemark() + "不能为空！', trigger: 'blur',pattern: /.+/ },\n");
                    sb.append("\t\t\t\t\t],\n");
                }
            }
        }
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\twatch: {\n");
        sb.append("\t\t\tvalue(val) {\n");
        sb.append("\t\t\t\tthis.show = val;\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tshow(val) {\n");
        for (ColumnInfo c : columns) {
            if (c.getFormShow() && StringUtils.isNotBlank(c.getDictName()) && !c.getDictName().trim().equals("null")) {
                if (c.getFormType().equals("Radio")) {
                    sb.append("\t\t\t\tthis.get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "Radio(),\n");
                }
                if (c.getFormType().equals("Select")) {
                    sb.append("\t\t\t\tthis.get" + toFirstCharUpCase(underlineToCamel(c.getColumnName())) + "Priority(),\n");
                }
            }
        }

        sb.append("\t\t\t\tthis.initForm();\n");
        sb.append("\t\t\t\tthis.loading=false;\n");
        sb.append("\t\t\t\tthis.editTitle = this.modalTitle;\n");
        sb.append("\t\t\t\tif(this.editTitle == \"查看\"){\n");
        sb.append("\t\t\t\t\tthis.disabled = true;\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t\telse{\n");
        sb.append("\t\t\t\t\tthis.disabled = false;\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t\tif(val) {\n");
        sb.append("\t\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].resetFields();\n");
        sb.append("\t\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].id = null;\n");
        sb.append("\t\t\t\t\tif(this." + beanName + "Id!=null&&this." + beanName + "Id.trim().length>0){\n");
        sb.append("\t\t\t\t\t\tget" + beanName + "({id: this." + beanName + "Id}).then(res => {\n");
        sb.append("\t\t\t\t\t\t\t\tif (res.success) {\n");

        sb.append("\t\t\t\t\t\t\t\t\t this." + toFirstCharLowerCase(beanName) + "Form = res.data;\n");
        //for (ColumnInfo c : columns) {
        //    if (c.getFormShow()) {
        //        if (c.getFormType().equals("Date")) {
        //            String dateVal = "res.result." + underlineToCamel(c.getColumnName());
        //            sb.append("\t\t\t\t\t\t\t\tif(" + dateVal + "!=''){\n");
        //            sb.append("\t\t\t\t\t\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + "Time = formartDate(new Date(" + dateVal + ").getTime(), 'yyyy-MM-dd hh:mm:ss');\n");
        //            sb.append("\t\t\t\t\t\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + " = formartDate(new Date(" + dateVal + ").getTime(), 'yyyy-MM-dd hh:mm:ss');\n");
        //
        //            sb.append("\t\t\t\t\t\t\t\t}\n");
        //        } else {
        //            String dateVal = "res.data." + underlineToCamel(c.getColumnName());
        //
        //            sb.append("\t\t\t\t\t\t\t\tthis." + toFirstCharLowerCase(beanName) + "Form." + underlineToCamel(c.getColumnName()) + " = " + dateVal + ";\n");
        //        }
        //    }
        //}
        sb.append("\t\t\t\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\t\t\t\tthis.$Message.error(res.msg);\n");
        sb.append("\t\t\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\t\t});\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\tthis.closeModal(val);\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("</script>\n");
        FileOutputStream fos = new FileOutputStream(fEntity);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    /**
     * 功能描述：生成addVue的相关文件
     *
     * @param webPath  工程路径
     * @param beanName 实体类的名称
     * @param table    表信息
     */
    private static void genAddVue(String webPath, String beanName, TableModel table) throws IOException {
        // vue文件目录不存在则创建api文件目录
        String vuePath = webPath + "/src/view/" + toFirstCharLowerCase(beanName);
        File file = new File(vuePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //生成vue列表的文件
        File fEntity = new File(vuePath + "/add" + beanName + "" + ".vue");
        if (fEntity.exists()) {
            fEntity.delete();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<template>\n");
        sb.append("\t<Modal v-model=\"show\" title=\"新增\" @on-ok=\"ok\" :loading=\"loading\" :mask-closable=\"false\">\n");
        sb.append("\t\t<Form ref=\"" + toFirstCharLowerCase(beanName) + "Form\" :model=\"" + toFirstCharLowerCase(beanName) + "Form\" :rules=\"" + toFirstCharLowerCase(beanName) + "FormRule\">\n");
        for (ColumnModel c : table.getColumns()) {
            if (!c.isPrimaryKey()) {
                if (c.getFieldType().equals("String")) {
                    sb.append("\t\t\t<FormItem label=\"" + c.getRemarks() + "\" prop=\"" + c.getFieldName() + "\">\n");
                    sb.append("\t\t\t\t<Input type=\"text\" :maxlength=50 v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + c.getFieldName() + "\" placeholder=\"请输入" + c.getRemarks() + "\"/>\n");
                    sb.append("\t\t\t</FormItem>\n");
                }
                if (c.getFieldType().equals("Date")) {
                    sb.append("\t\t\t<FormItem label=\"" + c.getRemarks() + "\" prop=\"" + c.getFieldName() + "\">\n");
                    sb.append("\t\t\t\t<DatePicker style=\"width:100%;\" type=\"datetime\" placeholder=\"请输入" + c.getRemarks() + "\"  v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + c.getFieldName() + "\" ></DatePicker>\n");
                    sb.append("\t\t\t</FormItem>\n");
                }
                if (c.getFieldType().equals("Integer") || c.getFieldType().equals("Long")) {
                    sb.append("\t\t\t<FormItem label=\"" + c.getRemarks() + "\" prop=\"" + c.getFieldName() + "\">\n");
                    sb.append("\t\t\t\t<InputNumber style=\"width:100%;\"  :min=\"1\" v-model=\"" + toFirstCharLowerCase(beanName) + "Form." + c.getFieldName() + "\"></InputNumber>\n");
                    sb.append("\t\t\t</FormItem>\n");
                }
            }
        }
        sb.append("\t\t</Form>\n");
        sb.append("\t</Modal>\n");
        sb.append("</template>\n");
        sb.append("<script>\n");
        sb.append("\timport {add" + beanName + "} from '@/api/" + toFirstCharLowerCase(beanName) + "/" + toFirstCharLowerCase(beanName) + ";\n");
        sb.append("\texport default {\n");
        sb.append("\t\tname: \"add" + beanName + "\",\n");
        sb.append("\t\tprops: {\n");
        sb.append("\t\t\tvalue: {\n");
        sb.append("\t\t\t\ttype: Boolean,\n");
        sb.append("\t\t\t\tdefault: false\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\tdata() {\n");
        sb.append("\t\t\treturn {\n");
        sb.append("\t\t\t\tshow: this.value,\n");
        sb.append("\t\t\t\tloading: true,\n");
        sb.append("\t\t\t\t" + toFirstCharLowerCase(beanName) + "Form: {\n");
        for (ColumnModel c : table.getColumns()) {
            if (c.getFieldType().equals("String")) {
                sb.append("\t\t\t\t\t").append(c.getFieldName()).append(":'',\n");
            }
            if (c.getFieldType().equals("Date")) {
                sb.append("\t\t\t\t\t").append(c.getFieldName()).append(":'',\n");
            }
            if (c.getFieldType().equals("Integer") || c.getFieldType().equals("Long")) {
                sb.append("\t\t\t\t\t").append(c.getFieldName()).append(":1,\n");
            }
        }
        sb.append("\t\t\t\t},\n");
        sb.append("\t\t\t\t" + toFirstCharLowerCase(beanName) + "FormRule: this.get" + beanName + "FormRule()\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\tmethods: {\n");
        sb.append("\t\t\tok() {\n");
        sb.append("\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].validate((valid) => {\n");
        for (ColumnModel c : table.getColumns()) {
            if (c.getFieldType().equals("Date")) {
                String dateVal = "this." + toFirstCharLowerCase(beanName) + "Form." + c.getFieldName();
                sb.append("\t\t\t\t\t\tif(" + dateVal + "!=''){\n");
                sb.append("\t\t\t\t\t\t\t" + dateVal + "=this.formatDate(new Date(" + dateVal + "), 'yyyy-MM-dd hh:mm:ss')\n");
                sb.append("\t\t\t\t\t\t}\n");
            }
        }
        sb.append("\t\t\t\t\tif (valid) {\n");
        sb.append("\t\t\t\t\t\tadd" + beanName + "(this." + toFirstCharLowerCase(beanName) + "Form).then(res => {\n");
        sb.append("\t\t\t\t\t\t\tif (res.success) {\n");
        sb.append("\t\t\t\t\t\t\t\tthis.closeModal(false);\n");
        sb.append("\t\t\t\t\t\t\t\tthis.$emit('handSearch');\n");
        sb.append("\t\t\t\t\t\t\t\tthis.$Message.success(res.msg);\n");
        sb.append("\t\t\t\t\t\t\t}else{\n");
        sb.append("\t\t\t\t\t\t\t\tthis.$Message.error(res.msg);\n");
        sb.append("\t\t\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\t\t})\n");
        sb.append("\t\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\t\tthis.$Message.error('表单验证不通过！');\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\tsetTimeout(() => {\n");
        sb.append("\t\t\t\t\t\tthis.loading = false;\n");
        sb.append("\t\t\t\t\t\tthis.$nextTick(() => {\n");
        sb.append("\t\t\t\t\t\t\tthis.loading = true;\n");
        sb.append("\t\t\t\t\t\t});\n");
        sb.append("\t\t\t\t\t}, 1000);\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tcloseModal(val) {\n");
        sb.append("\t\t\t\tthis.$emit('input', val);\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tget" + beanName + "FormRule() {\n");
        sb.append("\t\t\t\treturn {\n");
        for (ColumnModel c : table.getColumns()) {
            if (c.getFieldType().equals("String")) {
                sb.append("\t\t\t\t\t" + c.getFieldName() + ": [\n");
                sb.append("\t\t\t\t\t\t{required: true, message: '" + c.getRemarks() + "不能为空！', trigger: 'blur'},\n");
                sb.append("\t\t\t\t\t\t{type: 'string', max: " + c.getColumnSize() + ", message: '数据的最大长度为" + c.getColumnSize() + "！', trigger: 'blur'}\n");
                sb.append("\t\t\t\t\t],\n");
            }
            if (c.getFieldType().equals("Date")) {
                sb.append("\t\t\t\t\t" + c.getFieldName() + ": [\n");
                sb.append("\t\t\t\t\t\t{required: true, message: '" + c.getRemarks() + "不能为空！', trigger: 'blur',pattern: /.+/ },\n");
                sb.append("\t\t\t\t\t],\n");
            }
            if (c.getFieldType().equals("Integer") || c.getFieldType().equals("Long")) {
                sb.append("\t\t\t\t\t" + c.getFieldName() + ": [\n");
                sb.append("\t\t\t\t\t\t{required: true,pattern:/^[0-9]+$/, message: '" + c.getRemarks() + "不能为空！', trigger: 'blur' },\n");
                sb.append("\t\t\t\t\t],\n");
            }
        }
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\twatch: {\n");
        sb.append("\t\t\tvalue(val) {\n");
        sb.append("\t\t\t\tthis.show = val;\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\tshow(val) {\n");
        sb.append("\t\t\t\tif (val) {\n");
        sb.append("\t\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].resetFields();\n");
        sb.append("\t\t\t\t\tthis.$refs['" + toFirstCharLowerCase(beanName) + "Form'].id = null;\n");
        sb.append("\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\tthis.closeModal(val);\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("</script>\n");
        FileOutputStream fos = new FileOutputStream(fEntity);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    /**
     * 功能描述：生成listVue的相关文件
     *
     * @param webPath  工程路径
     * @param beanName 实体类的名称
     * @param columns  表信息
     */
    private static void genListVue(String webPath, String beanName, List<ColumnInfo> columns, GenConfig genConfig) throws IOException {
        // vue文件目录不存在则创建api文件目录

        Map<String, Object> genMap = GenUtil.getGenMap(columns, genConfig);
        if (genMap == null) {
            return;
        }
        String className = genMap.get("className").toString();
        String vuePath = webPath + "/src/view/" + toFirstCharLowerCase(beanName);
        File file = new File(vuePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //生成vue列表的文件
        File fEntity = new File(vuePath + "/" + toFirstCharLowerCase(beanName) + "List" + ".vue");
        if (fEntity.exists()) {
            fEntity.delete();
        }
        StringBuilder sb = new StringBuilder();
        // html部分开始

        sb.append("<style lang=\"less\">\n");
        sb.append("." + toFirstCharLowerCase(beanName) + "List{\n");

        sb.append("}\n");
        sb.append("</style>\n");

        sb.append("<template>\n");
        sb.append("\t<div class=\"" + toFirstCharLowerCase(beanName) + "List\">\n");

        sb.append("\t\t<Card>\n");
        sb.append("\t\t\t<Row v-show=\"openSearch\">\n");
        sb.append("\t\t\t\t<Form ref=\"searchForm\"  :model=\"searchForm\" inline :label-width=\"70\" @keydown.enter.native=\"handSearch\">\n");
        for (ColumnInfo c : columns) {
            if (StringUtils.isNotBlank(c.getQueryType())) {
                sb.append("\t\t\t\t\t<FormItem label=\"" + c.getRemark() + "\">\n");
                sb.append("\t\t\t\t\t\t<Input type=\"text\" v-model=\"searchForm." + underlineToCamel(c.getColumnName()) + "\" placeholder=\"请输入\" clearable style=\"width: 200px\" />\n");
                sb.append("\t\t\t\t\t</FormItem>\n");
            }
        }
        sb.append("\t\t\t\t\t<Form-item style=\"margin-left:-35px\" class=\"br\">\n");
        sb.append("\t\t\t\t\t\t<Button @click=\"handSearch\" type=\"primary\" icon=\"ios-search\">搜索</Button>\n");
        sb.append("\t\t\t\t\t\t<Button @click=\"handReset\">重置</Button>\n");
        sb.append("\t\t\t\t\t\t<a class=\"drop-down\" @click=\"dropDown\">\n");
        sb.append("\t\t\t\t\t\t\t{{dropDownContent}}\n");
        sb.append("\t\t\t\t\t\t\t<Icon :type=\"dropDownIcon\"></Icon>\n");
        sb.append("\t\t\t\t\t\t</a>\n");
        sb.append("\t\t\t\t\t</Form-item>\n");
        sb.append("\t\t\t\t</Form>\n");
        sb.append("\t\t\t</Row>\n");
        sb.append("\t\t\t<div class=\"operation\">\n");
        sb.append("\t\t\t\t<Button @click=\"add" + beanName + "\" type=\"primary\" icon=\"md-add\">新增</Button>\n");
        sb.append("\t\t\t\t<Dropdown @on-click=\"handDropdown\">\n");
        sb.append("\t\t\t\t\t<Button>\n");
        sb.append("\t\t\t\t\t\t更多操作\n");
        sb.append("\t\t\t\t\t\t<Icon type=\"md-arrow-dropdown\"/>\n");
        sb.append("\t\t\t\t\t</Button>\n");
        sb.append("\t\t\t\t\t<DropdownMenu slot=\"list\">\n");
        sb.append("\t\t\t\t\t\t<DropdownItem name=\"refresh\"><Icon type=\"md-sync\" />刷新</DropdownItem>\n");
        sb.append("\t\t\t\t\t\t<DropdownItem name=\"removeAll\"> <Icon type=\"md-trash\"/>批量删除</DropdownItem>\n");
        sb.append("\t\t\t\t\t\t<DropdownItem name=\"export\"> <Icon type=\"md-arrow-down\" />导出本页数据</DropdownItem>\n");
        sb.append("\t\t\t\t\t</DropdownMenu>\n");
        sb.append("\t\t\t\t</Dropdown>\n");

        sb.append("\t\t\t\t<Button type=\"dashed\" @click=\"openSearch=!openSearch\">{{openSearch ? '关闭搜索' : '开启搜索'}}</Button>\n");
        sb.append("\t\t\t\t<Button type=\"dashed\" @click=\"openTip=!openTip\">{{openTip ? '关闭提示' : '开启提示'}}</Button>\n");
        sb.append("\t\t\t\t<div style=\"float: right;\">\n");
        sb.append("\t\t\t\t\t<Poptip transfer trigger=\"click\" placement=\"bottom-end\" title=\"动态列\">\n");
        sb.append("\t\t\t\t\t\t<Button icon=\"md-list\"></Button>\n");
        sb.append("\t\t\t\t\t\t<div slot=\"content\" style=\"position:relative;min-height:5vh\">\n");
        sb.append("\t\t\t\t\t\t\t<Checkbox-group v-model=\"colOptions\" @on-change=\"checkboxChange\">\n");
        sb.append("\t\t\t\t\t\t\t\t<checkbox v-for=\"item in colSelect\" :label=\"item\" :key=\"item\" ></checkbox>\n");
        sb.append("\t\t\t\t\t\t\t</Checkbox-group>\n");
        sb.append("\t\t\t\t\t\t</div>\n");
        sb.append("\t\t\t\t\t</Poptip>\n");
        sb.append("\t\t\t\t</div>\n");

        sb.append("\t\t\t</div>\n");
        sb.append("\t\t\t<div v-show=\"openTip\">\n");
        sb.append("\t\t\t\t<Alert show-icon>\n");
        sb.append("\t\t\t\t\t已选择\n");
        sb.append("\t\t\t\t\t<span class=\"select-count\">{{selectCount}}</span> 项\n");
        sb.append("\t\t\t\t\t<a class=\"select-clear\" @click=\"clearSelectAll\">清空</a>\n");
        sb.append("\t\t\t\t\t<span style=\"float: right;\">点击右上角按钮配置动态列↑</span>\n");
        sb.append("\t\t\t\t</Alert>\n");
        sb.append("\t\t\t</div>\n");
        sb.append("\t\t\t<div>\n");
        sb.append("\t\t\t\t<Table :loading=\"loading\" border :columns=\"columns\" sortable=\"custom\" :data=\"data\" @on-sort-change=\"changeSort\" @on-selection-change=\"showSelect\" ref=\"table\">\n");
        sb.append("\t\t\t\t\t<template slot-scope=\"{ row, index }\" slot=\"action\">\n");

        sb.append("\t\t\t\t\t\t<Button type=\"primary\" @click=\"handLook(row, index)\" size=\"small\"><Icon type=\"ios-eye-outline\"/>查看</Button>\n");
        sb.append("\t\t\t\t\t\t<Dropdown :transfer=true>\n");
        sb.append("\t\t\t\t\t\t\t<Button size=\"small\">\n");
        sb.append("\t\t\t\t\t\t\t\t更多操作\n");
        sb.append("\t\t\t\t\t\t\t\t<Icon  type=\"md-arrow-dropdown\"/>\n");
        sb.append("\t\t\t\t\t\t\t</Button>\n");
        sb.append("\t\t\t\t\t\t\t<DropdownMenu slot=\"list\">\n");
        sb.append("\t\t\t\t\t\t\t\t<DropdownItem name=\"edit\" @click.native=\"handEdit(row, index)\"><Icon type=\"ios-create-outline\" />修改</DropdownItem>\n");
        sb.append("\t\t\t\t\t\t\t\t<DropdownItem name=\"delete\" @click.native=\"deleteData(row, index)\"><Icon type=\"md-trash\"></Icon>删除</DropdownItem>\n");
        sb.append("\t\t\t\t\t\t\t</DropdownMenu>\n");
        sb.append("\t\t\t\t\t\t</Dropdown>\n");
        sb.append("\t\t\t\t\t</template>\n");
        sb.append("\t\t\t\t</Table>\n");
        sb.append("\t\t\t</div>\n");
        sb.append("\t\t\t<Row type=\"flex\" justify=\"end\" class=\"page\">\n");
        sb.append("\t\t\t\t<Page :current=\"searchForm.pageNumber\" :total=\"total\" :page-size=\"searchForm.pageSize\" @on-change=\"changePage\" @on-page-size-change=\"changePageSize\" :page-size-opts=\"[10,20,50]\" size=\"small\" show-total show-elevator show-sizer></Page>\n");
        sb.append("\t\t\t</Row>\n");
        sb.append("\t\t</Card>\n");
        sb.append("\t\t<update" + beanName + " v-model=\"updateShow\" :" + beanName + "Id=\"" + beanName + "Id\" :modalTitle=\"title\" v-on:handSearch=\"handSearch\"></update" + beanName + ">\n");
        sb.append("\t</div>\n");
        sb.append("</template>\n");
        // html部分结束，js部分开始
        sb.append("<script>\n");
        sb.append("\timport {delete" + beanName + ", query" + beanName + "List} from '@/api/" + toFirstCharLowerCase(beanName) + "/" + toFirstCharLowerCase(beanName) + "'\n");
        sb.append("\timport { formartDate } from '@/api/tools/tool'\n");
        sb.append("\timport update" + beanName + " from './update" + beanName + "'\n");
        sb.append("\texport default {\n");
        sb.append("\t\tcomponents: {\n");
        sb.append("\t\t\tupdate" + beanName + "\n");
        sb.append("\t\t},\n");
        sb.append("\t\tdata() {\n");
        sb.append("\t\t\treturn {\n");
        sb.append("\t\t\t\tdrop: false,\n");
        sb.append("\t\t\t\tdropDownContent: \"展开\",\n");
        sb.append("\t\t\t\tdropDownIcon: \"ios-arrow-down\",\n");
        sb.append("\t\t\t\tsearch: '',\n");
        sb.append("\t\t\t\tdata: [],\n");
        sb.append("\t\t\t\tcolumns: this.get" + beanName + "Columns(),\n");
        sb.append("\t\t\t\topenSearch: true,//打开搜索\n");
        sb.append("\t\t\t\topenTip: true,//打开提示\n");
        sb.append("\t\t\t\tloading: true, // 表单加载状态\n");
        sb.append("\t\t\t\tselectCount: 0, // 多选计数\n");
        sb.append("\t\t\t\tselectList: [], // 多选数据\n");
        sb.append("\t\t\t\tsearchForm: {\n");
        sb.append("\t\t\t\t\t// 搜索框对应data对象\n");
        for (ColumnInfo c : columns) {
            if (StringUtils.isNotBlank(c.getQueryType())) {
                sb.append("\t\t\t\t\t" + underlineToCamel(c.getColumnName()) + ":'',\n");
            }
        }

        sb.append("\t\t\t\t\tpageNumber: 1, // 当前页数\n");
        sb.append("\t\t\t\t\tpageSize: 10, // 页面大小\n");
        sb.append("\t\t\t\t\tstartDate: null,//开始时间\n");
        sb.append("\t\t\t\t\tendDate: null,//结束时间\n");
        sb.append("\t\t\t\t\tsort: 'createTime', // 默认排序字段\n");
        sb.append("\t\t\t\t\torder: 'desc' // 默认排序方式\n");
        sb.append("\t\t\t\t},\n");
        sb.append("\t\t\t\ttotal: 0,\n");
        sb.append("\t\t\t\ttitle: '',\n");
        sb.append("\t\t\t\t" + beanName + "Id: '',\n");
        sb.append("\t\t\t\tupdateShow: false,\n");
        sb.append("\t\t\t\ttableHeight: 200,\n");
        sb.append("\t\t\t\tcolOptions: [\n");
        int cIndex = 0;
        for (ColumnInfo c : columns) {
            if (c.getListShow()) {
                sb.append("\"" + c.getRemark() + "\"");
                sb.append(",");
            }
        }
        sb.append("\"操作\"],\n");
        sb.append("\t\t\t\tcolSelect: [\n");
        int Select = 0;
        for (ColumnInfo c : columns) {
            if (c.getListShow()) {
                sb.append("\"" + c.getRemark() + "\"");
                sb.append(",");
            }
        }
        sb.append("\"操作\"],\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\t methods: {\n");

        sb.append("\t\t\t //列表上方更多操作\n");
        sb.append("\t\t\thandDropdown(name) {\n");
        sb.append("\t\t\t\tif (name == \"refresh\") {\n");
        sb.append("\t\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t\t} else if (name == \"removeAll\") {\n");
        sb.append("\t\t\t\t\tthis.delAll();\n");
        sb.append("\t\t\t\t}else if (name == \"export\") {\n");
        sb.append("\t\t\t\t\tlet excolumns = this.columns.filter(function(v){ return v.title!=\"操作\" && v. type!= 'selection'});\n");
        sb.append("\t\t\t\t\tthis.$refs.table.exportCsv({\n");
        sb.append("\t\t\t\t\t\tfilename: '本页数据',\n");
        sb.append("\t\t\t\t\t\tcolumns: excolumns,\n");
        sb.append("\t\t\t\t\t\tdata:this.data\n");
        sb.append("\t\t\t\t\t});\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t },\n");
        sb.append("\t\t\t //展开 收起\n");
        sb.append("\t\t\tdropDown() {\n");
        sb.append("\t\t\t\tif (this.drop) {\n");
        sb.append("\t\t\t\t\tthis.dropDownContent = \"展开\";\n");
        sb.append("\t\t\t\t\tthis.dropDownIcon = \"ios-arrow-down\";\n");
        sb.append("\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\tthis.dropDownContent = \"收起\";\n");
        sb.append("\t\t\t\t\tthis.dropDownIcon = \"ios-arrow-up\";\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t\tthis.drop = !this.drop;\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//时间选择事件\n");
        sb.append("\t\t\tselectDateRange(v) {\n");
        sb.append("\t\t\t\tif (v) {\n");
        sb.append("\t\t\t\t\tthis.searchForm.startDate = v[0];\n");
        sb.append("\t\t\t\t\tthis.searchForm.endDate = v[1];\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\t//新增\n");
        sb.append("\t\t\tadd" + beanName + "() {\n");
        sb.append("\t\t\tthis.title = '新增';\n");
        sb.append("\t\t\t\tthis.updateShow = true\n");
        sb.append("\t\t\t\tthis." + beanName + "Id='';\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//编辑\n");
        sb.append("\t\t\thandEdit(row, index) {\n");
        sb.append("\t\t\tthis.title = '编辑';\n");
        sb.append("\t\t\t\tthis." + beanName + "Id=row.id.toString();\n");
        sb.append("\t\t\t\tthis.updateShow = true;\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//查看\n");
        sb.append("\t\t\thandLook(row, index) {\n");
        sb.append("\t\t\tthis.title = '查看';\n");
        sb.append("\t\t\t\tthis." + beanName + "Id=row.id.toString();\n");
        sb.append("\t\t\t\tthis.updateShow = true;\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\t//分页查询\n");
        sb.append("\t\t\tgetDataList() {\n");
        sb.append("\t\t\t\tthis.loading = true;\n");
        sb.append("\t\t\t\tquery" + beanName + "List(this.searchForm).then(res => {\n");
        sb.append("\t\t\t\t\tif(res.success) {\n");
        sb.append("\t\t\t\t\t\tthis.loading = false;\n");
        sb.append("\t\t\t\t\t\tthis.data = res.data.records;\n");
        sb.append("\t\t\t\t\t\tthis.total = res.data.total;\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\t//单一删除\n");
        sb.append("\t\t\tdeleteData(row, index){\n");
        sb.append("\t\t\t\tthis.$Modal.confirm({\n");
        sb.append("\t\t\t\t\ttitle:\"确认删除\",\n");
        sb.append("\t\t\t\t\tcontent: \"您确认要删除所点击选的数据?\",\n");
        sb.append("\t\t\t\t\tloading: true,\n");
        sb.append("\t\t\t\t\tonOk: () => {\n");
        sb.append("\t\t\t\t\t\tthis.userLoading = true;\n");
        sb.append("\t\t\t\t\t\tvar ids = [];\n");
        sb.append("\t\t\t\t\t\tids.push(row.id);\n");
        sb.append("\t\t\t\t\t\tthis.patchDeleteData(ids);\n");
        sb.append("\t\t\t\t\t},\n");
        sb.append("\t\t\t\t\tonCancel: () => {\n");
        sb.append("\t\t\t\t\t\tthis.$Message.info('取消了当前的操作行为！');\n");
        sb.append("\t\t\t\t\t},\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//批量删除\n");
        sb.append("\t\t\tdelAll() {\n");
        sb.append("\t\t\t\tif(this.selectCount <= 0) {\n");
        sb.append("\t\t\t\t\tthis.$Message.warning('您还未选择要删除的数据');\n");
        sb.append("\t\t\t\t\treturn;\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t\tthis.$Modal.confirm({\n");
        sb.append("\t\t\t\t\ttitle: '确认删除',\n");
        sb.append("\t\t\t\t\tcontent: '您确认要删除所选的 ' + this.selectCount + '条数据?',\n");
        sb.append("\t\t\t\t\tloading: true,\n");
        sb.append("\t\t\t\t\tonOk: () => {\n");
        sb.append("\t\t\t\t\t\tlet ids =[];\n");
        sb.append("\t\t\t\t\t\tthis.selectList.forEach(function(e) {\n");
        sb.append("\t\t\t\t\t\t\tids.push(e.id );\n");
        sb.append("\t\t\t\t\t\t});\n");
        sb.append("\t\t\t\t\t\tthis.patchDeleteData(ids);\n");
        sb.append("\t\t\t\t\t},\n");
        sb.append("\t\t\t\t\tonCancel: () => {\n");
        sb.append("\t\t\t\t\t\tthis.$Message.info('取消了当前的操作行为！');\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t },\n");
        sb.append("\t\t\t//删除（后台）\n");
        sb.append("\t\t\tpatchDeleteData(ids) {\n");
        sb.append("\t\t\t\tif(ids == undefined || ids == null || ids.length == 0) {\n");
        sb.append("\t\t\t\t\tthis.$Message.error('没有选择的数据');\n");
        sb.append("\t\t\t\t\t\treturn;\n");
        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t\tdelete" + beanName + "({ids:ids}).then(res => {\n");
        sb.append("\t\t\t\t\t\tthis.userLoading = false;\n");
        sb.append("\t\t\t\t\t\tthis.$Modal.remove();\n");
        sb.append("\t\t\t\t\t\tif(res.success) {\n");
        sb.append("\t\t\t\t\t\t\tthis.modalTaskVisible = false;\n");
        sb.append("\t\t\t\t\t\t\tthis.$Message.success('删除成功');\n");
        sb.append("\t\t\t\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t\t\t\t} else {\n");
        sb.append("\t\t\t\t\t\t\tthis.$Message.error('删除失败');\n");
        sb.append("\t\t\t\t\t\t}\n");
        sb.append("\t\t\t\t});\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//改变页码\n");
        sb.append("\t\t\tchangePage(v) {\n");
        sb.append("\t\t\t\tthis.searchForm.pageNumber = v;\n");
        sb.append("\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t\tthis.clearSelectAll();\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//改变每页显示数据的条数\n");
        sb.append("\t\t\tchangePageSize(v) {\n");
        sb.append("\t\t\t\tthis.searchForm.pageSize = v;\n");
        sb.append("\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\t//改变排序方式\n");
        sb.append("\t\t\tchangeSort(e) {\n");
        sb.append("\t\t\t\tthis.searchForm.sort = e.key;\n");
        sb.append("\t\t\t\tthis.searchForm.order = e.order;\n");
        sb.append("\t\t\t\tif(e.order == 'normal') {\n");
        sb.append("\t\t\t\t\tthis.searchForm.order = '';\n");
        sb.append("\t\t\t\t}\n");
        sb.append("\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t},\n");

        sb.append("\t\t\t//查询\n");
        sb.append("\t\t\thandSearch() {\n");
        sb.append("\t\t\t\tthis.searchForm.pageNumber = 1;\n");
        sb.append("\t\t\t\tthis.searchForm.pageSize = 10;\n");
        sb.append("\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//重置\n");
        sb.append("\t\t\thandReset() {\n");
        sb.append("\t\t\t\tthis.$refs.searchForm.resetFields();\n");
        sb.append("\t\t\t\tthis.searchForm.pageNumber = 1;\n");
        sb.append("\t\t\t\tthis.searchForm.pageSize = 10;\n");
        sb.append("\t\t\t\tthis.searchForm.startDate = null;\n");
        sb.append("\t\t\t\tthis.searchForm.endDate = null;\n");
        sb.append("\t\t\t\tthis.selectDate = null;\n");
        for (ColumnInfo c : columns) {
            if (StringUtils.isNotBlank(c.getQueryType())) {
                sb.append("\t\t\t\tthis.searchForm." + underlineToCamel(c.getColumnName()) + "='';\n");
            }
        }
        sb.append("\t\t\t\t// 重新加载数据\n");
        sb.append("\t\t\t\tthis.getDataList();\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//显示选择\n");
        sb.append("\t\t\tshowSelect(e) {\n");
        sb.append("\t\t\t\tthis.selectList = e;\n");
        sb.append("\t\t\t\tthis.selectCount = e.length;\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//清空选择\n");
        sb.append("\t\t\tclearSelectAll() {\n");
        sb.append("\t\t\t\tthis.$refs.table.selectAll(false);\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//获取列表字段\n");
        sb.append("\t\t\tget" + beanName + "Columns() {\n");
        sb.append("\t\t\t\treturn [\n");
        sb.append("\t\t\t\t\t{\n");
        sb.append("\t\t\t\t\t\ttype: 'selection',\n");
        sb.append("\t\t\t\t\t\twidth: 60,\n");
        sb.append("\t\t\t\t\t\talign: 'center',\n");
        sb.append("\t\t\t\t\t},\n");
        for (ColumnInfo c : columns) {
            if (c.getListShow()) {
                if (c.getColumnType().equals("Date") || c.getColumnType().equals("timestamp") || c.getColumnType().equals("datetime")) {
                    sb.append("\t\t\t\t\t{\n");
                    sb.append("\t\t\t\t\t\ttitle: '" + c.getRemark() + "',\n");
                    sb.append("\t\t\t\t\t\tkey: '" + underlineToCamel(c.getColumnName()) + "',\n");
                    sb.append("\t\t\t\t\t\tsortable: true,\n");
                    sb.append("\t\t\t\t\t\talign: 'center',\n");
                    sb.append("\t\t\t\t\t\twidth: 180,\n");
                    sb.append("\t\t\t\t\t},\n");
                } else {
                    sb.append("\t\t\t\t\t{\n");
                    sb.append("\t\t\t\t\t\ttitle: '" + c.getRemark() + "',\n");
                    sb.append("\t\t\t\t\t\talign: 'center',\n");
                    sb.append("\t\t\t\t\t\tminWidth: 120,\n");
                    sb.append("\t\t\t\t\t\tellipsis: true,\n");
                    sb.append("\t\t\t\t\t\ttooltip: true,\n");
                    sb.append("\t\t\t\t\t\tkey: '" + underlineToCamel(c.getColumnName()) + "',\n");
                    sb.append("\t\t\t\t\t\tsortable: false\n");
                    sb.append("\t\t\t\t\t},\n");
                }
            }
        }
        sb.append("\t\t\t\t\t{\n");
        sb.append("\t\t\t\t\t\ttitle:'操作',\n");
        sb.append("\t\t\t\t\t\talign: 'center',\n");
        sb.append("\t\t\t\t\t\tslot: 'action',\n");
        sb.append("\t\t\t\t\t\twidth: 200,\n");

        sb.append("\t\t\t\t\t}\n");
        sb.append("\t\t\t\t]\n");
        sb.append("\t\t\t},\n");
        sb.append("\t\t\t//动态列实现\n");
        sb.append("\t\t\tcheckboxChange:function (data) {\n");
        sb.append("\t\t\tvar columnss =  this.getTSubstancesColumns();\n");
        sb.append("\t\t\tthis.columns= columnss.filter(function(v){ return data.indexOf(v.title) > -1 || v. type== 'selection'})\n");
        sb.append("\t\t\t}\n");
        sb.append("\t\t},\n");
        sb.append("\t\tmounted() {\n");
        sb.append("\t\t\tthis.getDataList();\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("</script>\n");
        sb.append("<style scoped=\"less\">\n");
        sb.append("\t .operation{\n");
        sb.append("\t\tmargin-bottom: 10px;\n");
        sb.append("\t}\n");
        sb.append("</style>\n");
        FileOutputStream fos = new FileOutputStream(fEntity);
        fos.write(sb.toString().getBytes());
        fos.close();
    }

    /**
     * 获取主键uuid
     *
     * @return uuid
     */
    private static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 功能描述：生成授权的sql脚本
     *
     * @param sqlPath        生成的sql存放的路径
     * @param beanName       实体名称
     * @param parentTreeCode 父节点编码
     */
    private static void genAuthSql(String sqlPath, String beanName, String parentTreeCode) throws IOException {
        if (sqlPath == null || "".equals(sqlPath)) {
            File file = new File("");
            sqlPath = file.getCanonicalPath() + "/doc";
        }
        System.out.println("sqlPath=>" + sqlPath);
        File file = new File(sqlPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());
        //生成sql脚本文件
        File fEntity = new File(sqlPath + "/" + DATE_TIME.format(LocalDateTime.now()) + "" + ".sql");
        if (fEntity.exists()) {
            fEntity.delete();
        }
        StringBuilder insertSql = new StringBuilder();
        // 生成插入菜单表列表的sql数据
        insertSql.append("insert into t_tree(treeName,treeCode,treeState,treeType,parentTreeId,parentTreeName,crtDate,fullPath,powerPath)\n");
        insertSql.append("select '" + beanName + "' as treeName,'system-manage-" + toFirstCharLowerCase(beanName) + "' as treeCode ,'1' as treeState,'1' as treeType,treeId as parentTreeId,treeName as parentTreeName,");
        insertSql.append("SYSDATE() as crtDate,fullPath,'/" + toFirstCharLowerCase(beanName) + "/query" + beanName + "List:/" + toFirstCharLowerCase(beanName) + "/get" + beanName + "' as powerPath from t_tree  where treeCode = '" + parentTreeCode + "';");
        insertSql.append("UPDATE t_tree set fullPath = CONCAT(fullPath,',',treeId) where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';\n");
        // 生成插入菜单底下的新增按钮节点的sql数据
        insertSql.append("insert into t_tree(treeName,treeCode,treeState,treeType,parentTreeId,parentTreeName,crtDate,fullPath,powerPath)\n");
        insertSql.append("select '新增' as treeName,'system-manage-" + toFirstCharLowerCase(beanName) + "-add' as treeCode ,'1' as treeState,'2' as treeType,treeId as parentTreeId,treeName as parentTreeName,");
        insertSql.append("SYSDATE() as crtDate,fullPath,'/" + toFirstCharLowerCase(beanName) + "/add" + beanName + "' as powerPath from t_tree  where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';");
        insertSql.append("UPDATE t_tree set fullPath = CONCAT(fullPath,',',treeId) where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';\n");
        // 生成插入菜单底下的修改按钮节点的sql数据
        insertSql.append("insert into t_tree(treeName,treeCode,treeState,treeType,parentTreeId,parentTreeName,crtDate,fullPath,powerPath)\n");
        insertSql.append("select '修改' as treeName,'system-manage-" + toFirstCharLowerCase(beanName) + "-update' as treeCode ,'1' as treeState,'2' as treeType,treeId as parentTreeId,treeName as parentTreeName,");
        insertSql.append("SYSDATE() as crtDate,fullPath,'/" + toFirstCharLowerCase(beanName) + "/update" + beanName + "' as powerPath from t_tree  where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';");
        insertSql.append("UPDATE t_tree set fullPath = CONCAT(fullPath,',',treeId) where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';\n");
        // 生成插入菜单底下的删除按钮节点的sql数据
        insertSql.append("insert into t_tree(treeName,treeCode,treeState,treeType,parentTreeId,parentTreeName,crtDate,fullPath,powerPath)\n");
        insertSql.append("select '新增' as treeName,'system-manage-" + toFirstCharLowerCase(beanName) + "-delete' as treeCode ,'1' as treeState,'2' as treeType,treeId as parentTreeId,treeName as parentTreeName,");
        insertSql.append("SYSDATE() as crtDate,fullPath,'/" + toFirstCharLowerCase(beanName) + "/delete" + beanName + "' as powerPath from t_tree  where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';");
        insertSql.append("UPDATE t_tree set fullPath = CONCAT(fullPath,',',treeId) where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "-delete';\n");
        // 生成角色菜单关联关系的数据
        insertSql.append("insert into t_role_tree(roleTreeId,roleId,treeId) select '" + getUUID() + "' as roleTreeId,'1' as roleId,treeId from t_tree where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "';\n");
        insertSql.append("insert into t_role_tree(roleTreeId,roleId,treeId) select '" + getUUID() + "' as roleTreeId,'1' as roleId,treeId from t_tree where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "-add';\n");
        insertSql.append("insert into t_role_tree(roleTreeId,roleId,treeId) select '" + getUUID() + "' as roleTreeId,'1' as roleId,treeId from t_tree where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "-delete';\n");
        insertSql.append("insert into t_role_tree(roleTreeId,roleId,treeId) select '" + getUUID() + "' as roleTreeId,'1' as roleId,treeId from t_tree where treeCode = 'system-manage-" + toFirstCharLowerCase(beanName) + "-update';\n");
        FileOutputStream fos = new FileOutputStream(fEntity);
        fos.write(insertSql.toString().getBytes());
        fos.close();
    }


    /**
     * 功能描述： 生成前端代码
     *
     * @param webPath   前端工程的路径
     * @param beanName  实体类名称
     * @param columns
     * @param genConfig
     */
    public static void genWebFiles(String webPath, String beanName,
                                   List<ColumnInfo> columns, GenConfig genConfig) throws IOException {

        //TableModel table = JdbcUtil.getTableStructure(driverClassName, username, password, url, tableName);
        // 生成API文件
        genApi(webPath, beanName);


        // 生成ListVue文件
        genListVue(webPath, beanName, columns, genConfig);
        // 生成addVue文件
        // genAddVue(webPath, beanName, table);
        // 生成updateVue文件
        genUpdateVue(webPath, beanName, columns, genConfig);
        // 生成路由信息
        // String parentTreeCode = genRouter(webPath, beanName, routerNode);
//        if (!"".equals(parentTreeCode)) {
//            genAuthSql(sqlPath, beanName, parentTreeCode);
//        }
    }

    /**
     * 功能描述： 生成后端代码
     *
     * @param author      作者
     * @param tableName   表名
     * @param basePackage 生成文件的包的基础路径
     * @param beanName    实体类名称
     * @param columns     实体列集合
     * @param genConfig   实体配置
     * @throws IOException
     */
    public static void genFiles(String author, String tableName, String basePackage, String beanName, List<ColumnInfo> columns, GenConfig genConfig) throws IOException {
        String packagePath = basePackage.replaceAll("\\.", "/");
        //TableModel table = JdbcUtil.getTableStructure(driverClassName, username, password, url, tableName);
//        String entityPath = createFloder("src/main/java", packagePath + "/entity");
//        //生成实体类文件
//        File fEntity = new File(entityPath + "/" + beanName + ".java");
//        if (fEntity.exists()) {
//            fEntity.delete();
//        }
//        FileOutputStream fos = new FileOutputStream(fEntity);
//        fos.write(genJavaBeanFromTableStructure(author, table, beanName, basePackage + ".entity").getBytes());
//        fos.close();
//        //生成mybatis配置文件
//        String mybatisPath = createFloder("/src/main" + mybatisBasePath);
//        System.out.println("mybatisPath=>" + mybatisPath);
//        fos = new FileOutputStream(mybatisPath + "/mybatis_" + toFirstCharLowerCase(beanName) + ".xml");
//        fos.write(MyBatisUtil.genMapperConfig(table, basePackage + ".dao." + beanName + "Dao", basePackage + ".entity." + beanName, beanName).getBytes());
//        fos.close();
//        //生成Dao
//        String daoPath = createFloder("src/main/java", packagePath + "/dao");
//        File fDao = new File(daoPath + "/" + beanName + "Dao.java");
//        fos = new FileOutputStream(fDao);
//        fos.write(genDao(author, basePackage + ".dao", beanName).getBytes());
//        fos.close();
        //生成Service
        String servicePath = createFloder("test/src/main/java", packagePath + "/service");
        File fService = new File(servicePath + "/I" + beanName + "Service.java");
        FileOutputStream fos = new FileOutputStream(fService);
        fos.write(genService(author, genConfig.getPack() + ".dao.service", beanName, columns).getBytes());
        fos.close();

        //生成ServiceImpl
        String serviceImplPath = createFloder("test/src/main/java", packagePath + "/service/impl");
        File fServiceImpl = new File(serviceImplPath + "/" + beanName + "ServiceImpl.java");
        fos = new FileOutputStream(fServiceImpl);
        fos.write(genServiceImpl(author, genConfig.getPack() + ".dao.service.impl", beanName, columns).getBytes());
        fos.close();
        // 生成controller
        String controllerPath = createFloder("test/src/main/java", packagePath + "/controller");
        File fController = new File(controllerPath + "/" + beanName + "Controller.java");
        fos = new FileOutputStream(fController);
        fos.write(genController(author, genConfig.getPack() + ".controller", beanName, columns, genConfig).getBytes());
        fos.close();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        genAuthSql("E:\\\\idea-work\\\\vcm\\\\vcm-we", "AbcTest", "system-manage");
        // genRouter("E:\\idea-work\\vcm\\vcm-web", "TTest", "abc");
//        genWebFiles("linzf",
//                "t_test",
//                "E:\\idea-work\\vcm\\vcm-web",
//                "TTest",
//                "com.mysql.jdbc.Driver",
//                "root",
//                "123456",
//                "jdbc:mysql://127.0.0.1:3306/vcm?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai",
//                "sys");
//        genFiles("linzf",
//                "t_test",
//                "com.github.lazyboyl.vcm.web.core",
//                "/resources/mybatis/mapper",
//                "TTest",
//                "com.mysql.cj.jdbc.Driver",
//                "root",
//                "123456",
//                "jdbc:mysql://127.0.0.1:3306/vcm?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai");
    }

    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        } else {
            s = toCamelCase(s);
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.toLowerCase();
            StringBuilder sb = new StringBuilder(s.length());
            boolean upperCase = false;

            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (c == '_') {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
    }
}
