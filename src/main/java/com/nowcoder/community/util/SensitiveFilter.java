package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符为星星
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        // PostConstruct 注解表示当容器调用该构造器后(也即服务启动时)，此初始化方法 init() 将被自动调用
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树当中去
    private void addKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); ++i) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 当某一个单词循环到最后一个字符，最后一个字符打上标记
            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针 1 ()，默认情况指向根
        TrieNode tempNode = rootNode;
        // 指针 2 ()
        int begin = 0;
        // 指针 3 ()
        int position = 0;
        // 默认变量指向最终的结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号 (例如遇到 开??**票>>**)
            if (isSymbol(c)) {
                // 判断指针 1 是否指向的是根
                // 若指针 1 处于根节点，将此符号计入结果
                // 让指针 2 向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 因为指针 3 无论如何都会往后走
                // 无论符号在开头还是中间，指针 3 都向下走一步
                // 指针 3 在外面加
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以 begin 为开头的字符串不是敏感词
                // 这样就将 begin 指向的字符记录下来
                sb.append(text.charAt(begin));
                // 进入下一个位置
                // position 需要和 begin 保持一致
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现了敏感词，将 begin ~ position 这一段的字符串替换掉
                sb.append(REPLACEMENT);
                // 让 position 进入下一个位置
                begin = ++position;
                // 再重新指向根节点
                tempNode = rootNode;
            } else {
                // 没有检测完也没有发现符号，需要继续往下执行
                // 检查下一个字符
                ++position;
            }
        }
        // 这样走了一轮，但不是严格走了一轮
        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // && 后面是东亚文字范围 0x2E80 到 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀树
    private class TrieNode {

        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点(key 是下级字符，value 是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }





    }

}
