package com.jarod.chat.repository;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jarod.chat.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findByUsername(String username);
}
