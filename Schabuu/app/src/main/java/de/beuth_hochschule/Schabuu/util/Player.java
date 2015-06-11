package de.beuth_hochschule.Schabuu.util;


public class Player {
    public String name;
    public String role;
    public String team;
    public String streamAudio;
    public String streamVideo;

    public Player(String name, String role, String team, String streamAudio, String streamVideo) {
        this.name = name;
        this.role = role;
        this.team = team;
        this.streamAudio = streamAudio;
        this.streamVideo = streamVideo;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", team='" + team + '\'' +
                ", streamAudio='" + streamAudio + '\'' +
                ", streamVideo='" + streamVideo + '\'' +
                '}';
    }
}
