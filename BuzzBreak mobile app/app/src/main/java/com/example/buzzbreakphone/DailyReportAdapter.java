package com.example.buzzbreakphone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.models.DailyReportModel;

import java.util.List;

/**
 * Adapter class for the RecyclerView in DailyReportActivity.
 * Displays a list of apps with their usage times.
 */
public class DailyReportAdapter extends RecyclerView.Adapter<DailyReportAdapter.ReportViewHolder> {
    private List<DailyReportModel.ReportRow> reportRows;

    public DailyReportAdapter(List<DailyReportModel.ReportRow> reportRows) {
        this.reportRows = reportRows;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usage_row, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        DailyReportModel.ReportRow row = reportRows.get(position);
        holder.bind(row);
    }

    @Override
    public int getItemCount() {
        return reportRows.size();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView appName;
        private TextView screenTime;
        private TextView frequency;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.tv_name);
            screenTime = itemView.findViewById(R.id.tv_time);
            frequency = itemView.findViewById(R.id.tv_freq);
        }

        public void bind(DailyReportModel.ReportRow row) {
            appName.setText(row.getAppName());
            screenTime.setText(row.getScreenTime());
            
            // For daily report, we don't show frequency, so we'll hide it
            frequency.setVisibility(View.GONE);
            
            // Set category-specific background
            int backgroundColor = getCategoryColor(row.getCategory());
            itemView.setBackgroundColor(backgroundColor);
        }
        
        private int getCategoryColor(int category) {
            switch (category) {
                case 0: // Social Media
                    return 0x33E94FFF; // Light pink with transparency
                case 1: // Games
                    return 0x334AFF88; // Light green with transparency
                case 2: // Productivity
                    return 0x334F8FF7; // Light blue with transparency
                case 3: // Others
                default:
                    return 0x33C74FFF; // Light purple with transparency
            }
        }
    }
}