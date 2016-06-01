package com.github.startup.repositories;

import com.github.startup.common.repository.BaseRepository;
import com.github.startup.models.User;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Created by bresai on 16/5/24.
 */

@Repository
@RepositoryRestResource(collectionResourceRel = "User", path = "user")
public interface UserRepository extends BaseRepository<User, Long> {

}
