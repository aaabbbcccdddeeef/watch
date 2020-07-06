package com.wisdomin.studentcard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.greendao.SmsMessage;
import com.wisdomin.studentcard.util.TimeUtils;

import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.ViewHolder> {

    private List<SmsMessage> mSmsMessageList;

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

    public SmsAdapter(List<SmsMessage> SmsMessageList) {
        mSmsMessageList = SmsMessageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smsmessage, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SmsMessage smsMessage = mSmsMessageList.get(position);
        holder.sms_time.setText(TimeUtils.getShortTime(smsMessage.getTime()));
        holder.sms_content.setText(smsMessage.getMessage());
        holder.ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickable.onItemClick(position);
            }
        });
        if(smsMessage.getStatus()==0){
            holder.no_read.setVisibility(View.VISIBLE);
        }else {
            holder.no_read.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mSmsMessageList.size();
    }

    public interface OnItemClickable{
        void onItemClick(int position);
    }

    OnItemClickable onItemClickable;

    public void setOnItemClickable(OnItemClickable onItemClickable) {
        this.onItemClickable = onItemClickable;
    }
}