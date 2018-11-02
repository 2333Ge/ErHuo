package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.erhuo.utils.HttpUtils;

import java.util.List;

import xx.erhuo.com.R;

public class CommodityImgAdapter extends RecyclerView.Adapter<CommodityImgAdapter.ViewHolder> {
    private List<String> imgList;
    public CommodityImgAdapter(List<String> imgList){
        this.imgList = imgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acommodity_img,parent,false);
        //如果attachToRoot=true,则布局文件将转化为View并绑定到root，
        // 然后返回root作为根节点的整个View;如果attachToRoot=false,
        // 则布局文件转化为View但不绑定到root，返回以布局文件根节点为根节点的View。
        // 在这两种情况下，root的LayoutParams***都*会影响布局文件的显示样式。
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HttpUtils.setImageView(holder.iv_commodityImg,imgList.get(position));

    }

    @Override
    public int getItemCount() {
        return imgList == null ? 0:imgList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_commodityImg;

        ViewHolder(View itemView) {
            super(itemView);
            iv_commodityImg = itemView.findViewById(R.id.iv_item_aCommodity);
        }
    }
}
