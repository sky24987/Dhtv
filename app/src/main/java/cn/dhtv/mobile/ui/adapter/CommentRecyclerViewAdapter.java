package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Comment;

/**
 * Created by Jack on 2015/6/16.
 */
public class CommentRecyclerViewAdapter extends BaseRecyclerViewAdapter<CommentRecyclerViewAdapter.ViewHolder> {
    ArrayList<Comment> commentArrayList;

    public CommentRecyclerViewAdapter(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    @Override
    public int itemCount() {
        return commentArrayList.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.message = (TextView) view.findViewById(R.id.message);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        Comment comment = commentArrayList.get(position);
        holder.title.setText(comment.getTitle());
        holder.message.setText(comment.getMessage());
    }

    @Override
    public int itemViewType(int position) {
        return 0;
    }


    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        TextView title,message;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
