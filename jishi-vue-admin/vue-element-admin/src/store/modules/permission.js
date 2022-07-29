import {asyncRoutes, constantRoutes} from '@/router'
import store from '@/store'
import Layout from '@/layout'

/**
 * Use meta.role to determine if the current user has permission
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  console.info('hasPermission')
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
  }
}

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []
  console.info('filterAsyncRoutes')
  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(roles, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, roles)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, roles) {

    return new Promise(resolve => {
      let accessedRoutes
      if (roles.includes('admin')) {
        accessedRoutes = asyncRoutes || []
      } else {
        accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      }
      let isSuper = store.state.user.isSuper
      console.info('当前登录用户是否为超级管理员', isSuper)
      let userRoutes = []
      if (isSuper === false) {
        // 不是超级管理员
        userRoutes = filterAsyncRouter(store.state.user.menu)
        commit('SET_ROUTES', userRoutes)
        resolve(userRoutes)
      }else{
        // 超级管理员，全部权限
        userRoutes = accessedRoutes
        commit('SET_ROUTES', userRoutes)
        resolve(userRoutes)
      }
      console.info('设置动态菜单权限', userRoutes)

    })
  }
}

/**
 * 获取一个字符串值在指定字符串第n次出现的位置
 * @param str 字符串
 * @param cha 查找的字符
 * @param num 第一次出现, 从0开始
 * @returns {*}
 */
function find(str, cha, num) {
  var x = str.indexOf(cha)
  for (var i = 0; i < num; i++) {
    x = str.indexOf(cha, x + 1)
  }
  return x
}

/**
 * 将 用户菜单JSON信息 转换为 router 可识别的路由json信息
 * @param t 管理员菜单JSON数组
 * @returns {*}
 */
function filterAsyncRouter(t) {
  t.filter(index => {
    if (index.component === 'Layout') {
      index.component = Layout
    } else {
      // 不是路由菜单，转换对应 vue组件
      // @/views/sn-manage/dict/index
      let component = index.component
      console.info(component)
      //
      /**
       * 截取 示例 @/views/sn-manage/dict/index 截取出 @/viesw/ 以外的字符，因为 拼接会异常
       * 新逻辑： 创建页面的时候去掉 @/viesw/
       * @type {*|string}
       * let test = component.substr(find(component, '/', 1) + 1)
       */
      index.component = require(`@/views/${component}.vue`).default

    }
    // 递归子菜单
    if (index.children && index.children.length) {
      index.children = filterAsyncRouter(index.children)
    }
    return true
  })
  return t
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
