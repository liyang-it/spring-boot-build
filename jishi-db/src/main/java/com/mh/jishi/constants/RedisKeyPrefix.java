package com.mh.jishi.constants;

/**
 * @Author ani
 * @Description 定义 特殊redis  key前缀
 * @CreateTime 2021-08-19 下午 2:23
 **/
public class RedisKeyPrefix {
    /**
     * 接口请求限制， 第一个占位符是 接口地址, 第二个占位符是 ip
     */
    public final static String CurrentLimitingRequest = "pms:current-limiting:%s:%s";
    /**
     * 页面内容前缀
     */
    public final static String PageContent = "jishi:content:page:";

    /**
     * 开屏广告缓存键
     */
    public final static String AppStartAd = "jishi:system:start-ad";

    /**
     * 系统配置前缀
     */
    public final static String SystemConfig = "jishi:system:";

    /**
     * 省城市 list
     */
    public final static String CityByProvinceList = "jishi:city:province-list";


    /**
     * 市 hash格式 key 为 省id，value 为 市列表
     */
    public final static String CityByCityList = "jishi:city:city-list";

    /**
     * 县 hash格式 key 为 市id，value 为县数据
     */
    public final static String CityByAreaList = "jishi:city:area-list";

    /**
     * 按照pinyin列分组 list
     */
    public final static String CityGroupByPinYinList = "jishi:city:group:list";

    /**
     * 城市查询 ，缓存
     */
    public final static String QueryCityGroupByPinYin = "jishi:city:group:query";

    /**
     * 热门城市
     */
    public final static String QueryCityGroupByHot = "jishi:city:group:hot";

    /**
     * 系统菜单总数量
     */
    public final static String SystemAdminMenuCount = "jishi:system:menu:count";

    /**
     * 管理员菜单，hash格式， 管理员id为键， 值为 菜单
     */
    public final static String SyStemAdminMenu = "jishi:system:menu:admin";

    /**
     * 所有菜单管理
     */
    public final static String SyStemAllMenu = "jishi:system:menu:all";
    /**
     * 管理员菜单ID列表， hash格式，key为管理员id， value为 id 列表
     */
    public final static String SystemAdminMenuIds = "jishi:system:menu:ids";

}
