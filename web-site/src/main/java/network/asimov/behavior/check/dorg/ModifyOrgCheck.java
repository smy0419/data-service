package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.constant.OperationAdditionalKey;
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
 * @date 2019-12-12
 */
@Component("modifyOrgCheck")
public class ModifyOrgCheck implements CheckBehavior {
    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Resource(name = "daoMysqlOrganizationService")
    private DaoOrganizationService daoOrganizationService;

    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        String newOrgName = (String) args.get("newOrgName");
        String modifyTarget = (String) args.get("modifyTarget");

        Optional<Organization> optional = organizationService.getOrganizationByAddress(contractAddress);
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }

        if (optional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        if (!optional.get().getPresident().equals(address)) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }
        if (OperationAdditionalKey.ORG_NAME.equals(modifyTarget)) {
            List<TDaoOrganization> orgList = daoOrganizationService.listOrgByName(newOrgName);
            for (TDaoOrganization org : orgList) {
                if (!org.getState().equals(Organization.Status.Init.ordinal())) {
                    return ResultView.error(ErrorCode.REPEAT_ORGANIZATION_NAME);
                } else {
                    Optional<TDaoOperation> operation = daoOperationService.getOperationByTxHash(org.getTxHash());
                    if (!operation.get().getTxStatus().equals(TxStatus.ContractRevert.ordinal())) {
                        return ResultView.error(ErrorCode.REPEAT_ORGANIZATION_NAME);
                    }
                }
            }
        }

        return ResultView.ok();
    }
}
