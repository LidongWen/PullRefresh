package com.wenld.pullrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wenld.pullrefreshlib.DefaultLeftLoadCreator;
import com.wenld.pullrefreshlib.DefaultRightLoadCreator;
import com.wenld.pullrefreshlib.OnLeftLoadMoreListener;
import com.wenld.pullrefreshlib.OnRightRefreshListener;
import com.wenld.pullrefreshlib.PullHorizontalRefreshLayout;

public class PullRightActivity extends AppCompatActivity {

    private ImageView iv_aty_pullleft;
    private PullHorizontalRefreshLayout pullleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull);
        initView();

        pullleft.setLoadViewCreator(new DefaultLeftLoadCreator());
        pullleft.setRefreshViewCreator(new DefaultRightLoadCreator());
//        pullleft.setLoadMoreLeft(false);
//        pullleft.setRightRefresh(false);
//        pullleft.setTranslationChild(false);
      initListener();
    }

    private void initListener() {
        pullleft.setOnRightRefreshListener(new OnRightRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pullleft.stopRefresh();
                    }
                }, 1000);
            }
        });
        pullleft.setOnLeftLoadMoreListener(new OnLeftLoadMoreListener() {
            @Override
            public void onLoadmore() {
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        pullleft.stopLoad();
                    }
                }, 1000);
            }
        });
    }

    private void initView() {
        iv_aty_pullleft = (ImageView) findViewById(R.id.iv_aty_pullleft);
        pullleft = (PullHorizontalRefreshLayout) findViewById(R.id.pullHorizontal);
    }
}
