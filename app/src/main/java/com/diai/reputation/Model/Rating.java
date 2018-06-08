package com.diai.reputation.Model;

public class Rating {

    private float seriousness;
    private float loyalty;
    private float punctuality;
    private float sociability;
    private float respect;
    private float avg;
    private int nb;



    public Rating(float seriousness, float loyalty, float punctuality, float sociability, float respect,float  avg) {
        this.seriousness = seriousness;
        this.loyalty = loyalty;
        this.punctuality = punctuality;
        this.sociability = sociability;
        this.respect = respect;
        this.avg=avg;

    }

    public Rating(Rating rating) {
        this.seriousness = rating.getSeriousness();
        this.loyalty = rating.getLoyalty();
        this.punctuality = rating.getPunctuality();
        this.sociability = rating.getSociability();
        this.respect = rating.getRespect();
        this.avg=rating.getAvg();
    }

    public float getSeriousness() {
        return seriousness;
    }

    public void setSeriousness(float seriousness) {
        this.seriousness = seriousness;
    }

    public float getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(float loyalty) {
        this.loyalty = loyalty;
    }

    public float getPunctuality() {
        return punctuality;
    }

    public void setPunctuality(float punctuality) {
        this.punctuality = punctuality;
    }

    public float getSociability() {
        return sociability;
    }

    public void setSociability(float sociability) {
        this.sociability = sociability;
    }

    public float getRespect() {
        return respect;
    }

    public void setRespect(float respect) {
        this.respect = respect;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }
}
