package com.captor.points.gtnaozuka.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.captor.points.gtnaozuka.pointscaptor.R;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private String[] data;
    private LayoutInflater inflater;
    private static ClickListener clickListener;

    public DataAdapter(Context context, String[] data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.data_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DataAdapter.clickListener = clickListener;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
