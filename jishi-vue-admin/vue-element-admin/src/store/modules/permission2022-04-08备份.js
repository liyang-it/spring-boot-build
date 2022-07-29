import {asyncRoutes, constantRoutes} from '@/router'
import store from '@/store'
import request from '@/utils/request'
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
      // 设置动态菜单权限
      // 拿到用户的menu数组

      let userRoutes = []
      let isSuper = store.state.user.isSuper
      if(isSuper === false){
        // 请求后台获取 当前管理员所拥有的菜单权限
        request.get('/admin/system/menu/queryAll').then((res)=>{
          userRoutes = res.data

          /**
           *  遍历，将后端传回的"component": "Layout", 转为"component": Layout组件对象
           *  参考文档: https://segmentfault.com/a/1190000015419713
           */
          userRoutes = filterAsyncRouter(res.data)
          commit('SET_ROUTES', userRoutes)
          resolve(userRoutes)
        })

      }else{
        // 超级管理员，全部权限
        userRoutes = accessedRoutes
        commit('SET_ROUTES', userRoutes)
        resolve(userRoutes)
      }
      console.info('设置动态菜单权限', accessedRoutes)

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
function find(str,cha,num){
  var x=str.indexOf(cha);
  for(var i=0;i<num;i++){
    x=str.indexOf(cha,x+1);
  }
  return x;
}

/**
 * 获取动态管理员菜单信息
 * @param t 管理员菜单
 * @returns {*}
 */
function filterAsyncRouter(t){
  t.filter(index =>{
    if(index.component === 'Layout'){
      index.component = Layout
    }else{
      // 不是路由菜单，转换对应 vue组件
      // @/views/sn-manage/dict/index
      let component = index.component
      console.info(component)
      // 截取 示例 @/views/sn-manage/dict/index 截取出 @/viesw/ 以外的字符，因为 拼接会异常
      let test = component.substr(find(component, '/', 1) + 1)
      index.component = require(`@/views/${test}.vue`).default

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
