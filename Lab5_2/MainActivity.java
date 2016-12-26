package com.lab31.admin.lab5_2;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvMain;
    EditText etArtistName;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        lvMain = (ListView) findViewById(R.id.lvMain);
        etArtistName = (EditText) findViewById(R.id.etArtistName);

        if (isConnected()) {
            LoadTask loadTask = new LoadTask(this, this);
            loadTask.execute("Three Days Grace", "Oomph!", "Hollywood Undead");
        } else {
            Toast.makeText(this, "Internet not check.", Toast.LENGTH_LONG).show();
        }
    }
    public void onSearch(View view) {
        String artistName = etArtistName.getText().toString();
        updateList(artistName);
    }
    public void updateList(String artistName) {
        TrackAdapter  trackAdapter;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
        if(artistName.length() == 0)
            c = db.query(DBHelper.tableTracksName, null, null, null, null, null, null);
        else
            c = db.query(DBHelper.tableTracksName, null, DBHelper.artistNameColumnName, new String[]{artistName.toLowerCase()}, null, null, null, null);

        ArrayList<Tracks> trackses = new ArrayList<>();
        if (c.moveToFirst()) {

            int trackNameColumnIndex = c.getColumnIndex(DBHelper.trackNameColumnName);
            int artistNameColumnIndex = c.getColumnIndex(DBHelper.artistNameColumnName);
            int listenersCountColumnIndex = c.getColumnIndex(DBHelper.listenersCountColumnName);
            int playCountColumnIndex = c.getColumnIndex(DBHelper.playCountColumnName);

            do {
                Tracks track = new Tracks(c.getString(trackNameColumnIndex), c.getString(artistNameColumnIndex),
                        c.getInt(listenersCountColumnIndex), c.getInt(playCountColumnIndex));
                trackses.add(track);
            } while (c.moveToNext());
        }
        c.close();
        if(trackses.size() == 0){
            LoadTask loadTask = new LoadTask(this, this);
            loadTask.execute(artistName);
        }
        else {
            trackAdapter = new TrackAdapter(this, trackses);
            lvMain.setAdapter(trackAdapter);
        }
    }
    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}