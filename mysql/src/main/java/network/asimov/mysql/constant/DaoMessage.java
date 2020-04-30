package network.asimov.mysql.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunmengyuan
 * @date 2020-01-15
 */
@AllArgsConstructor
public class DaoMessage {

    public enum State {
        Unread, Read, Disagree, Agree
    }

    @AllArgsConstructor
    public enum Category {
        BeMember(1),
        AddNewMember(2),
        CreateOrg(3),
        BeenRemoved(4),
        RemoveMember(5),
        BePresident(6),
        ChangePresident(7),
        TransferAsset(8),
        IssueAsset(9),
        ModifyOrgLogo(10),
        ModifyOrgName(11),
        NewVote(12),
        ProposalRejected(13),
        ProposalExpired(14),
        Invited(15),
        CloseOrg(16),
        ReceiveAsset(17),
        MintAsset(18);

        @Getter
        private int code;
    }

    public enum Type {
        ReadOnly, DirectAction, Vote
    }

    public enum Position {
        Web, Dao, Both
    }

}
