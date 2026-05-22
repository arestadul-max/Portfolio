package com.example.buzzbreakphone;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.models.AppUsageModel;

import java.util.List;

public class UsageAdapter extends RecyclerView.Adapter<UsageAdapter.VH> {

    private final List<AppUsageModel.UsageRow> data;

    public UsageAdapter(List<AppUsageModel.UsageRow> data) {
        this.data = data;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView time;
        TextView freq;

        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.img_icon);
            name = itemView.findViewById(R.id.tv_name);
            time = itemView.findViewById(R.id.tv_time);
            freq = itemView.findViewById(R.id.tv_freq);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AppUsageModel.UsageRow row = data.get(position);
        
        // Handle icon display
        if (row.getIcon() != null) {
            holder.icon.setImageDrawable(row.getIcon());
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            // Set default icon if none available
            holder.icon.setImageResource(android.R.drawable.sym_def_app_icon);
            holder.icon.setVisibility(View.VISIBLE);
        }
        
        holder.name.setText(row.getAppName());
        holder.time.setText(row.getScreenTime());
        holder.freq.setText(String.valueOf(row.getFrequency()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}