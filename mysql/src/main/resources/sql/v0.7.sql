alter table t_dao_organization add column tx_hash varchar(64) NOT NULL COMMENT 'Transaction Hash';
alter table `t_dao_organization` modify column `state` tinyint(2) COMMENT 'Organization Status: 0.normal 1.closed 2.local initialize';
alter table `t_dao_message` add column height bigint(20) NOT NULL COMMENT 'Block Height';
