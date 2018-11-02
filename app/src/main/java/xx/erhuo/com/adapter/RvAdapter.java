package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.TimeHelper;

import java.util.List;

import xx.erhuo.bmob.MessageBean;
import xx.erhuo.com.R;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {
    private List<MessageBean> listMessages;
    private ItemOnClickListener mClickListener;
    public RvAdapter(List<MessageBean> listMessages){
        this.listMessages = listMessages;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            MessageBean messageBean = listMessages.get(position);
            //LogUtils.e("ViewHolder getName()","====="+messageBean.getName());
            holder.tv_name.setText(messageBean.getName());
            holder.tv_content.setText(messageBean.getContent());
            holder.tv_time.setText(TimeHelper.longToLocalTime(messageBean.getTime()));
            LogUtils.e("messageBean.getHeadImgUrl()",messageBean.getHeadImgUrl());
            if(messageBean.getRecentNum() != 0){
                holder.tv_num.setVisibility(View.VISIBLE);
                holder.tv_num.setText(messageBean.getRecentNum() + "");
            }else{
                holder.tv_num.setVisibility(View.GONE);
            }
            HttpUtils.setImageView(holder.iv_head,messageBean.getHeadImgUrl());

            if (mClickListener != null){
                holder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mClickListener.nameOnClick(view,position);
                    }
                });
                holder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mClickListener.nameOnClick(view,position);
                    }
                });
                holder.rl_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mClickListener.nameOnClick(view,position);
                    }
                });
                holder.iv_head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mClickListener.headImgOnClick(view,position);
                    }
                });
                holder.tv_content.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mClickListener.nameOnLongClick(view,position);
                        return false;
                    }
                });
                holder.rl_message.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mClickListener.nameOnLongClick(view,position);
                        return false;
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        return listMessages == null ? 0:listMessages.size();
    }

    public interface ItemOnClickListener{
        void nameOnClick(View view,int position);
        void headImgOnClick(View view,int position);
        void nameOnLongClick(View view,int position);
    }
    public void setItemOnClickListener(ItemOnClickListener clickListener){
        mClickListener = clickListener;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_content,tv_time,tv_num;
        ImageView iv_head;
        RelativeLayout rl_message;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_messageName);
            tv_time = itemView.findViewById(R.id.tv_message_time);
            iv_head = itemView.findViewById(R.id.iv_headMessage);
            tv_content = itemView.findViewById(R.id.tv_messageDetail);
            rl_message = itemView.findViewById(R.id.rl_message);
            tv_num = itemView.findViewById(R.id.tv_num);
        }
    }
}
