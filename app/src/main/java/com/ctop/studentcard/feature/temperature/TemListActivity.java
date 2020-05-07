package com.ctop.studentcard.feature.temperature;


import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.adapter.TemAdapter;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.greendao.TemBean;
import com.ctop.studentcard.greendao.TemBeanDao;

import java.util.List;

public class TemListActivity extends BaseActivity implements View.OnClickListener {

    private TemBeanDao temBeanDao;
    private List<TemBean> temBeanList;
    private RecyclerView recyclerView;
    private ImageView iv_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tem_list);
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_message);
        iv_empty = findViewById(R.id.iv_empty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();

    }

    private void initData() {
        temBeanDao =  DaoManager.getInstance().getDaoSession().getTemBeanDao();
        temBeanList =   temBeanDao.queryBuilder().where(TemBeanDao.Properties.Time.gt(System.currentTimeMillis()-1000*60*60*24*7)).orderDesc(TemBeanDao.Properties.Time).list();
        if(temBeanList.size()>0){
            iv_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
            recyclerView.setLayoutManager(layoutManager);
            TemAdapter temAdapter = new TemAdapter(temBeanList);
            recyclerView.setAdapter(temAdapter);
            temAdapter.notifyDataSetChanged();
        }else {
            iv_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {

    }
}
