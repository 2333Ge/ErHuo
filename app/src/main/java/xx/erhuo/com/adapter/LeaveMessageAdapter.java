package xx.erhuo.com.adapter;

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
import xx.erhuo.bean.LeaveMessage;
import xx.erhuo.com.R;

public class LeaveMessageAdapter extends RecyclerView.Adapter <LeaveMessageAdapter.ViewHolder>{
    private List<LeaveMessage> listMessage ;
    public LeaveMessageAdapter(List<LeaveMessage> listMessage){
        this.listMessage = listMessage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leave_message,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LeaveMessage leaveMessage = listMessage.get(position);
        holder.tv_comment.setText(leaveMessage.getContent());
        holder.tv_name.setText(leaveMessage.getName());
        String[] time = leaveMessage.getTime().split(" ");
        holder.tv_timeYear.setText(time[0]);
        holder.tv_timeMinute.setText(time[1]);
        new SetCircleImageViewTask(holder.civ_head).execute(leaveMessage.getHeadUrl());
    }

    @Override
    public int getItemCount() {
        return listMessage == null ? 0:listMessage.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ_head;
        TextView tv_name,tv_comment,tv_timeMinute,tv_timeYear;
        public ViewHolder(View itemView) {
            super(itemView);
        civ_head = itemView.findViewById(R.id.civ_head_leaveMessage);
        tv_name = itemView.findViewById(R.id.tv_name_leaveMessage);
        tv_comment = itemView.findViewById(R.id.tv_evaluate_leaveMessage);
        tv_timeMinute = itemView.findViewById(R.id.tv_timeMinute_leaveMessage);
        tv_timeYear = itemView.findViewById(R.id.tv_time_leaveMessage);
    }
    }

    class SetCircleImageViewTask extends AsyncTask<String,Void,Bitmap> {
        CircleImageView civ;
        SetCircleImageViewTask(CircleImageView civ){
            this.civ = civ;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            LogUtils.e("leaveMessage=======================",url);
            if( url == null)
                return null;
            return HttpUtils.getBitMapFromUrl(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                civ.setImageBitmap(bitmap);
            }
        }
    }
}
