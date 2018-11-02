package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;

import java.util.List;

import xx.erhuo.bean.Commodity;
import xx.erhuo.bean.CommodityCommand;
import xx.erhuo.bmob.OthersBean;
import xx.erhuo.com.R;

public class AdapterHomeCommodity extends RecyclerView.Adapter<AdapterHomeCommodity.ViewHolder> {
    List<CommodityCommand> listCommodity;
    public AdapterHomeCommodity(List<CommodityCommand> listCommodity){
        this.listCommodity = listCommodity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homecommodity,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HttpUtils.setImageView(holder.iv_commodity1,listCommodity.get(position).getImgUrl());

//        holder.iv_commodity2.setVisibility(View.GONE);
//        holder.iv_commodity3.setVisibility(View.GONE);
        holder.tv_price.setText("" + listCommodity.get(position).getPrice());
        holder.tv_title.setText(listCommodity.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return listCommodity.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_commodity1,iv_commodity2,iv_commodity3;
        TextView tv_title,tv_price;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_commodity1 = itemView.findViewById(R.id.iv_commodity1);
//            iv_commodity2 = itemView.findViewById(R.id.iv_commodity2);
//            iv_commodity3 = itemView.findViewById(R.id.iv_commodity3);
            tv_title = itemView.findViewById(R.id.tv_commodityDetail);
            tv_price = itemView.findViewById(R.id.tv_commodityPrice);
        }
    }

}
