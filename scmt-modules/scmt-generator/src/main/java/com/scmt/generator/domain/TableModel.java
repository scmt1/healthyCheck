package com.scmt.generator.domain;

import java.util.List;
import java.util.Set;

/**
 * @author linzf
 * @since 2019-08-05
 * 类描述：表结构封装
 */
public class TableModel {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键列
     */
    private List<ColumnModel> primaryKeyColumns;

    /**
     * 列
     */
    private List<ColumnModel> columns;

    /**
     * 需要引入包
     */
    private Set<String> imports;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnModel> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(List<ColumnModel> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }
}
