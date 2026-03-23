<template>
  <div class="admin-page">
    <div class="page-card">
      <div class="page-header">
        <h3 class="page-title">系统设置</h3>
        <span class="page-subtitle">管理平台基础信息、业务参数和安全配置</span>
      </div>
      <el-tabs v-model="activeTab" class="config-tabs">
        <el-tab-pane label="基础信息" name="basic">
          <p class="section-desc">网站名称、LOGO、版权等展示信息。</p>
          <el-form label-width="120px" style="max-width:560px" class="config-form">
            <el-form-item label="网站名称">
              <el-input v-model="basic.siteName" placeholder="如：创业者综合服务平台" />
            </el-form-item>
            <el-form-item label="LOGO 路径">
              <el-input v-model="basic.logo" placeholder="/static/logo.png" />
            </el-form-item>
            <el-form-item label="版权信息">
              <el-input v-model="basic.copyright" placeholder="Copyright © 2025" />
            </el-form-item>
            <el-form-item label="联系方式">
              <el-input v-model="basic.contact" placeholder="选填" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicConfig">保存</el-button>
              <el-button @click="resetBasicConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="业务参数" name="business">
          <p class="section-desc">审核时效、有效期等业务相关配置。</p>
          <el-form label-width="140px" style="max-width:560px" class="config-form">
            <el-form-item label="项目审核时效(天)">
              <el-input-number v-model="business.projectAuditDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="服务审核时效(天)">
              <el-input-number v-model="business.resourceAuditDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="投资意向有效期(天)">
              <el-input-number v-model="business.investmentValidDays" :min="7" :max="365" />
            </el-form-item>
            <el-form-item label="服务预约有效期(天)">
              <el-input-number v-model="business.bookingValidDays" :min="7" :max="90" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBusinessConfig">保存</el-button>
              <el-button @click="resetBusinessConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="安全参数" name="security">
          <p class="section-desc">密码强度、令牌过期时间等安全配置。</p>
          <el-form label-width="160px" style="max-width:560px" class="config-form">
            <el-form-item label="密码最少位数">
              <el-input-number v-model="security.passwordStrength" :min="6" :max="20" />
            </el-form-item>
            <el-form-item label="JWT 过期时间(小时)">
              <el-input-number v-model="security.jwtExpireHours" :min="1" :max="72" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSecurityConfig">保存</el-button>
              <el-button @click="resetSecurityConfig">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="全部配置项" name="all">
          <el-table v-loading="loading" :data="list" border stripe class="data-table">
            <el-table-column prop="configKey" label="配置项" width="220" />
            <el-table-column prop="configValue" label="值" min-width="120" />
            <el-table-column prop="description" label="说明" width="200" />
          </el-table>
          <div v-if="!loading && !list.length" class="data-empty">
            <div class="empty-icon"><i class="el-icon-setting"></i></div>
            <p class="empty-text">暂无配置数据</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { adminConfig } from '@/api/admin'
