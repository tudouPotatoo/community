package com.nowcoder.community.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤工具类
 * 1. 定义前缀树类
 * 2. 根据敏感词初始化前缀树
 * 3. 提供过滤敏感词的方法
 */
@Slf4j
@Component
public class SensitiveWordsFilter {
    /**
     * 前缀树节点
     * 成员变量：
     *          当前节点的子节点-Map<Character, TrieNode> child
     *          当前节点是否叶子节点（即是否是关键词的结尾）-isKeywordEnd
     * 成员方法：
     *          给当前节点添加子节点-addChild(Character c, TrieNode node)
     *          获取子节点-getChild(Character c)
     *          设置isKeywordEnd的值-setKeywordEnd()
     *          获取isKeywordEnd的值-isKeywordEnd()
     */
    private class TrieNode {
        /**
         * 子节点
         * key为字符 value为字符对应的节点
         */
        private Map<Character, TrieNode> child = new HashMap<>();
        /**
         * 关键词结束标识
         */
        private boolean isKeywordEnd = false;

        public void addChild(Character c, TrieNode node) {
            child.put(c, node);
        }

        public TrieNode getChild(Character c) {
            return child.get(c);
        }

        public void setKeywordEnd(boolean isKeywordEnd) {
            this.isKeywordEnd = isKeywordEnd;
        }
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
    }

    /**
     * 前缀树根节点
     */
    private TrieNode trieRoot = new TrieNode();

    /**
     * 替换符
     */
    private static final String REPLACEMENT = "***";

    /**
     * 根据敏感词初始化前缀树
     * (该方法在SensitiveWordsFilter类的构造器被调用之后就会执行，
     * SensitiveWordsFilter类在服务启动时就会被初始化，
     * 因为该类被Spring容器所管理。)
     *
     * 1. 获取敏感词文件读取流
     * 2. 每次读一行 即一个关键词
     * 3. 更新前缀树
     */
    @PostConstruct
    private void init() {
        try (
                // 1. 获取敏感词文件读取流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            // 2. 每次读一行 即一个关键词
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 3. 更新前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            log.error("前缀树初始化失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 给前缀树添加敏感词
     * @param keyword
     */
    public void addKeyword(String keyword) {
        int keywordLen = keyword.length();
        TrieNode parent = trieRoot;
        for (int i = 0; i < keywordLen; i++) {
            char c = keyword.charAt(i);
            // 查看当前节点是否有字符为keyword[i]的子节点
            TrieNode child = parent.getChild(c);
            // 无 则添加新的子节点
            if (child == null) {
                child = new TrieNode();
                parent.addChild(c, child);
            }
            parent = child;
            // 如果当前是keyword的最后一个字 则会把isKeywordEnd设为true
            if (i == keywordLen - 1) {
                parent.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤str字符串的敏感词
     * 1. 定义三个指针
     *    triePointer-前缀树上的指针
     *    from-遍历str使用到的指针
     *    to-遍历str使用到的指针
     * 2. 定义StringBuilder res存储过滤敏感词后的结果字符串
     * 3. 遍历字符串的每一个字符
     *    3.0 当to == str的长度时 说明遍历结束 将str[from..]from往后的所有字符加入res（此时from往后的所有字符一定不含敏感词）
     *    3.1 设当前遍历到的字符，即to指针指向的字符为c
     *    3.2 判断当前字符是否特殊符号
     *          不是 --> 继续往下
     *            是 --> 判断当前特殊字符是否在敏感词内部(吸※毒)（即是否满足from==to）
     *                      不是 --> 则可以加入res，from右移一位，to=from，triePointer回到trieRoot
     *                        是 --> 则跳过当前字符，to右移一位
     *    3.3 判断当前的父节点是否包含字符为c的子节点
     *          不是 --> 说明[from, to]这个子串不是敏感词，则from位置的字符加入res
     *                   from右移一位 to=from
     *                   triePointer回到trieRoot
     *            是 --> 判断字符c对应的子节点是否是叶子节点
     *                      不是 --> 说明[from, to]这个字符串有可能构成敏感词，to右移一位，triePointer=子节点
     *                        是 --> 说明[from, to]这个字符串是敏感词，res添加替换词，to右移一位，from=to，triePointer回到trieRoot
     *
     * @param str
     */
    public String filter(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        // 1. 定义三个指针
        // 前缀树上的指针
        TrieNode triePointer = trieRoot;
        // 遍历str时使用到的指针
        int from = 0;
        int to = 0;
        // 2. 定义StringBuilder res存储过滤敏感词后的结果字符串
        StringBuilder res = new StringBuilder();
        // 3. 遍历字符串的每一个字符
        int strLen = str.length();
        while (to < strLen){
            // 3.1 设当前遍历到的字符，即to指针指向的字符为c
            char c = str.charAt(to);
            // 3.2 判断当前字符是否特殊符号
            if (isSymbol(c)) {
                // 判断当前特殊字符是否在敏感词内部(吸※毒)（即是否满足from==to）
                // 当前特殊字符不在敏感词内部
                if (from == to) {
                    res.append(c);
                    from++;
                    to = from;
                    // triePointer回到trieRoot
                    triePointer = trieRoot;
                } else {
                    // 当前特殊字符在敏感词内部
                    to++;
                }
            } else {
                // 如果当前字符不是特殊符号

                // 3.3 判断当前的父节点是否包含字符为c的子节点
                TrieNode child = triePointer.getChild(c);

                if (child == null) {
                    // 说明[from, to]这个子串不是敏感词，则from位置的字符加入res
                    res.append(str.charAt(from));
                    // from右移一位 to=from
                    from++;
                    to = from;
                    // triePointer回到trieRoot
                    triePointer = trieRoot;
                } else {
                    // 判断字符c对应的子节点是否是叶子节点
                    if (child.isKeywordEnd()) {
                        // 说明[from, to]这个字符串是敏感词，res添加替换词，to右移一位，from=to
                        res.append(REPLACEMENT);
                        to++;
                        from = to;
                        // triePointer回到trieRoot
                        triePointer = trieRoot;
                    } else {
                        // 说明[from, to]这个字符串有可能构成敏感词，to右移一位
                        to++;
                        // triePointer=子节点
                        triePointer = child;
                    }
                }
            }
        }
        // 将剩余的字符加入res，此时str 从from到最后的字符串一定不包含敏感词
        res.append(str.substring(from));
        // 返回结果
        return res.toString();
    }

    /**
     * 判断当前字符是否特殊符号
     * 1. 不是字符或数字
     * 2. 且不是东亚文字
     * @param c
     * @return
     */
    public boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
