package com.javaguy.springredis.repository;

import com.javaguy.springredis.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {
    List<User> findByName(String name);
    List<User> findByAge(int age);
}
