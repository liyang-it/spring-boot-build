import axios from 'axios'
import {Message} from 'element-ui'
import store from '@/store'
import {getToken} from '@/utils/auth'
import router from '@/router'

// create an axios instance
const service = axios.create({
  // 接口地址
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // baseURL: process.env.NODE_ENV === 'development' ? undefined : process.env.VUE_APP_BASE_API,
  // withCredentials: true, // send cookies when cross-domain requests
  // 超时时间 毫秒
  timeout: 1200000
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      // 请求头配置
      config.headers['token'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    // 请求异常配置
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data

    /**
     * 状态码 判断请求状态
     */
    if (res.code === 601) {
      console.info('未登录')
      Message({
        message: '请登录',
        type: 'error',
        duration: 5 * 1000
      })
      setTimeout(() => {
        router.push({
          path: '/login'
        })
      }, 300);
      return Promise.reject(new Error(res.message || 'Error'))
    } else if (res.code === 506) {
      // 接口限流异常状态码
      Message({
        message: res.msg,
        type: 'info',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else if (res.code <= 505 && res.code > 0) {
      // 状态不是 0 都是异常
      Message({
        message: res.msg,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    Message({
      message: '网络连接错误',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)
export default service
