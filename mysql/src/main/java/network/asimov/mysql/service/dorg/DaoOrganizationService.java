package network.asimov.mysql.service.dorg;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoOrganization;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.util.TimeUtil;
import org.apache.logging.log4j.util.Strings;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-14
 */
@Service("daoMysqlOrganizationService")
public class DaoOrganizationService extends BaseService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public void saveOrganization(TDaoOrganization org) {
        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, id)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, org.getTxHash())
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, Strings.EMPTY)
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, Strings.EMPTY)
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, org.getCreatorAddress())
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, org.getOrgName())
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, org.getOrgLogo() == null ? Strings.EMPTY : org.getOrgLogo())
                .set(Tables.T_DAO_ORGANIZATION.STATE, org.getState())
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();
    }

    public void updateOrganization(String contractAddress, String newOrgLogo) {
        dSLContext.update(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, newOrgLogo)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, TimeUtil.currentSeconds())
                .where(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS.eq(contractAddress))
                .execute();
    }

    public Optional<TDaoOrganization> getOrgByContractAddress(String contractAddress) {
        Condition condition = Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS.eq(contractAddress);
        TDaoOrganization result = dSLContext.select()
                .from(Tables.T_DAO_ORGANIZATION)
                .where(condition)
                .fetchOneInto(TDaoOrganization.class);

        return Optional.ofNullable(result);
    }

    public List<TDaoOrganization> listOrgByName(String orgName) {
        Condition condition = Tables.T_DAO_ORGANIZATION.ORG_NAME.eq(orgName);
        return dSLContext.select()
                .from(Tables.T_DAO_ORGANIZATION)
                .where(condition)
                .fetchInto(TDaoOrganization.class);
    }
}
