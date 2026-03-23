package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Post;
import com.entrepreneur.platform.service.PostService;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 社区控制器
 */
@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public Result getPostList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) String keyword) {
        return Result.ok(postService.getPostList(page, size, category, type, keyword));
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result getPostDetail(@PathVariable Long id) {
        postService.incrementViewCount(id);
        return Result.ok(postService.getPostById(id));
    }

    /**
     * 发布帖子
     */
    @PostMapping("/posts")
    public Result createPost(@RequestBody Post post) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        post.setUserId(userId);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1); // 直接发布，实际项目中可能需要审核
        postService.save(post);
        return Result.ok("发布成功");
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public Result updatePost(@PathVariable Long id, @RequestBody Post post) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Post existingPost = postService.getPostById(id);
        if (existingPost == null) {
            return Result.fail("帖子不存在");
        }
        if (!existingPost.getUserId().equals(userId)) {
            return Result.fail("无权修改");
        }
        post.setId(id);
        post.setUserId(userId);
        postService.updateById(post);
        return Result.ok("更新成功");
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{id}")
    public Result deletePost(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Post post = postService.getPostById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Result.fail("无权删除");
        }
        postService.removeById(id);
        return Result.ok("删除成功");
    }

    /**
     * 设置最佳答案
     */
    @PutMapping("/posts/{id}/best-answer")
    public Result setBestAnswer(@PathVariable Long id, @RequestParam Long commentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) return Result.fail(401, "未登录");
        Post post = postService.getPostById(id);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            return Result.fail("只有作者可以设置最佳答案");
        }
        postService.setBestAnswer(id, commentId);
        return Result.ok("设置成功");
    }
}