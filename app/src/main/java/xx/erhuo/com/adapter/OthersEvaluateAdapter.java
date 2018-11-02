package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import xx.erhuo.com.R;

public class OthersEvaluateAdapter extends RecyclerView.Adapter<OthersEvaluateAdapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_others_history,parent,false);
        //如果attachToRoot=true,则布局文件将转化为View并绑定到root，
        // 然后返回root作为根节点的整个View;如果attachToRoot=false,
        // 则布局文件转化为View但不绑定到root，返回以布局文件根节点为根节点的View。
        // 在这两种情况下，root的LayoutParams***都*会影响布局文件的显示样式。
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_evaluateType;
        TextView tv_evaluateContent;
        TextView tv_commodity;
        TextView tv_time;
        CircleImageView civ_head;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name            = itemView.findViewById(R.id.tv_name_evaluate);
            tv_evaluateType  = itemView.findViewById(R.id.tv_evaluateType);
            tv_evaluateContent = itemView.findViewById(R.id.tv_content_evaluate);
            tv_commodity  = itemView.findViewById(R.id.tv_commodity_evaluate);
            tv_time  = itemView.findViewById(R.id.tv_time_evaluate);
            civ_head = itemView.findViewById(R.id.civ_head_evaluate);
        }
    }
}
