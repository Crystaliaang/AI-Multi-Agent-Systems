package searchclient;
import java.util.ArrayList;

public class GroupOfColors {


    public final Color color;
    public Integer[] agents ; 
    public Character[] boxes;
    public int[][] filteredGoalSchedule;

    public GroupOfColors(Color c){
        this.color=c;
    }

    public void setAgentsID(ArrayList<Integer> agentsID){
        agents = new Integer[agentsID.size()];
        agentsID.toArray(agents);
    }

    public void setBoxesID(ArrayList<Character> boxesID){
        boxes = new Character[boxesID.size()];
        boxesID.toArray(boxes);
    }
    
}