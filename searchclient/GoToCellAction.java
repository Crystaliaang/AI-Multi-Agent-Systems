package searchclient;

import java.util.ArrayList;

public class GoToCellAction{

    public final int xCellDest;
    public final int yCellDest;
    private final ActionsGoals goal;

    public GoToCellAction(int xCD, int yCD){
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        this.goal= new ActionsGoals(xCD,yCD);
    }

    public GoToCellAction(GoToCellAction parent){

        this.xCellDest=parent.xCellDest;
        this.yCellDest=parent.yCellDest;
        this.goal=parent.getGoal();

    }
    public ActionsGoals getGoal(){
        return this.goal;
    }
    public void refinement(){ //then change from void to array of Actions
        Action primitiveAction;
        GoToCellAction goToCell= new GoToCellAction(this);

        /*need to create a way to update the state after the primitive action is decided, 
        so the recursive call GoToCell is aware of the updated state after the primitive action*/

        /*search from the actual state, which applicable primitive action brings the HLA
        goal closer*/

        /*after finding the primitive action, it's needed to know if we achieve the HLA goal state 
        with this new action. If it's we return just that action, if it's not we do a recursive call
        to refinement*/
    }


}