package xx.erhuo.com.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuo.utils.HttpUtils;
import com.erhuo.utils.LogUtils;
import com.erhuo.utils.MyApplication;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xx.erhuo.bean.Constant;
import xx.erhuo.bean.User;
import xx.erhuo.com.Activity_SettingEdit;
import xx.erhuo.com.R;

public class UserSettingAdapter extends RecyclerView.Adapter<UserSettingAdapter.ViewHolder> {
    private List<String> titleList,contentList;
    private Context context;
    private User user;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        void onItemClick(int position,View view);
    }
    public UserSettingAdapter(Context context, List<String> titleList, List<String> contentList){
        this.titleList = titleList;
        this.contentList = contentList;
        this.context = context;
        user = MyApplication.getUser();
        if(titleList.size() != contentList.size()){
            LogUtils.e("SettingAdapter","titleList.size() != contentList.size()");
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_settings,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_title.setText(titleList.get(position));
        holder.tv_content.setText(contentList.get(position));
        holder.iv_iconNext.setOnClickListener(new MOnclickListener(position));

    }

    @Override
    public int getItemCount() {
        return titleList == null ? 0:titleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;
        ImageView iv_iconNext;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_settingItem_title);
            tv_content = itemView.findViewById(R.id.tv_settingItem_content);
            iv_iconNext = itemView.findViewById(R.id.iv_settingItem_iconNext);
        }
    }
    class MOnclickListener implements View.OnClickListener {
        int position;
        MOnclickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(position,view);
            }else{
                LogUtils.e("onItemClickListener","onItemClickListener == null");
            }

        }
    }


}
