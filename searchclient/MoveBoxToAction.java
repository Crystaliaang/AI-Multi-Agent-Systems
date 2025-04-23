package searchclient;

import java.util.ArrayList;

public class MoveBoxToAction{

    public final int xCellDest;
    public final int yCellDest;
    public final char boxID;
    private final ActionsGoals goal;

    public MoveBoxToAction(int xCD, int yCD,char bID){
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        this.boxID=bID;
        this.goal= new ActionsGoals(bID,xCD,yCD);
    }

    public MoveBoxToAction(MoveBoxToAction parent){

        this.xCellDest=parent.xCellDest;
        this.yCellDest=parent.yCellDest;
        this.boxID=parent.boxID;
        this.goal= parent.getGoal();

    }
    public ActionsGoals getGoal(){
        return this.goal;
    }
    public void refinement() { //then change from void to Arralist of ACtion
        Action primitiveAction;
        MoveBoxToAction moveBox= new MoveBoxToAction(this);
        
        /*need to create a way to update the state after the primitive action is decided, 
        so the recursive call MoveBoxTo is aware of the updated state after the primitive action*/
        
        /*search from the actual state, which applicable primitive action brings the HLA
        goal closer*/

        /*after finding the primitive action, it's needed to know if we achieve the HLA goal state 
        with this new action. If it's we return just that action, if it's not we do a recursive call
        to refinement*/
    }


}