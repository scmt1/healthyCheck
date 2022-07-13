package com.scmt.core.common.utils;

import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.service.StopWordService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.WordTree;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author exrick
 */
@Slf4j
public class StopWordsUtil {

    private static StopWordService stopWordService = SpringUtil.getBean(StopWordService.class);

    private StopWordsUtil() {

    }

    private static WordTree wordTree;

    public static WordTree getInstance() {

        if (wordTree == null) {
            // 初始加载数据
            wordTree = new WordTree();
            stopWordService.getAll().forEach(e -> wordTree.addWord(e.getTitle()));
        }
        return wordTree;
    }

    public static void addWord(String word) {

        getInstance().addWord(word);
    }

    public static void removeWord(String word) {

        getInstance().remove(word);
    }

    /**
     * 返回匹配的词
     * @param param
     * @return
     */
    public static String match(String param) {

        return getInstance().match(param);
    }

    /**
     * 检测到禁用词直接抛出异常
     * @param param
     * @return
     */
    public static void matchWord(String param) {

        if (StrUtil.isNotBlank(match(param))) {
            throw new ScmtException("包含禁用词：" + param);
        }
    }

    /**
     * 返回所有匹配的词
     * @param param
     * @return
     */
    public static List<String> matchAll(String param) {

        return getInstance().matchAll(param);
    }

    /**
     * 将匹配词过滤为*
     * @param param
     * @return
     */
    public static String filter(String param) {

        return filter(param, "*");
    }

    /**
     * 自定义替换符号字符
     * @param param
     * @param replacement
     * @return
     */
    public static String filter(String param, String replacement) {

        for(String e : matchAll(param)){
            param = param.replaceAll(e, replacement);
        }
        return param;
    }
}
