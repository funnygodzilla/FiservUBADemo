package com.fiserv.uba.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String userId;
    private String itUserId;
    private String branchId;
    public String getUserId(){return userId;} public void setUserId(String userId){this.userId=userId;}
    public String getItUserId(){return itUserId;} public void setItUserId(String itUserId){this.itUserId=itUserId;}
    public String getBranchId(){return branchId;} public void setBranchId(String branchId){this.branchId=branchId;}
}
