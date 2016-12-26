package com.lab31.admin.lab5_2;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class LoadTask extends AsyncTask<String, Void, List<Tracks>> {
    private static boolean firstTime = true;
    DBHelper dbHelper;
    MainActivity mainActivity;
    private final Context context;
    private ProgressDialog progress;
    private static final String urlString =
            "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=%s&" +
                    "api_key=b628113b46ad5192662480874650d418&format=json&limit=10";
    private static final String tag = "LoadTask";

    LoadTask(Context c, MainActivity mainActivity) {
        this.context = c;
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        Log.d(tag, "onPreExecute");
        super.onPreExecute();
        progress= new ProgressDialog(this.context);
        progress.setMessage("Loading");
        progress.show();
    }

    @Override
    protected void onPostExecute(List<Tracks> trackses) {
        Log.d(tag, "onPostExecute");
        super.onPostExecute(trackses);
        progress.dismiss();
        saveTracks(trackses);
        mainActivity.updateList("");
    }

    @Override
    protected List<Tracks> doInBackground(String... params) {
        Log.d(tag, "doInBackground");
        List<Tracks> trackses = new LinkedList<>();
        for (String artist : params) {
            List<Tracks> nextTrackses = loadTracksByArtist(artist);
            if (nextTrackses != null) {
                trackses.addAll(nextTrackses);
            }
        }
        return trackses;
    }

    private void saveTracks(List<Tracks> trackses) {
        Log.d(tag, "Saving " + trackses.size() + " trackses.");
        if (trackses.isEmpty()) {
            Toast.makeText(context, "Error occurred. Working with local version.", Toast.LENGTH_LONG).show();
            return;
        }
        dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(firstTime) {
            db.delete(DBHelper.tableTracksName, null, null);
            firstTime = false;
        }
        for (Tracks track : trackses) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.artistNameColumnName, track.artistName);
            values.put(DBHelper.trackNameColumnName, track.name);
            values.put(DBHelper.listenersCountColumnName, track.listenersCount);
            values.put(DBHelper.playCountColumnName, track.playCount);
            db.insert(DBHelper.tableTracksName, null, values);
        }
    }
    public static List<Tracks> loadTracksByArtist(String artistName) {
        Log.d(tag, "Start load tracks by artist.");
        try {
            URL url = new URL(String.format(urlString, artistName));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            final StringBuilder output = new StringBuilder();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                br.close();
            } else {
                return null;
            }
            JSONArray tracks = new JSONObject(output.toString()).getJSONObject("toptracks").getJSONArray("track");
            List<Tracks> tracksList = new LinkedList<>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                Tracks tracks1 = new Tracks();
                tracks1.name = track.optString("name", "");
                tracks1.artistName = track.getJSONObject("artist").optString("name", "");
                tracks1.listenersCount = track.optInt("listeners", 0);
                tracks1.playCount = track.optInt("playcount", 0);
                tracksList.add(tracks1);
            }
            return tracksList;
        } catch (Exception e) {
            return null;
        }
    }
}