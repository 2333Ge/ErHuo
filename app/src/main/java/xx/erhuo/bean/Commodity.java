package xx.erhuo.bean;

import java.util.List;

public class Commodity {

    private String title;
    private String content;
    private List<String> imgUrlList;
    private float price;
    private String ownerHeadImgUrl;
    private String id;

    private String date;


    private float priceOriginal;
    private long dateLong;
    //"uid":"76","user_image":"http://39.105.0.212/shop/products/3ddd9a1a1d5df3bed7ef8ae6f9cab18a.png"
    // ,"sex":"male","user_guanzhu":"22","user_onsell_num":"20","user_jiaoyi":"5"
    private String ownerId;
    private String ownerName;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    private boolean ownerSex;

    public int getGuanZhu() {
        return guanZhu;
    }

    public void setGuanZhu(int guanZhu) {
        this.guanZhu = guanZhu;
    }

    private int guanZhu;
    private int onSellNum;
    private int selledNum;

    public String getOwnerHeadImgUrl() {
        return ownerHeadImgUrl;
    }

    public void setOwnerHeadImgUrl(String ownerHeadImgUrl) {
        this.ownerHeadImgUrl = ownerHeadImgUrl;
    }

    public boolean isOwnerSex() {
        return ownerSex;
    }

    public void setOwnerSex(boolean ownerSex) {
        this.ownerSex = ownerSex;
    }

    public int getOnSellNum() {
        return onSellNum;
    }

    public void setOnSellNum(int onSellNum) {
        this.onSellNum = onSellNum;
    }

    public int getSelledNum() {
        return selledNum;
    }

    public void setSelledNum(int selledNum) {
        this.selledNum = selledNum;
    }

    private int concerned;
    public int getConcerned() {
        return concerned;
    }

    public void setConcerned(int concerned) {
        this.concerned = concerned;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(float priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }
}
