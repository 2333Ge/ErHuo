package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuo.utils.HttpUtils;

import java.util.List;

import xx.erhuo.bean.CommodityCommand;
import xx.erhuo.com.R;

public class CommandAdapter extends RecyclerView.Adapter<CommandAdapter.ViewHolder> {

    private List<CommodityCommand> commandList;
    private OnCommandClickListener onCommandClick;

    public CommandAdapter(List<CommodityCommand> commandList){
        this.commandList = commandList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commodity_command2,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CommodityCommand command = commandList.get(position);
        holder.tv_price.setText(command.getPrice());
        String[] time = command.getTime().split(" ");
        holder.tv_time.setText(time[0]);
        holder.tv_title.setText(command.getTitle());
        HttpUtils.setImageView(holder.iv,command.getImgUrl());
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onCommandClick != null){
                    onCommandClick.onClick(view,command.getId(),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commandList == null ? 0:commandList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv_title,tv_price,tv_time;


        public ViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_command_img);
            tv_price = itemView.findViewById(R.id.tv_command_price);
            tv_title = itemView.findViewById(R.id.tv_command_title);
            tv_time= itemView.findViewById(R.id.tv_command_time);

        }
    }
    public interface OnCommandClickListener{
        void onClick(View v,String id,int position);
    }
    public void setOnCommandClickListener(OnCommandClickListener onCommandClick){
        this.onCommandClick = onCommandClick;
    }

}
