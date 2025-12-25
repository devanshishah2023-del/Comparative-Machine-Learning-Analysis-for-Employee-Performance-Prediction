package com.performance.service;

import com.performance.model.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Stateless
public class AuthenticationBean {
    
    @PersistenceContext(unitName = "PerformanceAnalyticsPU")
    private EntityManager em;
    
    public User authenticate(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            User user = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.password = :password", 
                User.class)
                .setParameter("username", username)
                .setParameter("password", hashedPassword)
                .getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public User registerUser(String username, String password, String email, String role) {
        try {
            User existingUser = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
            return null;
        } catch (NoResultException e) {
            User user = new User(username, hashPassword(password), email, role);
            em.persist(user);
            return user;
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
