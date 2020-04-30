package network.asimov.mysql.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@AllArgsConstructor
public enum DaoOperationType {
    CreateOrg(1),
    CloseOrg(2),
    ModifyOrgName(3),
    RemoveMember(4),
    IssueAsset(5),
    TransferAsset(6),
    InviteNewPresident(7),
    ConfirmPresident(8),
    InviteNewMember(9),
    JoinNewMember(10),
    Vote(11),
    ModifyOrgLogo(12),
    MintAsset(13);

    @Getter
    private int code;
}
