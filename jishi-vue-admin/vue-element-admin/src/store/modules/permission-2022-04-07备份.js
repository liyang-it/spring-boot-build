import {asyncRoutes, constantRoutes} from '@/router'
import store from '@/store'
import request from '@/utils/request'

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
      let userMenu = store.state.user.menu
      let userRoutes = []
      // let ss = accessedRoutes.find(o >= o.name === 'SN管理')
      /*
      for(let i = 0; i < accessedRoutes.length; i ++){
        console.info('遍历', accessedRoutes[i])
      }
      console.info('设置动态菜单权限', accessedRoutes)
      commit('SET_ROUTES', accessedRoutes)
       */
      let all = userMenu.find( o => o === '*')
      if(all === undefined){
        /*
        // 不是超级管理员，只拥有部分权限, 旧逻辑，控制粒度太大，不合适，只能控制大模块
        for(let i = 0; i < userMenu.length; i ++){
          // 查询用户菜单对应的路由
          let query = accessedRoutes.find(o => o.name === userMenu[i])
          if(query !== undefined){
             userRoutes.push(query)
           }

        }
         */
        // 请求后台获取 当前管理员所拥有的菜单权限
        request.get('/admin/system/menu/queryAll').then((res)=>{
          userRoutes = res.data
          console.info(111, userRoutes)
          commit('SET_ROUTES', userRoutes)
        })

      }else{
        // 超级管理员，全部权限
        userRoutes = accessedRoutes
        commit('SET_ROUTES', userRoutes)
      }
      console.info('设置动态菜单权限', userRoutes)



      resolve(userRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
