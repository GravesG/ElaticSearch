package com.graves.gravesesapi;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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
    void testDelIndex() throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest("graves_index");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }
}
