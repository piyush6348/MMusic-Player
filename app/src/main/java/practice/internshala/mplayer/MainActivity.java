package practice.internshala.mplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import practice.internshala.mplayer.fragments.FavouritesFragment;
import practice.internshala.mplayer.fragments.MainFragment;
import practice.internshala.mplayer.fragments.NowPlaying;
import practice.internshala.mplayer.models.Song;
import practice.internshala.mplayer.service.MusicService;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController musicController;
    private boolean paused=false, playbackPaused=false;
    private Toolbar topToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();

        if(musicController==null)
        setMusicController();
        
        setUpNavigationDrawer();
    }

    public MusicService getMusicService(){
        return musicSrv;
    }
    private void setUpNavigationDrawer() {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.all_songs)
                .withIcon(R.drawable.library_music);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.favourites)
                .withIcon(R.drawable.music_circle);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.now_playing)
                .withIcon(R.drawable.ic_play_arrow_black_36dp);

//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(topToolBar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        Fragment fragment = null;

                        if(position==1){
                            Log.e("onItemClick: ", " All Songs");
                            fragment = new MainFragment();
                        }
                        else if(position==2){
                            Log.e("onItemClick: ", " Favourites");
                            fragment = new FavouritesFragment();
                        }
                        else if(position==3){
                            Song song = musicSrv.getSong();
                            fragment = NowPlaying.newInstance(song.getAlbumID(),
                                    song.getAlbum(),song.getTitle(),song.getId());
                        }

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fl_container, fragment);
                        fragmentTransaction.commit();

                        return false;
                    }
                })
                .build();
    }

    //connect to the service
    public ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(MainFragment.getSongsList(musicSrv.getApplicationContext()));
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public ServiceConnection getServiceConnection(){
        return musicConnection;
    }
    @Override
    protected void onStart() {
        super.onStart();

        if(musicSrv!=null && musicSrv.isPng());
        else if(playIntent==null){
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
        musicController.setAnchorView(findViewById(R.id.fl_container));
        musicController.setEnabled(true);
        //musicController.show();
    }

    public void setMusicBound(boolean bound){
        this.musicBound = bound;
    }
    private void playNextSong() {
        musicSrv.playNext();
        if(playbackPaused){
            setMusicController();
            playbackPaused=false;
        }
        musicController.show(0);

        boolean checkPresence = checkPresenceOfNowPlayingFragment();
        if(checkPresence)
            updateNowPlayingFragment();
    }

    private void updateNowPlayingFragment() {
        Song song = musicSrv.getSong();
        Fragment fragment = NowPlaying.newInstance(song.getAlbumID(),
                song.getAlbum(),song.getTitle(),song.getId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment);
        fragmentTransaction.commit();
    }

    private boolean checkPresenceOfNowPlayingFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment instanceof NowPlaying)
            return true;
        return false;
    }

    private void playPrevSong() {
        musicSrv.playPrev();
        if(playbackPaused){
            setMusicController();
            playbackPaused=false;
        }
        musicController.show(0);
        boolean checkPresence = checkPresenceOfNowPlayingFragment();
        if(checkPresence)
            updateNowPlayingFragment();
    }

    private void initializeView() {
        topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_container,new MainFragment());
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
        if (fragment instanceof MainFragment)
            super.onBackPressed();
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
