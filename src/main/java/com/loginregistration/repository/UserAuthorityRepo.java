package com.loginregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.loginregistration.model.UserAuthorities;

public interface UserAuthorityRepo extends JpaRepository<UserAuthorities, Long>{

}
