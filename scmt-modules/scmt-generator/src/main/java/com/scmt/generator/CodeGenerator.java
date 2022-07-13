package com.scmt.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import io.micrometer.core.instrument.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * com.study.luozhen.util
 *
 * @author dengjie
 * @version V1.0
 * @date 2020-05-21
 * @description
 */

public class CodeGenerator {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }

        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        //String projectPath = System.getProperty("user.dir");
        String projectPath = "D:\\code\\/src/main/java";
        gc.setOutputDir(projectPath );
        // 作者信息
        gc.setAuthor("dengjie");
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        gc.setOpen(false);
        gc.setIdType(IdType.UUID);
        gc.setSwagger2(true);
        //是否覆盖已有文件
        gc.setFileOverride(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://192.168.1.147:3306/healthy?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8");
        //dsc.setUrl("jdbc:oracle:thin:@192.168.30.15:1521:orcl");
        // dsc.setSchemaName("public");
        //dsc.setDriverName("oracle.jdbc.driver.OracleDriver");
        dsc.setDriverName("com.mysql.jdbc.Driver");

        dsc.setUsername("develop");
        dsc.setPassword("123456");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //设置模块名
        //pc.setModuleName(scanner("模块名"));
        pc.setParent("com.scmt.healthy");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                /*return projectPath + "/src/main/resources/mapper/dao/building/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;*/
                // 自定义输入文件名称
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(scanner("表名"));
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityColumns("id");
        //strategy.setSuperEntityClass("com.study.luozhen.common.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        //strategy.setSuperControllerClass("com.study.luozhen.common.BaseController");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        // 切换为 freemarker 模板引擎
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 切换为 beetl 模板引擎
        //mpg.setTemplateEngine(new BeetlTemplateEngine());
        mpg.execute();
    }

}
