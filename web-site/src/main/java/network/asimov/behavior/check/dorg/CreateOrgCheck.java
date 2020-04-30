package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.tables.pojos.TDaoOperation;
import network.asimov.mysql.database.tables.pojos.TDaoOrganization;
import network.asimov.mysql.service.dorg.DaoOperationService;
import network.asimov.mysql.service.dorg.DaoOrganizationService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@Component("createOrgCheck")
public class CreateOrgCheck implements CheckBehavior {
    @Resource(name = "daoMysqlOrganizationService")
    private DaoOrganizationService daoOrganizationService;

    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String orgName = (String) args.get("orgName");

        // check organization name is available
        List<TDaoOrganization> orgList = daoOrganizationService.listOrgByName(orgName);
        for (TDaoOrganization org : orgList) {
            // If already have an organization in a normal or closed state, return an error message directly
            if (org.getState() != Organization.Status.Init.ordinal()) {
                return ResultView.error(ErrorCode.REPEAT_ORGANIZATION_NAME);
            } else {
                Optional<TDaoOperation> operation = daoOperationService.getOperationByTxHash(org.getTxHash());
                if (operation.get().getTxStatus() != TxStatus.ContractRevert.ordinal()) {
                    return ResultView.error(ErrorCode.REPEAT_ORGANIZATION_NAME);
                }
            }
        }

        return ResultView.ok();
    }
}
