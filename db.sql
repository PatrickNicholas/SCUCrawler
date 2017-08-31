SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `article_type`;
DROP TABLE IF EXISTS `article`;

CREATE TABLE `article_type` (
    `type_id` INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `internal_name` CHAR(32) NOT NULL UNIQUE,
    `name` CHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE `article` (
    `article_id` INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `type_id` INTEGER NOT NULL,
    `title` VARCHAR(128) NOT NULL,
    `url` VARCHAR(512) NOT NULL UNIQUE,
    `thumb` VARCHAR(512) DEFAULT NULL,
    `content` TEXT NOT NULL,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(`type_id`) REFERENCES `article_type`(`type_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

INSERT INTO `article_type` (`internal_name`,`name`) VALUES("jwcxw", "教务处");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("jwctz", "教务处");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("xgbxw", "学工部");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("xgbtz", "学工部");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("qccdxw", "青春川大");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("qccdtz", "青春川大");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("csxw", "计算机学院");
INSERT INTO `article_type` (`internal_name`,`name`) VALUES("cstz", "计算机学院");

SET FOREIGN_KEY_CHECKS = 1;
