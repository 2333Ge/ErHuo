package xx.erhuo.bean;

public final class Constant {

    //所有网络请求传递参数的方式可以为get也可以为post
    /**
     *搜索用户url
     */
    public static final String findUsersUrl = "http://39.105.0.212/android/user_findUserByKeyword";
    //?keyword=***
    /**
     *商品推荐url
     */
    public static final String findCommandProductsUrl = "http://39.105.0.212/android/product_findFourProducts";
    /**
     *修改用户url
     */
    public static final String updateUserInformationUrl = "http://39.105.0.212/android/user_updateUser";
    /*
    * 用户id:   uid
昵称:	username
姓名:	name
性别:	sex
生日:	birth(字符串类型)
电话:	telephone
qq:		birthday(你按照birthday给我传递qq,一开始数据库写错了,但是不想改了,太麻烦)
邮箱:	email
地址:	addr
修改成功:返回success  失败返回fail*/
    /**
     * 查看其他用户资料
     */
    public static final String findUserInformationUrl = "http://39.105.0.212/android/user_findUserDataByUid";
    //?uid=***
    /**
     * 发送具体商品评论
     */
    public static final String sendCommentUrl = "http://39.105.0.212/android/product_comment";
    //uid=***&pid=***&comment=***
    /**
     * 查看某个商品的评论
     */
    public static final String checkCommentUrl = "http://39.105.0.212/android/product_liuyan";
    //pid=***

    /**
     * 查看某人的历史交易
     */
    //[{"product_name":"果6s16g全网通，自己用的手机屏幕没有划痕，后面有痕迹自己看图",
    // "pid":"143","image":"http://39.105.0.212/shop/products/cb36ff75e30c46f29ef6be7e001b6114.jpg"
    // ,"price":"800.0","liuyan":"null","uid_buyer":"77","name_buyer":"冰雪灵之心",
    // "payDate":"2018-6-26 3:59:55"}
    public static final String checkSoldHistoryUrl = "http://39.105.0.212/android/user_ownSellThings";
    //uid=
    /**
     *查看卖记录
     */
    //{"product_name":"蒂佳婷药丸面膜","pid":"183",
    // "image":"http://39.105.0.212/shop/products/89b3074f732a445ebffb1ea3124e14db.jpg",
    // "price":"35.0","liuyan":"null","uid_seller":"77","name_seller":"冰雪灵之心",
    // "payDate":"2018-6-26 5:03:27"}
    public static final String checkBoughtHistoryUrl = "http://39.105.0.212/android/user_ownBuyThings";
    //uid=***
    /**
     * 查看关注
     */
    public static final String checkConcernUrl = "http://39.105.0.212/android/user_findGuanZhu";
    //uid=***
    /**
     * 首页大推荐
     */
    public static final String checkProductRecommendUrl = "http://39.105.0.212/android/product_recommend";
    /**
     * 关注某人
     */
    public static final String concernSomeOneUrl = "http://39.105.0.212/android/user_guanzhuOther";
    //uid=***&uid2=***
    /**
     * 关注某物
     */
    public static final String concernSomethingUrl = "http://39.105.0.212/android/user_userToProduct";
    //?uid=***&pid***
    /**
     * 查看已经上架的
     */
    public static final String checkOnSellUrl = "http://39.105.0.212/android/product_findOnSellByUid";
    //uid=***
    /**
     * 下架，成功返回success失败返回fail
     */
    public static final String disOnSellUrl = "http://39.105.0.212/android/product_downProduct";
    //idss=***
    //其中idss为商品的id,可以为1个,也可以为多个,如果对多个商品进行下架,需要把每个商品的id使用逗号连接起来
    public static final String findNameAndImgUrl = "http://39.105.0.212/android/user_findUserByUidEasy";
    //uid=***
}
