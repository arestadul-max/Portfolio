package com.example.buzzbreakphone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzbreakphone.models.SelectAppsModel;

import java.util.List;

/**
 * Adapter class for the RecyclerView in SelectAppsActivity.
 * Displays a list of apps with checkboxes for selection.
 */
public class SelectAppsAdapter extends RecyclerView.Adapter<SelectAppsAdapter.AppViewHolder> {
    private SelectAppsActivity activity;
    private List<SelectAppsModel.AppInfo> apps;

    public SelectAppsAdapter(SelectAppsActivity activity, List<SelectAppsModel.AppInfo> apps) {
        this.activity = activity;
        this.apps = apps;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        SelectAppsModel.AppInfo app = apps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private CheckBox checkBox;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.iv_app_icon);
            appName = itemView.findViewById(R.id.tv_app_name);
            checkBox = itemView.findViewById(R.id.cb_select_app);
        }

        public void bind(SelectAppsModel.AppInfo app) {
            if (app.getIcon() != null) {
                appIcon.setImageDrawable(app.getIcon());
            } else {
                appIcon.setImageResource(R.mipmap.ic_launcher);
            }
            appName.setText(app.getAppName());
            checkBox.setChecked(app.isSelected());

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                app.setSelected(!app.isSelected());
                checkBox.setChecked(app.isSelected());
                activity.onAppSelectionChanged();
            });

            // Set click listener for the checkbox
            checkBox.setOnClickListener(v -> {
                app.setSelected(checkBox.isChecked());
                activity.onAppSelectionChanged();
            });
        }
    }
}