package network.asimov.behavior.convert;

import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.response.common.AssetView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
@Component("assetConvertBehavior")
public class AssetConvertBehavior implements ConvertBehavior<AssetSummary, AssetView> {
    @Resource(name = "assetService")
    private AssetService assetService;

    @Override
    public AssetView convert(AssetSummary origin) {
        Optional<Asset> result = assetService.getAsset(origin.getAsset());
        AssetView assetView = AssetView.builder().asset(origin.getAsset()).value(String.valueOf(origin.getValue())).build();
        result.ifPresent(v -> {
            assetView.setName(v.getName());
            assetView.setSymbol(v.getSymbol());
            assetView.setDescription(v.getDescription());
            assetView.setLogo(v.getLogo());
        });

        return assetView;
    }

    @Override
    public List<AssetView> convert(List<AssetSummary> origins) {
        return origins.stream().map(this::convert).collect(Collectors.toList());
    }
}
