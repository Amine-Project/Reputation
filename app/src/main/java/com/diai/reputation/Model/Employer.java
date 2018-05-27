package com.diai.reputation.Model;

public class Employer {
    private String firstName;
    private String lastName;
    private String service;

    public Employer() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Employer(String firstName, String lastName, String service) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.service = service;
    }

    
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}

