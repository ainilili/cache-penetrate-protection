package org.smallnico.demo.domain;

import org.nico.ourbatis.annotation.RenderPrimary;

public class User {

    @RenderPrimary
    private Integer id;
    
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
