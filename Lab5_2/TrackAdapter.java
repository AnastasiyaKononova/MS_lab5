package com.lab31.admin.lab5_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class TrackAdapter extends BaseAdapter{
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Tracks> objects;

    public TrackAdapter(Context context, ArrayList<Tracks> trackses) {
        ctx = context;
        objects = trackses;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row_layout, parent, false);
        }

        Tracks t = getTrack(position);

        ((TextView) view.findViewById(R.id.artistName)).setText(t.artistName);
        ((TextView) view.findViewById(R.id.trackName)).setText(t.name);
        ((TextView) view.findViewById(R.id.listenersCount)).setText("Listeners: " + t.listenersCount);
        ((TextView) view.findViewById(R.id.playCount)).setText("Play count: " + t.playCount);

        return view;
    }

    Tracks getTrack(int position) {
        return ((Tracks) getItem(position));
    }

    ArrayList<Tracks> getAll() {
        return objects;
    }
}