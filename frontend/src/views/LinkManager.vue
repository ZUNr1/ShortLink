<template>
  <div class="link-manage">
    <!-- 创建短链卡片 -->
    <el-card class="create-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>创建短链接</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="长链接" prop="url">
          <el-input
              v-model="form.url"
              placeholder="请输入长链接，例如：https://example.com/very/long/url"
              clearable
          />
        </el-form-item>

        <el-form-item label="短链名称" prop="name">
          <el-input
              v-model="form.name"
              placeholder="请输入短链名称，方便管理"
              clearable
          />
        </el-form-item>

        <el-form-item label="过期时间">
          <el-date-picker
              v-model="form.expire"
              type="datetime"
              placeholder="选择过期时间（可选）"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
              :disabled-date="disabledDate"
              clearable
          />
          <span class="tip-text">不设置则永久有效</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleCreate" :loading="createLoading">
            生成短链接
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 我的短链列表 -->
    <el-card class="list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的短链列表</span>
          <el-button type="primary" link @click="loadUserLinks" :loading="listLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="linkList" stripe v-loading="listLoading" style="width: 100%">
        <el-table-column prop="name" label="名称" width="150" />
        <el-table-column prop="shortCode" label="短码" width="280">
          <template #default="{ row }">
            <el-tag>{{ row.shortCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="longUrl" label="长链接" min-width="250" show-overflow-tooltip />
        <el-table-column prop="clickCount" label="点击次数" width="100" align="center">
          <template #default="{ row }">
            <span class="click-count">{{ row.clickCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expireAt" label="过期时间" width="180">
          <template #default="{ row }">
            <span :class="{ 'expired': row.expireAt && new Date(row.expireAt) < new Date() }">
              {{ formatExpireTime(row.expireAt) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="copyShortLink(row)">
              <el-icon><CopyDocument /></el-icon>
              复制链接
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查询短链卡片 -->
    <el-card class="query-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>查询短链</span>
        </div>
      </template>

      <div class="query-form">
        <el-input
            v-model="shortCode"
            placeholder="请输入短码，例如：abc123"
            clearable
            style="flex: 1"
            @keyup.enter="handleQuery"
        />
        <el-button type="primary" @click="handleQuery" :loading="queryLoading">
          查询
        </el-button>
      </div>
    </el-card>

    <!-- 短链详情卡片 -->
    <el-card v-if="linkDetail" class="detail-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>短链详情</span>
          <el-button type="primary" link @click="copyShortUrl">
            <el-icon><CopyDocument /></el-icon>
            复制短链
          </el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="短链名称">
          {{ linkDetail.name || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="短码">
          <el-tag>{{ linkDetail.shortCode }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="短链接">
          <el-link :href="shortUrl" target="_blank" type="primary">
            {{ shortUrl }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="长链接">
          <el-link :href="linkDetail.longUrl" target="_blank" type="primary" :underline="false">
            {{ linkDetail.longUrl }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="点击次数">
          <span class="click-count">{{ linkDetail.clickCount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="过期时间">
          <span :class="{ 'expired': isExpired }">
            {{ formatExpireTime(linkDetail.expireAt) }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDate(linkDetail.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ formatDate(linkDetail.updatedAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted, computed} from 'vue'
import {ElMessage, type FormInstance, type FormRules} from 'element-plus'
import { CopyDocument, Refresh } from '@element-plus/icons-vue'
import type {LinkResponse} from "../types";
import {linkApi} from "../api/link.ts";



const formRef = ref<FormInstance>()
const createLoading = ref(false)
const queryLoading = ref(false)
const shortCode = ref('')
const linkDetail = ref<LinkResponse | null>(null)

// 列表相关
const linkList = ref<LinkResponse[]>([])
const listLoading = ref(false)

const form = reactive({
  url: '',
  name: '',
  expire: ''
})

const rules: FormRules = {
  url: [
    { required: true, message: '请输入长链接', trigger: 'blur' },
    {
      pattern: /^https?:\/\/.+/,
      message: '请输入正确的URL格式（以http://或https://开头）',
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入短链名称', trigger: 'blur' },
    { max: 128, message: '名称不能超过128个字符', trigger: 'blur' }
  ]
}

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now()
}

const shortUrl = computed(() => {
  if (!linkDetail.value) return ''
  const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${backendUrl}/api/link/${linkDetail.value.shortCode}`
})

const isExpired = computed(() => {
  if (!linkDetail.value?.expireAt) return false
  return new Date(linkDetail.value.expireAt) < new Date()
})

const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const formatExpireTime = (expireAt: string | null) => {
  if (!expireAt) return '永久有效'
  if (new Date(expireAt) < new Date()) return `已过期 ${formatDate(expireAt)}`
  return formatDate(expireAt)
}

// 加载用户所有短链
const loadUserLinks = async () => {
  listLoading.value = true
  try {
    const res = await linkApi.getUserLinks()
    linkList.value = res
  } catch (error) {
    console.error('加载列表失败', error)
  } finally {
    listLoading.value = false
  }
}

// 复制短链接
const copyShortLink = async (link: LinkResponse) => {
  const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const shortUrl = `${backendUrl}/api/link/${link.shortCode}`
  try {
    await navigator.clipboard.writeText(shortUrl)
    ElMessage.success('复制成功')
  } catch {
    ElMessage.error('复制失败')
  }
}

const handleCreate = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      createLoading.value = true
      try {
        const res = await linkApi.switchUrl({
          url: form.url,
          name: form.name,
          expire: form.expire || undefined
        })
        shortCode.value = res
        await handleQuery()
        await loadUserLinks()  // 创建成功后刷新列表
        ElMessage.success('短链生成成功')
        // 清空表单
        form.url = ''
        form.name = ''
        form.expire = ''
      } catch (error) {
        // 错误已在拦截器中处理
      } finally {
        createLoading.value = false
      }
    }
  })
}

const handleQuery = async () => {
  if (!shortCode.value.trim()) {
    ElMessage.warning('请输入短码')
    return
  }

  queryLoading.value = true
  try {
    const res = await linkApi.getLinkByShortCode(shortCode.value)
    linkDetail.value = res
  } catch (error) {
    linkDetail.value = null
  } finally {
    queryLoading.value = false
  }
}

const copyShortUrl = async () => {
  try {
    await navigator.clipboard.writeText(shortUrl.value)
    ElMessage.success('复制成功')
  } catch {
    ElMessage.error('复制失败')
  }
}

// 页面加载时获取列表
onMounted(() => {
  loadUserLinks()
})
</script>

<style scoped>
.link-manage {
  max-width: 1200px;
  margin: 0 auto;
}

.create-card,
.query-card,
.detail-card,
.list-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}

.tip-text {
  margin-left: 12px;
  font-size: 12px;
  color: #909399;
}

.query-form {
  display: flex;
  gap: 12px;
}

.click-count {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.expired {
  color: #f56c6c;
}

:deep(.el-descriptions__label) {
  width: 120px;
  font-weight: 600;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #ebeef5;
  padding: 16px 20px;
}
</style>