package com.globe.paymybuddy.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(name = "uq_user_email", columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 45)
    private String username;

    @DecimalMin(value = "0.00", message = "Balance could not be negative")
    @Column(nullable = false)
    private double balance;

    // Connection add by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Connection> connections = new ArrayList<>();

    // Transaction send by this user
    @OneToMany(mappedBy = "sender")
    private final List<Transaction> sendTransactions = new ArrayList<>();

    // Transaction received by this user
    @OneToMany(mappedBy = "receiver")
    private final List<Transaction> receivedTransactions = new ArrayList<>();



    // Constructors

    public User() {
    }

    public User(String userName, String password, String email, Double balance) {
        this.username = userName;
        this.password = password;
        this.email = email;
        this.balance =  balance;
    }


    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<Transaction> getSendTransactions() {
        return sendTransactions;
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }
}
