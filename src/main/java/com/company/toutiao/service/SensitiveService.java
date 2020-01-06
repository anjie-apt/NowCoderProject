package com.company.toutiao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineText;
            while ((lineText = bufferedReader.readLine()) != null) {
                addWord(lineText.trim());
            }
            reader.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    //增加关键词
    public void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;

        for (int i = 0; i < lineTxt.length(); i++) {
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                tempNode.setkeywordEnd(true);
            }

        }
    }
    private class TrieNode {
        //是不是关键词的结尾
        private boolean end = false;

        //当前节点下所有的子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }

        void setkeywordEnd(boolean end) {
            this.end = end;
        }
    }
    private TrieNode rootNode = new TrieNode();

    private boolean isSymbol(char c) {
        int ic = (int) c;
        //东亚文字0x2E80-0x9FFF
        return !Character.isAlphabetic(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    public String filter(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        String replacement = "***";
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            if (tempNode == null) {
                result.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                //发现敏感词
                result.append(replacement);
                position += 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    public static void main(String[] args) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        System.out.println(s.filter("你好色情"));
    }

}
