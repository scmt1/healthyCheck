package com.scmt.core.common.utils;

import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.vo.PageVo;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Exrickx
 */
public class PageUtil extends cn.hutool.core.util.PageUtil {

    private final static String[] KEYWORDS = {"master", "truncate", "insert", "select",
            "delete", "update", "declare", "alter", "drop", "sleep"};

    private PageUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * JPA分页封装
     * @param page
     * @return
     */
    public static Pageable initPage(PageVo page) {

        Pageable pageable = null;
        int pageNumber = page.getPageNumber();
        int pageSize = page.getPageSize();
        String sort = page.getSort();
        String order = page.getOrder();

        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        if (StrUtil.isNotBlank(sort)) {
            Sort.Direction d;
            if (StrUtil.isBlank(order)) {
                d = Sort.Direction.DESC;
            } else {
                d = Sort.Direction.valueOf(order.toUpperCase());
            }
            Sort s = Sort.by(d, sort);
            pageable = PageRequest.of(pageNumber - 1, pageSize, s);
        } else {
            pageable = PageRequest.of(pageNumber - 1, pageSize);
        }
        return pageable;
    }

    /**
     * Mybatis-Plus分页封装
     * @param page
     * @return
     */
    public static Page initMpPage(PageVo page) {

        Page p = null;
        int pageNumber = page.getPageNumber();
        int pageSize = page.getPageSize();
        String sort = page.getSort();
        String order = page.getOrder();

        SQLInject(sort);

        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        if (StrUtil.isNotBlank(sort)) {
            Boolean isAsc = false;
            if (StrUtil.isBlank(order)) {
                isAsc = false;
            } else {
                if ("desc".equals(order.toLowerCase())) {
                    isAsc = false;
                } else if ("asc".equals(order.toLowerCase())) {
                    isAsc = true;
                }
            }
            p = new Page(pageNumber, pageSize);
            if (isAsc) {
                p.addOrder(OrderItem.asc(camel2Underline(sort)));
            } else {
                p.addOrder(OrderItem.desc(camel2Underline(sort)));
            }

        } else {
            p = new Page(pageNumber, pageSize);
        }
        return p;
    }

    /**
     * List 手动分页
     * @param page
     * @param list
     * @return
     */
    public static List listToPage(PageVo page, List list) {

        int pageNumber = page.getPageNumber() - 1;
        int pageSize = page.getPageSize();

        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }

        int fromIndex = pageNumber * pageSize;
        int toIndex = pageNumber * pageSize + pageSize;

        if (fromIndex > list.size()) {
            return new ArrayList();
        } else if (toIndex >= list.size()) {
            return list.subList(fromIndex, list.size());
        } else {
            return list.subList(fromIndex, toIndex);
        }
    }

    /**
     * 驼峰法转下划线
     */
    public static String camel2Underline(String str) {

        if (StrUtil.isBlank(str)) {
            return "";
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                sb.append("_" + Character.toLowerCase(str.charAt(i)));
            } else {
                sb.append(str.charAt(i));
            }
        }
        return (str.charAt(0) + sb.toString()).toLowerCase();
    }

    /**
     * 防Mybatis-Plus order by注入
     * @param param
     */
    public static void SQLInject(String param) {

        if (StrUtil.isBlank(param)) {
            return;
        }

        // 转换成小写
        param = param.toLowerCase();
        // 判断是否包含非法字符
        for (String keyword : KEYWORDS) {
            if (param.contains(keyword)) {
                throw new ScmtException(param + "包含非法字符");
            }
        }
    }


    public static List toPage(int page, int size, List list) {
        int fromIndex = page * size;
        int toIndex = page * size + size;
        if (fromIndex > list.size()) {
            return new ArrayList();
        } else {
            return toIndex >= list.size() ? list.subList(fromIndex, list.size()) : list.subList(fromIndex, toIndex);
        }
    }

    public static Map<String, Object> toPage(org.springframework.data.domain.Page page) {
        Map<String, Object> map = new LinkedHashMap(2);
        map.put("content", page.getContent());
        map.put("totalElements", page.getTotalElements());
        return map;
    }

    public static Map<String, Object> toPage(Object object, Object totalElements) {
        Map<String, Object> map = new LinkedHashMap(2);
        map.put("content", object);
        map.put("totalElements", totalElements);
        return map;
    }
}
