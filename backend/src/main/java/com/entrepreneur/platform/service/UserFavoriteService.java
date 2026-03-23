package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.Resource;
import com.entrepreneur.platform.entity.UserFavorite;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ResourceMapper;
import com.entrepreneur.platform.mapper.UserFavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏与点赞（通过收藏表或消息表实现；此处用收藏表，点赞用 project.like_count 递增 + 防重复需单独表，简化为仅收藏）
 */
@Service
@RequiredArgsConstructor
public class UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;

    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long userId, String targetType, Long targetId) {
        UserFavorite existing = userFavoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, targetType).eq(UserFavorite::getTargetId, targetId));
        if (existing != null) {
            userFavoriteMapper.deleteById(existing.getId());
            if ("project".equals(targetType)) {
                Project p = projectMapper.selectById(targetId);
                if (p != null && p.getLikeCount() > 0) p.setLikeCount(p.getLikeCount() - 1);
                if (p != null) projectMapper.updateById(p);
            } else if ("resource".equals(targetType)) {
                Resource r = resourceMapper.selectById(targetId);
                if (r != null && r.getFavoriteCount() > 0) r.setFavoriteCount(r.getFavoriteCount() - 1);
                if (r != null) resourceMapper.updateById(r);
            }
        } else {
            UserFavorite uf = new UserFavorite();
            uf.setUserId(userId);
            uf.setTargetType(targetType);
            uf.setTargetId(targetId);
            userFavoriteMapper.insert(uf);
            if ("project".equals(targetType)) {
                Project p = projectMapper.selectById(targetId);
                if (p != null) {
                    p.setLikeCount(p.getLikeCount() + 1);
                    projectMapper.updateById(p);
                }
            } else if ("resource".equals(targetType)) {
                Resource r = resourceMapper.selectById(targetId);
                if (r != null) {
                    r.setFavoriteCount(r.getFavoriteCount() + 1);
                    resourceMapper.updateById(r);
                }
            }
        }
    }

    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        return userFavoriteMapper.selectCount(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, targetType).eq(UserFavorite::getTargetId, targetId)) > 0;
    }

    public List<Long> myFavoriteProjectIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "project"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Project> myFavoriteProjects(Long userId) {
        List<Long> projectIds = myFavoriteProjectIds(userId);
        if (projectIds.isEmpty()) return Collections.emptyList();
        return projectMapper.selectBatchIds(projectIds);
    }

    public List<Long> myFavoriteResourceIds(Long userId) {
        return userFavoriteMapper.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getTargetType, "resource"))
                .stream().map(UserFavorite::getTargetId).collect(Collectors.toList());
    }

    public List<Resource> myFavoriteResources(Long userId) {
        List<Long> resourceIds = myFavoriteResourceIds(userId);
        if (resourceIds.isEmpty()) return Collections.emptyList();
        return resourceMapper.selectBatchIds(resourceIds);
    }
}
