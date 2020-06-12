package com.graves.gravesesjd.controller;

import com.alibaba.fastjson.JSON;
import com.graves.gravesesjd.pojo.Content;
import com.graves.gravesesjd.service.ContentService;
import com.graves.gravesesjd.utils.HtmlParseUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Graves
 * @DESCRIPTION controller
 * @create 2020/6/11
 */
@RestController
public class ContentController {
    @Autowired
    private ContentService configService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword ) throws IOException {
        return configService.parseContent(keyword);
    }

    @GetMapping("/search/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("keyWord") String keyWord,
                                           @PathVariable("pageNo") int pageNo,
                                           @PathVariable("pageSize") int pageSize) throws IOException {
        return configService.searchHighlightBuilder(keyWord,pageNo,pageSize);
    }

    @GetMapping("/searchHighlightBuilder/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchHighlightBuilder(@PathVariable("keyWord") String keyWord,
                                           @PathVariable("pageNo") int pageNo,
                                           @PathVariable("pageSize") int pageSize) throws IOException {
        return configService.searchHighlightBuilder(keyWord,pageNo,pageSize);
    }
}
