package practice.internshala.mplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;

import practice.internshala.mplayer.adapter.MASongsListAdapter;
import practice.internshala.mplayer.models.Song;
import practice.internshala.mplayer.service.MusicService;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{

    private RecyclerView rvSongs;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController musicController;
    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();
        setUpAdapter(getSongsList(this),this);
        setMusicController();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(getSongsList(musicSrv.getApplicationContext()));
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setMusicController();
            paused=false;
        }
    }

    @Override
    protected void onPause(){
        /*
        playbackPaused=true;
        musicSrv.pausePlayer();*/
        super.onPause();
    }

    @Override
    protected void onStop() {
        musicController.hide();
        super.onStop();
    }

    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){
            setMusicController();
            playbackPaused=false;
        }
        musicController.show(0);
    }

    private void setUpAdapter(ArrayList<Song> songsList, Context context) {
        MASongsListAdapter maSongsListAdapter = new MASongsListAdapter(
                songsList,context,MainActivity.this
        );
        rvSongs.setLayoutManager(new LinearLayoutManager(context));
        rvSongs.setAdapter(maSongsListAdapter);
    }

    private void setMusicController(){
        if(musicController == null){
            musicController = new MusicController(this){
                @Override
                public void show(int timeout) {
                    if(timeout == 3000)
                        timeout = Integer.MAX_VALUE;
                    super.show(timeout);
                }
            };
        }

        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on click next
                playNextSong();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // on click prev
                playPrevSong();
            }
        });

        musicController.setMediaPlayer(this);
        musicController.setAnchorView(findViewById(R.id.rv_songs));
        musicController.setEnabled(true);
        //musicController.show();
    }

    private void playNextSong() {
        musicSrv.playNext();
        if(playbackPaused){
            setMusicController();
            playbackPaused=false;
        }
        musicController.show(0);
    }

    private void playPrevSong() {
        musicSrv.playPrev();
        if(playbackPaused){
            setMusicController();
            playbackPaused=false;
        }
        musicController.show(0);
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
                    String albumID = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));

                    Song song = new Song(title,album,artist,duration,path,albumID,id);
                    // Add code to get more column here
                    songArrayList.add(song);
                    // Save to your list here
                }

            }
        }

        cur.close();
        return songArrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                musicSrv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
