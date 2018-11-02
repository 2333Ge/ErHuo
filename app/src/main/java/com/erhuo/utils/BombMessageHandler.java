package com.erhuo.utils;

import android.os.Handler;
import android.util.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import xx.erhuo.bmob.MessageBean;
import xx.erhuo.bmob.MessageDBUtils;

public class BombMessageHandler extends BmobIMMessageHandler {

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //在线消息
            LogUtils.e("onMessageReceive",event.getMessage().getContent());
        setMessageIntoDB(event);
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent eventLarge) {
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
        LogUtils.d("OfflineMessageEvent","离线消息条数：" + eventLarge.getTotalNumber());
        Iterator<List<MessageEvent>> itera = eventLarge.getEventMap().values().iterator();
        while (itera.hasNext()){
            List<MessageEvent> list = itera.next();
            for(int i =0;i < list.size();i++){
                MessageEvent event = list.get(i);
                setMessageIntoDB(event);
            }
        }
    }

    /**
     *
     * @param event
     */
    private void setMessageIntoDB(final MessageEvent event){
        String content = event.getMessage().getContent();
        String thisMessageId = event.getMessage().getFromId();
        String thisMessageName = event.getFromUserInfo().getName();
        String thisMessageImgUrl = event.getFromUserInfo().getAvatar();
        LogUtils.e("onMessageReceive","getFromUserInfo/name == " + thisMessageName);
        LogUtils.e("onMessageReceive","getFromUserInfo/img == " + thisMessageImgUrl);

        MessageBean message = new MessageBean();
        message.setContent(content);
        message.setFromOthers(true);
        message.setUserId(thisMessageId);
        message.setTime(TimeHelper.getTimeNow());
        message.setMessageType(MessageBean.TEXT);
        MessageDBUtils messageDBUtils = MyApplication.getMessageDBUtils();

        message.setName(thisMessageName);
        message.setHeadImgUrl(thisMessageImgUrl);
        String tableName = "bombChat"+ MyApplication.getUser().getUserId()+"and" + thisMessageId;
        //数据库表名字命名规则，bombChat(自己id)and(别人id)
        messageDBUtils.createNewTable(tableName);
        messageDBUtils.insertAMessage(message,tableName);
        messageDBUtils.insertARecentMsg(message);
        LogUtils.e("setMessageIntoDB","thisMessage table name ==" + tableName);
    }
}
