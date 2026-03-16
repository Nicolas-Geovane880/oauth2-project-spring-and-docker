CREATE TABLE IF NOT EXISTS `processed_event` (
    `id` BIGINT AUTO_INCREMENT,
    `uuid` VARCHAR (40) NOT NULL,
    `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `event_type` VARCHAR (50) NOT NULL,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uuid` (`uuid`)
);