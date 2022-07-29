import request from './request'

export default {
    /**
     * 下载文件
     * @param url 文件接口地址，网络文件不支持，只支持本项目接口
     * @param fileName 文件名称
     * @return 返回一个Promise对象，用 then接受返回结果, 返回结果 true或者 false
     */
    downFile(url, fileName) {
        let s = request.get(url, { responseType: "blob" }).then((res) => {
            let url = window.URL.createObjectURL(res); // 创建一个临时的url指向blob对象
            let a = document.createElement("a");
            a.href = url;
            a.download = fileName;
            a.click();
            // 释放这个临时的对象url
            window.URL.revokeObjectURL(url);
            return true
        }).catch((err) => {
            return true
        });
        return s
    },
    /**
     * 下载文件,带查询参数
     * @param url 文件接口地址，网络文件不支持，只支持本项目接口
     * @param params json参数对象
     * @param fileName 文件名称
     * @return 返回一个Promise对象，用 then接受返回结果, 返回结果 true或者 false
     */
    downFileInParams(url, params, fileName) {
        let s = request({
            'url': url,
            'params': params,
            'method': 'GET',
            'responseType':'blob' }).then((res) => {
            let url = window.URL.createObjectURL(res); // 创建一个临时的url指向blob对象
            let a = document.createElement("a");
            a.href = url;
            a.download = fileName;
            a.click();
            // 释放这个临时的对象url
            window.URL.revokeObjectURL(url);
            return true
        }).catch((err) => {
            return true
        });
        return s
    }
}
