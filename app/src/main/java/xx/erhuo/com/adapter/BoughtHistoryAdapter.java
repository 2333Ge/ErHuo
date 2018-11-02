package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;

import java.util.List;

import xx.erhuo.bean.SoldHistoryBean;
import xx.erhuo.com.R;

public class BoughtHistoryAdapter extends RecyclerView.Adapter<BoughtHistoryAdapter.ViewHolder> {

    private List<SoldHistoryBean> historyBeanList;


    public BoughtHistoryAdapter(List<SoldHistoryBean> historyBeanList){
        this.historyBeanList = historyBeanList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bought,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SoldHistoryBean bean = historyBeanList.get(position);
        String[] timeDay = bean.getDate().split(" ");
        holder.tv_date.setText(timeDay[0]);
        holder.tv_title.setText(bean.getTitle());
        holder.tv_price.setText(bean.getPrice());
        String evaluate = bean.getLeaveMessage();
        if(evaluate != null && !evaluate.equals("null")){

            holder.tv_default.setText("查看评价");
        }else{
            holder.tv_default.setText("尚未给出评价");
        }
        HttpUtils.setImageView(holder.iv_img,bean.getImgUrl());
        if(itemClickListener != null){
            holder.rl_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.otherClick(position);
                }
            });
            holder.tv_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.defaultClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_img;
        TextView tv_title,tv_date,tv_price,tv_default;
        RelativeLayout rl_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_boughtHistory);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_time);
            tv_price = itemView.findViewById(R.id.tv_priceBoughtHistory);
            tv_default = itemView.findViewById(R.id.tv_evaluateOrNot);
            rl_parent = itemView.findViewById(R.id.rl_parent);
        }
    }

    private OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void defaultClick(int position);
        void otherClick(int position);
    }
}
