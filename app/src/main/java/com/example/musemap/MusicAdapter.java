package com.example.musemap;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter {
    private Context context;
    private String[] itemNames;

    public MusicAdapter(Context context, String[] itemNames) {
        this.context = context;
        this.itemNames = itemNames;
    }

    @Override
    public int getCount() {
        return itemNames.length;
    }

    @Override
    public Object getItem(int position) {
        return itemNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtSong = view.findViewById(R.id.txt_song);
        txtSong.setSelected(true);
        txtSong.setText(itemNames[position]);

        return view;
    }
}
