# Pull-Refresh
上拉刷新，下拉加载。 右拉刷新、左拉加载。 四个方向都可以刷新加载。

> ###V1.0.0
 实现左拉加载 ;

## 引用
```groovy
// 项目引用
dependencies {
    compile 'com.github.LidongWen:PullRefresh:1.0.0'
}

// 根目录下引用

allprojects {
    repositories {
        jcenter()
        maven { url "https://www.jitpack.io" }
    }
}
```
## 使用
```
        pullleft.setLoadViewCreator(new DefaultLoadCreator());
//        pullleft.setLoadMore(false);  //是否可以下拉
//      pullleft.setTranslationChild(false);//在左拉过程中是否位移画面
        pullleft.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadmore() {
                pullleft.stopLoad();
            }
        });

```
```
    <com.wenld.pullrefreshlib.PullLeftToRefreshLayout
        android:id="@+id/pullleft"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <You's View
            android:id="@+id/iv_aty_pullleft"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#ccccaa"
           />
    </com.wenld.pullrefreshlib.PullLeftToRefreshLayout>

```
### 自定义加载头部样式
继承 LoadViewCreator  类   实现相对应的方法
```
//拉获取更多
    public static int PULL_TO_LOADMORE=2;
    //释放进行加载
    public static int UP_TO_LOADMORE=1;

    /**
     * 获取上拉加载更多的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getLoadView(Context context, ViewGroup parent);

    /**
     * 正在上拉
     *
     * @param currentDragHeight 当前拖动的高度
     * @param loadViewHeight    总的加载高度
     * @param currentLoadStatus 当前状态
     */
    public abstract void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus);

    /**
     * 正在加载中
     */
    public abstract void onLoading();

    /**
     * 停止加载
     */
    public abstract void onStopLoad();

```

### bug
多手操作问题；