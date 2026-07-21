CREATE TABLE `connection` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `connected_user_id` bigint NOT NULL,
                              `user_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uc_connection` (`user_id`,`connected_user_id`),
                              KEY `fk_connection_target` (`connected_user_id`),
                              CONSTRAINT `fk_connection_target` FOREIGN KEY (`connected_user_id`) REFERENCES `user` (`id`),
                              CONSTRAINT `fk_connection_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transaction` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `amount` decimal(15,2) NOT NULL,
                               `created_at` datetime(6) NOT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               `receiver_id` bigint NOT NULL,
                               `sender_id` bigint NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `idx_tx_sender` (`sender_id`),
                               KEY `idx_tx_receiver` (`receiver_id`),
                               CONSTRAINT `fk_tx_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`),
                               CONSTRAINT `fk_tx_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `balance` decimal(38,2) NOT NULL,
                        `email` varchar(100) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `username` varchar(45) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uq_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;