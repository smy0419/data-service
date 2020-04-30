package network.asimov.behavior.convert;

import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.response.common.AssetView;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Component("balanceConvertBehavior")
public class BalanceConvertBehavior implements ConvertBehavior<AssetDTO, AssetView> {
    @Override
    public AssetView convert(AssetDTO origin) {
        AssetView assetView = AssetView.builder()
                .asset(origin.getAsset())
                .value(origin.getValue())
                .name(origin.getName())
                .symbol(origin.getSymbol())
                .description(origin.getDescription())
                .logo(origin.getLogo()).build();
        if (CollectionUtils.isNotEmpty(origin.getIndivisibleList())) {
            assetView.setIndivisibleList(origin.getIndivisibleList().stream().map(v -> AssetView.Indivisible.builder().number(v.getNumber()).description(v.getDescription()).build()).collect(Collectors.toList()));
        }
        return assetView;
    }

    @Override
    public List<AssetView> convert(List<AssetDTO> origins) {
        return origins.stream().map(this::convert).collect(Collectors.toList());
    }
}
