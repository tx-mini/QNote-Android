package com.ace.network.util;


public class ConstUrl {

    public static final String HOST = "http://10.236.232.4:8080/";

    public static final String USER_REGISTER = "user/register"; //注册
    public static final String USER_LOGIN = "user/login"; //登录
    public static final String USER_CHANGE_PASSWORD ="user/changePassword";//修改密码
    public static final String USER_CHANG_EMAIL ="user/changeMail";//修改邮箱
    public static final String USER_CHANGE_USERNAME ="user/changeUsername";//修改昵称
    public static final String USER_GET_INFO ="user/getUserInfo";//获取用户信息

    public static final String MANAGER_ORDER = "order/getOrderByRestaurantId";//获得管理员订单
    public static final String GET_ORDER_BY_USER = "order/getOrder";    //获取用户的订单
    public static final String SAVE_ORDER = "order/saveOrder";    //保存用户的订单
    public static final String UPDATE_ORDER = "order/changeOrder";    //保存用户的订单
    public static final String SENT_ORDER = "order/commitOrder";  //提交订单
    public static final String CHOOSE_COOKER_FOR_ORDER = "order//chooseCookerForOrder";//分配订单给厨师

    public static final String GET_CHEFS_BY_RESTAURANTID = "user/getCookerByRestaurantId";//获取餐厅所有厨师
    public static final String RESTAURANT_GET_ALL = "restaurant/getAll";//获取餐厅信息
    public static final String GET_FOOD_BY_RESTAURANT = "food/getFoodsByRestaurantId";//获取餐厅食物
    public static final String GET_TABLE_BY_RESTAURANT = "/restaurant/getTableByRestaurant";//获取餐厅所有桌子

    public static final String GET_RESTAURANT_BY_MANAGER = "restaurant/getRestaurantByManager";//根据管理员获取餐厅id

    public static final String ADD_FOOD = "food/addFood";//添加食物
    public static final String CHANGE_FOOD = "food/changeFood";//修改食物

    public static final String SENT_COMMENT = "comment/comment";

    public static final String GET_ALL_COMMENT = "comment/getCommentsByRestaurant";

    public static final String  GET_ORDER_BY_COOKER = "order/getOrderByCooker";//厨师查看自己的订单
    public static final String  READY_FOR_ORDER = "order/readyForOrder";//厨师查看自己的订单

}
