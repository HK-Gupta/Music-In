package com.example.musemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] itemNames;
    final long MAXI = 15*60*1000;
    final long MINI = 2*60*1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#333949"));
        actionBar.setBackgroundDrawable(colorDrawable);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));


        listView = findViewById(R.id.listView);

        // This permission must be called after initialising the list view toi avoid any runtime exception.
        runTimePermission();
    }

    public void runTimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singeFile: files) {
            if(singeFile.isDirectory() && !singeFile.isHidden()) {
                arrayList.addAll((findSong(singeFile)));
            } else {
                if(singeFile.getName().endsWith(".mp3") || singeFile.getName().endsWith((".wav"))) {
                    long duration = getDuration(singeFile.getAbsolutePath());
                    if(duration >= MINI && duration <= MAXI) {
                        arrayList.add(singeFile);
                    }
                }
            }
        }
        return arrayList;
    }

    public void displaySong() {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        itemNames = new String[mySongs.size()];
        for(int i=0; i<mySongs.size(); i++) {
            itemNames[i] = mySongs.get(i).getName().toString()
                    .replace("mp3", "")
                    .replace(".wav", "");

        }

        MusicAdapter musicAdapter = new MusicAdapter(this, itemNames);
        listView.setAdapter(musicAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), SongPlayer.class);
                intent.putExtra("songs", mySongs);
                intent.putExtra("songName", songName);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
    }

    private long getDuration(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(duration);
    }
    
}