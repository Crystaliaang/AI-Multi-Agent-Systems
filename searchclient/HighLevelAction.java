package searchclient;

enum HLA_Type{
    NoOp,
    GoToCell,
    MoveBoxTo,
    SolveGoalBox,
    SolveGoalAgent,
    //then we will need to add a HLA action related to ask for help
}


public class HighLevelAction{

    public final String name;
    public final HLA_Type type;
    public final int agent;
    public final int xCellAgent;
    public final int yCellAgent;
    public final char boxID;
    public final int xCellBox; //only needed for SolveGoalBox
    public final int yCellBox; //only needed for SolveGoalBox
    public final int xCellOldBox; //only needed for SolveGoalBox
    public final int yCellOldBox; //only needed for SolveGoalBox

    //each action will not need all these parameters, but the ones they don't need 
    //are null. this way we have a general approach to HLA.

    public HighLevelAction(String name,HLA_Type type,int agent,int xCA, int yCA,char bID,int xCB,int yCB,int xCOB,int yCOB){
        this.name=name;
        this.type=type;
        this.agent=agent;
        this.xCellAgent=xCA;
        this.yCellAgent=yCA;
        this.boxID=bID;
        this.xCellBox=xCB;
        this.yCellBox=yCB;
        this.xCellOldBox=xCOB;
        this.yCellOldBox=yCOB;
    }

    // constructor for GoToCell
    public HighLevelAction(String name,HLA_Type type,int agent,int xCA, int yCA){
        this.name=name;
        this.type=type;
        this.agent=agent;
        this.xCellAgent=xCA;
        this.yCellAgent=yCA;
        this.boxID='\0';
        this.xCellBox=-1;
        this.yCellBox=-1;
        this.xCellOldBox=-1;
        this.yCellOldBox=-1;
    }
    // constructor for NoOp
    public HighLevelAction(String name,HLA_Type type){
        this.name=name;
        this.type=type;
        this.agent=-1;
        this.xCellAgent=-1;
        this.yCellAgent=-1;
        this.boxID='\0';
        this.xCellBox=-1;
        this.yCellBox=-1;
        this.xCellOldBox=-1;
        this.yCellOldBox=-1;
    }

    /*GoToCell:
        -boxID null
        -xCellDest and yCellDest represent the destination cell
    */
    /*MoveBoxTo:
        -boxId represents the box assigned to the action
        -xCellDest and yCellDest represent the destination cell
    */
    /*SolveGoal:
        -boxID null means it is an agent goal
        -boxID not null means it is a box goal and represents the 
        box assigned to the action
        -xCellDest and yCellDest not needed because we can obtain it from the box/agent goal defined
    */
    /*NoOp:
    IDEA:the parameters could mean a condition until when the NoOp should continue
    */
    

}