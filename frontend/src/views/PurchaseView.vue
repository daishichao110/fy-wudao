<template>
  <div class="purchase-view">
    <div class="view-header">
      <h2>集中采购与费用公示明细</h2>
      <p class="subtitle">团购舞鞋、演出服、保险大巴等资金流向公开透明</p>
    </div>

    <div v-for="item in purchaseList" :key="item.purchaseId" class="dance-card">
      <div class="card-header">
        <span class="tag-badge tag-blue"><ShoppingBag :size="12" /> {{ item.category }}</span>
        <span class="total-price">￥{{ item.totalAmount }}</span>
      </div>

      <h3 class="item-title">{{ item.itemName }}</h3>
      <div class="meta-info">
        <span>单价: ￥{{ item.unitPrice }}</span>
        <span>数量: {{ item.quantity }} 件/双</span>
      </div>

      <p class="remark" v-if="item.remark">备注：{{ item.remark }}</p>

      <div class="proof-row" v-if="item.proofUrl">
        <button @click="openProofModal(item.proofUrl)" class="btn-outline" style="padding: 4px 10px; font-size: 12px;">
          <Receipt :size="13" /> 查看发票收据凭证
        </button>
      </div>
    </div>

    <!-- 发票模态框 -->
    <div v-if="proofModal.show" class="modal-overlay">
      <div class="modal-content text-center">
        <h3>发票与报销凭证电子档</h3>
        <img :src="proofModal.url" alt="凭证图片" style="max-width: 100%; border-radius: 8px; margin: 12px 0;" />
        <button @click="proofModal.show = false" class="btn-primary">关闭预览</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ShoppingBag, Receipt } from 'lucide-vue-next'
import api from '../api'

const purchaseList = ref([])
const proofModal = reactive({ show: false, url: '' })

const loadPurchases = async () => {
  try {
    const res = await api.getPurchases()
    if (res.data) purchaseList.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const openProofModal = (url) => {
  proofModal.url = url
  proofModal.show = true
}

onMounted(() => {
  loadPurchases()
})
</script>

<style scoped>
.view-header { margin-bottom: 16px; }
.view-header h2 { font-size: 18px; font-weight: 700; color: var(--text-main); }
.subtitle { font-size: 12px; color: var(--text-muted); }

.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.total-price { font-size: 18px; font-weight: 800; color: var(--primary-rose); }

.item-title { font-size: 15px; font-weight: 700; color: var(--text-main); margin-bottom: 4px; }
.meta-info { font-size: 12px; color: var(--text-muted); display: flex; gap: 12px; margin-bottom: 6px; }
.remark { font-size: 12px; color: #475569; background: var(--bg-app); padding: 6px 10px; border-radius: var(--radius-sm); margin-bottom: 8px; }
.proof-row { display: flex; justify-content: flex-end; }
</style>
