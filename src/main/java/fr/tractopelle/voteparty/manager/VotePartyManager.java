package fr.tractopelle.voteparty.manager;

public class VotePartyManager {

    private int votePartyCurrent;
    private int votePartyMax;


    public VotePartyManager(int votePartyCurrent, int votePartyMax) {
        this.votePartyCurrent = votePartyCurrent;
        this.votePartyMax = votePartyMax;
    }

    public int getVotePartyCurrent() {
        return votePartyCurrent;
    }

    public void setVotePartyCurrent(int votePartyCurrent) {
        this.votePartyCurrent = votePartyCurrent;
    }

    public int getVotePartyMax() {
        return votePartyMax;
    }

    public void setVotePartyMax(int votePartyMax) {
        this.votePartyMax = votePartyMax;
    }
}
