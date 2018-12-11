package com.example.dochubserver.service;

import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 网站敏感词过滤
 */
@Service
public class SensitiveWordsFilter implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(SensitiveWordsFilter.class);

    //根节点
    private TrieNode rootNode = new TrieNode();


    /**
     * 项目初始化后读取敏感词库并构建字典库
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords/SensitiveWord2.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String LineText;
            while ((LineText = bufferedReader.readLine())!=null)
            {
                LineText = LineText.trim();
                addWord(LineText);
            }
            reader.close();
        }catch (Exception e)
        {
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }

    public boolean HasSensitiveWord(String word)
    {
        TrieNode pointer = rootNode;
        int begin = 0;//标识词语的开头位置方便回滚
        int position = 0;//当前比较的位置

        while (begin<word.length())
        {
            if (position==word.length())
            {
                begin++;
                position=begin;
                pointer = rootNode;
                continue;
            }

            char c = word.charAt(position);

            if (isSymbol(c))
            {
                if (pointer == rootNode)
                {
                    begin++;
                }
                position++;
                continue;
            }

            pointer = pointer.getSubNode(c);
            if (pointer==null)
            {
                //说明以begin开始的该字符没有敏感词
                begin++;
                position=begin;
                pointer=rootNode;
            }
            else if (pointer.isKeyWordEnd())
            {
                //说明存在敏感词
                return true;
            }
            else
            {
                position++;
            }
        }
        return false;
    }

    /**
     * 构造前缀树
     */
    private class TrieNode{

        //是否是一个关键词的终结节点
        private boolean end = false;

        //该节点的子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //添加子节点
        public void addSubNodes(Character key,TrieNode subnode)
        {
            subNodes.put(key,subnode);
        }
        //获取子节点
        public TrieNode getSubNode(Character key)
        {
            return subNodes.get(key);
        }
        //将当前节点设置为一个关键词的终结节点
        public void setNodeEnd()
        {
            this.end = true;
        }

        public boolean isKeyWordEnd()
        {
            return end;
        }

    }

    /**
     * 判断是否是字符
     * @param c
     * @return
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    private void addWord(String word)
    {
        TrieNode pointer = rootNode;
        for(int i=0;i<word.length();i++)
        {
            Character character = word.charAt(i);
            //过滤空格
            if(isSymbol(character))
            {
                continue;
            }
            TrieNode node = pointer.getSubNode(character);
            if (node==null)
            {
                node = new TrieNode();
                pointer.addSubNodes(character,node);
            }
            pointer = node;//移动指针
            if (i==word.length()-1)
            {
                pointer.setNodeEnd();//将该字符设置为该敏感词的终点
            }
        }
    }

}
