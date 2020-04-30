package network.asimov.util;

/**
 * @author zhangjing
 * @date 2020-01-29
 */
public class AssetUtil {
    public static boolean indivisible(String asset) {
        if (asset == null || asset.length() != 24) {
            return false;
        }
        return (Integer.parseInt(asset.substring(0, 8)) & 1) == 1;
    }

    public static String generateAssetId(int assetType, int orgId, int assetIndex) {
        String assetTypeHex = bytesToHex(intToByte(assetType));
        String orgIdHex = bytesToHex(intToByte(orgId));
        String assetIndexHex = bytesToHex(intToByte(assetIndex));
        return assetTypeHex + orgIdHex + assetIndexHex;
    }

    public static byte[] intToByte(int val) {
        byte[] b = new byte[4];
        b[3] = (byte) (val & 0xff);
        b[2] = (byte) ((val >> 8) & 0xff);
        b[1] = (byte) ((val >> 16) & 0xff);
        b[0] = (byte) ((val >> 24) & 0xff);
        return b;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
