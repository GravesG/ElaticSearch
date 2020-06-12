package com.graves.gravesesjd.service;

import com.alibaba.fastjson.JSON;
import com.graves.gravesesjd.pojo.Content;
import com.graves.gravesesjd.utils.HtmlParseUtil;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Graves
 * @DESCRIPTION service
 * @create 2020/6/11
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //解析数据放入es索引中
    public Boolean parseContent(String keyword) throws IOException {
        List<Content> contents = new HtmlParseUtil().parseJD(keyword);
        // 将查询结果集放入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jd_goods")
                            .source(JSON.toJSONString(contents.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    // 2.获取顺实现搜索功能
    public List<Map<String,Object>> searchPage(String keyWord, int pageNo, int pageSize) throws IOException {
        if(pageNo <= 1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from();
        sourceBuilder.size();

        // 精准匹配
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyWord);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    // 高亮搜索
    public List<Map<String,Object>> searchHighlightBuilder(String keyWord, int pageNo, int pageSize) throws IOException {
        if(pageNo <= 1){
            pageNo = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from();
        sourceBuilder.size();

        // 精准匹配
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyWord);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);// 关闭多个高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // 解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(title != null){
                Text[] fragments = title.fragments();
                String nTitle = "";
                for (Text text : fragments){
                    nTitle += text;
                }
                sourceAsMap.put("title",nTitle);
            }

            list.add(sourceAsMap);

        }
        return list;
    }
}
