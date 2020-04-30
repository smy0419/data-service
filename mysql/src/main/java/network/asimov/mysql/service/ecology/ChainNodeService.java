package network.asimov.mysql.service.ecology;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TChainNode;
import network.asimov.mysql.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-24
 */
@Service("chainNodeService")
public class ChainNodeService extends BaseService {
    public List<TChainNode> listChainNode() {
        return dSLContext.select().from(Tables.T_CHAIN_NODE).fetchInto(TChainNode.class);
    }
}
