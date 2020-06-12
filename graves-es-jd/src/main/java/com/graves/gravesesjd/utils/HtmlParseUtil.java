package com.graves.gravesesjd.utils;

import com.graves.gravesesjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Graves
 * @DESCRIPTION 解析网页
 * @create 2020/6/11
 */
public class HtmlParseUtil {

    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
    }

    public List<Content> parseJD(String keyword) throws IOException {
        String url = "https://search.jd.com/Search?keyword="+keyword;
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        ArrayList<Content> goodList = new ArrayList<>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodList.add(content);
        }
        return goodList;
    }

//    public static void main(String[] args) throws IOException {
//        // 获取请求  https://search.jd.com/Search?keyword=java
//        // 前提需要联网
//
//        String url = "https://search.jd.com/Search?keyword=java";
//
//        //解析网页
//        Document document = Jsoup.parse(new URL(url), 30000);
//        Element element = document.getElementById("J_goodsList");
//        // 获取所有的li
//        Elements elements = element.getElementsByTag("li");
//        // 这里的el就是每一个li标签
//        for (Element el : elements) {
//            String img = el.getElementsByTag("img").eq(0).attr("src");
//            String price = el.getElementsByClass("p-price").eq(0).text();
//            String title = el.getElementsByClass("p-name").eq(0).text();
//
//            System.out.println("++++++++++++++++++++");
//            System.out.println(img);
//            System.out.println(price);
//            System.out.println(title);
//        }
//    }
}
