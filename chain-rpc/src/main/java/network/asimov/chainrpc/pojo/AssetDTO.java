package network.asimov.chainrpc.pojo;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.util.AssetUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-09-20
 */
@Data
public class AssetDTO implements Comparable<AssetDTO> {
    private String asset;
    private String value;
    private String name;
    private String symbol;
    private String description;
    private String logo;
    private final List<Indivisible> indivisibleList;

    @Builder
    public AssetDTO(String asset, String value, Map<String, Asset> assetMap, String indivisibleDesc) {
        this.asset = asset;
        if (assetMap != null && assetMap.containsKey(asset)) {
            Asset tmp = assetMap.get(asset);
            this.name = tmp.getName();
            this.symbol = tmp.getSymbol();
            this.description = tmp.getDescription();
            this.logo = tmp.getLogo();
        } else {
            this.name = StringUtils.EMPTY;
            this.symbol = StringUtils.EMPTY;
            this.description = StringUtils.EMPTY;
            this.logo = StringUtils.EMPTY;
        }

        // 不可分割资产
        if (this.indivisible()) {
            this.value = "1";
            indivisibleList = Lists.newArrayList(Indivisible.builder().number(value).description(indivisibleDesc).build());
        } else {
            indivisibleList = null;
            this.value = value;
        }
    }

    @Override
    public int compareTo(@NotNull AssetDTO o) {
        return this.asset.compareTo(o.asset);
    }

    @Data
    @Builder
    public static class Indivisible implements Comparable<Indivisible> {
        private String number;
        private String description;

        @Override
        public int compareTo(@NotNull Indivisible o) {
            return Long.valueOf(this.number).compareTo(Long.valueOf(o.number));
        }
    }

    private boolean indivisible() {
        return AssetUtil.indivisible(this.getAsset());
    }

    public void addIndivisible(String value, String description) {
        this.value = String.valueOf(Long.valueOf(this.value) + 1);
        this.indivisibleList.add((Indivisible.builder().number(value).description(description).build()));
    }
}
