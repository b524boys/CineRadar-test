package com.wztc.demo.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import com.wztc.demo.common.ElasticSearchResult;
import com.wztc.demo.entity.Category;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.exception.ServiceException;
import com.wztc.demo.mapper.CategoryMapper;
import com.wztc.demo.mapper.GoodsMapper;
import com.wztc.demo.util.DateUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电影 Service层
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ElasticsearchClient client;

    /**
     * ES索引名称
     */
    @Value("${elasticsearch.index}")
    private String index;


    /**
     * 根据主键查询
     */
    public Goods selectById(Integer id){
        return goodsMapper.selectById(id);
    }

    /**
     * 分页查询数据
     */
    public List<Goods> selectByPage(Goods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return goodsMapper.select(goods); // 执行分页查询
    }

    /**
     *  热门电影 (点击数)
     */
    public List<Goods> selectHotGoods() {
        List<Goods> lstGood = goodsMapper.selectHotGoods();
        return lstGood;
    }

    /**
     * 查询所有
     */
    public List<Goods> select(Goods goods) {
        return goodsMapper.select(goods); // 执行分页查询
    }


    /**
     * 新增数据
     */
    public void add(Goods goods) {
        Goods goodQuery = new Goods();
        goodQuery.setGoodsName(goods.getGoodsName());
        List<Goods> lstGood = goodsMapper.select(goodQuery);
        goodQuery = CollectionUtil.isEmpty(lstGood) ? null : lstGood.get(0);

        if(ObjectUtil.isNotEmpty(goodQuery)){
            throw new ServiceException("同名电影已经存在");
        }
        goods.setCreateTime(DateUtils.date2ShortString(new Date()));

        // 更新分类
        List<String> cateNameList = Arrays.asList(goods.getCateName().split(","));
        cateNameList.forEach(cateName->{
            Category category = new Category();
            category.setCateName(cateName);
            List<Category> lstCate = categoryMapper.select(category);
            if(CollectionUtil.isEmpty(lstCate)){
                categoryMapper.insert(category);
            }
        });
        goodsMapper.insert(goods);
        //对应处理ES数据库
        try {
            String Result = insertES(goodsMapper.selectByName(goods.getGoodsName()));
            System.out.println(Result);
        }catch (Exception e){
            System.out.println("插入ES失败");
        }
    }

    /**
     * 修改数据
     */
    public void update(Goods goods) {
        //电影不能重复
        if(ObjectUtil.isNotEmpty(goods.getGoodsName())){
            Goods goodsQuery = new Goods();
            goodsQuery.setGoodsName(goods.getGoodsName());
            List<Goods> lstGood = goodsMapper.select(goodsQuery);
            goodsQuery = CollectionUtil.isEmpty(lstGood) ? null : lstGood.get(0);

            if(ObjectUtil.isNotEmpty(goodsQuery) && goods.getId().intValue() != goodsQuery.getId().intValue()){
                throw new ServiceException("同名电影已经存在");
            }
        }


        // 更新分类
        List<String> cateNameList = Arrays.asList(goods.getCateName().split(","));
        cateNameList.forEach(cateName->{
            Category category = new Category();
            category.setCateName(cateName);
            List<Category> lstCate = categoryMapper.select(category);
            if(CollectionUtil.isEmpty(lstCate)){
                categoryMapper.insert(category);
            }
        });

        goodsMapper.update(goods);
        //对应处理ES数据库
        try {
            String Result = insertES(goods);
            System.out.println(Result);
        }catch (Exception e){
            System.out.println("更新ES文档失败");
        }
    }

    /**
     * 根据id删除数据
     */
    public void deleteById(Integer id) {
        goodsMapper.deleteById(id);
        //对应处理ES数据库
        try {
            String Result = deleteESById(String.valueOf(id));
            System.out.println(Result);
        }catch (Exception e){
            System.out.println("删除ES文档时发生错误");
        }
    }


    /**
     * 批量删除数据
     */
    public void deleteBatch(List<Integer> ids) {
        goodsMapper.deleteBatch(ids);
        //对应处理ES数据库
        try {
            bulkDeleteESByIds(ids);
            System.out.println("批量删除ES文档成功");
        }catch (Exception e){
            System.out.println("批量删除ES文档时发生错误");
        }
    }

    /**
     * Elasticsearch分页查询
     */
    public ElasticSearchResult<Goods> selectByPageES(Goods goods, Integer pageNum, Integer pageSize) throws IOException {

        // 参数校验
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        int from = (pageNum - 1) * pageSize;
        String searchKeyword = goods.getSearchGoodsName();
        String categoryName = goods.getCateName();
        String chineseOnly = searchKeyword.replaceAll("[^\\u4e00-\\u9fa5]", "");

        String[] searchKeywordterms = searchKeyword.split("\\s+"); // 按空格分割
        if (searchKeywordterms.length > 1) {
            return selectMultiMatchES(from, pageSize, searchKeywordterms, categoryName);
        }

        // 构建布尔查询的 should 子句
        Query goodsNamePhraseQuery = MatchPhraseQuery.of(m -> m
                .field("goodsName")
                .query(chineseOnly)
                .boost(20f)
        )._toQuery();

        Query goodsNamePinyinPhraseQuery = MatchPhraseQuery.of(m -> m
                .field("goodsName.pinyin")
                .query(searchKeyword)
                .boost(4f)
        )._toQuery();

        Query castsQuery = MatchQuery.of(m -> m
                .field("casts")
                .query(searchKeyword)
                .minimumShouldMatch("2")  // 设置minimum_should_match参数,至少符合2个分词条件
                .boost(1f)
        )._toQuery();

        Query directorQuery = MatchPhraseQuery.of(m -> m
                .field("director")
                .query(searchKeyword)
                .slop(1)
                .boost(1f)
        )._toQuery();

        Query yearQuery = TermQuery.of(m -> m
                .field("year")
                .value(searchKeyword)
                .boost(1f)
        )._toQuery();

        Query countryQuery = MatchPhraseQuery.of(m -> m
                .field("country")
                .query(searchKeyword)
                .boost(1f)
        )._toQuery();

        // 构建 Bool 查询
        BoolQuery boolQuery = null;
        if(categoryName.isEmpty()){
            if(searchKeyword.isEmpty()){
                return selectAllES(from, pageSize);
            }else{
                boolQuery = BoolQuery.of(b -> b
                        .should(goodsNamePhraseQuery)
                        .should(goodsNamePinyinPhraseQuery)
                        .should(castsQuery)
                        .should(directorQuery)
                        .should(yearQuery)
                        .should(countryQuery)
                );
            }
        }else{
            Query cateQuery = MatchPhraseQuery.of(m -> m
                    .field("cateName")
                    .query(categoryName)
            )._toQuery();

            if(searchKeyword.isEmpty()){
                boolQuery = BoolQuery.of(b -> b
                        .must(cateQuery)  // 筛选指定类型下的所有电影
                );
            }
            else{
                boolQuery = BoolQuery.of(b -> b
                        .must(cateQuery)  // 必须满足的分类条件
                        .should(goodsNamePhraseQuery)  // 至少以下一个条件
                        .should(goodsNamePinyinPhraseQuery)
                        .should(castsQuery)
                        .should(directorQuery)
                        .should(yearQuery)
                        .should(countryQuery)
                        .minimumShouldMatch("1")  // 至少满足一个should条件
                );
            }
        }

        // 构建高亮配置
        Highlight highlight = Highlight.of(h -> h
                .fields("goodsName.pinyin", f -> f
                        .preTags("<span style='color:#ff9900'>")
                        .postTags("</span>")
                        .numberOfFragments(0)
                )
                .fields("goodsName", f -> f
                        .preTags("<span style='color:#ff9900'>")
                        .postTags("</span>")
                        .numberOfFragments(0)
                )
        );

        // 构建完整请求
        BoolQuery finalBoolQuery = boolQuery;
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index) // 替换为实际的索引名
                .from(from)
                .size(pageSize)
                .query(q -> q
                        .bool(finalBoolQuery)
                )
                .highlight(highlight)
        );
        // 执行查询
        SearchResponse<Goods> response = client.search(searchRequest, Goods.class);
        // 处理结果
        // 获取总数
        long totalHits = response.hits().total() != null ?
                response.hits().total().value() : 0;
        // 获取结果列表
        List<Goods> goodsList = response.hits().hits().stream()
                .map(hit -> {
                    Goods source = hit.source();
                    // 处理高亮结果 - 优先使用pinyin高亮，其次使用goodsName
                    if (hit.highlight() != null) {
                        if (hit.highlight().containsKey("goodsName.pinyin")) {
                            List<String> pinyinHighlights = hit.highlight().get("goodsName.pinyin");
                            if (!pinyinHighlights.isEmpty()) {
                                source.setGoodsName(pinyinHighlights.get(0));
                            }
                        } else if (hit.highlight().containsKey("goodsName")) {
                            List<String> nameHighlights = hit.highlight().get("goodsName");
                            if (!nameHighlights.isEmpty()) {
                                source.setGoodsName(nameHighlights.get(0));
                            }
                        }
                    }
                    return source;
                })
                .collect(Collectors.toList());

        return new ElasticSearchResult<>(totalHits, goodsList);

    }

    /**
     * ES新增数据
     */
    public String insertES(Goods goods) throws IOException {

        // 构建索引请求并指定ID
        IndexRequest<Goods> request = IndexRequest.of(i -> i
                .index(index)
                .id(goods.getId().toString())
                .document(goods)
        );

        // 执行插入操作
        IndexResponse response = client.index(request);

        // 返回操作结果信息
        return String.format("ES文档插入/更新成功，ID: %s, 索引: %s, 版本: %d",
                response.id(),
                response.index(),
                response.version());

    }

    /**
     * ES安全删除方法（带存在性检查）
     * @param id 要删除的文档ID
     * @return 删除结果信息
     * @throws IOException 如果发生I/O错误
     */
    public String deleteESById(String id) throws IOException {

            // 先检查文档是否存在
            boolean exists = client.exists(e -> e
                    .index(index)
                    .id(id)
            ).value();

            if (!exists) {
                return "ES文档不存在，无需删除";
            }else{
                //构建ES删除请求
                DeleteRequest request = DeleteRequest.of(d -> d
                        .index(index)
                        .id(id)
                );
                DeleteResponse response = client.delete(request);
                return String.format("ES文档删除成功%s，ID: %s, 索引: %s, 版本: %d",
                        response.result().jsonValue(), // 返回 deleted 或 not_found
                        response.id(),
                        response.index(),
                        response.version());
            }

    }

    /**
     * 批量删除ES文档
     * @param ids 要删除的文档ID列表（Integer类型）
     * @throws IOException 如果发生I/O错误
     */
    public void bulkDeleteESByIds(List<Integer> ids) throws IOException {
        // 分批处理防止请求过大
        for (int i = 0; i < ids.size(); i += 50) {
            List<Integer> batchIds = ids.subList(i, Math.min(i + 50, ids.size()));
            BulkRequest.Builder br = new BulkRequest.Builder();
            batchIds.forEach(id ->
                    br.operations(op -> op
                            .delete(d -> d
                                    .index(index)
                                    .id(String.valueOf(id)) // 将Integer转为String
                            )
                    )
            );
            client.bulk(br.build()); // 执行批量操作
        }
    }

    /**
     * 查询全部ES文档（按ID倒序排列）
     *
     */
    public ElasticSearchResult<Goods> selectAllES(Integer from, Integer pageSize) throws IOException {
        SortOptions sortById = SortOptions.of(s -> s
                .field(f -> f
                        .field("id").order(SortOrder.Desc)
                )
        );
        // 构建完整请求
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index) // 替换为实际的索引名
                .from(from)
                .size(pageSize)
                .query(q -> q
                        .matchAll(m -> m)      // 匹配所有文档
                )
                .sort(sortById)
        );
        // 执行查询
        SearchResponse<Goods> response = client.search(searchRequest, Goods.class);
        // 获取总数
        long totalHits = response.hits().total() != null ?
                response.hits().total().value() : 0;
        // 处理结果
        List<Goods> goodsList = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());

        return new ElasticSearchResult<>(totalHits, goodsList);
    }

    /**
     * ES文档 多条件并列查询
     *
     */
    public ElasticSearchResult<Goods> selectMultiMatchES(Integer from, Integer pageSize, String[] terms, String categoryName) throws IOException {
        // 创建BoolQuery.Builder
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 为每个词项添加must条件
        for (String term : terms) {
            boolBuilder.must(m -> m
                    .multiMatch(mm -> mm
                            .query(term)
                            .fields("goodsName", "casts", "director", "year", "country") // 指定具体字段
                            .type(TextQueryType.BestFields) // 使用匹配度最高的单个字段的得分
                            .tieBreaker(0.3) // 适当考虑其他匹配字段
                    )
            );
        }

        // 如果分类存在，增加分类
        if (!categoryName.isEmpty()) {
            boolBuilder.must(q -> q
                    .matchPhrase(m -> m
                            .field("cateName")
                            .query(categoryName)
                    )
            );
        }

        // 构建高亮配置
        Highlight highlight = Highlight.of(h -> h
                .fields("goodsName.pinyin", f -> f
                        .preTags("<span style='color:#ff9900'>")
                        .postTags("</span>")
                        .numberOfFragments(0)
                )
                .fields("goodsName", f -> f
                        .preTags("<span style='color:#ff9900'>")
                        .postTags("</span>")
                        .numberOfFragments(0)
                )
        );

        // 构建完整查询
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(index) // 替换为实际的索引名
                .from(from)
                .size(pageSize)
                .query(q -> q.bool(boolBuilder.build()))
                .highlight(highlight)
        );
        // 执行查询
        SearchResponse<Goods> response = client.search(searchRequest, Goods.class);
        // 处理结果
        // 获取总数
        long totalHits = response.hits().total() != null ?
                response.hits().total().value() : 0;
        // 获取结果列表
        List<Goods> goodsList = response.hits().hits().stream()
                .map(hit -> {
                    Goods source = hit.source();
                    // 处理高亮结果 - 优先使用pinyin高亮，其次使用goodsName
                    if (hit.highlight() != null) {
                        if (hit.highlight().containsKey("goodsName.pinyin")) {
                            List<String> pinyinHighlights = hit.highlight().get("goodsName.pinyin");
                            if (!pinyinHighlights.isEmpty()) {
                                source.setGoodsName(pinyinHighlights.get(0));
                            }
                        } else if (hit.highlight().containsKey("goodsName")) {
                            List<String> nameHighlights = hit.highlight().get("goodsName");
                            if (!nameHighlights.isEmpty()) {
                                source.setGoodsName(nameHighlights.get(0));
                            }
                        }
                    }
                    return source;
                })
                .collect(Collectors.toList());

        System.out.println("多重条件查询");
        return new ElasticSearchResult<>(totalHits, goodsList);
    }

}
