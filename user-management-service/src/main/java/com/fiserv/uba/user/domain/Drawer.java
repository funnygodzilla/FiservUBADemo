package com.fiserv.uba.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drawers")
public class Drawer {
    @Id
    private String drawerId;
    private String branchId;
    private String name;
    public String getDrawerId(){return drawerId;} public void setDrawerId(String drawerId){this.drawerId=drawerId;}
    public String getBranchId(){return branchId;} public void setBranchId(String branchId){this.branchId=branchId;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
}
