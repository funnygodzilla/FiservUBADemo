package com.fiserv.uba.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_drawer_mapping")
public class UserDrawerMapping {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String drawerId;
    public Long getId(){return id;} public String getUserId(){return userId;} public void setUserId(String u){userId=u;} public String getDrawerId(){return drawerId;} public void setDrawerId(String d){drawerId=d;}
}
