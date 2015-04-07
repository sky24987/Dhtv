package cn.dhtv.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

/**
 * Created by Jack on 2015/4/7.
 */
public abstract class BaseRecyclerViewAdapter<VH extends BaseRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private LinkedList<VH> headerViewList = new LinkedList<>();
    private LinkedList<VH> footerViewList = new LinkedList<>();
    private VH emptyView;

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if(emptyView != null && emptyView.viewType == viewType){
            return emptyView;
        }

        for(VH viewHolder : headerViewList){
            if(viewHolder.viewType == viewType){
                return viewHolder;
            }
        }

        for(VH viewHolder : footerViewList){
            if(viewHolder.viewType == viewType){
                return viewHolder;
            }
        }

        return onCreateVH(parent,viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if(inDataSet(position)){
            holder.position = position;
            onBindVH(holder, position);
            //holder.itemView.setOnClickListener(this);
            return;
        }


        holder.onVHbind(holder, position);
        return;
    }

    @Override
    public int getItemCount() {
        int count = itemCount();
        if(count == 0){
            if(emptyView == null){
                return 0;
            }else {
                return 1;
            }
        }

        return headerViewList.size()+count+footerViewList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(inEmpty(position)){
            return emptyView.viewType;
        }

        if(inHeaders(position)){
            return headerViewList.get(position).viewType;
        }

        if(inFooters(position)){
            int footerPosition = position - headerViewList.size() - itemCount();
            return footerViewList.get(footerPosition).viewType;
        }

        int dataPosition = position - headerViewList.size();
        return itemViewType(dataPosition);
    }

    @Override
    public void onClick(View v) {

    }

    private boolean inEmpty(int viewPosition){
        if(itemCount() == 0){
            return true;
        }else {
            return false;
        }
    }

    private boolean inHeaders(int viewPosition){
        if(inEmpty(viewPosition) == true){
            return false;
        }

       return viewPosition < headerViewList.size();
    }

    private boolean inFooters(int viewPosition){
        if(footerViewList.size() == 0){
            return false;
        }

        if(inEmpty(viewPosition) == true){
            return false;
        }

        if(viewPosition < headerViewList.size()+itemCount()){
            return false;
        }else {
            return true;
        }
    }

    private boolean inDataSet(int viewPosition){
        if(inEmpty(viewPosition)){
            return false;
        }

        if(inHeaders(viewPosition)){
            return false;
        }

        if(inFooters(viewPosition)){
            return false;
        }

        return true;
    }



    public abstract int itemCount();

    public abstract VH onCreateVH(ViewGroup parent, int viewType);

    public abstract void onBindVH(VH holder, int position);

    public abstract int itemViewType(int position);





    public static abstract class ViewHolder extends RecyclerView.ViewHolder{
        public static final int VIEW_TYPE_DEFAULT = 0;
        public static final int VIEW_TYPE_EMPTY = -1;
        public static final int VIEW_TYPE_HEADER = -2;
        public static final int VIEW_TYPE_FOOTER = -3;



        public int position;
        public int viewType;
        private OnBindVHListener mOnBindVHListener;

        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
        }

        public void setOnBindVHListener(OnBindVHListener onBindVHListener) {
            this.mOnBindVHListener = onBindVHListener;
        }

        public void onVHbind(RecyclerView.ViewHolder holder, int position){
            if(mOnBindVHListener != null){
                mOnBindVHListener.onBindViewHolder(holder, position);
            }
        }

        public interface OnBindVHListener{
            void onBindViewHolder(RecyclerView.ViewHolder holder, int position);
        }

    }
}
