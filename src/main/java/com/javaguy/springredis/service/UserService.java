package com.javaguy.springredis.service;

import com.javaguy.springredis.model.User;
import com.javaguy.springredis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @CachePut(value = "users", key = "#result.id")
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        System.out.println("💾 SAVING USER TO REDIS: " + user.getName() + " (ID: " + user.getId() + ")");
        User savedUser = userRepository.save(user);
        System.out.println("✅ USER SAVED AND CACHED: " + savedUser.getId());
        return savedUser;
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(String id) {
        System.out.println("🔍 CACHE MISS - Fetching user from Redis database for id: " + id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            System.out.println("✅ USER FOUND IN DATABASE: " + user.get().getName());
        } else {
            System.out.println("❌ USER NOT FOUND IN DATABASE: " + id);
        }
        return user;
    }

    public List<User> getAllUsers() {
        System.out.println("📋 FETCHING ALL USERS FROM REDIS");
        return (List<User>) userRepository.findAll();
    }

    public List<User> getUsersByName(String name) {
        System.out.println("🔎 SEARCHING USERS BY NAME: " + name);
        return userRepository.findByName(name);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id) {
        System.out.println("🗑️ DELETING USER AND CACHE: " + id);
        userRepository.deleteById(id);
        System.out.println("✅ USER DELETED FROM REDIS AND CACHE EVICTED");
    }

    @CacheEvict(value = "users", allEntries = true)
    public void clearCache() {
        System.out.println("🧹 CLEARING ALL CACHE ENTRIES!");
    }
}
