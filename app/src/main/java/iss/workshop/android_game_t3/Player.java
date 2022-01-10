package iss.workshop.android_game_t3;

public class Player implements Comparable<Player>{
    private String name;
    private Integer score;
    private Long time;

    public Player(String name, Integer score, Long time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public Long getTime() { return time; }

    @Override
    public int compareTo(Player player) {
        if(this.score < player.score){
            return 1;
        }
        else return -1;
    }
}
