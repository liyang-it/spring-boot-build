<template>
  <div class="login-container">
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      autocomplete="on"
      class="login-form"
      label-position="left"
    >
      <div class="title-container">
        <h3 class="title">{{ lang.login }}</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          autocomplete="on"
          placeholder="用户名"
          tabindex="1"
          type="text"
        />
      </el-form-item>

      <el-tooltip
        v-model="capsTooltip"
        content="Caps lock is On"
        manual
        placement="right"
      >
        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            :key="passwordType"
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            autocomplete="on"
            name="password"
            placeholder="密码"
            tabindex="2"
            @blur="capsTooltip = false"
            @keyup.native="checkCapslock"
          />
          <span class="show-pwd" @click="showPwd">
            <svg-icon
              :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
            />
          </span>
        </el-form-item>
      </el-tooltip>
      <el-form-item prop="code">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          ref="code"
          v-model="loginForm.code"
          maxlength="4"
          placeholder="请输入验证码"
          type="text"
          @keyup.enter.native="handleLogin"
        >
          <!-- @keyup.enter="handleLogin"
                  v-on:keyup.enter.native="handleLogin" -->
        </el-input>
        <img :src="code" class="code_class" @click="getCodeUrl">
      </el-form-item>

      <el-button
        :loading="loading"
        style="width: 100%; margin-bottom: 30px"
        type="primary"
        @click.native.prevent="handleLogin"
      >{{ lang.login_btn }}
        <!-- @keyup.enter.native="handleLogin" -->
        <!-- @keyup.enter="handleLogin" -->
        <!-- v-on:keyup.enter.native="handleLogin" -->
      </el-button>

      <div style="position: relative">
        <div class="tips">
          <h3>演示账号</h3>
        </div>
        <div class="tips">
          <span>Username : {{ demo.username }}</span>
          <span>Password : {{ demo.password }}</span>
        </div>

        <el-button
          class="thirdparty-button"
          style="display: none"
          type="primary"
          @click="showDialog = true"
        >
          Or connect with
        </el-button>
      </div>
    </el-form>

    <el-dialog :visible.sync="showDialog" title="Or connect with">
      Can not be simulated on local, so please combine you own business
      simulation! ! !
      <br>
      <br>
      <br>
      <social-sign />
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request.js'
import language from '@/components/ImageCropper/utils/language.js'
import SocialSign from './components/SocialSignin'

export default {
  name: 'Login',
  components: {
    SocialSign
  },
  props: {
    // 语言类型
    langType: {
      type: String,
      default: 'zh'
    }
  },
  data() {
    // 多语言
    const { langType } = this
    const lang = language[langType] ? language[langType] : language['en']
    const validateUsername = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error(lang.login_page.validateUsername))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error(lang.login_page.validatePassword))
      } else {
        callback()
      }
    }
    const validateCode = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error(lang.login_page.validateCode))
      } else {
        callback()
      }
    }
    return {
      lang,
      loginForm: {
        username: '',
        password: '',
        code: ''
      },
      loginRules: {
        username: [
          {
            required: true,
            trigger: 'blur',
            validator: validateUsername
          }
        ],
        password: [
          {
            required: true,
            trigger: 'blur',
            validator: validatePassword
          }
        ],
        code: [
          {
            required: true,
            trigger: 'blur',
            validator: validateCode
          }
        ]
      },
      demo: {
        username: 'admin123',
        password: '123456'
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      showDialog: false,
      redirect: undefined,
      otherQuery: {},
      code: ''
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  created() {
    // window.addEventListener('storage', this.afterQRScan)
    this.loginForm.username = this.demo.username
    this.loginForm.password = this.demo.password
    this.getCodeUrl()
  },

  mounted() {
    if (this.loginForm.username === '') {
      this.$refs.username.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.password.focus()
    } else if (this.loginForm.code === '') {
      this.$refs.code.focus()
    }
  },
  destroyed() {},
  methods: {
    getCodeUrl() {
      // 请求获取 验证码
      // 请求验证码
      this.loginForm.code = ''
      request.get('/admin/auth/kaptcha').then((res) => {
        this.code = res.data
      })
    },
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && key >= 'A' && key <= 'Z'
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          this.$store
            .dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({
                path: this.redirect || '/',
                query: this.otherQuery
              })
              this.loading = false
            })
            .catch((res) => {
              // 刷新验证码
              this.getCodeUrl()
              this.loading = false
            })
        } else {
          return false
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    }
    // afterQRScan() {
    //   if (e.key === 'x-admin-manage-oauth-code') {
    //     const code = getQueryObject(e.newValue)
    //     const codeMap = {
    //       wechat: 'code',
    //       tencent: 'code'
    //     }
    //     const type = codeMap[this.auth_type]
    //     const codeName = code[type]
    //     if (codeName) {
    //       this.$store.dispatch('LoginByThirdparty', codeName).then(() => {
    //         this.$router.push({ path: this.redirect || '/' })
    //       })
    //     } else {
    //       alert('第三方登录失败')
    //     }
    //   }
    // }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

.code_class {
  position: absolute;
  margin-left: -20;
  right: 10px;
  top: 5px;
}

.code_class:hover {
  cursor: pointer;
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg: #2d3a4b;
$klyl-bg: #012ea5;
$dfll-bg: #82d8cf;
$dark_gray: #889aa4;
$light_gray: #eee;

/**
 * 必应每日壁纸API， <a href="https://blog.oneneko.com/posts/2020/07/01/bing-api.html">参考地址</a>>
 */
.login-container {
  min-height: 100%;
  width: 100%;
  //background-color: $bg;
  background: url(https://api.oneneko.com/v1/bing_today) rgba(0, 0, 0, 0.5)
    no-repeat center center;
  background-blend-mode: multiply;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }
}
</style>
