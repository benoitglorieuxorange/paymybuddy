package com.globe.paymybuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {
}
