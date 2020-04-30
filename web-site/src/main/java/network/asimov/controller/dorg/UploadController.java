package network.asimov.controller.dorg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import network.asimov.chainrpc.service.OssService;
import network.asimov.error.ErrorCode;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.dorg.UploadView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author zhangjing
 * @date 2019-11-08
 */
@CrossOrigin
@RestController("daoUploadController")
@Api(tags = "dao")
@RequestMapping(path = "/dao/upload", produces = RequestConstants.CONTENT_TYPE_JSON)
public class UploadController {
    private static final long PHOTO_SIZE_LOW = 128L;
    private static final long PHOTO_SIZE_HIGH = 2L * 1024 * 1024;

    @Resource(name = "daoOssService")
    private OssService ossService;

    @ApiOperation(value = "Upload photo")
    @PostMapping("/photo")
    public ResultView<UploadView> uploadPhoto(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                              @RequestParam("pic") @ApiParam("File") MultipartFile pic) {
        long size = pic.getSize();
        if (size < PHOTO_SIZE_LOW || size > PHOTO_SIZE_HIGH) {
            return ResultView.error(ErrorCode.INVALID_PIC_SIZE);
        }

        String path;
        try {
            // upload photo
            String hash = ossService.uploadPicture(pic.getInputStream(), "daoTest");
            if (StringUtils.isEmpty(hash)) {
                return ResultView.error(ErrorCode.UPLOAD_PHOTO_ERROR);
            }
            path = ossService.getFullPathUrl("daoTest", hash);
        } catch (IOException e) {
            return ResultView.error(ErrorCode.UPLOAD_PHOTO_ERROR);
        }

        return ResultView.ok(UploadView.builder().resourceUrl(path).build());
    }
}
