import Vue from 'vue'
import 'default-passive-events'
import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import Element from 'element-ui' // vue-element-admin-manage 使用的是 2.13.2 版本，现在升级为 2.15.7版本
import './styles/element-variables.scss'
import zhLang from 'element-ui/lib/locale/lang/zh-CN' // 如果使用中文语言包请默认支持，无需额外引入，请删除该依赖
import '@/styles/index.scss' // global css
import App from './App'
import store from './store'
import router from './router'

import './icons' // icon
import './permission' // permission control
import './utils/error-log' // error log
import * as filters from './filters' // global filters
/**
 * pl-table 插件
 */
import plTable from 'pl-table'
import 'pl-table/themes/index.css' // 引入样式（必须引入)，vuecli3.0不需要配置，cli2.0请查看webpack是否配置了url-loader对woff，ttf文件的引用,不配置会报错哦
import 'pl-table/themes/plTableStyle.css' // 默认表格样式很丑 引入这个样式就可以好看啦（如果你不喜欢这个样式，就不要引入，不引入就跟ele表格样式一样）
/**
 * 打印插件 <a href="https://blog.csdn.net/qq_45325810/article/details/124186991">参考地址</a>
 */
import Print from 'vue-print-nb'

Vue.use(plTable);
Vue.use(Print);
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}
Vue.use(Element, {
   size: Cookies.get('size') || 'medium', // set element-ui default size
   locale: zhLang // 如果使用中文，无需设置，请删除
})

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
