<template>
  <!-- 这个是 客户信息抽屉框 通用组件 -->
  <div>
    <el-drawer
      title="客户信息列表"
      :visible.sync="nowShow"
      size="58%"
      @close="closeShow"
    >
      <div class="main-div">
        <el-input
          v-model="tableSearch"
          placeholder="表格数据关键字查询"
          style="width: 300px; margin: 10px"
          size="small"
          clearable
        />
        <el-button type="primary" @click="loadTableData" icon="el-icon-search"  size="small"
          >刷新表格数据</el-button
        >
        <el-table
          v-loading="tLoading"
          :data="
            tableData.filter(
              (data) =>
                !tableSearch ||
                data.consignee
                  .toLowerCase()
                  .includes(tableSearch.toLowerCase()) ||
                data.countries
                  .toLowerCase()
                  .includes(tableSearch.toLowerCase()) ||
                data.customerCode
                  .toLowerCase()
                  .includes(tableSearch.toLowerCase()) ||
                data.oldGuest.toLowerCase().includes(tableSearch.toLowerCase())
            )
          "
          style="width: 95%"
          size="mini"
          @row-dblclick="useBatch"
          max-height="550"
          border=""
        >
          <el-table-column prop="countries" label="国家" width="150" />

          <el-table-column prop="customerCode" label="客户代码" width="200" />

          <el-table-column prop="consignee" label="收货人" width="200" />

          <el-table-column prop="oldGuest" label="国家中文名" width="150" />
          <el-table-column prop="doc" label="客户备注" width="150" />

          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button
                @click="useBatch(scope.row, 'w', '')"
                type="text"
                size="small"
              >
                选择
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          :current-page="tableDataInfo.page"
          :page-size="tableDataInfo.limit"
          :page-sizes="[100, 200, 300]"
          :total="tableDataInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "custmer-drawer",
  components: {},
  props: {
    show: {
      // 其他组件传递过来 是否显示关闭
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      openRecord: false,
      nowShow: false,
      tLoading: false,
      tableSearch: "",
      tableData: [],
      tableDataInfo: {
        page: 1,
        limit: 100,
        total: 0,
      },
    };
  },
  created() {},
  methods: {
    // 选中 客户信息
    useBatch(row, index, e) {
      this.$emit("useBatch", JSON.parse(JSON.stringify(row)));
      this.closeShow();
    },
    // 关闭
    closeShow() {
      this.$emit("closeShow");
    },
    // 加载客户信息数据
    loadTableData() {
      this.tLoading = true;
      let params = {};
      params.page = this.tableDataInfo.page;
      params.limit = this.tableDataInfo.limit;
      request({
        url: "/admin/dict/customer/pageQueryPartVO",
        method: "GET",
        params: params,
      }).then((res) => {
        this.tableDataInfo.page = res.data.current;
        this.tableDataInfo.total = res.data.total;
        this.tableData = res.data.records;
        this.tLoading = false;
      });
    },
    // 分页点击页数事件
    handleCurrentChange(page) {
      this.tableDataInfo.page = page;
      this.loadTableData(this);
    },
    // 分页选择显示条数事件
    handleSizeChange(limit) {
      this.tableDataInfo.limit = limit;
      this.loadTableData(this);
    },
  },
  watch: {
    show: {
      handler(newValue, oldValue) {
        if (newValue) {
          if (!this.openRecord) {
            this.loadTableData();
            this.openRecord = true;
          }
        }
        this.nowShow = newValue;
      },
      immediate: true,
    },
  },
  computed: {},
  beforeMount() {},
  mounted() {},
};
</script>
<style lang="scss" scoped>
.main-div {
  padding: 20px;
  margin-top: -30px;
}
/**
自定义el-table表格滚动栏样式
*/
.el-table {
  ::v-deep .el-table__body-wrapper::-webkit-scrollbar {
    width: 10px; /*滚动条宽度*/
    height: 10px; /*滚动条高度*/
  }
  /*定义滚动条轨道 内阴影+圆角*/
  ::v-deep .el-table__body-wrapper::-webkit-scrollbar-track {
    box-shadow: 0px 1px 3px #ccced2 inset; /*滚动条的背景区域的内阴影*/
    border-radius: 10px; /*滚动条的背景区域的圆角*/
    background-color: #ccced2; /*滚动条的背景颜色*/
  }
  /*定义滑块 内阴影+圆角*/
  ::v-deep .el-table__body-wrapper::-webkit-scrollbar-thumb {
    box-shadow: 0px 1px 3px #00a0e9 inset; /*滚动条的内阴影*/
    border-radius: 10px; /*滚动条的圆角*/
    background-color: #00a0e9; /*滚动条的背景颜色*/
  }
}
</style>
