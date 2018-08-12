package com.nowcoder.service;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger=LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认敏感词替换符
     */
    private static final String DEFAULT_REPLACEMENT = "**";

    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader=new InputStreamReader(is);
            BufferedReader bufferedReader=new BufferedReader(reader);
            String lineTxt;
            while((lineTxt=bufferedReader.readLine())!=null){
                addWord(lineTxt.trim());
            }
            bufferedReader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败"+e.getMessage());
        }
    }

    /**
     * 增加关键词
     */
    private void addWord(String lineTxt){
        TrieNode tempNode=rootNode;
        // 循环每个字节
        for(int i=0;i<lineTxt.length();i++){
            Character c=lineTxt.charAt(i);
            // 过滤空格和符号
            if(isSymbol(c)){
                continue;
            }

            TrieNode node=tempNode.getSubNode(c);
            if(node==null){ // 没初始化
                node=new TrieNode();
                tempNode.addSubNode(c,node);
            }

            tempNode=node;
            if(i==lineTxt.length()-1){
                // 关键词结束，设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }


    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c){
        int ic=(int)c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E80||ic>0x9FFF);
    }

    private class TrieNode{
        /**
         * 是不是关键词的结尾<br>
         * true 关键词的终结； false 继续
         */
        private boolean end=false;
        /**
         * 当前节点下的所有子节点<br>
         * key下一个字符，value是对应的节点
         */
        private Map<Character,TrieNode> subNodes=new HashMap<Character,TrieNode>();

        /**
         * 向指定位置添加节点树
         */
        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        /**
         * 获取下个节点
         */
        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd(){
            return end;
        }

        void setKeywordEnd(boolean end){
            this.end=end;
        }
    }

    /**
     * 根节点
     */
    private  TrieNode rootNode=new TrieNode();

    /**
     * 过滤敏感词
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        StringBuilder result=new StringBuilder();
        TrieNode tempNode=rootNode;
        int begin=0; // 回滚数
        int position=0; // 当前比较的位置
        while(position<text.length()){
            char c=text.charAt(position);
            // 空格和符号直接跳过
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode=tempNode.getSubNode(c);
            // 当前位置的匹配结束
            if(tempNode==null){
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position=begin+1;
                begin=position;
                // 回到树初始节点
                tempNode=rootNode;
            }else if(tempNode.isKeywordEnd()){
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(DEFAULT_REPLACEMENT);
                position=position+1;
                begin=position;
                tempNode=rootNode;
            }else{
                position++;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    /*public static void main(String[] argv){
        SensitiveService s=new SensitiveService();
        s.addWord("色情");
        s.addWord("赌博");
        System.out.print(s.filter("hi  你▄好▄色▄情"));
    }*/
}
