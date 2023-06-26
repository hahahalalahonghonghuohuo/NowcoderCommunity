package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;


/**
 * @Author: yaoyp
 * @Date: 2023/5/24 - 05 - 24 - 18:40
 * @Description: com.nowcoder.community.controller
 * @version: 1.0
 */

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
            elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 先将帖子传进去
                map.put("post", post);
                // 然后传入帖子的作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 然后传入点赞的数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 设置分页信息从而实现分页功能
        page.setPath("/search?keyword=" + keyword);
        // 设置一共有多少条数据，从而后续计算总的页数
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        // 返回模板
        return "/site/search";
    }

}
