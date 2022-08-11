import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'

/* Router Modules */
Vue.use(Router)

// import chartsRouter from './modules/charts'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin-manage','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/use/login/index'),
    hidden: true,
    meta: { title: 'Login' }
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/use/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: '面板',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '面板', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/my-info',
    component: Layout,
    redirect: '/dashboard',
    name: 'myinfo',
    children: [
      {
        path: '/my-info',
        component: () => import('@/views/use/my-info/index'),
        name: 'myinfo',
        meta: { title: '个人信息', icon: 'dashboard', affix: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  // children 子级菜单 超过两级 keep缓存会失效，vue-element-admin 小bug
  // 管理员管理
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin',
    alwaysShow: true, // will always show the root menu
    name: 'admin-manage',
    meta: {
      title: '管理员管理',
      icon: 'user'
    },
    children: [

      {
        path: '/admin/index',
        component: () => import('@/views/use/admin/index'),
        name: 'admin-plant-index',
        meta: {title: '管理员列表', icon: 'edit'}
      },
      {
        path: '/admin/index',
        component: () => import('@/views/use/admin/index'),
        name: 'admin-plant-index',
        meta: {title: '管理员列表', icon: 'edit'},
        hidden: true // 不显示在 左侧导航栏菜单
      }
    ]
  },
  // 系统管理
  {
    path: '/system',
    component: Layout,
    name: 'system-manage',
    meta: {
      title: '系统管理',
      icon: 'user'
    },
    children: [
      {
        path: '/system/menu/index',
        component: () => import('@/views/use/system-manage/menu/index'),
        name: 'system-menu-index',
        meta: { title: '菜单管理', icon: 'edit' }
      }
    ]
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior(to, from, savedPosition) {
    // 解决路由跳转后 会滚动到底部
    if (savedPosition) {
      console.info(1)
      return savedPosition
    } else {
      return { x: 0, y: 0 }
    }
  },
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
