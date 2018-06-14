package com.diai.reputation.Model;

public class Rating {

    private float seriousness;
    private float punctuality;
    private float sociability;
    private float respect;
    private float work_quality;

    private float avg;
    private int nb;

    public Rating() {

    }

    public Rating(float seriousness, float punctuality, float sociability, float respect, float work_quality, float  avg) {
        this.seriousness = seriousness;
        this.punctuality = punctuality;
        this.sociability = sociability;
        this.respect = respect;
        this.work_quality = work_quality;

        this.avg=avg;

    }

    public Rating(Rating rating) {
        this.seriousness = rating.getSeriousness();
        this.punctuality = rating.getPunctuality();
        this.sociability = rating.getSociability();
        this.respect = rating.getRespect();
        this.work_quality = rating.getWork_quality();

        this.avg=rating.getAvg();
    }

    public float getWork_quality() { return work_quality; }

    public void setWork_quality(float work_quality) { this.work_quality = work_quality; }

    public float getSeriousness() {
        return seriousness;
    }

    public void setSeriousness(float seriousness) {
        this.seriousness = seriousness;
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
