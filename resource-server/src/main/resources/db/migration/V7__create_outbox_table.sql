CREATE TABLE IF NOT EXISTS `outbox` (
    `id` BIGINT AUTO_INCREMENT,
    `code` VARCHAR (36) NOT NULL,
    `payload` JSON NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_processed` TINYINT (1) NOT NULL DEFAULT 0,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uuid_outbox` (`code`)
);