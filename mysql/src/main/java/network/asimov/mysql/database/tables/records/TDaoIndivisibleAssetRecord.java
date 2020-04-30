/*
 * This file is generated by jOOQ.
 */
package network.asimov.mysql.database.tables.records;


import javax.annotation.Generated;

import network.asimov.mysql.database.tables.TDaoIndivisibleAsset;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;


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
public class TDaoIndivisibleAssetRecord extends UpdatableRecordImpl<TDaoIndivisibleAssetRecord> implements Record9<Long, String, String, String, Long, String, Byte, Long, Long> {

    private static final long serialVersionUID = 1881893890;

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.id</code>. 主键
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.id</code>. 主键
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.tx_hash</code>. 交易Hash
     */
    public void setTxHash(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.tx_hash</code>. 交易Hash
     */
    public String getTxHash() {
        return (String) get(1);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.contract_address</code>. 组织合约地址
     */
    public void setContractAddress(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.contract_address</code>. 组织合约地址
     */
    public String getContractAddress() {
        return (String) get(2);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.asset</code>. 资产ID
     */
    public void setAsset(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.asset</code>. 资产ID
     */
    public String getAsset() {
        return (String) get(3);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.voucher_id</code>. 资产编号
     */
    public void setVoucherId(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.voucher_id</code>. 资产编号
     */
    public Long getVoucherId() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.asset_desc</code>. 资产描述
     */
    public void setAssetDesc(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.asset_desc</code>. 资产描述
     */
    public String getAssetDesc() {
        return (String) get(5);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.asset_status</code>. 资产状态，0-初始化，待投票通过，1-发行成功，2-发行失败
     */
    public void setAssetStatus(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.asset_status</code>. 资产状态，0-初始化，待投票通过，1-发行成功，2-发行失败
     */
    public Byte getAssetStatus() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.create_time</code>. 创建时间
     */
    public void setCreateTime(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.create_time</code>. 创建时间
     */
    public Long getCreateTime() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>asimov_server.t_dao_indivisible_asset.update_time</code>. 更新时间
     */
    public void setUpdateTime(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>asimov_server.t_dao_indivisible_asset.update_time</code>. 更新时间
     */
    public Long getUpdateTime() {
        return (Long) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Long, String, String, String, Long, String, Byte, Long, Long> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row9<Long, String, String, String, Long, String, Byte, Long, Long> valuesRow() {
        return (Row9) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.TX_HASH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.ASSET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field7() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field9() {
        return TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getTxHash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getContractAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getAsset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component5() {
        return getVoucherId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getAssetDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component7() {
        return getAssetStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component8() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component9() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getTxHash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getContractAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getAsset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getVoucherId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getAssetDesc();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value7() {
        return getAssetStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value8() {
        return getCreateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value9() {
        return getUpdateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value2(String value) {
        setTxHash(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value3(String value) {
        setContractAddress(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value4(String value) {
        setAsset(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value5(Long value) {
        setVoucherId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value6(String value) {
        setAssetDesc(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value7(Byte value) {
        setAssetStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value8(Long value) {
        setCreateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord value9(Long value) {
        setUpdateTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TDaoIndivisibleAssetRecord values(Long value1, String value2, String value3, String value4, Long value5, String value6, Byte value7, Long value8, Long value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TDaoIndivisibleAssetRecord
     */
    public TDaoIndivisibleAssetRecord() {
        super(TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET);
    }

    /**
     * Create a detached, initialised TDaoIndivisibleAssetRecord
     */
    public TDaoIndivisibleAssetRecord(Long id, String txHash, String contractAddress, String asset, Long voucherId, String assetDesc, Byte assetStatus, Long createTime, Long updateTime) {
        super(TDaoIndivisibleAsset.T_DAO_INDIVISIBLE_ASSET);

        set(0, id);
        set(1, txHash);
        set(2, contractAddress);
        set(3, asset);
        set(4, voucherId);
        set(5, assetDesc);
        set(6, assetStatus);
        set(7, createTime);
        set(8, updateTime);
    }
}