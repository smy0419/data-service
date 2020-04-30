package network.asimov.behavior.convert;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.ascan.Transaction;
import network.asimov.mongodb.entity.ascan.Vin;
import network.asimov.mongodb.entity.ascan.Vout;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.response.ascan.RawTxView;
import network.asimov.response.ascan.VinView;
import network.asimov.response.ascan.VoutView;
import network.asimov.response.common.AssetView;
import network.asimov.util.AssetUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-11-02
 */
@Component("rawTxAssembleBehavior")
public class RawTxAssembleBehavior {

    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    public void setVinVout(Transaction tx, RawTxView rawTxView, List<Vin> vins, List<Vout> vouts) {
        // Vin
        List<VinView> vinViews = Lists.newArrayList();
        vins.forEach(vin -> {
            VinView vinView = VinView.builder()
                    .sequence(vin.getSequence())
                    .coinBase(StringUtils.isBlank(vin.getCoinBase()) ? StringUtils.EMPTY : vin.getCoinBase())
                    .outTxHash(StringUtils.isBlank(vin.getOutTxHash()) ? StringUtils.EMPTY : vin.getOutTxHash())
                    .vout(StringUtils.isBlank(vin.getCoinBase()) ? vin.getVOut() : -1)
                    .build();
            if (StringUtils.isBlank(vin.getCoinBase())) {
                VinView.ScriptSigView scriptSigView = VinView.ScriptSigView.builder()
                        .hex(vin.getScriptSig().getHex())
                        .build();
                vinView.setScriptSig(scriptSigView);
                VinView.PrevOutView prevOutView = VinView.PrevOutView.builder()
                        .addresses(vin.getPrevOut().getAddresses())
                        .asset(generateAsset(vin.getPrevOut().getAsset(), String.valueOf(vin.getPrevOut().getValue())))
                        .build();
                vinView.setPrevOutView(prevOutView);
            }
            vinViews.add(vinView);
        });
        rawTxView.setVin(vinViews);

        // Vout
        List<VoutView> voutViews = Lists.newArrayList();
        vouts.forEach(vout -> {
            VoutView.ScriptPubKeyView scriptPubKeyView = VoutView.ScriptPubKeyView.builder()
                    .addresses(vout.getScriptPubKey().getAddresses())
                    .asm(vout.getScriptPubKey().getAsm())
                    .reqSigs(vout.getScriptPubKey().getReqSigs())
                    .type(vout.getScriptPubKey().getType())
                    .build();

            VoutView voutView = VoutView.builder()
                    .asset(generateAsset(vout.getAsset(), String.valueOf(vout.getValue())))
                    .n(vout.getN())
                    .scriptPubKey(scriptPubKeyView)
                    .build();

            voutViews.add(voutView);
        });
        rawTxView.setVout(voutViews);
    }

    private AssetView generateAsset(String asset, String value) {
        AssetView assetView = AssetView.builder().asset(asset).build();
        Optional<Asset> assetOptional = assetService.getAsset(asset);
        assetOptional.ifPresent(v -> {
            assetView.setName(v.getName());
            assetView.setSymbol(v.getSymbol());
            assetView.setDescription(v.getDescription());
            assetView.setLogo(v.getLogo());
        });
        if (AssetUtil.indivisible(asset)) {
            // indivisible asset
            assetView.setValue(StringUtils.EMPTY);
            AssetView.Indivisible indivisible = AssetView.Indivisible.builder().number(value).description(daoIndivisibleAssetService.getIndivisibleDesc(asset, Long.valueOf(value))).build();
            assetView.setIndivisibleList(Lists.newArrayList(indivisible));
        } else {
            // divisible asset
            assetView.setValue(value);
        }
        return assetView;
    }
}
