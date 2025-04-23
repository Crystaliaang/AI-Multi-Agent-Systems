package searchclient;

import java.util.ArrayList;

public class ActionsGoals{

    public final int xCellDest;
    public final int yCellDest;
    public final char boxID;

    public ActionsGoals(char bID,int xCD, int yCD){ //box goal
        this.boxID=bID;
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        //before we define the xCellDest and yCellDest are the box cell goal
    }
    public ActionsGoals(int xCD, int yCD){ // agent goal
        this.boxID='-';
        this.xCellDest=xCD;
        this.yCellDest=yCD;
        //before we define the xCellDest and yCellDest are the agent goal cell
    }

}