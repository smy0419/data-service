package network.asimov.behavior.check.ascan;

import lombok.Builder;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.service.ascan.TransactionStatisticsService;
import network.asimov.response.ResultView;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2020-01-28
 */
@Builder
public class AddressExistCheck implements CheckBehavior {
   private TransactionStatisticsService transactionStatisticsService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String key = (String) args.get("key");
        Optional addressOptional = transactionStatisticsService.get(key);
        if (addressOptional.isPresent()) {
            return ResultView.ok(addressOptional.get());
        } else {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
    }
}
