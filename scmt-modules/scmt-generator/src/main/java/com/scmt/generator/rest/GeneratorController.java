package com.scmt.generator.rest;

import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.utils.PageUtil;
import com.scmt.core.common.utils.ResultUtil;
import com.scmt.core.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.scmt.generator.domain.ColumnInfo;
import com.scmt.generator.service.GenConfigService;
import com.scmt.generator.service.GeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2019-01-02
 */
@RestController
@RequestMapping("/scmt/generator")
@Api(tags = "系统：代码生成管理")
public class GeneratorController {
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private GenConfigService genConfigService;

    private Boolean generatorEnabled = true;

    //public GeneratorController(GeneratorService generatorService, GenConfigService genConfigService) {
    //    this.generatorService = generatorService;
    //    this.genConfigService = genConfigService;
    //}

    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables/all")
    public ResponseEntity<Object> getTables() {
        return new ResponseEntity<>(generatorService.getTables(), HttpStatus.OK);
    }

    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables")
    public ResponseEntity<Object> getTables(@RequestParam(defaultValue = "") String name,
                                            @RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        return new ResponseEntity<>(generatorService.getTables(name, startEnd), HttpStatus.OK);
    }

    @ApiOperation("查询字段数据")
    @GetMapping(value = "/columns")
    public ResponseEntity<Object> getTables(@RequestParam String tableName) {
        List<ColumnInfo> columnInfos = generatorService.getColumns(tableName);
        return new ResponseEntity<>(PageUtil.toPage(columnInfos, columnInfos.size()), HttpStatus.OK);
    }

    @ApiOperation("保存字段数据")
    @PutMapping
    public ResponseEntity<HttpStatus> save(@RequestBody List<ColumnInfo> columnInfos) {
        generatorService.save(columnInfos);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("同步字段数据")
    @PostMapping(value = "sync")
    public ResponseEntity<HttpStatus> sync(@RequestBody List<String> tables) {
        for (String table : tables) {
            generatorService.sync(generatorService.getColumns(table), generatorService.query(table));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("生成代码")
    @PostMapping(value = "/{tableName}/{type}")
    public Result<Object> generator(@PathVariable String tableName, @PathVariable Integer type, HttpServletRequest request, HttpServletResponse response) {
        if (!generatorEnabled && type == 0) {
            throw new ScmtException("此环境不允许生成代码，请选择预览或者下载查看！");
        }
        switch (type) {
            // 生成代码
            case 0:
                generatorService.generator(genConfigService.find(tableName), generatorService.getColumns(tableName));
                break;
            // 预览
            case 1:
                return generatorService.preview(genConfigService.find(tableName), generatorService.getColumns(tableName));
            // 打包
            case 2:
                generatorService.download(genConfigService.find(tableName), generatorService.getColumns(tableName), request, response);
                break;
            default:
                throw new ScmtException("没有这个选项");
        }
        return ResultUtil.data("OK");
    }
}
