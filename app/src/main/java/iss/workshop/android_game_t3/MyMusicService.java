package iss.workshop.android_game_t3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MyMusicService extends Service {

    private MediaPlayer mainMusicPlayer;
    private MediaPlayer gameMusicPlayer;

    private final IBinder musicBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyMusicService getService() {return MyMusicService.this;}

    }

    public MyMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return super.onUnbind(intent);
    }

    public void playMainMusic(int resId){
        if(gameMusicPlayer != null){
            gameMusicPlayer.pause();}
        if(mainMusicPlayer != null){
            mainMusicPlayer.pause();
        }
        if (mainMusicPlayer == null){
            mainMusicPlayer = MediaPlayer.create(this,resId);
            mainMusicPlayer.setLooping(true);
        }
        mainMusicPlayer.start();
    }

    public void playGameMusic(int resId){
        if(mainMusicPlayer != null){
            mainMusicPlayer.pause();
        }
        if(gameMusicPlayer != null){
            gameMusicPlayer.pause();
        }
        if(gameMusicPlayer == null){
            gameMusicPlayer = MediaPlayer.create(this,resId);
            gameMusicPlayer.setLooping(true);
        }
        gameMusicPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){

            case "play_main": playMainMusic(R.raw.acousticbreeze);break;
            case "play_game": playGameMusic(R.raw.punky);break;
        }

        return super.onStartCommand(intent, flags, startId);
    }
}