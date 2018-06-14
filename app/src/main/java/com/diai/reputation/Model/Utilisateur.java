package com.diai.reputation.Model;

public class Utilisateur {
    private String firstName;
    private String job;
    private String phoneNumer;

    public Utilisateur() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Utilisateur(String firstName, String job, String phoneNumer) {
        this.firstName = firstName;
        this.job = job;
        this.phoneNumer = phoneNumer;
    }
    public Utilisateur(Utilisateur utilisateur) {
        this.firstName = utilisateur.firstName;
        this.job = utilisateur.job;
        this.phoneNumer = utilisateur.phoneNumer;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhoneNumer() { return phoneNumer; }

    public void setPhoneNumer(String phoneNumer) { this.phoneNumer = phoneNumer; }
}
