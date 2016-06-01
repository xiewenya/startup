package com.github.startup.models;

import com.github.startup.common.model.BaseModel;
import com.github.startup.utils.OrderState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;


//import org.springframework.data.annotation.Id;
//
//import javax.persistence.GeneratedValue;

/**
 * Created by bresai on 16/5/23.
 */

@Entity
@Table(name="user")
public class User extends BaseModel {

    @Id
    @GeneratedValue
    @Min(0)
    private long id;

    @NotNull
    @Size(min=3, max=20)
    private String username;

    @NotNull
    @Size(min=6, max=20)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private String address;

    private Date created;

    @Enumerated(EnumType.ORDINAL)
    private OrderState state;

    @JsonIgnore
    private Boolean isActive;

    public User() {
    }

//    public User(@NotNull String username,
//                @NotNull String password,
//                String address,
//                Date created,
//                Boolean isActive) {
//        this.username = username;
//        this.password = password;
//        this.address = address;
//        this.created = created;
//        this.isActive = isActive;
//    }


    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", created=" + created +
                ", state=" + state +
                ", isActive=" + isActive +
                '}';
    }
}