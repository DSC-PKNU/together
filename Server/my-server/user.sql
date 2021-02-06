CREATE TABLE `user` (
    `id` varchar(20) NOT NULL,
    `password` varchar(20) NOT NULL,
    `name` varchar(20) NOT NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `topic` VALUES ('admin_id','0000','관리자')