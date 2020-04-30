package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-12-18
 */
@Component("issueAssetCheck")
public class IssueAssetCheck implements CheckBehavior {
    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");

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

        return ResultView.ok();
    }
}
