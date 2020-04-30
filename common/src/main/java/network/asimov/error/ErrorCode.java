package network.asimov.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangjing
 * @date 2019-03-02
 */
@AllArgsConstructor
public enum ErrorCode {
    /**
     * 　Server Internal Error
     */
    UNKNOWN_ERROR(1001, "system error"),

    /**
     * Parameter Validation Failed
     */
    PARAMETER_INVALID(1002, "invalid arguments : %s"),

    /**
     * Data Abnormal
     */
    DATA_ERROR(1004, "data error"),

    /**
     * Action Failed
     */
    ACTION_FAILED(1005, "action failed"),

    /**
     * Missing Token
     */
    MISSING_TOKEN(1006, "missing token"),

    /**
     * Repeat Operation
     */
    REPEAT_OPERATION_ERROR(1007, "repeat operation"),

    /**
     * Permission Deny
     */
    PERMISSION_DENIED_ERROR(1008, "permission denied"),

    /**
     * Data Doesn't Exist
     */
    DATA_NOT_EXISTS(1009, "data not exists"),

    /**
     * Third Party Request Failed To Return Results
     */
    THIRD_PARTY_RESPONSE_ERROR(2300, "third party response error"),

    /**
     * Third Party Request Failed
     */
    THIRD_PARTY_REQUEST_FAILED(2301, "third party request failed"),

    /**
     * Proposal Expired
     */
    PROPOSAL_EXPIRED(2402, "proposal has been expired"),

    /**
     * Proposal Status Incorrect
     */
    INVALID_PROPOSAL_STATUS(2403, "invalid proposal status"),

    /**
     * Member status Incorrect
     */
    MEMBER_STATUS_ERROR(2404, "member status error"),

    /**
     * Handling Fee Exceeds Allowable Value
     */
    ASSET_EXCEED_MAXIMUM(2405, "number of assets exceeds the maximum"),

    /**
     * Insufficient Member Number
     */
    NOT_ENOUGH_MEMBER(2411, "not enough member"),


    /**
     * Insufficient Number Of Blocks
     */
    NOT_ENOUGH_PRODUCED(2413, "not enough produced"),

    /**
     * Insufficient Foundation Balance
     */
    NOT_ENOUGH_AMOUNT(2414, "not enough amount"),

    /**
     * Illegal Picture Size
     */
    INVALID_PIC_SIZE(2500, "invalid picture size"),

    /**
     * Picture Upload Failed
     */
    UPLOAD_PHOTO_ERROR(2501, "upload photo error"),

    /**
     * Duplicate Dao Organization name
     */
    REPEAT_ORGANIZATION_NAME(2502, "repeat organization name"),

    /**
     * Reading Message Error
     */
    READ_MESSAGE_ERROR(2503, "read message error"),

    /**
     * Duplicate Indivisible Asset ID
     */
    VOUCHER_ID_REPEAT_ERROR(2504, "voucher id repeat error"),

    /**
     * Organization Doesn't Exist
     */
    ORGANIZATION_NOT_EXISTS_ERROR(2600, "organization not exists"),

    /**
     * Organization Has Been Closed
     */
    ORGANIZATION_CLOSED_ERROR(2601, "organization has been closed");

    @Getter
    private int code;
    @Getter
    private String msg;
}
