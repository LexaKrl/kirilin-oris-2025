package com.kirilin.springboot.repository;


import com.kirilin.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value = "select u from User u where u.name = :name")
    List<User> findByName(String name);

    @Query(value = "select * from users u where u.name = ?1", nativeQuery = true)
    List<User> findAllByName(String name);

    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.username = :username")
    void updateEnabledStatus(@Param("username") String username,
                             @Param("enabled") boolean enabled);

    @Query("SELECT u from User u where u.enabled = :enabled")
    List<User> findAllByEnabled(boolean enabled);
}
