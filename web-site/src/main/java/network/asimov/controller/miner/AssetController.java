package network.asimov.controller.miner;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-10-09
 */

@CrossOrigin
@RestController("minerAssetController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class AssetController {
    @Resource
    private AssetService assetService;

    @ApiOperation(value = "List asset")
    @PostMapping(path = "/asset/list")
    public ResultView<List<AssetView>> listAsset() {
        List<AssetView> assetViewList = Lists.newArrayList();
        List<Asset> assetList = assetService.listAllAsset();
        for (Asset asset : assetList) {
            if (asset.getAsset().equals(ChainConstant.ASSET_ASIM)) {
                continue;
            }
            assetViewList.add(AssetView.builder()
                    .asset("0x" + asset.getAsset())
                    .name(asset.getName())
                    .build());
        }

        return ResultView.ok(assetViewList);
    }
}
