<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />
    <!-- 修改密码dialog -->
             <!-- Form -->
   <el-dialog :visible.sync="dialogFormVisible" title="修改密码" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form :model="updForm" :rules="rules" ref="updFormRuleForm" :label-position="labelPosition">
      <el-form-item label="新密码" prop="password">
        <el-input v-model="updForm.password" autocomplete="off" maxlength="20"/>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirm_password">
        <el-input v-model="updForm.confirm_password" autocomplete="off" maxlength="20"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="confirm_upd">确认修改</el-button>
      </span>
    </template>
  </el-dialog>
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <!-- <search id="header-search" class="right-menu-item" /> -->
        <welcome />
        <error-log class="errLog-container right-menu-item hover-effect" />

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <!-- <el-tooltip content="Global Size" effect="dark" placement="bottom">

          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip> -->

      </template>
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
<!--          <img src="https://wpimg.wallstcn.com/577965b9-bb9e-4e02-9f0c-095b41417191" class="user-avatar">-->
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="dialogFormVisibleFun">修改密码</el-dropdown-item>

          <!-- <router-link to="/">
            <el-dropdown-item>Dashboard</el-dropdown-item>
          </router-link>
          <a target="_blank" href="https://github.com/PanJiaChen/vue-element-admin/">
            <el-dropdown-item>Github</el-dropdown-item>
          </a>
          <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
            <el-dropdown-item>Docs</el-dropdown-item>
          </a> -->
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import Hamburger from "@/components/Hamburger";
import ErrorLog from "@/components/ErrorLog";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import elDragDialog from "@/directive/el-drag-dialog";
import welcome from "@/components/welcome";
import request from "@/utils/request";
export default {
  directives: { elDragDialog },
  components: {
    Breadcrumb,
    Hamburger,
    ErrorLog,
    Screenfull,
    SizeSelect,
    Search,
    elDragDialog,
    welcome
  },
  data() {
    return {
      labelPosition: 'left',
      dialogFormVisible: false,
      updForm: {
        password: "",
        confirm_password: ""
      },
      rules: {
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "长度不得小于 6 个字符", trigger: "blur" }
        ],
        confirm_password: [
          { required: true, message: "请再次输入密码", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    ...mapGetters(["sidebar", "avatar", "device", "name"])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
    },
    async logout() {
      await this.$store.dispatch("user/logout");
      this.$router.push(`/login`);
      // this.$router.push(`/login?redirect=${this.$route.fullPath}`);
    },
    dialogFormVisibleFun() {
      this.dialogFormVisible = true;
    },
    confirm_upd() {
      let this_ = this;
      this_.$refs["updFormRuleForm"].validate(valid => {
        if (valid) {
          // 验证密码是否一致
          if (this_.updForm.password != this_.updForm.confirm_password) {
            this_.$message.error("密码不一致");
            return;
          }
          let object = new Object();
          object.password = this_.updForm.password
          request
            .post("/admin/auth/resetPswd", object)
            .then(res => {
              if (res.code !== 0) {
                this_.$message.error(res.msg);
                return;
              }
              this_.$message({
                message: "操作成功",
                type: "success"
              });
              // 跳转回首页  - 直接刷新
              setTimeout(() => {
                this_.$router.go(0);
              }, 1000);
            });
        } else {
          return false;
        }
      });
    }
  },
  created() {
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
