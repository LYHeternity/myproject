package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneur.platform.entity.Project;
import com.entrepreneur.platform.entity.ProjectProgress;
import com.entrepreneur.platform.entity.ProjectTeamMember;
import com.entrepreneur.platform.mapper.ProjectMapper;
import com.entrepreneur.platform.mapper.ProjectProgressMapper;
import com.entrepreneur.platform.mapper.ProjectTeamMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 项目服务：发布、编辑、列表、详情、进展、上下架、点赞
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectProgressMapper projectProgressMapper;
    private final ProjectTeamMemberMapper projectTeamMemberMapper;

    @Transactional(rollbackFor = Exception.class)
    public Project publish(Project project) {
        project.setStatus("pending");
        project.setViewCount(0);
        project.setLikeCount(0);
        projectMapper.insert(project);
        return project;
    }

    public Project getById(Long id) {
        return projectMapper.selectById(id);
    }

    public void incViewCount(Long id) {
        Project p = projectMapper.selectById(id);
        if (p != null) {
            p.setViewCount(p.getViewCount() + 1);
            projectMapper.updateById(p);
        }
    }

    public IPage<Project> listMy(Page<Project> page, Long founderId, String status) {
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>().eq(Project::getFounderId, founderId);
        if (StringUtils.hasText(status)) q.eq(Project::getStatus, status);
        q.orderByDesc(Project::getCreateTime);
        return projectMapper.selectPage(page, q);
    }

    public IPage<Project> listPublic(Page<Project> page, String industry, String stage,
                                      BigDecimal fundingMin, BigDecimal fundingMax, String orderBy) {
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>().eq(Project::getStatus, "online");
        if (StringUtils.hasText(industry)) q.eq(Project::getIndustry, industry);
        if (StringUtils.hasText(stage)) q.eq(Project::getStage, stage);
        if (fundingMin != null) q.ge(Project::getFundingNeeded, fundingMin);
        if (fundingMax != null) q.le(Project::getFundingNeeded, fundingMax);
        if ("hot".equals(orderBy)) q.orderByDesc(Project::getLikeCount);
        else if ("view".equals(orderBy)) q.orderByDesc(Project::getViewCount);
        else q.orderByDesc(Project::getCreateTime);
        return projectMapper.selectPage(page, q);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Project project) {
        projectMapper.updateById(project);
    }

    public void setStatus(Long id, String status, String rejectReason) {
        Project p = projectMapper.selectById(id);
        if (p != null) {
            p.setStatus(status);
            if ("rejected".equals(status) && rejectReason != null) {
                // 这里可以添加保存拒绝原因的逻辑
                // 例如，p.setRejectReason(rejectReason);
            }
            projectMapper.updateById(p);
        }
    }

    public void setStatus(Long id, String status) {
        setStatus(id, status, null);
    }

    public void addProgress(Long projectId, String content, String attachments) {
        ProjectProgress pp = new ProjectProgress();
        pp.setProjectId(projectId);
        pp.setContent(content);
        pp.setAttachments(attachments);
        projectProgressMapper.insert(pp);
    }

    public List<ProjectProgress> listProgress(Long projectId) {
        return projectProgressMapper.selectList(
                new LambdaQueryWrapper<ProjectProgress>().eq(ProjectProgress::getProjectId, projectId)
                        .orderByDesc(ProjectProgress::getCreateTime));
    }

    public IPage<Project> listPending(Page<Project> page) {
        return projectMapper.selectPage(page,
                new LambdaQueryWrapper<Project>().eq(Project::getStatus, "pending").orderByDesc(Project::getCreateTime));
    }

    public IPage<Project> listAll(Page<Project> page, String keyword, String industry, String status) {
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            q.like(Project::getTitle, keyword).or().like(Project::getIndustry, keyword);
        }
        if (StringUtils.hasText(industry)) {
            q.eq(Project::getIndustry, industry);
        }
        if (StringUtils.hasText(status)) {
            q.eq(Project::getStatus, status);
        }
        q.orderByDesc(Project::getCreateTime);
        return projectMapper.selectPage(page, q);
    }

    public void removeById(Long id) {
        projectMapper.deleteById(id);
    }

    /**
     * 获取相关项目推荐
     * @param projectId 当前项目ID
     * @param limit 推荐数量
     * @return 相关项目列表
     */
    public List<Project> getRelatedProjects(Long projectId, int limit) {
        Project currentProject = projectMapper.selectById(projectId);
        if (currentProject == null) {
            return Collections.emptyList();
        }

        // 根据行业和阶段推荐相似项目，排除当前项目
        LambdaQueryWrapper<Project> q = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, "online")
                .ne(Project::getId, projectId);

        // 优先推荐同行业的项目
        if (StringUtils.hasText(currentProject.getIndustry())) {
            q.eq(Project::getIndustry, currentProject.getIndustry());
        }

        // 如果同行业项目不足，再推荐同阶段的项目
        if (StringUtils.hasText(currentProject.getStage())) {
            q.or().eq(Project::getStage, currentProject.getStage());
        }

        q.orderByDesc(Project::getLikeCount)
         .orderByDesc(Project::getViewCount)
         .orderByDesc(Project::getCreateTime)
         .last("LIMIT " + limit);

        return projectMapper.selectList(q);
    }

    /**
     * 添加项目团队成员
     * @param projectId 项目ID
     * @param name 成员姓名
     * @param role 成员角色
     * @param avatar 成员头像
     * @param bio 成员简介
     */
    public void addTeamMember(Long projectId, String name, String role, String avatar, String bio) {
        ProjectTeamMember member = new ProjectTeamMember();
        member.setProjectId(projectId);
        member.setName(name);
        member.setRole(role);
        member.setAvatar(avatar);
        member.setBio(bio);
        projectTeamMemberMapper.insert(member);
    }

    /**
     * 获取项目团队成员列表
     * @param projectId 项目ID
     * @return 团队成员列表
     */
    public List<ProjectTeamMember> listTeamMembers(Long projectId) {
        return projectTeamMemberMapper.selectList(
                new LambdaQueryWrapper<ProjectTeamMember>().eq(ProjectTeamMember::getProjectId, projectId)
                        .orderByAsc(ProjectTeamMember::getCreateTime));
    }

    /**
     * 删除项目团队成员
     * @param memberId 成员ID
     */
    public void removeTeamMember(Long memberId) {
        projectTeamMemberMapper.deleteById(memberId);
    }
}
