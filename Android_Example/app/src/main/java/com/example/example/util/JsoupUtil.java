package com.example.example.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;

/**
 * 用jsoup 解析html 所有标签,目前只获取标签中的 超链接
 */
public class JsoupUtil {
    public static String createHtml(String html, int size) {
        Document doc = Jsoup.parse(html);
        if (doc.text().length() < size) {
            return html;
        }

        Element body = doc.body();
        StringBuffer sb = new StringBuffer();
        if (body.childNodeSize()> 0){
            List<Node> nodes = body.childNodes();
            for (Node node : nodes) {
                int length = Jsoup.parse(sb.toString()).text().length();
                if (node.nodeName().equals("#text")) {
                    String s = node.attributes().get("#text");
                    sb.append(s);
                } else {
                    String node1 = createNode(node, length, size);
                    sb.append(node1);
                }

                if (length > size) {
                    break;
                }
            }
        }

        return sb.toString();

    }

    /**
     * 解析标签, 然后将标签中的内容获取处理, 再重新生成 html 文本
     * @param node
     * @param l
     * @param maxSize
     * @return
     */
    private static String createNode(Node node, int l, int maxSize) {
        int length = l;
        StringBuffer sb = new StringBuffer();
        String start = "<" + node.nodeName();
        sb.append(start);
        Attributes attributes = node.attributes();
        List<Attribute> attributes1 = attributes.asList();
        for (Attribute attr : attributes1) {
            sb.append(" ");
            sb.append(attr.getKey());
            sb.append("=");
            String value =attr.getValue();
            if (attr.getKey().equals("href")){
                value+= "\""+value+"\"";
            }
            sb.append(value);
        }
        sb.append(">");
        if (node.childNodeSize() > 0){
            List<Node> nodes1 = node.childNodes();
            for (Node chNode : nodes1) {
                if (chNode.nodeName().equals("#text")) {
                    String sub = chNode.attributes().get("#text");
                    sb.append(sub);
                    length += sub.length();
                    if (length > maxSize) {
                        break;
                    }
                } else {
                    String node1 = createNode(chNode, length, maxSize);
                    sb.append(node1);
                }
            }
        }

        String end = "</" + node.nodeName() + ">";
        sb.append(end);

        return sb.toString();
    }
}
