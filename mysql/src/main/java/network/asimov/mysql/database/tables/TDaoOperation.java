/*
 * This file is generated by jOOQ.
 */
package network.asimov.mysql.database.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import network.asimov.mysql.database.AsimovServer;
import network.asimov.mysql.database.Indexes;
import network.asimov.mysql.database.Keys;
import network.asimov.mysql.database.tables.records.TDaoOperationRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.12"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TDaoOperation extends TableImpl<TDaoOperationRecord> {

    private static final long serialVersionUID = -268387262;

    /**
     * The reference instance of <code>asimov_server.t_dao_operation</code>
     */
    public static final TDaoOperation T_DAO_OPERATION = new TDaoOperation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TDaoOperationRecord> getRecordType() {
        return TDaoOperationRecord.class;
    }

    /**
     * The column <code>asimov_server.t_dao_operation.id</code>. 主键
     */
    public final TableField<TDaoOperationRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "主键");

    /**
     * The column <code>asimov_server.t_dao_operation.tx_hash</code>. 交易Hash
     */
    public final TableField<TDaoOperationRecord, String> TX_HASH = createField("tx_hash", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "交易Hash");

    /**
     * The column <code>asimov_server.t_dao_operation.contract_address</code>. 组织合约地址
     */
    public final TableField<TDaoOperationRecord, String> CONTRACT_ADDRESS = createField("contract_address", org.jooq.impl.SQLDataType.VARCHAR(64).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "组织合约地址");

    /**
     * The column <code>asimov_server.t_dao_operation.operation_type</code>. 操作类型：1、创建组织 2、关闭组织 3、修改组织名 4、移除成员 5、创建资产 6、转移资产 7、转让主席 8、确认主席 9、邀请成员 10、加入组织 11、投票 12、修改组织logo 13、增发资产
     */
    public final TableField<TDaoOperationRecord, Byte> OPERATION_TYPE = createField("operation_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "操作类型：1、创建组织 2、关闭组织 3、修改组织名 4、移除成员 5、创建资产 6、转移资产 7、转让主席 8、确认主席 9、邀请成员 10、加入组织 11、投票 12、修改组织logo 13、增发资产");

    /**
     * The column <code>asimov_server.t_dao_operation.additional_info</code>. 附加信息(JSON字符串)
     */
    public final TableField<TDaoOperationRecord, String> ADDITIONAL_INFO = createField("additional_info", org.jooq.impl.SQLDataType.VARCHAR(1024).nullable(false), this, "附加信息(JSON字符串)");

    /**
     * The column <code>asimov_server.t_dao_operation.tx_status</code>. 交易状态，0-待确认，1-交易入块、合约执行成功，2-交易入块、合约执行失败，3-无需上链
     */
    public final TableField<TDaoOperationRecord, Byte> TX_STATUS = createField("tx_status", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "交易状态，0-待确认，1-交易入块、合约执行成功，2-交易入块、合约执行失败，3-无需上链");

    /**
     * The column <code>asimov_server.t_dao_operation.operator</code>. 操作人地址
     */
    public final TableField<TDaoOperationRecord, String> OPERATOR = createField("operator", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "操作人地址");

    /**
     * The column <code>asimov_server.t_dao_operation.create_time</code>. 创建时间
     */
    public final TableField<TDaoOperationRecord, Long> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "创建时间");

    /**
     * The column <code>asimov_server.t_dao_operation.update_time</code>. 更新时间
     */
    public final TableField<TDaoOperationRecord, Long> UPDATE_TIME = createField("update_time", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "更新时间");

    /**
     * Create a <code>asimov_server.t_dao_operation</code> table reference
     */
    public TDaoOperation() {
        this(DSL.name("t_dao_operation"), null);
    }

    /**
     * Create an aliased <code>asimov_server.t_dao_operation</code> table reference
     */
    public TDaoOperation(String alias) {
        this(DSL.name(alias), T_DAO_OPERATION);
    }

    /**
     * Create an aliased <code>asimov_server.t_dao_operation</code> table reference
     */
    public TDaoOperation(Name alias) {
        this(alias, T_DAO_OPERATION);
    }

    private TDaoOperation(Name alias, Table<TDaoOperationRecord> aliased) {
        this(alias, aliased, null);
    }

    private TDaoOperation(Name alias, Table<TDaoOperationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TDaoOperation(Table<O> child, ForeignKey<O, TDaoOperationRecord> key) {
        super(child, key, T_DAO_OPERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return AsimovServer.ASIMOV_SERVER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.T_DAO_OPERATION_INDEX_CONTRACT_ADDRESS, Indexes.T_DAO_OPERATION_INDEX_OPERATION_TYPE, Indexes.T_DAO_OPERATION_INDEX_OPERATOR_TYPE, Indexes.T_DAO_OPERATION_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TDaoOperationRecord> getPrimaryKey() {
        return Keys.KEY_T_DAO_OPERATION_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TDaoOperationRecord>> getKeys() {
        return Arrays.<UniqueKey<TDaoOperationRecord>>asList(Keys.KEY_T_DAO_OPERATION_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoOperation as(String alias) {
        return new TDaoOperation(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoOperation as(Name alias) {
        return new TDaoOperation(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TDaoOperation rename(String name) {
        return new TDaoOperation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TDaoOperation rename(Name name) {
        return new TDaoOperation(name, null);
    }
}
