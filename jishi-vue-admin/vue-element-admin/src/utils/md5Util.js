/**
 * 引入md5工具，博客: https://www.cnblogs.com/hspl/p/11189088.html
 */
import md5 from 'js-md5'
// 加密文本前缀
const prefix = 'li'
// 加密文本后缀
const suffix = 'yang'
// 插入前缀的下标
const index = 1
// 插入后缀的下标
const lastIndex = 3
/**
 * 管理员密码md5加密
 * @param pswd
 * @returns {string}
 */
export default {
  /**
   * <h2>加密 密码</h2>
   * 加密规则： 加密后的md5 字符串，在下标 ${index}的位置插入前缀字符串，md5字符串长度减${index}插入后缀
   * @param pswd 密码字符串
   * @returns {*}
   */
  getMd5Pswd(pswd) {
    let md5Str = md5(pswd)
    // 插入前缀
    md5Str = md5Str.slice(0, index) + prefix + md5Str.slice(index)
    // 插入后缀
    md5Str = md5Str.slice(0, md5Str.length - lastIndex) + suffix + md5Str.slice(md5Str.length - lastIndex)
    return md5Str
  }
}
