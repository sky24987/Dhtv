package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.ProgramDetailActivity;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.Program;
import cn.dhtv.mobile.model.ProgramCollector;

/**
 * Created by Jack on 2015/3/26.
 */
public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ViewHolder> implements View.OnClickListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private ItemViewDataSet dataList;
    private Context mContext;

    public ProgramListAdapter(ItemViewDataSet dataList,Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_program,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Category program = (Category) dataList.getItem(i);
        viewHolder.itemView.setTag(program);
        viewHolder.itemView.setOnClickListener(this);

        //TODO
        /*if(DEBUG){


             viewHolder.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    TextView textView = (TextView) v.findViewById(R.id.title);
                    Log.d(LOG_TAG,"attach"+textView.getText());
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    TextView textView = (TextView) v.findViewById(R.id.title);
                    Log.d(LOG_TAG,"detach"+textView.getText());
                }
            });
        }*/


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
        Category program = (Category) v.getTag();
        Intent intent = new Intent(mContext, ProgramDetailActivity.class);
        intent.putExtra("program",program);
        mContext.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public Category program;
        public NetworkImageView mNetworkImageView;
        public TextView textViewTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            mNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.image);
            textViewTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
