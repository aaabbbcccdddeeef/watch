package com.ctop.studentcard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.bean.CallPhoneBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordAdapter.ViewHolder> {


    private SimpleDateFormat mDestFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA);

    private List<CallPhoneBean> mList = new ArrayList<CallPhoneBean>();

    private SimpleDateFormat mSourceFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    public CallRecordAdapter(List<CallPhoneBean> mList) {
        this.mList = mList;
    }

    public int getItemCount() {
        return this.mList.size();
    }

    public List<CallPhoneBean> getList() {
        return this.mList;
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final CallPhoneBean callPhoneBean = this.mList.get(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (CallRecordAdapter.this.onCallItemClickListener != null)
                    CallRecordAdapter.this.onCallItemClickListener.onItemClick(position);
            }
        });
        String str2 = callPhoneBean.getNumber();
        viewHolder.tv_phone.setText(str2);
        viewHolder.tv_status.setText(callPhoneBean.getState());
        viewHolder.tv_name.setText(callPhoneBean.getName());
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_call_record, viewGroup, false));
    }

    public void setList(List<CallPhoneBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_status;
        TextView tv_phone;

        public ViewHolder(View param1View) {
            super(param1View);
            this.tv_name = param1View.findViewById(R.id.tv_name);
            this.tv_status = param1View.findViewById(R.id.tv_status);
            this.tv_phone = param1View.findViewById(R.id.tv_phone);
        }
    }

    private OnCallItemClickListener onCallItemClickListener;

    public interface OnCallItemClickListener {
        void onItemClick(int position);
    }

    public void setOnCallItemClickListener(OnCallItemClickListener onCallItemClickListener) {
        this.onCallItemClickListener = onCallItemClickListener;
    }
}

