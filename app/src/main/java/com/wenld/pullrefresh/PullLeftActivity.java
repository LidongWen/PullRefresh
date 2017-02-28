package com.wenld.pullrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wenld.pullrefreshlib.DefaultLoadCreator;
import com.wenld.pullrefreshlib.OnLoadMoreListener;
import com.wenld.pullrefreshlib.PullLeftToRefreshLayout;

public class PullLeftActivity extends AppCompatActivity {

    private ImageView iv_aty_pullleft;
    private PullLeftToRefreshLayout pullleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pullleft);
        initView();
        DefaultLoadCreator defaultLoadCreator = new DefaultLoadCreator();

//       ((ViewGroup) findViewById(R.id.content)).addView( defaultLoadCreator.getLoadView(this, (ViewGroup) findViewById(R.id.content)));
//        defaultLoadCreator.onLoading();
//        defaultLoadCreator.onStopLoad();

        pullleft.setLoadViewCreator(new DefaultLoadCreator());
//        pullleft.setLoadMore(false);
//        pullleft.setTranslationChild(false);
        pullleft.setOnLoadMoreListener(new OnLoadMoreListener() {
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
        pullleft = (PullLeftToRefreshLayout) findViewById(R.id.pullleft);
    }
}
