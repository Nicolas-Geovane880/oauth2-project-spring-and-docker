CREATE TABLE IF NOT EXISTS `processed_event` (
    `id` BIGINT AUTO_INCREMENT,
    `code` VARCHAR (36) NOT NULL,
    `event_type` VARCHAR (50) NOT NULL,
    `processed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uuid_event` (`code`)
);