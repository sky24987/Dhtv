package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Favorite;

/**
 * Created by Jack on 2015/6/17.
 */
public class FavoriteRecyclerViewAdapter extends BaseRecyclerViewAdapter<FavoriteRecyclerViewAdapter.ViewHolder> {
    ArrayList<Favorite> favoriteArrayList;

    public FavoriteRecyclerViewAdapter(ArrayList<Favorite> favoriteArrayList) {
        this.favoriteArrayList = favoriteArrayList;
    }

    @Override
    public int itemCount() {
        return favoriteArrayList.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.description = (TextView) view.findViewById(R.id.description);
        viewHolder.dateline = (TextView) view.findViewById(R.id.dateline);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        Favorite favorite = favoriteArrayList.get(position);
        holder.title.setText(favorite.getTitle());
        holder.description.setText(favorite.getDescription());
        holder.dateline.setText(favorite.getDateline());
    }

    @Override
    public int itemViewType(int position) {
        return 0;
    }

    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        TextView title,description,dateline;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
