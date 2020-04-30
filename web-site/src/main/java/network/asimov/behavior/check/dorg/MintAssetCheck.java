package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.constant.DaoAsset;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-02-12
 */
@Component("mintAssetCheck")
public class MintAssetCheck implements CheckBehavior {
    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        String asset = (String) args.get("asset");
        Integer assetType = (Integer) args.get("assetType");
        Long amountOrVoucherId = (Long) args.get("amountOrVoucherId");

        Optional<Organization> optional = organizationService.getOrganizationByAddress(contractAddress);
        // Check that the organization exists and is in the correct state
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.ORGANIZATION_NOT_EXISTS_ERROR);
        }

        if (optional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        // Check whether the user is the organization chairman
        if (!optional.get().getPresident().equals(address)) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        if (assetType.equals(DaoAsset.Type.Indivisible.ordinal())) {
            int count = daoIndivisibleAssetService.countIndivisibleAssetByVoucherId(contractAddress, asset, amountOrVoucherId);
            if (count > 0) {
                return ResultView.error(ErrorCode.VOUCHER_ID_REPEAT_ERROR);
            }
        }

        return ResultView.ok();
    }
}
