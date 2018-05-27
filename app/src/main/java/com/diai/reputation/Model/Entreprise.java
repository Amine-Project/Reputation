package com.diai.reputation.Model;

public class Entreprise {
    private String companyName;
    private String service;

    public Entreprise() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Entreprise(String companyName, String service) {
        this.companyName = companyName;
        this.service = service;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
