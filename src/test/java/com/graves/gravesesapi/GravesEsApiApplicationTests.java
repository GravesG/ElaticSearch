package com.graves.gravesesapi;

import com.alibaba.fastjson.JSON;
import com.graves.gravesesapi.pojo.User;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GravesEsApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("graves_index");
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices()
                .create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    @Test
    void testGetIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("graves_index");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    void testDelIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("graves_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * @author Graves
     * @DESCRIPTION:
     * @params: 测试添加文档
     * @return: void
     * @Date: 2020/6/8 21:59
     * @Modified By:
     */
    @Test
    void testAddDocument() throws IOException {
        User user = new User("Graves", 3);

        IndexRequest request = new IndexRequest("graves_index");

        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        request.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());
    }

    /**
     * @author Graves
     * @DESCRIPTION:
     * @params: 判断文档是否存在
     * @return: void
     * @Date: 2020/6/8 22:08
     * @Modified By:
     */
    @Test
    void testIsExists() throws IOException {
        GetRequest request = new GetRequest("graves_index", "1");
        // 不获取返回的_source 的上下文了
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * @author Graves
     * @DESCRIPTION:
     * @params: 获取文档信息
     * @return: void
     * @Date: 2020/6/8 22:15
     * @Modified By:
     */
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("graves_index", "1");
        GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString()); //打印文档内容
        System.out.println(documentFields); //但会全部内容和命令是一致的
    }

    /**
     * \
     * 更新文档
     *
     * @throws IOException
     */
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("graves_index", "1");
        request.timeout("1s");

        User user = new User("GravesEs", 18);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }

    /**
     * @author Graves
     * @DESCRIPTION:
     * @params: 删除文档
     * @return: void
     * @Date: 2020/6/8 22:22
     * @Modified By:
     */
    @Test
    void testDelDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("graves_index", "1");
        request.timeout("1s");

        DeleteResponse delete = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    // 批处理
    @Test
    void testBulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("graves1", 3));
        userList.add(new User("graves2", 3));
        userList.add(new User("graves3", 3));
        userList.add(new User("graves4", 3));
        userList.add(new User("graves5", 3));

        // 批处理
        for (int i = 0; i < userList.size(); i++) {
            request.add(
                    new IndexRequest("graves_index")
                            .id("" + (i + 1))
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    // 查询
    @Test
    void  testSearch() throws IOException {
        SearchRequest request = new SearchRequest("graves_index");
        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "graves3");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        System.out.println("++++++++++++++++++++++++");
        for(SearchHit documentFields : response.getHits().getHits()){
            System.out.println(documentFields.getSourceAsString());
        }

    }
}
