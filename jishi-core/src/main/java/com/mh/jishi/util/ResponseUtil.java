package com.mh.jishi.util;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应操作结果
 * <pre>
 *  {
 *      errno： 错误码，
 *      errmsg：错误消息，
 *      data：  响应数据
 *  }
 * </pre>
 *
 * <p>
 * 错误码：
 * <ul>
 * <li> 0，成功；
 * <li> 4xx，前端错误，说明前端开发者需要重新了解后端接口使用规范：
 * <ul>
 * <li> 401，参数错误，即前端没有传递后端需要的参数；
 * <li> 402，参数值错误，即前端传递的参数值不符合后端接收范围。
 * </ul>
 * <li> 5xx，后端错误，除501外，说明后端开发者应该继续优化代码，尽量避免返回后端错误码：
 * <ul>
 * <li> 501，验证失败，即后端要求用户登录；
 * <li> 502，系统内部错误，即没有合适命名的后端内部错误；
 * <li> 503，业务不支持，即后端虽然定义了接口，但是还没有实现功能；
 * <li> 504，更新数据失效，即后端采用了乐观锁更新，而并发更新时存在数据更新失效；
 * <li> 505，更新数据失败，即后端数据库更新失败（正常情况应该更新成功）。
 * </ul>
 * <li> 6xx，小商城后端业务错误码，
 * 具体见litemall-admin-api模块的AdminResponseCode。
 * <li> 7xx，管理后台后端业务错误码，
 * 具体见litemall-wx-api模块的WxResponseCode。
 * </ul>
 */
public class ResponseUtil<T> {
    private int code;
    private String msg;
    private T data;

    public ResponseUtil(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseUtil{" +
                "状态码:[code] = " + code +
                ", 响应信息:[msg] = '" + msg + '\'' +
                ", 响应数据:[data] = " + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ResponseUtil ok() {
        return new ResponseUtil(0, "请求成功", null);
    }
    public static ResponseUtil okUpd() {
        return new ResponseUtil(0, "修改成功", null);
    }
    public static ResponseUtil okDel() {
        return new ResponseUtil(0, "刪除成功", null);
    }
    public static ResponseUtil ok(String msg, Object data) {
        return new ResponseUtil(0, msg, data);
    }

    public static ResponseUtil ok(Object data) {
        return new ResponseUtil(0, "请求成功", data);
    }
    public static ResponseUtil updateFailed() {
        return fail(504, "数据已被更新，请刷新重试", null);
    }

    /**
     * 业务状态
     * 状态码为 401
     * @return
     */
    public static ResponseUtil business(String msg){
        return fail(401, msg, null);
    }

    public static ResponseUtil server(String msg){
        return fail(501, msg, null);
    }
    public static ResponseUtil fail() {
        return new ResponseUtil(-1, "请求失败", null);
    }

    public static ResponseUtil fail(int errno, String errmsg) {
        return new ResponseUtil(errno, errmsg, null);
    }

    public static ResponseUtil fail( String errmsg) {
        return new ResponseUtil(401, errmsg, null);
    }

    public static ResponseUtil fail(int errno, String errmsg, String data) {
        return new ResponseUtil(errno, errmsg, data);
    }

    public static ResponseUtil badArgument() {
        return fail(401, "参数不对", null);
    }
    public static ResponseUtil badArgumentValue() {
        return fail(402, "参数值不对", null);
    }

    public static ResponseUtil unlogin() {
        return fail(601, "请登录", null);
    }

    public static ResponseUtil serious() {
        return fail(502, "系统内部异常", null);
    }
    public static ResponseUtil updatedDataFailed() {
        return fail(505, "更新數據失敗", null);
    }

    public static ResponseUtil unauthz() {
        return fail(506, "您沒有此功能權限", null);
    }
}

