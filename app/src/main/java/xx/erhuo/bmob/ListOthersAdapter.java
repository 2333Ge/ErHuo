package xx.erhuo.bmob;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xx.erhuo.com.R;

public class ListOthersAdapter extends RecyclerView.Adapter<ListOthersAdapter.ViewHolder> {
    private List<OthersBean> listOthers;
    private OnClickListener onClickListener;
    public ListOthersAdapter(List<OthersBean> listOthers){
        this.listOthers = listOthers;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_others,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_name.setText(listOthers.get(position).getName());
        String imgUrl = listOthers.get(position).getImgUrl();
        HttpUtils.setImageView(holder.civ_head,imgUrl);
        if (onClickListener != null){
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.nameOnClick(view,position);
                }
            });
            holder.civ_head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.imgOnClick(view,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOthers.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView civ_head;
        TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            civ_head = itemView.findViewById(R.id.iv_others_head);
            tv_name = itemView.findViewById(R.id.tv_others_name);
        }
    }
    public interface OnClickListener{
        void nameOnClick(View v,int position);
        void imgOnClick(View v,int position);
    }
    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }



}

