package network.asimov.controller.ecology;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.mysql.database.tables.pojos.TChainNode;
import network.asimov.mysql.service.ecology.ChainNodeService;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.ecology.ChainNodeView;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-10-24
 */

@CrossOrigin
@RestController("ecologyController")
@Api(tags = "ecology")
@RequestMapping(path = "/ecology", produces = RequestConstants.CONTENT_TYPE_JSON)
public class EcologyController {

    @Resource(name = "chainNodeService")
    private ChainNodeService chainNodeService;

    @ApiOperation(value = "Get all nodes")
    @PostMapping(path = "/node")
    public ResultView<List<ChainNodeView>> listChainNode() {
        List<TChainNode> nodes = chainNodeService.listChainNode();
        List<ChainNodeView> result = nodes.stream().map(v -> ChainNodeView.builder()
                .ip(v.getIp())
                .city(v.getCity())
                .subdivision(v.getSubdivision())
                .country(v.getCountry())
                .longitude(v.getLongitude())
                .latitude(v.getLatitude()).build()).collect(Collectors.toList());
        return ResultView.ok(result);
    }
}
