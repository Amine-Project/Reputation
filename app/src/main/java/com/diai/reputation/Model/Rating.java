package com.diai.reputation.Model;

public class Rating {

    private String rating_id;
    private String from;
    private String to;
    private float seriousness;
    private float loyalty;
    private float punctuality;
    private float sociability;
    private float respect;

    public Rating(String rating_id, String from, String to, float seriousness, float loyalty, float punctuality, float sociability, float respect) {
        this.rating_id = rating_id;
        this.from = from;
        this.to = to;
        this.seriousness = seriousness;
        this.loyalty = loyalty;
        this.punctuality = punctuality;
        this.sociability = sociability;
        this.respect = respect;
    }




}
