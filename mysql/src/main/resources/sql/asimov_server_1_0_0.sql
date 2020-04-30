create database asimov_server default character set utf8mb4 collate utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `t_miner_operation`;
CREATE TABLE `t_miner_operation` (
  `id` bigint(64) NOT NULL COMMENT 'Primary Key',
  `tx_hash` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Transaction Hash',
  `operation_type` tinyint(4) NOT NULL COMMENT 'Operation Type: 1.proposal 3.sign up 3.vote',
  `round` bigint(20) NOT NULL COMMENT 'Round',
  `additional_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Additional Information(JSON Format)',
  `tx_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Operator Address',
  `create_time` bigint(20) NOT NULL COMMENT 'Create Time',
  `update_time`  bigint(20) NOT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE UNIQUE INDEX INDEX_TX_HASH_UNIQUE ON `t_miner_operation`(`tx_hash`);
CREATE INDEX INDEX_OPERATOR_TYPE ON `t_miner_operation`(`operator`,`operation_type`);
CREATE INDEX INDEX_OPERATION_TYPE ON `t_miner_operation`(`operation_type`);
CREATE INDEX INDEX_ROUND_OPERATOR_TYPE ON `t_miner_operation`(`round`,`operator`,`operation_type`);

DROP TABLE IF EXISTS `t_rollback`;
CREATE TABLE `t_rollback` (
  `id` bigint(64) NOT NULL COMMENT 'Primary Key',
  `height` bigint(20) NOT NULL COMMENT 'Block Height',
  `record_id` bigint(64) NOT NULL COMMENT 'Roll Back Table ID',
  `table_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Roll Back Table Name',
  `original_value` tinyint(4) COMMENT 'Original Value',
  `expect_value` tinyint(4) COMMENT 'Expect Value',
  `create_time` bigint(20) NOT NULL COMMENT 'Create Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX INDEX_HEIGHT ON `t_rollback`(`height`);

DROP TABLE IF EXISTS `t_foundation_operation`;
CREATE TABLE `t_foundation_operation` (
  `id` bigint(64) NOT NULL COMMENT 'Primary Key',
  `tx_hash` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Transaction Hash',
  `operation_type` tinyint(4) NOT NULL COMMENT 'Operate Type: 1.proposal 2.vote 3.donate',
  `additional_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Additional Information(JSON Format)',
  `tx_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Operator Address',
  `create_time` bigint(20) NOT NULL COMMENT 'Create Time',
  `update_time`  bigint(20) NOT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE UNIQUE INDEX INDEX_TX_HASH_UNIQUE ON `t_foundation_operation`(`tx_hash`);
CREATE INDEX INDEX_OPERATOR_TYPE ON `t_foundation_operation`(`operator`,`operation_type`);
CREATE INDEX INDEX_OPERATION_TYPE ON `t_foundation_operation`(`operation_type`);

DROP TABLE IF EXISTS `t_chain_node`;
CREATE TABLE `t_chain_node` (
  `id` bigint(64) NOT NULL COMMENT 'Primary Key',
  `ip` varchar (32) NOT NULL COMMENT 'IP',
  `city` varchar(32) NOT  NULL DEFAULT '' COMMENT 'City',
  `subdivision` varchar(32) NOT  NULL DEFAULT '' COMMENT 'Province',
  `country` varchar(32) NOT  NULL DEFAULT '' COMMENT 'County',
  `longitude` varchar(32) NOT  NULL DEFAULT '' COMMENT 'Longitude',
  `latitude` varchar(32) NOT  NULL DEFAULT '' COMMENT 'Latitude',
  `create_time` bigint(20) NOT NULL COMMENT 'Create Time',
  `update_time`  bigint(20) NOT NULL COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX INDEX_IP ON `t_chain_node`(`ip`);

DROP TABLE IF EXISTS `t_dao_account`;
CREATE TABLE IF NOT EXISTS `t_dao_account`
(
    `id`          BIGINT(64)   NOT NULL COMMENT 'Primary Key',
    `address`     VARCHAR(80)  NOT NULL COMMENT 'Address',
    `nick_name`   VARCHAR(64)  NOT NULL COMMENT 'Nick Name',
    `avatar`      VARCHAR(256) NOT NULL DEFAULT '' COMMENT 'Avatar',
    `create_time` bigint(20)   NOT NULL COMMENT 'Create Time',
    `update_time` bigint(20)   NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
CREATE UNIQUE INDEX `t_account_address_unique` ON `t_dao_account` (address);

DROP TABLE IF EXISTS `t_dao_operation`;
CREATE TABLE `t_dao_operation`
(
    `id`               bigint(64)    NOT NULL COMMENT 'Primary Key',
    `tx_hash`          varchar(64)   NOT NULL DEFAULT '' COMMENT 'Transaction Hash',
    `contract_address` varchar(64)            DEFAULT '' COMMENT 'Contract Address',
    `operation_type`   tinyint(4)    NOT NULL COMMENT 'Operation Type: 1.create organization 2.close organization 3.modify organization name 4.remove member 5.create asset 6.transfer asset 7.president transfer 8.president confirm 9.invite member 10.join organization 11.vote 12.modify organization logo 13.mint asset',
    `additional_info`  varchar(1024) NOT NULL COMMENT 'Additional Information(JSON Format)',
    `tx_status`        tinyint(4)    NOT NULL DEFAULT '0' COMMENT 'Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed 3.local action',
    `operator`         varchar(64)   NOT NULL COMMENT 'Operator Address',
    `create_time`      bigint(20)    NOT NULL COMMENT 'Create Time',
    `update_time`      bigint(20)    NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
CREATE INDEX INDEX_OPERATOR_TYPE ON `t_dao_operation` (`operator`, `operation_type`);
CREATE INDEX INDEX_OPERATION_TYPE ON `t_dao_operation` (`operation_type`);
CREATE INDEX INDEX_CONTRACT_ADDRESS ON `t_dao_operation` (`contract_address`);

DROP TABLE IF EXISTS `t_dao_asset`;
CREATE TABLE IF NOT EXISTS t_dao_asset
(
    `id`               BIGINT(64)   NOT NULL COMMENT 'Primary Key',
    `tx_hash`          varchar(64)  NOT NULL COMMENT 'Transaction Hash',
    `contract_address` VARCHAR(64)  NOT NULL COMMENT 'Contract Address',
    `asset`            VARCHAR(64)  NOT NULL COMMENT 'Asset ID',
    `name`             VARCHAR(64)  NOT NULL COMMENT 'Asset Name',
    `symbol`           VARCHAR(64)  NOT NULL COMMENT 'Asset Symbol',
    `description`      VARCHAR(64)  NOT NULL COMMENT 'Asset Description',
    `logo`             VARCHAR(256) NOT NULL DEFAULT '' COMMENT 'Asset Logo',
    `asset_status`     tinyint(4)   NOT NULL DEFAULT '0' COMMENT 'Asset Status: 0.initialize 1.issue success 2.issue failed',
    `create_time`      bigint(20)   NOT NULL COMMENT 'Create Time',
    `update_time`      bigint(20)   NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

insert into t_dao_asset(id, tx_hash, contract_address, asset, name, symbol, description, logo, asset_status,
                        create_time, update_time)
values (0, '', '', '000000000000000000000000', 'ASIM', 'ASIM', '',
        'https://fingocn.oss-cn-hangzhou.aliyuncs.com/daoTest20/e9/eb/20e9eb87ad204cd0a8e261a71800fd63', 1, now(),
        now());


DROP TABLE IF EXISTS `t_dao_indivisible_asset`;
CREATE TABLE IF NOT EXISTS t_dao_indivisible_asset
(
    `id`               BIGINT(64)   NOT NULL COMMENT 'Primary Key',
    `tx_hash`          varchar(64)  NOT NULL COMMENT 'Transaction Hash',
    `contract_address` VARCHAR(64)  NOT NULL COMMENT 'Contract Address',
    `asset`            VARCHAR(64)  NOT NULL COMMENT 'Asset ID',
    `voucher_id`       bigint(20)   NOT NULL COMMENT 'Asset Number',
    `asset_desc`       VARCHAR(256) NOT NULL DEFAULT '' COMMENT 'Asset Description',
    `asset_status`     tinyint(4)   NOT NULL DEFAULT '0' COMMENT 'Asset Status: 0.initialize 1.issue success 2.issue failed',
    `create_time`      bigint(20)   NOT NULL COMMENT 'Create Time',
    `update_time`      bigint(20)   NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `t_dao_message`;
CREATE TABLE `t_dao_message`
(
    `id`               bigint(64)    NOT NULL COMMENT 'Primary Key',
    `category`         int(4)        NOT NULL COMMENT 'Message Category: 1.be member 2.add new member 3.create organization 4.been removed 5.remove member 6.be president 7.change president 8.transfer asset 9.issue asset 10.modify organization logo 11.modify organization name 12.new vote 13.proposal rejected 14.proposal expired 15.invited 16.close organization 17.receive asset 18.mint asset',
    `type`             int(4)        NOT NULL COMMENT 'Message Type: 1.readonly 2.direct execute 3.vote',
    `message_position` int(4)        NOT NUll COMMENT 'Message Scope: 0.official website 1.dao organization 2.all',
    `contract_address` varchar(64)   NOT NUll COMMENT 'Contract Address',
    `receiver`         varchar(64) DEFAULT NULL COMMENT 'Receiver Address',
    `additional_info`  varchar(1024) NOT NULL COMMENT 'Additional Information(JSON Format)',
    `state`            int(4)        NOT NUll COMMENT 'Message Status: 1.unread 2.read 3.disagree 4.agree',
    `create_time`      bigint(20)    NOT NULL COMMENT 'Create Time',
    `update_time`      bigint(20)    NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `t_dao_organization`;
CREATE TABLE `t_dao_organization`
(
    `id`                    BIGINT(64)  NOT NULL COMMENT 'Primary Key',
    `contract_address`      varchar(64) NOT NULL COMMENT 'Contract Address',
    `vote_contract_address` varchar(64) NOT NULL COMMENT 'Vote Contract Address',
    `creator_address`       varchar(64) NOT NULL DEFAULT '' COMMENT 'Creator Address',
    `org_name`              varchar(64) NOT NULL COMMENT 'Organization Name',
    `org_logo`              varchar(256)         DEFAULT '' COMMENT 'Organization Logo',
    `state`                 tinyint(2)  NOT NULL COMMENT 'Organization Status: 1.normal 2.closed 3.local initialize',
    `create_time`           bigint(20)  NOT NULL COMMENT 'Create Time',
    `update_time`           bigint(20)  NOT NULL COMMENT 'Update Time',
    PRIMARY KEY (`id`),
    -- UNIQUE KEY `t_organization_idx_1` (`contract_address`),
    -- UNIQUE KEY `t_organization_idx_2` (`org_name`),
    KEY `t_organization_idx_3` (`creator_address`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
