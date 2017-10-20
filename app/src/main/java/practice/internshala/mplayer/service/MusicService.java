package practice.internshala.mplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import practice.internshala.mplayer.MainActivity;
import practice.internshala.mplayer.R;
import practice.internshala.mplayer.models.Song;

/**
 * Created by piyush on 18/10/17.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
                MediaPlayer.OnErrorListener{

    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private static final int NOTIFY_ID=1;

    private String songTitle ="";
    private final IBinder musicBind = new MusicBinder();

    private boolean shuffle=false;
    private Random rand;
    @Override
    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        player = new MediaPlayer();
        initMusicPlayer();
        rand = new Random();
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mp.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.btn_playback_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Content Title")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setList(ArrayList<Song> songArrayList){
        this.songs = songArrayList;
    }

    public void playSong(){
        player.reset();
        Song song = songs.get(songPosn);
        long currentSong = Long.parseLong(song.getId());
        songTitle = song.getTitle();

        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }
    public Song getSong(){
        return songs.get(songPosn);
    }

    public void playPrev(){
        songPosn--;
        if(songPosn <0) songPosn=songs.size()-1;
        playSong();
    }
    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn >=songs.size()) songPosn=0;
        }
        playSong();
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public class MusicBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
}
