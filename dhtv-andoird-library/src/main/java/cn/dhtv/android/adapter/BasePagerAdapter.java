package cn.dhtv.android.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/3/5.
 * 分页适配器，用于给PageView提供视图。需要一个页面的生产者（PageFactory）以提供页面，页面功能由生产者或者其他类控制，
 * 此类只负责页面的管理。开发者可以提供PageHolder，以存放和访问页面，因此外部可以直接控视图，不需要通过此类访问。
 * 开发者也可以使用默认的PageHolder。
 */
public class BasePagerAdapter extends PagerAdapter {
    private PageFactory mPageFactory;
    private PageHolder mPageHolder;

    /**
     *
     * @param pageFactory
     * 页面提供者
     * @param PageHolder
     * 页面保存者,若空，则使用默认的
     */
    BasePagerAdapter(PageFactory pageFactory,PageHolder PageHolder){
        mPageFactory = pageFactory;
        if(PageHolder != null){
            mPageHolder = PageHolder;
        }else {
            mPageHolder = new PageHolder();
        }
    }

    @Override
    public int getCount() {
        return mPageFactory.pageCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Page)object).getPageView() == view;
    }

    @Override
    public Page instantiateItem(ViewGroup container, int position) {
        String title = mPageFactory.getPageTitle(position);
        Page page;
        if(mPageHolder != null && mPageHolder.contains(title)){
            page = mPageHolder.get(title);
        }else {
            page = mPageFactory.generatePage(position);
            mPageHolder.add(page);
        }
        container.addView(page.getPageView());
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Page page = (Page) object;
        container.removeView(page.getPageView());
        mPageHolder.remove(page);
        return;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageFactory.getPageTitle(position);
    }

    @Override
    public int getItemPosition(Object object) {
        int position =  mPageFactory.getPagePosition(((Page)object).title);
        if(position >= 0){
            return position;
        }else {
            return POSITION_NONE;
        }
    }

    public PageHolder getPageHolder() {
        return mPageHolder;
    }

    public static class Page{
        private String title;
        private View pageView;
        private int id;

        Page(String title,View pageView){
            this.title = title;
            this.pageView = pageView;
        }

        public String getTitle() {
            return title;
        }

        public View getPageView() {
            return pageView;
        }

        public int getId() {
            return id;
        }


    }

    public static class PageHolder{
        private ArrayList<Page> mPageList = new ArrayList<>();
        public Page get(String title){
            int index = indexOf(title);
            if(index >= 0){
                return mPageList.get(index);
            }else {
                return null;
            }
        }

        public int indexOf(String title){
            if(mPageList.size() == 0){
                return -1;
            }

            for(int i = 0;i < mPageList.size();++i){
                if(mPageList.get(i).title == title){
                    return i;
                }
            }

            return -1;
        }

        public boolean contains(String title){
            int index = indexOf(title);
            if(index >= 0){
                return true;
            }else {
                return false;
            }
        }

        public void add(Page page){
            int index = indexOf(page.title);
            if(index < 0){
                mPageList.add(page);
            }else {
                mPageList.remove(index);
                mPageList.add(page);
            }
        }

        public void remove(Page page){
            int index = indexOf(page.title);
            if(index >= 0){
                mPageList.remove(index);
            }
        }

        public int size(){
            return mPageList.size();
        }
    }

    public interface PageFactory {
        int pageCount();
        Page generatePage(int position);
        String getPageTitle(int position);
        int getPagePosition(String title);
    }
}