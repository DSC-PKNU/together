CREATE TABLE `user` (
    `id` varchar(20) NOT NULL,
    `password` varchar(20) NOT NULL,
    `name` varchar(20) NOT NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `user` VALUES ('admin','0000','관리자')