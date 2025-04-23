package searchclient;

import java.util.ArrayList;

public class SolveGoalAction{

    public final int xCellDest;
    public final int yCellDest;
    public final char boxID;
    private final ActionsGoals goal;


    public SolveGoalAction(char bID,int xCD, int yCD){ //box goal
        this.boxID=bID;
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        this.goal= new ActionsGoals(bID,xCD,yCD);
        //before we define the xCellDest and yCellDest are the box cell goal
    }
    public SolveGoalAction(int xCD, int yCD){ // agent goal
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        this.goal= new ActionsGoals(xCD,yCD);
        this.boxID='-';
        //before we define the xCellDest and yCellDest are the agent goal cell
    }
    public SolveGoalAction(SolveGoalAction parent){
        
        this.xCellDest=parent.xCellDest;
        this.yCellDest=parent.yCellDest;
        this.boxID=parent.boxID; //check if this works well (if parent.boxID= null this.boxID is also null)
        this.goal=parent.getGoal();
    }
    public ActionsGoals getGoal(){
        return this.goal;
    }
    public void refinement(){
        Action primitiveAction;
        SolveGoalAction solveGoal= new SolveGoalAction(this);
        
        /*need to create a way to update the state after the primitive action is decided, 
        so the recursive call GoToCell is aware of the updated state after the primitive action*/

        /*search from the actual state, which applicable primitive action brings the HLA
        goal closer*/

        /*after finding the primitive action, it's needed to know if we achieve the HLA goal state 
        with this new action. If it's we return just that action, if it's not we do a recursive call
        to refinement*/
    }


}