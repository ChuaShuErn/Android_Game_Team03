package iss.workshop.android_game_t3;

public class Player implements Comparable<Player>{
    private String name;
    private Integer score;

    public Player(String name, Integer score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }


    @Override
    public int compareTo(Player player) {
        if(this.score < player.score){
            return 1;
        }
        else return -1;
    }
}