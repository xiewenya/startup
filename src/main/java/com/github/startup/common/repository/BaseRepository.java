package com.github.startup.common.repository;

//import org.springframework.data.jpa.repositories.JpaRepository;
//import org.springframework.transaction.annotation.Transactional;

import com.github.startup.common.model.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by bresai on 16/5/24.
 */

@NoRepositoryBean
public interface BaseRepository<T extends BaseModel, ID extends Serializable> extends JpaRepository<T,ID> {

}