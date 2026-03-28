CREATE TABLE IF NOT EXISTS `account_transfer_lock` (
    `id` BIGINT AUTO_INCREMENT,
    `is_locked` TINYINT(1) NOT NULL DEFAULT 0,
    `locked_at` TIMESTAMP,
    `total_value_transferred_today` DECIMAL(6,2) NOT NULL DEFAULT 0.0,

    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `client` (
    `id` BIGINT AUTO_INCREMENT,
    `name` VARCHAR (80) NOT NULL,
    `last_name` VARCHAR (80),
    `email` VARCHAR (80) NOT NULL,
    `phone` VARCHAR (80) NOT NULL,
    `age` INT NOT NULL,
    `birth_date` TIMESTAMP NULL DEFAULT NULL,
    `auth_user_id` BIGINT NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
);

CREATE TABLE IF NOT EXISTS `client_account` (
    `id` BIGINT AUTO_INCREMENT,
    `cpf` VARCHAR (60) NOT NULL,
    `client_id` BIGINT NOT NULL,
    `balance` DECIMAL (6,2) NOT NULL,
    `account_transfer_lock_id` BIGINT NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cpf` (`cpf`),
    CONSTRAINT `fk_client_id` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`),
    CONSTRAINT `fk_account_transfer_lock_id` FOREIGN KEY (`account_transfer_lock_id`) REFERENCES `account_transfer_lock` (`id`)
);

CREATE TABLE IF NOT EXISTS `transfer` (
    `id` BIGINT AUTO_INCREMENT,
    `source_client_account_id` BIGINT NOT NULL,
    `target_client_account_id` BIGINT NOT NULL,
    `value` DECIMAL (6,2) NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_source_client_account_id` FOREIGN KEY (`source_client_account_id`) REFERENCES `client_account` (`id`),
    CONSTRAINT `fk_target_client_account_id` FOREIGN KEY (`target_client_account_id`) REFERENCES `client_account` (`id`)
);

