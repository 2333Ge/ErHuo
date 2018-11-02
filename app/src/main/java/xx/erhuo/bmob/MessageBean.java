package xx.erhuo.bmob;

public class MessageBean {



    private String userId;
    private Boolean fromOthers;//信息的来源，true发过来，false发过去
    private String name;
    private Long time;
    private String content;
    private String headImgUrl;
    private boolean isSendSuccessful = true;
    private String messageType = TEXT;//文本(text);连接(url);音频(voice);视频(video);图片(image);图文(news)

    private int recentNum;//专为查看最近消息使用
    public static final String TEXT = "text";

    public int getRecentNum() {
        return recentNum;
    }

    public void setRecentNum(int recentNum) {
        this.recentNum = recentNum;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isSendSuccessful() {
        return isSendSuccessful;
    }

    public void setSendSuccessful(boolean sendSuccessful) {
        isSendSuccessful = sendSuccessful;
    }


    public Boolean getFromOthers() {
        return fromOthers;
    }

    public void setFromOthers(Boolean fromOthers) {
        this.fromOthers = fromOthers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "userId='" + userId + '\'' +
                ", fromOthers=" + fromOthers +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", isSendSuccessful=" + isSendSuccessful +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
