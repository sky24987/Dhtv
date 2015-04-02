package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.model.AbsListCollector;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.widget.FooterRefreshView;

/**
 * Created by Jack on 2015/4/1.
 */
public class NewsListAdapter2 extends RecyclerView.Adapter<NewsListAdapter2.ViewHolder>{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private Category category;
    private AbstractListAdapter.ListViewDataList listViewDataList;
    private ImageLoader mImageLoader = NetUtils.getImageLoader();
    private FooterRefreshView mFooterRefreshView;
    private View mHeaderView;
    private View mEmptyView;
    private Context mContext;

    private int headerCount=0;
    private int footerCount=0;

    public NewsListAdapter2(Category category, AbstractListAdapter.ListViewDataList listViewDataList,Context context) {
        this.category = category;
        this.listViewDataList = listViewDataList;
        mContext = context;
        setFooterRefreshView((FooterRefreshView) LayoutInflater.from(context).inflate(R.layout.widget_refresh_footer, null));
        setEmptyView(LayoutInflater.from(context).inflate(R.layout.widget_empty_view,null));
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        if(mHeaderView == null){
            headerCount =  0;
        }else {
            headerCount = 1;
        }
    }

    public void setFooterRefreshView(FooterRefreshView footerRefreshView){
        mFooterRefreshView = footerRefreshView;
        if(mFooterRefreshView == null){
            footerCount = 0;
        }else {
            footerCount = 1;
        }
    }

    public FooterRefreshView getFooterRefreshView(){
        return mFooterRefreshView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(isHeaderView(i)){
            return new ViewHolder(getHeaderView());
        }
        if(isNoneView(i)){
            return new ViewHolder(getEmptyView());
        }

        if(isFooterView(i)){
            return new ViewHolder(getFooterRefreshView());
        }

        if(positionInData(i)) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_news, viewGroup, false);
            return new ViewHolder(v);
        }

        //正常逻辑不会到这里，如果到了这里，说明程序写错了
        Log.e(LOG_TAG,"onCreateViewHolder:err program logic");
        return new ViewHolder(null);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(!positionInData(i)){
            return;
        }


        NewsOverview item = (NewsOverview) listViewDataList.getItem(i);
        viewHolder.networkImageView.setDefaultImageResId(R.drawable.default_image);
        if(item.getPic().length > 0) {
            viewHolder.networkImageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX + item.getPic()[0].getSrc(), mImageLoader);
        }else {
            viewHolder.networkImageView.setImageUrl(null, mImageLoader);
        }
        viewHolder.title.setText(item.getTitle());
        viewHolder.summary.setText(item.getSummary());
    }

    @Override
    public int getItemCount() {
        int count = listViewDataList.size();
        if(count == 0){
            count = 1;//表示空视图
        }else {
            count=count + headerCount+footerCount;//加上底部刷新视图
        }
        return count;
    }

    private boolean positionInData(int position){
        if(isNoneView(position) || isFooterView(position)){
            return false;
        }else{
            return true;
        }
    }

    private boolean isNoneView(int position){
        if(position ==1 && listViewDataList.size() == 0){
            return true;
        }else {
            return false;
        }
    }

    private boolean isHeaderView(int position){

        if(listViewDataList.size() > 0 && position < headerCount){
            return true;
        }else {
            return false;
        }

    }

    private boolean isFooterView(int position){
        if(listViewDataList.size()>0 && position >= headerCount + listViewDataList.size()){
            return true;
        }else {
            return false;
        }
    }



    @Override
    public int getItemViewType(int position) {
        if(isNoneView(position)){
            return AbsListCollector.VIEW_TYPE_NONE;
        }else if(isFooterView(position)){
            return AbsListCollector.VIEW_TYPE_FOOTER_REFRESH;
        }else if(isHeaderView(position)) {
            return AbsListCollector.VIEW_TYPE_HEADER;
        }else if(positionInData(position)){
            return listViewDataList.viewType(position-headerCount);
        }else {
            //如果到了这里，说明以上判断逻辑不正确
            Log.e(LOG_TAG,"getItemViewType():err program logic");
            return listViewDataList.viewType(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public NetworkImageView networkImageView;
        public TextView title;
        public TextView summary;

        public ViewHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.news_image);
            title = (TextView) itemView.findViewById(R.id.news_title);
            summary = (TextView) itemView.findViewById(R.id.news_summary);
        }
    }
}
