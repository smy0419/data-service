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
 * @date 2019-12-12
 */
@Component("closeOrgCheck")
public class CloseOrgCheck implements CheckBehavior {
    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        // check if the organization exists
        Optional<Organization> optional = organizationService.getOrganizationByAddress(contractAddress);
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        // Check if the president exists
        if (!optional.get().getPresident().equals(address)) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }
        // check if organization status is normal
        if (!optional.get().getStatus().equals(Organization.Status.Normal.ordinal())) {
            return ResultView.error(ErrorCode.DATA_ERROR);
        }

        return ResultView.ok();
    }
}