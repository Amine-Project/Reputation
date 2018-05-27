package com.diai.reputation.Model;

public class Utilisater {
    public String firstName;
    public String lastName;

    public Utilisater() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Utilisater(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    //Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
