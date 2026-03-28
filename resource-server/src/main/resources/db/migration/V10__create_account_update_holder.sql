CREATE TABLE IF NOT EXISTS `bank_account_update_holder` (
    `id` BIGINT AUTO_INCREMENT,
    `client_code` VARCHAR (36) NOT NULL,
    `name` VARCHAR (80) NULL,
    `last_name` VARCHAR (80) NULL,
    `email` VARCHAR (80) NULL,
    `phone` VARCHAR (20) NULL,
    `birth_date` TIMESTAMP NULL DEFAULT NULL,
    `age` INT NULL,
    `cpf` VARCHAR (11) NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uuid_update_holder` (`client_code`)
);