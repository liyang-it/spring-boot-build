<template>
  <div class="main-div">
    <!--    添加用户dialog-->
    <el-dialog :visible.sync="addFormDialogVisible" title="添加账户" width="40%">
      <el-form ref="form" :model="addForm" label-width="120px" size="mini">
        <el-form-item label="用户名">
          <el-input ref="addUsername" v-model="addForm.username"></el-input>
        </el-form-item>

        <el-form-item label="密码">
          <el-input ref="addPassword" v-model="addForm.password"></el-input>
        </el-form-item>

        <el-form-item label="账户类型">
          <el-select ref="s2" v-model="addForm.isSuperAdmin">
            <el-option v-for="item in accountTypeList" :key="item.label" :label="item.label" :value="item.type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单权限" v-show="addForm.isSuperAdmin === 0">
          <el-tree ref="addTree" :data="menuData" :expand-on-click-node="false" default-expand-all node-key="id" show-checkbox>
            <span slot-scope="{ node, data }" class="custom-tree-node">
              <span>{{ data.meta.title }}</span>
              </span>
          </el-tree>
        </el-form-item>
        <el-form-item label="状态">
          <el-select ref="s2" v-model="updForm.status">
            <el-option v-for="item in statusList" :key="item.label" :label="item.label" :value="item.type"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelAdd">取 消</el-button>
        <el-button type="primary" @click="confirmAdd">立即创建</el-button>
      </span>
    </el-dialog>

    <!--    修改用户dialog-->
    <el-dialog :visible.sync="updFormDialogVisible" title="修改账户" width="40%">
      <el-form ref="form" :model="updForm" label-width="120px" size="mini">
        <el-form-item label="用户名">
          <el-input ref="updUsername" v-model="updForm.username"></el-input>
        </el-form-item>

        <el-form-item label="账户类型">
          <el-select ref="s2" v-model="updForm.isSuperAdmin">
            <el-option v-for="item in accountTypeList" :key="item.label" :label="item.label" :value="item.type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单权限" v-show="updForm.isSuperAdmin === 0">
          <el-tree ref="updTree" :data="menuData" :expand-on-click-node="false" default-expand-all node-key="id" show-checkbox>
            <span slot-scope="{ node, data }" class="custom-tree-node">
              <span>{{ data.meta.title }}</span>
              </span>
          </el-tree>
        </el-form-item>
        <el-form-item label="状态">
          <el-select ref="s2" v-model="updForm.status">
            <el-option v-for="item in statusList" :key="item.label" :label="item.label" :value="item.type"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="updFormDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmUpd">确认修改</el-button>
      </span>
    </el-dialog>


    <div class="main-search-div">
      <el-form ref="search" :model="search" label-width="100px" size="mini">
        <el-form-item label="用户名" prop="username">
          <el-col :span="4">
            <el-input v-model="search.username" clearable></el-input>
          </el-col>

          <el-col :span="5">
            <el-form-item label="管理员类型" prop="deleted">
              <el-select ref="s4" v-model="search.isSuper" placeholder="是否禁用">
                <el-option v-for="item in plantTypeList" :key="item.id" :id="item.id" :label="item.label" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="状态" prop="deleted">
              <el-select ref="s3" v-model="search.status" placeholder="是否禁用">
                <el-option v-for="item in deleteds" :key="item.label" :id="item.status" :label="item.label" :value="item.status"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <div style="margin-left: 10px;">
              <el-button icon="el-icon-search" type="primary" @click="confirmSearch">查询</el-button>
              <el-button icon="el-icon-delete" @click="resetSearchForm()">清空</el-button>
              <el-button icon="el-icon-plus" @click="addFormDialogVisible = true">添加账户</el-button>
            </div>
          </el-col>
        </el-form-item>


      </el-form>
    </div>
    <div class="main-table-div">
      <el-table v-loading="tableLoading" :data="tableData" border height="450" size="mini" style="width: 100%" ref="table">
        <el-table-column align="center" label="用户名" prop="username" width="200">
        </el-table-column>
        <el-table-column align="center" label="账户类型" width="110">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isSuperAdmin === true">超级管理员</el-tag>
            <el-tag v-else type="info">普通管理员</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="创建时间" prop="addTime" width="150">
        </el-table-column>
        <el-table-column align="center" label="最后登录IP" prop="lastLoginIp" width="110">
        </el-table-column>

        <el-table-column align="center" label="最后登录时间" prop="lastLoginTime" width="160">
        </el-table-column>

        <el-table-column align="center" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-show="scope.row.status === false" type="success">启用</el-tag>
            <el-tag v-show="scope.row.status === true" type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button class="xf-class" size="small" type="text" @click="openUpdUser(scope.row)">
              修改
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="main-page">
      <div class="main-page-div">
        <el-pagination :current-page="search.page" :page-size="search.limit" :page-sizes="[10, 50, 100]" :total="search.total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script>
  import request from '@/utils/request'

  export default {
    name: 'admin-manage-index',
    props: [''],
    data() {
      return {
        addFormDialogVisible: false,
        updFormDialogVisible: false,
        tableLoading: false,
        deleteds: [{
          status: -1,
          label: '全部'
        },
        {
          status: 0,
          label: '正常'
        },
        {
          status: 1,
          label: '禁用'
        }],
        accountTypeList: [
          { type: 1, label: '超级管理员' },
          { type: 0, label: '普通管理员' }
        ],
        tableData: [],
        search: {
          username: '',
          isSuper: -1,
          status: -1,
          page: 1,
          limit: 10,
          total: 0
        },
        plantTypeList: [
          { id: -1, label: '全部' },
          { id: 1, label: '超级管理员' },
          { id: 0, label: '普通管理员' }
        ],
        statusList: [{ label: '启用', type: 0 }, { label: '禁用', type: 1 }],
        addForm: {
          username: '',
          password: '',
          roleIds: [1],
          status: 0,
          menu: [],
          isSuperAdmin: 1,
        },
        updForm: {
          id: 0,
          username: '',
          roleIds: [1],
          status: 0,
          menu: [],
          isSuperAdmin: true,
        },
        menuData: [],
        plantData: []
      }
    },

    components: {},

    computed: {},

    beforeMount() {},

    mounted() {},
    created() {
      this.loadMenu(this)
      this.loadTableData(this)

    },
    methods: {
      selectBYBlur(r) {
        this.$refs[r].blur()
      },
      // 点击页数事件
      handleCurrentChange(page) {
        this.search.page = page
        this.loadTableData(this)
      },
      // 选择显示条数事件
      handleSizeChange(limit) {
        this.search.limit = limit
        this.loadTableData(this)
      },
      // 校验 添加管理员form表单
      checkAddOrUpdForm(this_) {
        if (this.addForm.username.length === 0) {
          this.$message.error('请输入账户用户名')
          this.$refs.addUsername.focus()
          return false
        }
        if (this.addForm.password.length < 6) {
          this.$message.error('密码不得小于6位字符')
          this.$refs.addPassword.focus()
          return false
        }
        if (this.addForm.isSuperAdmin === 0) {
          if (this.addForm.menu.length === 0) {
            this.$message.error('请选择菜单权限')
            this.$refs.addTree.focus()
            return false
          }
        }

        return true
      },
      // 校验 修改管理员form表单
      checkUpdForm(this_) {

        if (this.updForm.username.length === 0) {
          this.$message.error('请输入账户用户名')
          this.$refs.updUsername.focus()
          return false
        }
        if (this.updForm.type === 2) {
          if (this.updForm.menu.length === 0) {
            this.$message.error('请选择菜单权限')
            this.$refs.updMenu.focus()
            return false
          }
        }
        return true
      },
      // 取消创建管理员
      cancelAdd() {
        this.addForm.username = ''
        this.addForm.password = ''
        this.addForm.status = 0
        this.addForm.isSuperAdmin = 1
        this.addForm.menu = []
        this.addFormDialogVisible = false
      },
      // 确认创建管理员
      confirmAdd() {
        let c = this.checkUserIssSuper()
        if (c === false) {
          this.$message.error('普通管理员，无权限操作')
          return
        }
        this.addForm.menu = this.$refs['addTree'].getCheckedKeys()
        let check = this.checkAddOrUpdForm(this)
        if (check) {

          request.post('/admin/admin/create', this.addForm).then((res) => {
            this.$message.success('添加成功')
            this.asyncLoadData(this)
            this.cancelAdd()
          })
        }

      },
      // 确认修改管理员信息
      confirmUpd() {

        let c = this.checkUserIsSuper()
        if (c === false) {
          this.$message.error('普通管理员，无权限操作')
          return
        }
        this.updForm.menu = this.$refs['updTree'].getCheckedKeys()
        let check = this.checkUpdForm(this)
        if (check) {
          request.post('/admin/admin/update', this.updForm).then((res) => {
            this.$message.success('修改成功')
            this.asyncLoadData(this)
            this.updFormDialogVisible = false
          })
        }
      },
      // 异步重新加载数据
      asyncLoadData(t) {
        setTimeout(() => {
          t.loadTableData(t)
        }, 1000)
      },
      // 打开修改账户dialog
      openUpdUser(row) {
        // 普通管理员无法操作，判断是否有全部菜单权限，有就是超级管理员
        let c = this.checkUserIsSuper()
        if (c === false) {
          this.$message.error('普通管理员，无权限操作')
          return
        }
        this.updForm.id = row.id
        this.updForm.username = row.username
        this.updForm.status = row.status === true ? 1 : 0
        this.updForm.isSuperAdmin = row.isSuperAdmin === true ? 1 : 0
        this.$nextTick(() => {
          this.$refs['updTree'].setCheckedKeys(row.menu, false)
        })
        this.updFormDialogVisible = true

      },
      checkUserIsSuper() {
        // 普通管理员无法操作，判断是否有全部菜单权限，有就是超级管理员
        let c = this.$store.state.user.isSuper
        return c
      },
      // 确认查询
      confirmSearch() {
        this.loadTableData(this)
      },
      // 清空form表单
      resetSearchForm() {
        this.search.username = ''
        this.search.status = -1
        this.search.isSuper = -1
      },
      // 查询所有菜单信息
      loadMenu(t) {
        request.get('/admin/system/menu/queryAll').then((res) => {
          t.menuData = res.data
        })
      },
      // 查询管理员列表
      loadTableData(this_) {
        this_.tableLoading = true
        request({
          url: '/admin/admin/list',
          params: this_.search
        }).then((res) => {
          this_.tableData = res.data.records
          this_.search.page = res.data.current
          this_.search.limit = res.data.size
          this_.search.total = res.data.total
          setTimeout(() => {
            this_.tableLoading = false
          }, 200)

        })
      },
    },

    watch: {},
    activated() {
      this.$nextTick(() => {
        this.$refs.table.doLayout(); //解决表格错位
      });
    }

  }
</script>
<style lang="scss" scoped>
  .main-div {
    margin: 10px;

  }

  .el-form-item--mini.el-form-item {
    margin-bottom: 5px;
  }

  .el-table .warning-row {
    background: oldlace;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }

  .xf-class:hover {
    color: red;
  }

  .main-page-div {
    height: 50px;
  }

  .main-page-div .el-pagination {
    text-align: center;
  }
</style>