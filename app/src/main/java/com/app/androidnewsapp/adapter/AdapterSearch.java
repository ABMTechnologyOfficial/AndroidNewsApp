package com.app.androidnewsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.androidnewsapp.R;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {

    private static final String SEARCH_HISTORY_KEY = "_SEARCH_HISTORY_KEY";
    private static final int MAX_HISTORY_ITEMS = 25;

    private List<String> items;
    private OnItemClickListener onItemClickListener;
    private OnItemActionClickListener onItemActionClickListener;
    SharedPreferences sharedPreferences;

    public interface OnItemClickListener {
        void onItemClick(View view, String viewModel, int pos);
    }

    public interface OnItemActionClickListener {
        void onItemActionClick(View view, String viewModel, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemActionClickListener(OnItemActionClickListener onItemActionClickListener) {
        this.onItemActionClickListener = onItemActionClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public RelativeLayout lytParent;
        public ImageButton imgSetText;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            lytParent = v.findViewById(R.id.lyt_parent);
            imgSetText = v.findViewById(R.id.imgSetText);
        }
    }

    public AdapterSearch(Context context) {
        sharedPreferences = context.getSharedPreferences("PREF_RECENT_SEARCH", Context.MODE_PRIVATE);
        this.items = getSearchHistory();
        Collections.reverse(this.items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_suggestion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String p = items.get(position);
        final int pos = position;
        holder.title.setText(p);
        holder.lytParent.setOnClickListener(v -> new Handler().postDelayed(()-> onItemClickListener.onItemClick(v, p, pos), 200));
        holder.imgSetText.setOnClickListener(v -> new Handler().postDelayed(()-> onItemActionClickListener.onItemActionClick(v, p, pos), 200));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshItems() {
        this.items = getSearchHistory();
        Collections.reverse(this.items);
        notifyDataSetChanged();
    }

    private static class SearchObject implements Serializable {
        public SearchObject(List<String> items) {
            this.items = items;
        }

        public List<String> items;
    }

    /**
     * To save last state request
     */
    @SuppressWarnings("RedundantCollectionOperation")
    public void addSearchHistory(String s) {
        SearchObject searchObject = new SearchObject(getSearchHistory());
        if (searchObject.items.contains(s)) searchObject.items.remove(s);
        searchObject.items.add(s);
        if (searchObject.items.size() > MAX_HISTORY_ITEMS) searchObject.items.remove(0);
        String json = new Gson().toJson(searchObject, SearchObject.class);
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, json).apply();
    }

    private List<String> getSearchHistory() {
        String json = sharedPreferences.getString(SEARCH_HISTORY_KEY, "");
        if (json.equals("")) return new ArrayList<>();
        SearchObject searchObject = new Gson().fromJson(json, SearchObject.class);
        return searchObject.items;
    }

    public void clearSearchHistory() {
        sharedPreferences.edit().clear().apply();
    }

}