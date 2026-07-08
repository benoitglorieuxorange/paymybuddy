-- =========================================================
-- Script de création de l'infrastructure de la base de données
-- Application : PayMyBuddy
-- Contenu : Création de la base + des tables
-- SGBD : MySQL
-- =========================================================

-- 1. Création de la base de données
DROP DATABASE IF EXISTS paymybuddy;
CREATE DATABASE paymybuddy
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE paymybuddy;

-- =========================================================
-- 2. Table des utilisateurs
-- =========================================================
CREATE TABLE user (
                      id            INT AUTO_INCREMENT PRIMARY KEY,
                      username      VARCHAR(50)  NOT NULL,
                      email         VARCHAR(100) NOT NULL,
                      password      VARCHAR(255) NOT NULL,
                      balance       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                      created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      CONSTRAINT uq_user_email UNIQUE (email)
) ENGINE=InnoDB;

-- =========================================================
-- 3. Table des connexions (relations d'amis / bénéficiaires)
--    Un utilisateur peut ajouter un autre utilisateur comme relation
-- =========================================================
CREATE TABLE connection (
                            id                INT AUTO_INCREMENT PRIMARY KEY,
                            user_id           INT NOT NULL,
                            connection_id     INT NOT NULL,
                            created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_connection_user
                                FOREIGN KEY (user_id) REFERENCES user(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_connection_connection
                                FOREIGN KEY (connection_id) REFERENCES user(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT uq_connection_pair UNIQUE (user_id, connection_id),

                            CONSTRAINT chk_connection_not_self
                                CHECK (user_id <> connection_id)
) ENGINE=InnoDB;

-- =========================================================
-- 4. Table des transactions
--    Un envoyeur transfère de l'argent à un destinataire
-- =========================================================
CREATE TABLE transaction (
                             id              INT AUTO_INCREMENT PRIMARY KEY,
                             sender_id       INT NOT NULL,
                             receiver_id     INT NOT NULL,
                             description     VARCHAR(255),
                             amount          DECIMAL(10,2) NOT NULL,
                             created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_transaction_sender
                                 FOREIGN KEY (sender_id) REFERENCES user(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_transaction_receiver
                                 FOREIGN KEY (receiver_id) REFERENCES user(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT chk_transaction_amount_positive
                                 CHECK (amount > 0)
) ENGINE=InnoDB;

-- =========================================================
-- 5. Index utiles pour les recherches fréquentes
-- =========================================================
CREATE INDEX idx_connection_user_id ON connection(user_id);
CREATE INDEX idx_transaction_sender_id ON transaction(sender_id);
CREATE INDEX idx_transaction_receiver_id ON transaction(receiver_id);