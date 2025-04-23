package searchclient;
import java.util.ArrayList;

public class Box {

    public char id;
    public Color color ;

    //these cells are arrays because the same identifier box can have multiple places
    public int[] possibleAgents;
    public int[] distanceFromAgents;
    public ArrayList<Integer> xCellGoal = new ArrayList<>();
    public ArrayList<Integer> yCellGoal = new ArrayList<>();
    public ArrayList<Integer> xCellInit= new ArrayList<>();
    public ArrayList<Integer> yCellInit = new ArrayList<>();

    public Box(char id,Color c){ 
        this.id=id;
        this.color=c;
    }
    public void setInitial(int xCI, int yCI){
        this.xCellInit.add(xCI);
        this.yCellInit.add(yCI);
    }
    public void setGoal(int xCG,int yCG){
        this.xCellGoal.add(xCG);
        this.yCellGoal.add(yCG);
    }  


}