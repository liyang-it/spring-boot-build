<template>
  <div class="main-div">
    <el-descriptions :column="3" border class="margin-top" title="个人信息">
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-unlock"></i>
          账户类型
        </template>
        {{ user.isSuper === true ? '超级管理员' : '普通管理员' }}
      </el-descriptions-item>

      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-picture"></i>
          头像
        </template>
        <el-avatar :size="60" :src="user.avatar" fit="fill" shape="square"></el-avatar>
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">
          <i class="el-icon-menu"></i>
          菜单权限
        </template>
        <el-tree
          ref="updTree"
          :data="treeData"
          :default-checked-keys="defaultChecks"
          :expand-on-click-node="false"
          default-expand-all
          node-key="id"
          show-checkbox
        >
                  <span slot-scope="{ node, data }" class="custom-tree-node">
                     <span>{{ data.meta.title }}</span>
                  </span>
        </el-tree>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'myinfo',
  props: [''],
  data() {
    return {
      user: {},
      treeData: [],
      defaultChecks: [1],
    }
  },

  components: {},

  computed: {},

  beforeMount() {
  },
  created() {
    this.user = this.$store.state.user
    request.get('/admin/system/menu/queryAll').then((res)=>{
      this.treeData = res.data
      // 验证是否为超级管理员，超级管理员选中所有 一级
      let isSuper = this.$store.state.user.isSuper
      if(isSuper){
        for(let i = 0; i < res.data.length; i ++){
          this.defaultChecks.push(res.data[i].id)
        }
      }else{
        // 不是超级管理员，只选中有的
        this.defaultChecks = this.$store.state.user.menuIds
      }
    })
  },
  mounted() {
  },

  methods: {},

  watch: {}

}

</script>
<style lang="scss" scoped>
.main-div {
  padding: 20px;
}

.touxiang:hover {
  cursor: pointer
}
</style>
