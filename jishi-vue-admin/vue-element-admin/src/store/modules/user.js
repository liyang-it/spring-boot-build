import {getInfo, login, logout} from '@/api/user'
import {getToken, removeToken, setToken} from '@/utils/auth'
import router, {resetRouter} from '@/router'
import {Message} from 'element-ui'

const state = {
  token: getToken(),
  name: '',
  avatar: '',
  introduction: '',
  roles: [],
  menuIds:[],
  isSuper: false,
  menu: [],
  plantInfo:{},
}

const mutations = {
  SET_PLANT_INFO: (state, i) =>{
    // 账户厂家信息
    state.plantInfo = i
  },
  SET_MENU: (state, i) =>{
    // 管理员菜单
    state.menu = i
  },
  SET_MENU_IDS: (state, ids) =>{
    // 设置管理员菜单ID
    state.menuIds = ids
  },
  SET_IS_SUPER: (state, i) =>{
    // 设置是否为超级管理员
    state.isSuper = i
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  // user login
  login({ commit }, userInfo) {
    const { username, password, code } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password, code: code }).then(response => {
        // 验证登录逻辑
        console.info('data', response)
        if(response.code !== 0){
          Message({
            message: response.msg,
            type: 'error',
            duration: 5 * 1000
          })
          reject({})
        }
        let token = response.data.token;
        commit('SET_TOKEN', token)
        setToken(token)
        resolve()

      }).catch(error => {
        console.info('登录异常')
        reject(error)
      })
      console.info('完成请求登录')
    })
  },

  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      //此方法是login登陆成功后执行用写死的数据代替返回值，注意框架结构！
      getInfo(state.token).then(response => {
        console.info('请求数据', response)
        const  data  = {
          roles: ['admin'],
          introduction: 'I am a super administrator',
          avatar: response.data.avatar,
          name: response.data.name,
          menuIds: response.data.menuIds,
          isSuperAdmin: response.data.isSuperAdmin,
          menu: response.data.menu,
          plantInfo: {plantType: response.data.plantType, info: response.data.plantInfo},
        }

        if (!data) {
          reject('Verification failed, please Login again.')
        }

        const { roles, name, avatar, introduction, menuIds, isSuperAdmin, menu,  plantInfo} = data

        // roles must be a non-empty array
        if (!roles || roles.length <= 0) {
          reject('getInfo: roles must be a non-null array!')
        }

        commit('SET_ROLES', roles)
        commit('SET_NAME', name)
        commit('SET_AVATAR', avatar)
        commit('SET_INTRODUCTION', introduction)
        commit('SET_MENU_IDS', menuIds)
        commit('SET_IS_SUPER', isSuperAdmin)
        commit('SET_MENU', menu)
        commit('SET_PLANT_INFO', plantInfo)

        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  // user logout
  logout({ commit, state, dispatch }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        removeToken()
        resetRouter()

        // reset visited views and cached views
        // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
        dispatch('tagsView/delAllViews', null, { root: true })

        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_ROLES', [])
      removeToken()
      resolve()
    })
  },

  // dynamically modify permissions
  async changeRoles({ commit, dispatch }, role) {
    const token = role + '-token'

    commit('SET_TOKEN', token)
    setToken(token)

    const { roles } = await dispatch('getInfo')

    resetRouter()

    // generate accessible routes map based on roles
    const accessRoutes = await dispatch('permission/generateRoutes', roles, { root: true })
    // dynamically add accessible routes
    router.addRoutes(accessRoutes)

    // reset visited views and cached views
    dispatch('tagsView/delAllViews', null, { root: true })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
