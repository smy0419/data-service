package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.constant.DaoAsset;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Component("expenseCheck")
public class ExpenseCheck implements CheckBehavior {
    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        String asset = (String) args.get("asset");
        Integer assetType = (Integer) args.get("assetType");
        Long amount = (Long) args.get("amount");

        Optional<Organization> orgOptional = organizationService.getOrganizationByAddress(contractAddress);
        // Check that the organization exists and is in the correct state
        if (!orgOptional.isPresent()) {
            return ResultView.error(ErrorCode.ORGANIZATION_NOT_EXISTS_ERROR);
        }

        if (orgOptional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        // Check whether the user is the organization chairman
        if (!orgOptional.get().getPresident().equals(address)) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        // Check Balance
        if (assetType.equals(DaoAsset.Type.Divisible.ordinal())) {
            long balance = this.getAmountByAsset(contractAddress, asset);
            if (balance < amount) {
                return ResultView.error(ErrorCode.NOT_ENOUGH_AMOUNT);
            }
        } else {
            boolean containFlag = ownIndivisibleAsset(contractAddress, asset, amount);
            if (!containFlag) {
                return ResultView.error(ErrorCode.NOT_ENOUGH_AMOUNT);
            }
        }

        return ResultView.ok();
    }

    private long getAmountByAsset(String contractAddress, String asset) {
        long value = 0;
        List<AssetDTO> assetDTOS = balanceService.listBalance(contractAddress);
        for (AssetDTO assetDTO : assetDTOS) {
            if (assetDTO.getAsset().equals(asset)) {
                value = Long.parseLong(assetDTO.getValue());
                break;
            }
        }
        return value;
    }

    public boolean ownIndivisibleAsset(String contractAddress, String asset, Long voucherId) {
        List<AssetDTO> assetDTOS = balanceService.listBalance(contractAddress);
        for (AssetDTO assetDTO : assetDTOS) {
            if (assetDTO.getAsset().equals(asset)) {
                for (AssetDTO.Indivisible indivisible : assetDTO.getIndivisibleList()) {
                    if (indivisible.getNumber().equals(String.valueOf(voucherId))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
