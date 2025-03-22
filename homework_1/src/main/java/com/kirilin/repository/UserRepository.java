package com.kirilin.repository;

import com.kirilin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET u.name = :#{#user.name}, u.lastname = :#{#user.lastname}, u.login = :#{#user.login}, u.password = :#{#user.password} WHERE u.id = :id")
    void updateById(@Param("id") Long id, @Param("user") User user);
}
