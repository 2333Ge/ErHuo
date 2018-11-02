package xx.erhuo.com.adapter;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import xx.erhuo.bean.Commodity;
import xx.erhuo.com.R;

public class RvSearchAdapter extends RecyclerView.Adapter<RvSearchAdapter.ViewHolder>{
    private List<Commodity> commodityList;
    private CommodityClickListener listener;
    private List<Bitmap> listBitmap = new ArrayList<>();
    public RvSearchAdapter(List<Commodity> commodityList){
        this.commodityList = commodityList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Commodity commodity = commodityList.get(position);
        holder.tv_price.setText(commodity.getPrice() + "");
        holder.tv_title.setText(commodity.getTitle());
        float priceOriginal = commodity.getPriceOriginal();
        String[] timeDay = commodity.getDate().split(" ");
        holder.tv_time.setText(timeDay[0]);
        if( priceOriginal != 0){
            holder.tv_priceOld.setText(priceOriginal + "");
            holder.tv_priceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_priceOld.setVisibility(View.VISIBLE);
        }else{
            holder.tv_priceOld.setVisibility(View.GONE);
        }
        CommodityClick click = new CommodityClick(position);
        holder.iv_commodity.setOnClickListener(click);
        holder.tv_title.setOnClickListener(click);
        holder.tv_price.setOnClickListener(click);
//        if(listBitmap.size() <= position){
//            Bitmap temp = HttpUtils.getBitMapFromUrl(commodityList.get(position).getImgUrlList().get(0));
//        }
        HttpUtils.setImageView(holder.iv_commodity,commodityList.get(position).getImgUrlList().get(0));
    }

    @Override
    public int getItemCount() {
        return commodityList == null ? 0:commodityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title,tv_price,tv_time,tv_priceOld;
        ImageView iv_commodity;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_searchItem);
            tv_price = itemView.findViewById(R.id.tv_price_searchItem);
            tv_time = itemView.findViewById(R.id.tv_time_searchItem);
            iv_commodity = itemView.findViewById(R.id.iv_searchItem);
            tv_priceOld = itemView.findViewById(R.id.tv_priceOld_searchItem);

        }
    }

    public interface CommodityClickListener{
        void onClick(View view, int position);
    }
    public void setCommodityClickListener(CommodityClickListener listener){
        this.listener = listener;
    }

    class CommodityClick implements View.OnClickListener{
        int position;
        CommodityClick(int position){
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if(listener != null){
                listener.onClick(view,position);
            }
        }
    }
}
