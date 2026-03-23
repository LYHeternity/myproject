package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.mapper.PostMapper;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.common.PageResult;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public PageResult<Post> getPostList(int page, int size, String category, String type, String keyword) {
        Page<Post> postPage = new Page<>(page, size);
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq("category", category);
        }
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword).or().like("content", keyword);
        }
        
        wrapper.eq("status", 1).orderByDesc("created_at");
        
        Page<Post> result = baseMapper.selectPage(postPage, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public Post getPostById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void incrementViewCount(Long postId) {
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            baseMapper.updateById(post);
        }
    }

    @Override
    public void updateLikeCount(Long postId) {
        // 实际实现中，应该根据like表的统计结果更新
        // 这里简化处理
    }

    @Override
    public void updateCommentCount(Long postId) {
        // 实际实现中，应该根据comment表的统计结果更新
        // 这里简化处理
    }

    @Override
    public void setBestAnswer(Long postId, Long commentId) {
        Post post = baseMapper.selectById(postId);
        if (post != null) {
            post.setBestAnswerId(commentId);
            baseMapper.updateById(post);
        }
    }
}