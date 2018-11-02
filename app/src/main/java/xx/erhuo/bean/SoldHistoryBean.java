package xx.erhuo.bean;

/**
 * 买历史和卖历史共用一个实体类
 */
public class SoldHistoryBean {
    //[{"product_name":"果6s16g全网通，自己用的手机屏幕没有划痕，后面有痕迹自己看图",
    // "pid":"143","image":"http://39.105.0.212/shop/products/cb36ff75e30c46f29ef6be7e001b6114.jpg"
    // ,"price":"800.0","liuyan":"null","uid_buyer":"77","name_buyer":"冰雪灵之心",
    // "payDate":"2018-6-26 3:59:55"}
    private String title,price,buyerName;
    private String pid,buyerId;
    private String imgUrl;
    private String leaveMessage;
    private String date;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
