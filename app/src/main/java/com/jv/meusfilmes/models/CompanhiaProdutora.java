package com.jv.meusfilmes.models;

import java.io.Serializable;

public class CompanhiaProdutora implements Serializable {

    private String logo_path, name;

    public String getLogo_path() {
        return logo_path;
    }

    public void setLogo_path(String logo_path) {
        this.logo_path = logo_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
