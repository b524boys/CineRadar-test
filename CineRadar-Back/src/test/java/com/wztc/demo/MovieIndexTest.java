package com.wztc.demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.wztc.demo.config.ElasticsearchConfig;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ElasticsearchConfig.class) // 显式导入配置类
public class MovieIndexTest {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private GoodsService goodsService;

    @Test
    public void createIndex() throws IOException {
        CreateIndexResponse products = client.indices().create(c -> c.index("db_idx6"));
        System.out.println(products.acknowledged());
    }

    @Test
    public void testImportGoodsToElasticsearch() throws Exception {
        // 测试参数
        String indexName = "movies";
        int batchSize = 100;
        Goods goods = new Goods();

        // 获取所有商品数据
        List<Goods> allGoods = goodsService.select(goods);
        assertNotNull(allGoods, "Goods list should not be null");
        assertFalse(allGoods.isEmpty(), "Goods list should not be empty");

        // 执行批量导入
        long startTime = System.currentTimeMillis();
        int totalCount = allGoods.size();

        try {
            // 分批处理
            for (int i = 0; i < totalCount; i += batchSize) {
                int end = Math.min(i + batchSize, totalCount);
                List<Goods> batch = allGoods.subList(i, end);

                BulkRequest.Builder br = new BulkRequest.Builder();

                for (Goods good : batch) {
                    br.operations(op -> op
                            .index(idx -> idx
                                    .index(indexName)
                                    .id(good.getId().toString())
                                    .document(good))
                    );
                }

                executeBulkRequest(br);
            }

            long endTime = System.currentTimeMillis();
            System.out.printf("Successfully imported %d goods in %d ms%n",
                    totalCount, (endTime - startTime));

        } catch (IOException e) {
            fail("Import failed: " + e.getMessage());
        }
    }

    private void executeBulkRequest(BulkRequest.Builder br) throws IOException {
        BulkResponse response = client.bulk(br.build());
        if (response.errors()) {
            // 记录错误详情
            response.items().stream()
                    .filter(item -> item.error() != null)
                    .forEach(item ->
                            System.err.printf("Failed to index goods %s: %s%n",
                                    item.id(), item.error().reason()));

            fail("Bulk request contained errors");
        }
    }


}
