package com.example.exerciseapp.models;

public class UserInfo {
    private String id;
    private long weight;

    public UserInfo(String id, long weight){
        this.id = id;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public long getWeight() {
        return weight;
    }

    public void setId(String id) {
        this.id = id;
    }
}
