package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mysql.constant.DaoAsset;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoIndivisibleAsset;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2020-02-11
 */
@Service("daoIndivisibleAssetService")
public class DaoIndivisibleAssetService extends BaseService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public void saveIndivisibleAsset(TDaoIndivisibleAsset obj) {
        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        dSLContext.insertInto(Tables.T_DAO_INDIVISIBLE_ASSET)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ID, id)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH, obj.getTxHash())
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS, obj.getContractAddress())
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET, obj.getAsset())
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID, obj.getVoucherId())
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC, obj.getAssetDesc())
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME, now)
                .execute();
    }

    private List<TDaoIndivisibleAsset> listIndivisibleAssets(String asset, List<Long> voucherIds) {
        if (StringUtils.isEmpty(asset) || voucherIds == null || voucherIds.isEmpty()) {
            return Lists.newArrayList();
        }
        Condition condition = Tables.T_DAO_INDIVISIBLE_ASSET.ASSET.eq(asset)
                .and(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS.eq((byte) DaoAsset.Status.Success.ordinal()))
                .and(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID.in(voucherIds));
        List<TDaoIndivisibleAsset> result = dSLContext.select()
                .from(Tables.T_DAO_INDIVISIBLE_ASSET)
                .where(condition)
                .fetchInto(TDaoIndivisibleAsset.class);
        return result;
    }

    public List<TDaoIndivisibleAsset> listIndivisibleAssets(String asset) {
        Condition condition = Tables.T_DAO_INDIVISIBLE_ASSET.ASSET.eq(asset);
        List<TDaoIndivisibleAsset> result = dSLContext.select()
                .from(Tables.T_DAO_INDIVISIBLE_ASSET)
                .where(condition)
                .orderBy(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME.asc())
                .fetchInto(TDaoIndivisibleAsset.class);
        return result;
    }

    public Map<Long, String> mapIndivisibleDesc(String asset, List<Long> voucherIds) {
        List<TDaoIndivisibleAsset> list = this.listIndivisibleAssets(asset, voucherIds);
        return list.stream().collect(Collectors.toMap(TDaoIndivisibleAsset::getVoucherId, TDaoIndivisibleAsset::getAssetDesc));
    }

    public String getIndivisibleDesc(String asset, Long voucherId) {
        List<TDaoIndivisibleAsset> list = this.listIndivisibleAssets(asset, Lists.newArrayList(voucherId));
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.get(0).getAssetDesc();
    }

    public int countIndivisibleAssetByVoucherId(String contractAddress, String asset, Long voucherId) {
        Condition condition = Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS.eq(contractAddress)
                .and(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET.eq(asset))
                .and(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID.eq(voucherId))
                .and(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS.ne((byte) DaoAsset.Status.Failed.ordinal()));
        return dSLContext.selectCount().from(Tables.T_DAO_INDIVISIBLE_ASSET).where(condition).fetchOne().value1();
    }
}
