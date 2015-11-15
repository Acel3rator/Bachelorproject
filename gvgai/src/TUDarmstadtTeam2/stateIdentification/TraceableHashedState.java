package TUDarmstadtTeam2.stateIdentification;

import TUDarmstadtTeam2.standardMCTS.TreeNode;

public class TraceableHashedState extends HashedState {
    private TreeNode node;

    private int avatarPosition;

    private int npcs;

    private int movables;

    private int inventory;


    public TraceableHashedState(int stateHash, int npcHash, int movablesHash, int inventoryHash, int avatarStateHash) {
        super(stateHash);
        this.npcs = npcHash;
        this.movables = movablesHash;
        this.inventory = inventoryHash;
    }
    public boolean hasAvatarMoved(TraceableHashedState newState) {
        return newState.getAvatarPosition() != this.avatarPosition;
    }

    public boolean hasMovedAnything(TraceableHashedState newState) {
        return newState.getMovables() != this.movables;
    }

    public boolean hasInventoryChanged(TraceableHashedState newState) {
        return newState.getInventory() != this.inventory;
    }

    public boolean haveNPCsMoved(TraceableHashedState newState) {
        return newState.getNpcs() != this.npcs;
    }

    public int getMovables() {
        return movables;
    }

    public int getNpcs() {
        return npcs;
    }

    public int getAvatarPosition() {
        return avatarPosition;
    }

    public int getInventory() {
        return inventory;
    }
}
