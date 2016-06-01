package com.github.demo.JacksonTest.model;

import com.github.startup.common.model.BaseModel;
import org.jetbrains.annotations.NotNull;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by bresai on 16/5/26.
 */
public class TestModel extends BaseModel {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private String address;

    private String content;

    public TestModel() {
    }

    public TestModel(String address, String content) {
        this.address = address;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(@NotNull String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }
}
