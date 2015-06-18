package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Comment;
import cn.dhtv.mobile.entity.Fact;

/**
 * Created by Jack on 2015/6/16.
 */
public class FactRecyclerViewAdapter extends BaseRecyclerViewAdapter<FactRecyclerViewAdapter.ViewHolder> {
    ArrayList<Fact> factArrayList;

    public FactRecyclerViewAdapter(ArrayList<Fact> factArrayList) {
        this.factArrayList = factArrayList;
    }

    @Override
    public int itemCount() {
        return factArrayList.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fact,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.content = (TextView) view.findViewById(R.id.content);
        viewHolder.comment = (TextView) view.findViewById(R.id.comment);
        viewHolder.dateline = (TextView) view.findViewById(R.id.dateline);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        Fact fact = factArrayList.get(position);
        holder.comment.setText(fact.getComment());
        holder.content.setText(fact.getContent());
        holder.dateline.setText(fact.getDateline());
    }

    @Override
    public int itemViewType(int position) {
        return 0;
    }

    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        TextView content,comment,dateline;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
