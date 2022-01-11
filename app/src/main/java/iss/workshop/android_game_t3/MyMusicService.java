package iss.workshop.android_game_t3;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

public class MyMusicService extends Service {

    private MediaPlayer mainMusicPlayer;
    private MediaPlayer gameMusicPlayer;
    private MediaPlayer fetchMusicPlayer;
    private MediaPlayer leaderboardMusicPlayer;
    private MediaPlayer tutorialMusicPlayer;

    private float volume = 0.20f;

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
            gameMusicPlayer.stop();
            gameMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer = null;
        }
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer=null;
        }
        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
        if (mainMusicPlayer == null){
            mainMusicPlayer = MediaPlayer.create(this,resId);
            mainMusicPlayer.setLooping(true);
        }
        mainMusicPlayer.start();
    }

    public void playFetchMusic(int resId){
        if(gameMusicPlayer != null){
            gameMusicPlayer.stop();
            gameMusicPlayer=null;
        }
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer = null;
        }
        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
        if (fetchMusicPlayer == null){
            fetchMusicPlayer = MediaPlayer.create(this,resId);
            fetchMusicPlayer.setLooping(true);
        }
        fetchMusicPlayer.start();
    }

    public void playGameMusic(int resId){
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer = null;
        }
        if(gameMusicPlayer != null){
            gameMusicPlayer.stop();
            gameMusicPlayer=null;
        }
        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
        if(gameMusicPlayer == null){
            gameMusicPlayer = MediaPlayer.create(this,resId);
            gameMusicPlayer.setVolume(volume,volume);
            gameMusicPlayer.setLooping(true);
        }
        gameMusicPlayer.start();
    }
    public void playTutorialMusic(int resId){
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer = null;
        }
        if(gameMusicPlayer != null){
            gameMusicPlayer.stop();
            gameMusicPlayer=null;}

        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
        tutorialMusicPlayer = MediaPlayer.create(this,resId);
        tutorialMusicPlayer.setLooping(true);
        tutorialMusicPlayer.start();
    }
    public void playLeaderBoardMusic(int resId){
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer = null;
        }
        if(gameMusicPlayer != null){
            gameMusicPlayer.stop();
            gameMusicPlayer=null;}

        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
        tutorialMusicPlayer = MediaPlayer.create(this,resId);
        tutorialMusicPlayer.setLooping(true);
        tutorialMusicPlayer.start();
    }

    public void pauseAllMusic(){
        if(mainMusicPlayer != null){
            mainMusicPlayer.stop();
            mainMusicPlayer = null;
        }
        if(gameMusicPlayer != null){
            gameMusicPlayer.stop();
            gameMusicPlayer = null;
        }
        if(fetchMusicPlayer != null){
            fetchMusicPlayer.stop();
            fetchMusicPlayer=null;
        }
        if (leaderboardMusicPlayer != null){
            leaderboardMusicPlayer.stop();
            leaderboardMusicPlayer = null;
        }
        if (tutorialMusicPlayer != null){
            tutorialMusicPlayer.stop();
            tutorialMusicPlayer=null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){

            case "play_main": playMainMusic(R.raw.acousticbreeze);break;
            case "play_game": playGameMusic(R.raw.punky);break;
            case "play_fetch": playFetchMusic(R.raw.remember);break;
            case "play_tutorial": playTutorialMusic(R.raw.goinghigher);break;
            case "play_leaderboard": playLeaderBoardMusic(R.raw.happyrock);break;
            case "pause_music": pauseAllMusic();break;
        }

        return super.onStartCommand(intent, flags, startId);
    }
}