CREATE TABLE `spring_ioc_account`
(
    `cardNo` varchar(64) NOT NULL COMMENT '银行卡号',
    `name`   varchar(64)    DEFAULT NULL COMMENT '用户名',
    `money`  decimal(15, 2) DEFAULT NULL COMMENT '账户金额',
    PRIMARY KEY (`cardNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户信息表';

INSERT INTO `spring_ioc_account`(`cardNo`, `name`, `money`)
VALUES ('110', '法外狂徒', 10000.00),
       ('120', '张三', 10000.00);