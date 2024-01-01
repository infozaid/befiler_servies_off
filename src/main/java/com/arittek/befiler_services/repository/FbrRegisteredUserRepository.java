package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.FbrRegisteredUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbrRegisteredUserRepository extends CrudRepository<FbrRegisteredUser, Integer> {
    List<FbrRegisteredUser> findAll();
    List<FbrRegisteredUser> findAllByStatus(Boolean status);
}

