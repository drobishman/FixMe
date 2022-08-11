package it.adrian.fixme.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Car implements Serializable {

    private Integer id;
    private String registrationNumber;
    private String chasisNumber;
    private String brand;
    private String model;
    //private Set<TroubleCode> troubleCodes = new HashSet<TroubleCode>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getChasisNumber() {
        return chasisNumber;
    }

    public void setChasisNumber(String chasisNumber) {
        this.chasisNumber = chasisNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /*public Set<TroubleCode> getTroubleCodes() {
        return troubleCodes;
    }

    public void setCarTroubleCodes(Set<TroubleCode> troubleCodes) {
        this.troubleCodes = troubleCodes;
    }*/

    @Override
    public boolean equals(Object obj) {
        if(id.equals(((Car) obj).getId()))
        return true;
        else return false;
    }
}
