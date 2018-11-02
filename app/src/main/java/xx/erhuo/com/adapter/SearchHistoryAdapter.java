package xx.erhuo.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xx.erhuo.com.R;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyList;
    private HistoryClickListener clickListener;
    public SearchHistoryAdapter(List<String> historyList){
        this.historyList  = historyList;
    }
    public void setHistoryClickListener(HistoryClickListener clickListener){
        this.clickListener = clickListener;
    }
    public interface HistoryClickListener{
        void historyItemClick(View v,String content,int position);
        void historyItemLongClick(View v,String content,int position);
        void deleteAll(View v);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(historyList.size() == 0){
            holder.tv_last.setVisibility(View.GONE);
            return;
        }
        if(position == historyList.size()){
            holder.tv_last.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            holder.tv_last.setOnClickListener(lastItemClick);
        }else{
            holder.tv_last.setVisibility(View.GONE);
            holder.tv.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            String text = historyList.get(historyList.size() - position - 1);
            holder.tv.setText(text);
            // LogUtils.e("onBindViewHolder",historyList.get(position));
            holder.tv.setOnClickListener(new HistoryItemClickListener(text,position));//因为这里是倒序存放的
            holder.tv.setOnLongClickListener(new HistoryLongClickListener(text,position));
        }
    }

    @Override
    public int getItemCount() {
        if(historyList == null){
            return 0;
        }
        if(historyList.size() == 0){
            return 0;
        }
        else return historyList.size() + 1;

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        TextView tv_last;
        View line;
        ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_item_history);
            tv_last = itemView.findViewById(R.id.tv_lastItem);
            line = itemView.findViewById(R.id.v_line);
        }
    }

    /**
     * 点击
     */
    class HistoryItemClickListener implements View.OnClickListener{
        String content;
        int position;
        HistoryItemClickListener(String content,int position){
            this.content = content;
            this.position = position;
        }
        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.historyItemClick(view,content,position);
            }
        }
    }

    /**
     * 长按
     */
    class HistoryLongClickListener implements View.OnLongClickListener{
        String content;
        int position;
        HistoryLongClickListener(String content,int position){
            this.content = content;
            this.position = position;
        }
        @Override
        public boolean onLongClick(View view) {
            if(clickListener != null){
                clickListener.historyItemLongClick(view,content,position);
            }
            return true;
        }
    }

    /**
     * 最后一项点击
     */
    View.OnClickListener lastItemClick =new  View.OnClickListener(){

        @Override
        public void onClick(View view) {

            if(clickListener != null){
                clickListener.deleteAll(view);
            }
        }
    };

}
