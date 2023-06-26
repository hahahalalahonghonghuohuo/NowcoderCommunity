package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: yaoyp
 * @Date: 2023/5/22 - 05 - 22 - 0:08
 * @Description: com.nowcoder.community
 * @version: 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    /**
     * 首先分析增删改查功能
     */

    @Test
    // 每次执行 save 方法则插入一条数据
    public void testInsert() {
        // 需要注意，这里能够运行的前提是 ES 的服务已经启动好了
        discussRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    // 能够插入多条数据
    public void testInsertList() {
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100, 0));
        discussRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100, 0));
    }

    @Test
    // 修改数据
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水.");
        discussRepository.save(post);
    }

    @Test
    // 删除数据
    public void testDelete() {
        // 这里注意尽量不要一次删除所有数据，这种操作非常危险
        discussRepository.deleteById(231);
    }

    /**
     * 然后分析搜索功能
     */
    @Test
    // 运用 Repository 接口实现搜索功能
    public void testSearchByRepository() {
        // 首先构造搜索条件
        // 注意，搜索结果中匹配的词需要变红
        // 实现原理：ES 可以把匹配到的词前后加一个标签

        // 下面这段代码通常可以直接复用
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // Repository 的底层是调用了上述的 Template 的方法去查询数据的，查到的数据由 Mapper 进行处理
        // 如果想把两份数据组装到一起，需要用 Mapper 进行处理
        // 底层获取到了高亮显示的值，但是没有做进一步的返回
        Page<DiscussPost> page = discussRepository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }

                // 若不是小于等于零，则说明命中了数据，就需要进行进一步的处理
                // 封装到一个集合中并返回
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    // ES 在传入日期的时候，最终要把字符串转成 Long 类型的数来传递(String createTime 是一个 Long 格式的字符串)
                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    // 评论的数量
                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    // 内容也做类似的处理
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    // 处理完了之后把 post 放入集合中
                    list.add(post);

                }
                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }
        });

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }



}
