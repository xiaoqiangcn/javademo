package com.johhny.java.demo.httpClient;

import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * Project: htmlCilent File Created at 2012-7-11下午5:07:05 $Id$ Copyright 1999-2012 Alibaba.com Corporation Limited. All
 * rights reserved. This software is the confidential and proprietary information of Alibaba Company.
 * ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Alibaba.com.
 */

/**
 * 类 HtmlParserTool 的实现描述：TODO 类实现描述
 * 
 * @author wb_zhiqiang.xiezq 2012-7-11下午5:07:05
 */
public class HtmlParse_EextracLinks {

    public static Set<String> extracLinks(String url, LinkFilter filter) {

        Set<String> links = new HashSet<String>();
        try {
            Parser parser = new Parser(url);
            parser.setEncoding("gb2312");
            // 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
            NodeFilter frameFilter = new NodeFilter() {

                public boolean accept(Node node) {
                    if (node.getText().startsWith("frame src=")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            // OrFilter 来设置过滤 <a> 标签，和 <frame> 标签
            OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
            // 得到所有经过过滤的标签
            NodeList list = parser.extractAllNodesThatMatch(linkFilter);
            for (int i = 0; i < list.size(); i++) {
                Node tag = list.elementAt(i);
                if (tag instanceof LinkTag)// <a> 标签
                {
                    LinkTag link = (LinkTag) tag;
                    String linkUrl = link.getLink();// url
                    if (filter.accept(linkUrl)) links.add(linkUrl);
                } else// <frame> 标签
                {
                    // 提取 frame 里 src 属性的链接如 <frame src="test.html"/>
                    String frame = tag.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);
                    int end = frame.indexOf(" ");
                    if (end == -1) end = frame.indexOf(">");
                    String frameUrl = frame.substring(5, end - 1);
                    if (filter.accept(frameUrl)) links.add(frameUrl);
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return links;
    }

    // 测试的 main 方法
    public static void main(String[] args) {
        Set<String> links = HtmlParse_EextracLinks.extracLinks("http://www.baidu.com", new LinkFilter() {

            // 提取以 http://www.twt.edu.cn 开头的链接
            public boolean accept(String url) {
                if (url.startsWith("http://www.baidu.com")) return true;
                else return false;
            }

        });
        for (String link : links)
            System.out.println(link);
    }
}

interface LinkFilter {

    public boolean accept(String url);
}
