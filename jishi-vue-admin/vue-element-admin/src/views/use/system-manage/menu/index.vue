<template>
  <!-- 菜单层级页面 限制最多两级，后台没有限制-->
  <div class="main-div">
    <!--    添加一级菜单dialog-->
    <el-dialog
      :visible.sync="addOneDialogVisible"
      title="添加一级菜单"
      width="50%"
    >
      <el-form ref="form" :model="addOneMenuData" label-width="80px" size="mini">
        <el-form-item label="菜单名称">
          <el-input v-model="addOneMenuData.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单路由">
          <el-input v-model="addOneMenuData.path"></el-input>
        </el-form-item>
        <el-form-item label="菜单标题">
          <el-input v-model="addOneMenuData.meta.title"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-select @blur="selectBYBlur('s1')" ref="s1" v-model="addOneMenuData.meta.icon" placeholder="请选择图标">
            <el-option
              v-for="item in icons"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span style="float: left; margin-right: 20px;">{{ item.label }}</span>
              <svg-icon :icon-class="item.value" />
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="addOneDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="confirmAddOneMenu">确 定</el-button>
  </span>
    </el-dialog>
    <!--    添加二级菜单dialog-->
    <el-dialog
      :visible.sync="addTowDialogVisible"
      :title="addTwoMenuData.diaTitle"
      width="50%"
    >
      <el-form ref="form" :model="addTwoMenuData" label-width="100px" size="mini">
        <el-form-item label="菜单名称">
          <el-input v-model="addTwoMenuData.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单路由">
          <el-input v-model="addTwoMenuData.path"></el-input>
        </el-form-item>
        <el-form-item label="菜单组件地址">
          <el-input v-model="addTwoMenuData.component"></el-input>
        </el-form-item>
        <el-form-item label="菜单标题">
          <el-input v-model="addTwoMenuData.meta.title"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-select @blur="selectBYBlur('s2')" ref="s2" v-model="addTwoMenuData.meta.icon" placeholder="请选择图标">
            <el-option
              v-for="item in icons"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span style="float: left; margin-right: 20px;">{{ item.label }}</span>
              <svg-icon :icon-class="item.value" />
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="addTowDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="confirmAddTwoMenu">确 定</el-button>
  </span>
    </el-dialog>


    <!--    修改菜单dialog-->
    <el-dialog
      :visible.sync="updDialogVisible"
      title="修改菜单"
      width="50%"
    >
      <el-form ref="form" :model="updMenuData" label-width="100px" size="mini">
        <el-form-item label="菜单名称">
          <el-input v-model="updMenuData.name"></el-input>
        </el-form-item>
        <el-form-item label="菜单路由">
          <el-input v-model="updMenuData.path"></el-input>
        </el-form-item>
        <el-form-item label="菜单组件地址" v-show="updMenuData.alwaysShow === false">
          <el-input v-model="updMenuData.component"></el-input>
        </el-form-item>
        <el-form-item label="菜单标题">
          <el-input v-model="updMenuData.meta.title"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-select @blur="selectBYBlur('s3')" ref="s3" v-model="updMenuData.meta.icon" placeholder="请选择图标">
            <el-option
              v-for="item in icons"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <span style="float: left; margin-right: 20px;">{{ item.label }}</span>
              <svg-icon :icon-class="item.value" />
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="updDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="confirmUpdMenu">确 定</el-button>
  </span>
    </el-dialog>



    <div class="main-search-div">
      <el-button icon="el-icon-circle-plus-outline" size="small" type="primary" @click="addOneDialogVisible = true">添加一级菜单</el-button>
     </div>
    <div class="main-table-div">
      <el-table
        :data="tableData"
        ref="table"
        style="width: 100%"
        size="mini"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <!-- 二级菜单表格  -->
            <div v-if="props.row.children.length !== 0" class="main-nodeTable-div">
              <el-button icon="el-icon-circle-plus-outline" size="small" type="success" @click="openAddTowDialog(props.row)">添加二级菜单</el-button>
              <el-table
                :data="props.row.children"
                style="width: 100%"
                :row-style="tableStyle"
                size="mini"
              >
                <el-table-column/>
                <el-table-column
                  label="菜单名称"
                  prop="name"
                >
                </el-table-column>
                <el-table-column
                  label="菜单标题"
                  prop="meta.title"
                >
                </el-table-column>

                <el-table-column
                  label="菜单图标"
                >
                  <template slot-scope="scope">
                    <svg-icon :icon-class="scope.row.meta.icon"/>
                    <span style="margin-left: 10px">{{ scope.row.meta.icon }}</span>
                  </template>
                </el-table-column>

                <el-table-column
                  label="菜单路由"
                  prop="path"
                >

                </el-table-column>

                <el-table-column
                  label="创建时间"
                  prop="addTime"
                >
                </el-table-column>
                <el-table-column
                  label="操作"
                  width="100">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="openUpdDialog(scope.row)">修改</el-button>
                    <el-button  type="text" size="small" @click="delMenu(scope.row)">删除</el-button>

                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-if="props.row.children.length === 0" class="main-nodeTable-div">
              <el-button icon="el-icon-plus" size="small" type="info" @click="openAddTowDialog(props.row)">添加二级菜单</el-button>
            </div>

          </template>
        </el-table-column>
        <el-table-column
          label="一级菜单名称"
          prop="name"
        >
        </el-table-column>
        <el-table-column
          label="菜单标题"
          prop="meta.title"
        >
        </el-table-column>

        <el-table-column
          label="菜单图标"
        >
          <template slot-scope="scope">
            <svg-icon :icon-class="scope.row.meta.icon"/>
            <span style="margin-left: 10px">{{ scope.row.meta.icon }}</span>
          </template>
        </el-table-column>

        <el-table-column
          label="菜单路由"
          prop="path"
        >

        </el-table-column>

        <el-table-column
          label="创建时间"
          prop="addTime"
        >
        </el-table-column>
        <el-table-column
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button  type="text" size="small" @click="openUpdDialog(scope.row)">修改</el-button>
            <el-button type="text" size="small" @click="delMenu(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'system-menu-index',
  props: [''],
  data() {

    return {
      updDialogVisible:false,
      icons: [{
        value: 'bug',
        label: 'bug'
      }, {
        value: 'chart',
        label: 'chart'
      }, {
        value: 'clipboard',
        label: 'clipboard'
      }, {
        value: 'component',
        label: 'component'
      }, {
        value: 'dashboard',
        label: 'dashboard'
      }, {
        value: 'documentation',
        label: 'documentation'
      }, {
        value: 'drag',
        label: 'drag'
      }, {
        value: 'edit',
        label: 'edit'
      }, {
        value: 'education',
        label: 'education'
      }, {
        value: 'email',
        label: 'email'
      }, {
        value: 'example',
        label: 'example'
      }, {
        value: 'excel',
        label: 'excel'
      }, {
        value: 'exit-fullscreen',
        label: 'exit-fullscreen'
      }, {
        value: 'eye-open',
        label: 'eye-open'
      }, {
        value: 'form',
        label: 'form'
      }, {
        value: 'guide',
        label: 'guide'
      }, {
        value: 'international',
        label: 'international'
      }, {
        value: 'list',
        label: 'list'
      }, {
        value: 'message',
        label: 'message'
      }, {
        value: 'nested',
        label: 'nested'
      }, {
        value: 'people',
        label: 'people'
      }, {
        value: 'search',
        label: 'search'
      }, {
        value: 'shopping',
        label: 'shopping'
      }, {
        value: 'skill',
        label: 'skill'
      }, {
        value: 'tab',
        label: 'tab'
      }, {
        value: 'table',
        label: 'table'
      }, {
        value: 'tree-table',
        label: 'tree-table'
      }, {
        value: 'tree',
        label: 'tree'
      }, {
        value: 'user',
        label: 'user'
      }],
      tableData: [],
      addTwoMenuData: {
        alwaysShow: false,
        diaTitle: '',
        parent: 0,
        component: '',
        name: '',
        path: '',
        meta: { title: '', icon: '' }
      },
      updMenuData: {
        id: '',
        alwaysShow: false,
        parent: '',
        component: '',
        name: '',
        path: '',
        meta: { title: '', icon: '' }
      },

      addOneMenuData: {
        alwaysShow: true, // 一级菜单固定值
        parent: 0, // 一级菜单固定值
        component: 'Layout', // 一级菜单固定值
        name: '',
        path: '',
        meta: { title: '', icon: '' }
      },
      addOneDialogVisible: false,
      addTowDialogVisible: false
    }
  },

  components: {},

  computed: {},

  beforeMount() {
  },

  mounted() {
  },
  created() {
    this.loadTableData(this)
  },
  methods: {
    // 确认修改菜单
    confirmUpdMenu(){
      request.post('/admin/system/menu/updMenu', this.updMenuData).then((res)=>{
        if(res.code === 0){
          this.loadTableData(this)
          this.updDialogVisible = false
        }
        this.$message.info(res.msg)
      })
    },
    // 打开修改dialog
    openUpdDialog(row){
      this.updMenuData.id = row.id
      this.updMenuData.parent = row.parent
      this.updMenuData.component = row.component
      this.updMenuData.alwaysShow = row.alwaysShow
      this.updMenuData.name = row.name
      this.updMenuData.path = row.path
      this.updMenuData.meta = row.meta
      this.updMenuData.version = row.version
      this.updDialogVisible = true
    },
    // 表格行样式
    tableStyle({row, rowIndex}){
    },
    // 删除菜单
    delMenu(row){
      this.$confirm('此操作将永久删除菜单 (' + row.name + ') , 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
          request.get('/admin/system/menu/delMenu?id=' + row.id).then((res)=>{
            if(res.code === 0){
              this.loadTableData(this)
            }
            this.$message.info(res.msg)
          })

      });
    },
    // 确认添加二级菜单
    confirmAddTwoMenu(){
      if(this.addTwoMenuData.name.length === 0){
        this.$message.error("请输入菜单名称");
        return
      }
      if(this.addTwoMenuData.path.length === 0){
        this.$message.error("请输入菜单路由");
        return
      }
      if(this.addTwoMenuData.component.length === 0){
        this.$message.error("请输入菜单组件地址");
        return
      }
      if(this.addTwoMenuData.component.indexOf("@/views/") === -1){
        this.$message.error("菜单组件无效，必须是 @/views/ 开头");
        return
      }
      if(this.addTwoMenuData.meta.title.length === 0){
        this.$message.error("请输入菜单标题");
        return
      }
      if(this.addTwoMenuData.meta.title.length === 0){
        this.$message.error("请选择菜单图标");
        return
      }
      request.post('/admin/system/menu/addMenu', this.addTwoMenuData).then((res)=>{
        if(res.code === 0){
          this.loadTableData(this)
          this.addTwoMenuData.name = ''
          this.addTwoMenuData.path = ''
          this.addTwoMenuData.component = ''
          this.addTwoMenuData.meta.title = ''
          this.addTwoMenuData.meta.icon = ''
          this.addTowDialogVisible = false
        }
        this.$message.info(res.msg)
      })
    },
    // 打开添加二级菜单dialog
    openAddTowDialog(one){
      this.addTwoMenuData.diaTitle = '添加 (' + one.name + ') 二级菜单'
      this.addTwoMenuData.parent = one.id
      this.addTowDialogVisible = true
    },
    // 确认添加一级菜单
    confirmAddOneMenu() {
      if(this.addOneMenuData.name.length === 0){
        this.$message.error("请输入菜单名称");
        return
      }
      if(this.addOneMenuData.path.length === 0){
        this.$message.error("请输入菜单路由");
        return
      }
      if(this.addOneMenuData.meta.title.length === 0){
        this.$message.error("请输入菜单标题");
        return
      }
      if(this.addOneMenuData.meta.title.length === 0){
        this.$message.error("请选择菜单图标");
        return
      }
      request.post('/admin/system/menu/addMenu', this.addOneMenuData).then((res)=>{
        if(res.code === 0){
          this.loadTableData(this)
          this.addOneMenuData.name = ''
          this.addOneMenuData.path = ''
          this.addOneMenuData.meta.title = ''
          this.addOneMenuData.meta.icon = ''
          this.addOneDialogVisible = false
        }
        this.$message.info(res.msg)
      })
    },
    selectBYBlur(r){
      // this.$refs[r].blur()
    },
    // 加载数据
    loadTableData(t) {
      request.get('/admin/system/menu/queryAll').then((res) => {
        t.tableData = res.data
      })
    }
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


.main-page-div {
  height: 50px;
}

.main-page-div .el-pagination {
  text-align: center;
  //position: absolute;
  //right: 0px;
  //width: 460px;
}

.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  //margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}

.main-nodeTable-div {
  margin-left: 10px;
}

.main-search-div {
  margin-bottom: 10px;
}
</style>
