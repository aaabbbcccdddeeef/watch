package com.ctop.studentcard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.greendao.TemBean;
import com.ctop.studentcard.util.TimeUtils;

import java.util.List;

public class TemAdapter extends RecyclerView.Adapter<TemAdapter.ViewHolder> {

    private List<TemBean> mTemBeanList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView no_read;
        TextView sms_time;
        TextView sms_content;
        RelativeLayout ll_all;

        public ViewHolder(View view) {
            super(view);
            no_read = view.findViewById(R.id.no_read);
            sms_time = view.findViewById(R.id.sms_time);
            sms_content = view.findViewById(R.id.sms_content);
            ll_all = view.findViewById(R.id.ll_all);
        }
    }

    public TemAdapter(List<TemBean> temBeanList) {
        mTemBeanList = temBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smsmessage, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TemBean temBean = mTemBeanList.get(position);
        holder.sms_time.setText(TimeUtils.getShortTime(temBean.getTime()));
        holder.sms_content.setText(temBean.getTem());
        if(temBean.getStatus()==0){
            holder.no_read.setVisibility(View.VISIBLE);
        }else {
            holder.no_read.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTemBeanList.size();
    }
}