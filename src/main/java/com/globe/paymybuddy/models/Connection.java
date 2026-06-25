package com.globe.paymybuddy.models;


import jakarta.persistence.*;

@Entity
@Table(name = "connection", uniqueConstraints = {
        @UniqueConstraint(
                name ="uc_connection",
                columnNames = {"user_id", "connected_user_id"}
        )
})
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_connection_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connected_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_connection_target"))
    private User connectedUser;


    // Constructors

    public Connection(){}

    public Connection(User user, User connectedUser){
        this.user = user;
        this.connectedUser = connectedUser;
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getConnectedUser() {
        return connectedUser;
    }

    public void setConnectedUser(User connectedUser) {
        this.connectedUser = connectedUser;
    }

}
