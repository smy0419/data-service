package network.asimov.mysql.service.dorg;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoAsset;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.util.TimeUtil;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sunmengyuan
 * @date 2020-01-07
 */
@Service("daoAssetService")
public class DaoAssetService extends BaseService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public void saveAsset(TDaoAsset asset) {
        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        dSLContext.insertInto(Tables.T_DAO_ASSET)
                .set(Tables.T_DAO_ASSET.ID, id)
                .set(Tables.T_DAO_ASSET.TX_HASH, asset.getTxHash())
                .set(Tables.T_DAO_ASSET.ASSET, asset.getAsset())
                .set(Tables.T_DAO_ASSET.CONTRACT_ADDRESS, asset.getContractAddress())
                .set(Tables.T_DAO_ASSET.DESCRIPTION, asset.getName())
                .set(Tables.T_DAO_ASSET.NAME, asset.getName())
                .set(Tables.T_DAO_ASSET.LOGO, asset.getLogo())
                .set(Tables.T_DAO_ASSET.SYMBOL, asset.getSymbol())
                .set(Tables.T_DAO_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_ASSET.UPDATE_TIME, now)
                .execute();
    }

    public int countOrgAsset(String contractAddress) {
        Condition condition = Tables.T_DAO_ASSET.CONTRACT_ADDRESS.eq(contractAddress);
        return dSLContext.selectCount().from(Tables.T_DAO_ASSET).where(condition).fetchOne().value1();
    }
}
