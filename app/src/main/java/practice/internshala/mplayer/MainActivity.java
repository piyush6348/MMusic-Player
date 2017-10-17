package practice.internshala.mplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import practice.internshala.mplayer.adapter.MASongsListAdapter;
import practice.internshala.mplayer.models.Song;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();
        MASongsListAdapter maSongsListAdapter = new MASongsListAdapter(
                getSongsList(this),this
        );
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(maSongsListAdapter);
    }

    private void initializeView() {
        rvSongs = (RecyclerView) findViewById(R.id.rv_songs);
    }

    public static ArrayList<Song> getSongsList(Context context){

        ArrayList<Song> songArrayList = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));

                    Song song = new Song(title,album,artist,duration,path);
                    // Add code to get more column here
                    songArrayList.add(song);
                    // Save to your list here
                }

            }
        }

        cur.close();
        return songArrayList;
    }
}
