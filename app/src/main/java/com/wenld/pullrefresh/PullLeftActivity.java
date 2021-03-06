package com.wenld.pullrefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wenld.pullrefreshlib.DefaultLeftLoadCreator;
import com.wenld.pullrefreshlib.OnLeftLoadMoreListener;
import com.wenld.pullrefreshlib.PullLeftToRefreshLayout;

public class PullLeftActivity extends AppCompatActivity {

    private ImageView iv_aty_pullleft;
    private PullLeftToRefreshLayout pullleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pullleft);
        initView();
        DefaultLeftLoadCreator defaultLoadCreator = new DefaultLeftLoadCreator();

//       ((ViewGroup) findViewById(R.id.content)).addView( defaultLoadCreator.getLoadView(this, (ViewGroup) findViewById(R.id.content)));
//        defaultLoadCreator.onLoading();
//        defaultLoadCreator.onStopLoad();

        pullleft.setLoadViewCreator(new DefaultLeftLoadCreator());
//        pullleft.setLoadMoreLeft(false);
//        pullleft.setTranslationChild(false);
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
        pullleft = (PullLeftToRefreshLayout) findViewById(R.id.pullleft);
    }
}