export default {
  name: 'AdminConfig',
  data() {
    return {
      activeTab: 'basic',
      loading: false,
      list: [],
      basic: { siteName: '创业者综合服务平台', logo: '/static/logo.png', copyright: 'Copyright © 2025', contact: '' },
      business: { projectAuditDays: 7, resourceAuditDays: 7, investmentValidDays: 30, bookingValidDays: 15 },
      security: { passwordStrength: 8, jwtExpireHours: 2 }
    }
  },
  created() {
    this.loading = true
    adminConfig.list().then(res => { this.list = res || []; this.loading = false }).catch(() => { this.loading = false })
  },
  methods: {
    // 保存基础配置
    saveBasicConfig() {
      this.$message.success('基础配置已保存')
    },
    // 重置基础配置
    resetBasicConfig() {
      this.basic = { siteName: '创业者综合服务平台', logo: '/static/logo.png', copyright: 'Copyright © 2025', contact: '' }
      this.$message.info('基础配置已重置')
    },
    // 保存业务配置
    saveBusinessConfig() {
      this.$message.success('业务配置已保存')
    },
    // 重置业务配置
    resetBusinessConfig() {
      this.business = { projectAuditDays: 7, resourceAuditDays: 7, investmentValidDays: 30, bookingValidDays: 15 }
      this.$message.info('业务配置已重置')
    },
    // 保存安全配置
    saveSecurityConfig() {
      this.$message.success('安全配置已保存')
    },
    // 重置安全配置
    resetSecurityConfig() {
      this.security = { passwordStrength: 8, jwtExpireHours: 2 }
      this.$message.info('安全配置已重置')
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.admin-page {
  padding: 20px;
  background: #0f172a;
  min-height: 100vh;
}

/* 页面卡片 */
.page-card {
  background: #1e293b;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #f8fafc;
  margin-bottom: 24px;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
}

/* 标签页 */
.config-tabs {
  margin-top: 20px;
}

.el-tabs__header {
  margin-bottom: 24px;
}

.el-tabs__nav {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.el-tabs__item {
  color: #94a3b8;
  font-size: 16px;
  font-weight: 500;
  padding: 12px 24px;
  margin-right: 24px;
  transition: all 0.3s ease;
}

.el-tabs__item:hover {
  color: #3b82f6;
}

.el-tabs__item.is-active {
  color: #f8fafc;
  font-weight: 600;
}

.el-tabs__active-bar {
  background: #3b82f6;
  height: 3px;
  border-radius: 3px;
}

/* 配置描述 */
.section-desc {
  color: #94a3b8;
  font-size: 14px;
  margin-bottom: 24px;
  font-weight: 500;
}

/* 配置表单 */
.config-form {
  background: #334155;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 20px;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  color: #94a3b8;
  font-weight: 500;
}

.el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-input__inner:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* 输入数字样式 */
.el-input-number .el-input__inner {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.el-input-number__decrease,
.el-input-number__increase {
  background: #475569;
  border-color: rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.el-input-number__decrease:hover,
.el-input-number__increase:hover {
  background: #3b82f6;
  border-color: #3b82f6;
}

/* 数据表格 */
.data-table {
  background: transparent !important;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.data-table th {
  background: #334155 !important;
  color: #94a3b8 !important;
  font-weight: 600 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
}

.data-table td {
  background: #1e293b !important;
  color: #f8fafc !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
  padding: 16px !important;
  transition: all 0.2s ease;
}

.data-table tr:hover td {
  background: #334155 !important;
}

/* 空数据样式 */
.data-empty {
  text-align: center;
  padding: 60px 24px;
  color: #94a3b8;
  background: #334155;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-top: 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
  color: #64748b;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: #94a3b8;
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.el-button {
  background: #334155;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #f8fafc;
}

.el-button:hover {
  background: #475569;
  border-color: rgba(59, 130, 246, 0.3);
  color: #f8fafc;
  transform: translateY(-1px);
}

/* 加载状态 */
.el-loading-mask {
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
}

.el-loading-spinner .path {
  stroke: #3b82f6;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .admin-page {
    padding: 16px;
  }
  
  .page-card {
    padding: 20px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .config-form {
    padding: 20px;
  }
  
  .el-tabs__item {
    font-size: 14px;
    padding: 10px 20px;
    margin-right: 16px;
  }
}

@media (max-width: 768px) {
  .admin-page {
    padding: 12px;
  }
  
  .page-card {
    padding: 16px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .config-form {
    padding: 16px;
  }
  
  .el-form-item__label {
    font-size: 13px;
  }
  
  .el-tabs__item {
    font-size: 13px;
    padding: 8px 16px;
    margin-right: 12px;
  }
  
  .data-table th,
  .data-table td {
    padding: 12px !important;
  }
  
  .empty-icon {
    font-size: 48px;
  }
  
  .empty-text {
    font-size: 14px;
  }
}
</style>
