package com.globe.paymybuddy.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "idx_tx_sender",   columnList = "sender_id"),
        @Index(name = "idx_tx_receiver", columnList = "receiver_id")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Utilisateur qui envoie l'argent
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tx_sender"))
    private User sender;

    /**
     * Utilisateur qui reçoit l'argent
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tx_receiver"))
    private User receiver;

    @NotNull
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    // Constructors

    public Transaction() {
    }

    public Transaction(User sender, User receiver, BigDecimal amount, String description) {
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("L'émetteur et le destinataire doivent être différents");
        }
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters


    public Integer getId() {
        return id;
    }


    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}