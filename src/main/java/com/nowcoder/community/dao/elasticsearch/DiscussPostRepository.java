package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: yaoyp
 * @Date: 2023/5/22 - 05 - 22 - 0:04
 * @Description: com.nowcoder.community.dao.elasticsearch
 * @version: 1.0
 */

// 注意这里不是 @Mapper 注解
// 父接口 ElasticsearchRepository 中已经事先定义好了对 ES 服务器访问的各种增删改查方法
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
