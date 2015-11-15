package TUDarmstadtTeam2.Search;

import TUDarmstadtTeam2.Search.SearchNode;

import java.util.Comparator;

/**
 * Created by philipp on 14.05.15.
 */
public class NodeComparator implements Comparator<SearchNode> {
    /**
     * decides how ties are broken, this has a significant impact on the performance of the agent,
     * but is not "constant" over all games
     */
    // TODO figure out how to break ties "good" in each game.
    private boolean tieBreak;
    public NodeComparator(boolean tieBreak){
        this.tieBreak = tieBreak;

    }

    @Override
    public int compare(SearchNode o1, SearchNode o2) {
        if(o1.getGameScore() > o2.getGameScore()){
            return -1;
        }
        if(o1.getGameScore() < o2.getGameScore()){
            return 1;
        }
        // Tie breaker, has huge impact.... why ever
        if(tieBreak){
            return 1;
        }
        else{
            return -1;
        }
    }
    //maybe useful
    public void setTieBreak(boolean tieBreak){
        this.tieBreak = tieBreak;
    }
    public boolean getTieBreak(){
        return tieBreak;
    }
    public void invertTieBreak(){
        this.tieBreak = !tieBreak;
    }
}
