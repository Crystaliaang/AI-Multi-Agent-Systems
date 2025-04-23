package searchclient;
import java.util.ArrayList;
public class Agent {

    public int id;
    public Color color ;
    /*maybe these 3 arrays will have to be ArrayList
    it depends from the implementation*/
    private ArrayList<Character> boxesAssigned ; //maybe here it will be an array of Box
    private ArrayList<Action> planPrimitive;
    private ArrayList<HighLevelAction> planHLA;
    public int xCellGoal,yCellGoal;
    public int xCellInit,yCellInit;

    public Agent(int id,Color c){ 
        this.id=id;
        this.color=c;
        this.xCellGoal=-1; //if there is no goal, the CellGoal values will be -1
        this.yCellGoal=-1; 

    }
    public void setInitial(int xCI, int yCI){
        this.xCellInit=xCI;
        this.yCellInit=yCI;
    }
    public void setGoal(int xCG,int yCG){
        this.xCellGoal=xCG;
        this.yCellGoal=yCG;
    }
    public ArrayList<Action>  getPlanPrimitive(){
        return this.planPrimitive;
    }
    public ArrayList<HighLevelAction> getPlanHLA(){
        return this.planHLA;
    }


}