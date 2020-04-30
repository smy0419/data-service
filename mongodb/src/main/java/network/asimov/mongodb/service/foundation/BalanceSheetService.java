package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.BalanceSheet;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("foundationBalanceSheetService")
public class BalanceSheetService extends BaseService {
    /**
     * List asset balance sheet
     *
     * @return balance sheet
     */
    public Pair<Long, List<BalanceSheet>> listBalanceSheet(Integer index, Integer limit) {
        Query query = new Query();
        return queryByPage(index, limit, query, BalanceSheet.class, Sort.Direction.DESC, "time");
    }
}
