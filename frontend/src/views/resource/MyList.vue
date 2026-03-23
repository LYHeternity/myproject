<template>
  <el-card>
    <h3>我的服务</h3>
    <el-table v-loading="loading" :data="list" border>
      <el-table-column prop="title" label="服务名称" width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'online' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'info'">
            {{ scope.row.status === 'pending' ? '待审核' : scope.row.status === 'online' ? '已上线' : scope.row.status === 'rejected' ? '已拒绝' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button type="text" @click="$router.push('/service/' + scope.row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:12px" :current-page="page.current" :page-size="page.size" :total="page.total" layout="total, prev, pager, next" @current-change="p => { page.current = p; load() }" />
  </el-card>
</template>

<script>
import { listMy } from '@/api/resource'
export default {
  name: 'MyServices',
  data() { return { loading: false, list: [], page: { current: 1, size: 10, total: 0 } } },
  created() { this.load() },
  methods: {
    async load() {
      this.loading = true
      try {
        const res = await listMy({ current: this.page.current, size: this.page.size })
        this.list = res.records || []
        this.page.total = res.total || 0
      } finally { this.loading = false }
    }
  }
}
</script>
