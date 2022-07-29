<template>
  <!-- 表格分页组件 -->
  <div>
            <el-pagination
              :current-page="thisCurrent"
              :page-size="thisLimit"
              :page-sizes="[10, 50, 100]"
              :total="thisTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
  </div>
</template>

<script>

export default {
  name: 'pages',
  props: {
    current:{ // 当前页
      type: Number,
      default: 0
    },
    limit:{ // 显示条数
      type: Number,
      default: 0
    },
    total:{ // 总条数
      type: Number,
      default: 0
    },
  },
  data() {
    return {
      thisCurrent: 0,
      thisLimit: 10,
      thisTotal: 0,
    }
  },

  components: {},

  computed: {},

  beforeMount() {
  },

  mounted() {
  },
  created() {
  },
  methods: {
    // 点击页数事件
    handleCurrentChange(page) {
      // 向上层父组件调用方法
      this.$emit('handleCurrentChange', page)
    },
    // 选择显示条数事件
    handleSizeChange(limit) {
      // 向上层父组件调用方法
      this.$emit('handleSizeChange', limit)

    },
  },

  watch: {
    // 监听 当前页、显示条数、总条数的变化
    current: {
      handler(newValue, oldValue) {
        this.thisCurrent = newValue
      },
      immediate: true
    },
    limit: {
      handler(newValue, oldValue) {
        this.thisLimit = newValue
      },
      immediate: true
    },
    total: {
      handler(newValue, oldValue) {
        this.thisTotal = newValue
      },
      immediate: true
    },
  }

}

</script>
<style lang="scss" scoped>

</style>
