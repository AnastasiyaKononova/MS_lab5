package com.lab31.admin.lab5_2;

public class Tracks {
    String name;
    String artistName;
    int listenersCount;
    int playCount;
    public Tracks(){
    }
    public Tracks(String name, String artistName, int listenersCount, int playCount){
        this.name = name;
        this.artistName = artistName;
        this.listenersCount = listenersCount;
        this.playCount = playCount;
    }
}