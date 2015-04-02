package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.ProgramDetailActivity;
import cn.dhtv.mobile.entity.Program;
import cn.dhtv.mobile.model.ProgramCollector;

/**
 * Created by Jack on 2015/3/26.
 */
public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ViewHolder> implements View.OnClickListener{
    private AbstractListAdapter.ListViewDataList dataList;
    private Context mContext;

    public ProgramListAdapter(AbstractListAdapter.ListViewDataList dataList,Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Program program = (Program) dataList.getItem(i);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_program,viewGroup,false);
        v.setTag(program);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Program program = (Program) dataList.getItem(i);
        viewHolder.program = program;
        viewHolder.mNetworkImageView.setDefaultImageResId(R.drawable.default_image);
        viewHolder.mNetworkImageView.setImageResource(R.drawable.default_image);
        viewHolder.textViewTitle.setText(program.getCatname());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onClick(View v) {
        Program program = (Program) v.getTag();
        Intent intent = new Intent(mContext, ProgramDetailActivity.class);
        intent.putExtra("program",program);
        mContext.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public Program program;
        public NetworkImageView mNetworkImageView;
        public TextView textViewTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            mNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.image);
            textViewTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
