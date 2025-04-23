package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class State
{
    private static final Random RNG = new Random(1);

    /*
        The agent rows, columns, and colors are indexed by the agent number.
        For example, this.agentRows[0] is the row location of agent '0'.
    */
    public int[] agentRows;
    public int[] agentCols;
    public static Color[] agentColors;

    //public int[] boxRows;
    //public int[] boxCols;

    /*
        The walls, boxes, and goals arrays are indexed from the top-left of the level, row-major order (row, col).
               Col 0  Col 1  Col 2  Col 3
        Row 0: (0,0)  (0,1)  (0,2)  (0,3)  ...
        Row 1: (1,0)  (1,1)  (1,2)  (1,3)  ...
        Row 2: (2,0)  (2,1)  (2,2)  (2,3)  ...
        ...

        For example, this.walls[2] is an array of booleans for the third row.
        this.walls[row][col] is true if there's a wall at (row, col).

        this.boxes and this.char are two-dimensional arrays of chars. 
        this.boxes[1][2]='A' means there is an A box at (1,2). 
        If there is no box at (1,2), we have this.boxes[1][2]=0 (null character).
        Simiarly for goals. 

    */
    public static boolean[][] walls;
    public char[][] boxes;
    public static char[][] goals;
    public int[][] agents;

    /*
        The box colors are indexed alphabetically. So this.boxColors[0] is the color of A boxes, 
        this.boxColor[1] is the color of B boxes, etc.
    */
    public static Color[] boxColors;
 
    public final State parent;
    public final State parentHLA;
    public final Action[] jointAction;
    public final HighLevelAction[] jointActionHLA;
    private final int g;
    private final int hla;
    private int hash = 0;
    public int [][] goalSchedule;


    // Constructs an initial state.
    // Arguments are not copied, and therefore should not be modified after being passed in.
    public State(int[] agentRows, int[] agentCols, Color[] agentColors, boolean[][] walls,
                 char[][] boxes, Color[] boxColors, char[][] goals, int[][] agents)
    {
        this.agentRows = agentRows;
        this.agentCols = agentCols;
        this.agentColors = agentColors;
        this.walls = walls;
        this.boxes = boxes;
        this.boxColors = boxColors;
        this.goals = goals;
        this.parent = null;
        this.parentHLA=null;
        this.jointAction = null; 
        this.jointActionHLA=null;
        this.g = 0;
        this.hla=0;
        this.agents=agents;
    }

    public State(State other){
        this.agentRows = other.agentRows;
        this.agentCols = other.agentCols;
        this.agentColors = other.agentColors;
        this.walls = other.walls;
        this.boxes = other.boxes;
        this.boxColors = other.boxColors;
        this.goals = other.goals;
        this.parent = null;
        this.parentHLA=null;
        this.jointAction = null;
        this.jointActionHLA=null; 
        this.g = 0;
        this.hla=0;
        this.agents=other.agents;
    }
    // Constructs the state resulting from applying jointAction in parent.
    // Precondition: Joint action must be applicable and non-conflicting in parent state.
    private State(State parent, HighLevelAction[] jointAction){

        // Copy parent
        this.agentRows = Arrays.copyOf(parent.agentRows, parent.agentRows.length);
        this.agentCols = Arrays.copyOf(parent.agentCols, parent.agentCols.length);
        
        this.boxes = new char[parent.boxes.length][];
        for (int i = 0; i < parent.boxes.length; i++)
        {
            this.boxes[i] = Arrays.copyOf(parent.boxes[i], parent.boxes[i].length);
        }

        this.goalSchedule = new int[parent.goalSchedule.length][];
        for (int i = 0; i < parent.goalSchedule.length; i++)
        {
            this.goalSchedule[i] = Arrays.copyOf(parent.goalSchedule[i], parent.goalSchedule[i].length);
        }

        // Set own parameters
        this.hla=parent.hla+1;
        this.parent = null; //this is just while we are in HLA search
        this.parentHLA = parent;
        this.jointActionHLA = Arrays.copyOf(jointAction, jointAction.length);
        this.jointAction=null;
        

        this.g = 0; //this is just while we are in HLA search

        int numberOfAgents=jointAction.length;
        int oldBoxRow,oldBoxCol,newBoxCol,newBoxRow;
        
        for(int agent=0;agent<numberOfAgents;agent++){
            HighLevelAction action=jointAction[agent];

            //switch case for each type of action

            switch (action.type) {  
                case NoOp:
                    // no changes
                    break;
                case SolveGoalAgent:
                    this.agentRows[agent] += action.xCellAgent;
                    this.agentCols[agent] += action.yCellAgent;
                    
                    this.goalSchedule[action.xCellAgent][action.yCellAgent]=-1; //goal solved
                    //then maybe also include matrix agents in the change
                    break;
                case SolveGoalBox:
                    this.agentRows[agent] += action.xCellAgent;
                    this.agentCols[agent] += action.yCellAgent;

                    //then maybe also include matrix agents in the change
                    oldBoxRow = action.xCellOldBox;
                    oldBoxCol = action.yCellOldBox;
                    newBoxRow = action.xCellBox;
                    newBoxCol = action.yCellBox;
                        
                    this.boxes[newBoxRow][newBoxCol] = this.boxes[oldBoxRow][oldBoxCol];
                    this.boxes[oldBoxRow][oldBoxCol] ='\0';

                    this.goalSchedule[action.xCellBox][action.yCellBox]=-1;
                    break;
                case GoToCell:
                    this.agentRows[agent] += action.xCellAgent;
                    this.agentCols[agent] += action.yCellAgent;

                    //then maybe also include matrix agents in the change
                    break;
            }

        }

        


    }
    private State(State parent, Action[] jointAction)
    {
        // Copy parent
        this.agentRows = Arrays.copyOf(parent.agentRows, parent.agentRows.length);
        this.agentCols = Arrays.copyOf(parent.agentCols, parent.agentCols.length);
        
        this.boxes = new char[parent.boxes.length][];
        for (int i = 0; i < parent.boxes.length; i++)
        {
            this.boxes[i] = Arrays.copyOf(parent.boxes[i], parent.boxes[i].length);
        }

        // Set own parameters
        this.hla=0;
        this.parent = parent;
        this.parentHLA = null;
        this.jointActionHLA=null;
        this.jointAction = Arrays.copyOf(jointAction, jointAction.length);
        this.g = parent.g + 1;
        this.goalSchedule=parent.goalSchedule;

        // Apply each action
        int numAgents = this.agentRows.length;
        for (int agent = 0; agent < numAgents; ++agent)
        {
            Action action = jointAction[agent];
            char box;

            int oldBoxRow;
            int oldBoxCol;
            int newBoxRow;
            int newBoxCol;

            
            switch (action.type)
            {
                case NoOp:
                    break;

                case Move:
                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;
                    break;

                case Push:

                    oldBoxRow = this.agentRows[agent] + action.agentRowDelta;
                    oldBoxCol = this.agentCols[agent] + action.agentColDelta;
                    newBoxRow = this.agentRows[agent] + action.agentRowDelta + action.boxRowDelta;
                    newBoxCol = this.agentCols[agent] + action.agentColDelta + action.boxColDelta;
                        
                    this.boxes[newBoxRow][newBoxCol] = this.boxes[oldBoxRow][oldBoxCol];
                    this.boxes[oldBoxRow][oldBoxCol] =0;

                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;

                    //this.boxes[this.agentRows[agent]+action.boxRowDelta][this.agentCols[agent]+action.boxColDelta] = this.boxes[this.agentRows[agent]][this.agentCols[agent]];
                    //this.boxes[this.agentRows[agent]][this.agentCols[agent]] = 0;

                    break;

                case Pull:

                    oldBoxRow = this.agentRows[agent] - action.boxRowDelta;
                    oldBoxCol = this.agentCols[agent] - action.boxColDelta;
                    newBoxRow = this.agentRows[agent];
                    newBoxCol = this.agentCols[agent];

                    this.boxes[newBoxRow][newBoxCol] = this.boxes[oldBoxRow][oldBoxCol];
                    this.boxes[oldBoxRow][oldBoxCol] =0;
                    //System.out.print("BOX MOVE");
                    //System.out.print(this.boxes[oldBoxRow][oldBoxCol]);
                    //System.out.print(this.boxes[newBoxRow][newBoxCol]);
                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;
                                        
                    break;
                 

                   
            }
        }
    }
    

    public State copy() {
        int[] agentRowsCopy = Arrays.copyOf(agentRows, agentRows.length);
        int[] agentColsCopy = Arrays.copyOf(agentCols, agentCols.length);
        Color[] agentColorsCopy = Arrays.copyOf(agentColors, agentColors.length);
        boolean[][] wallsCopy = new boolean[walls.length][walls[0].length];
        for (int i = 0; i < walls.length; i++) {
            wallsCopy[i] = Arrays.copyOf(walls[i], walls[i].length);
        }
        char[][] boxesCopy = new char[boxes.length][boxes[0].length];
        for (int i = 0; i < boxes.length; i++) {
            boxesCopy[i] = Arrays.copyOf(boxes[i], boxes[i].length);
        }
        Color[] boxColorsCopy = Arrays.copyOf(boxColors, boxColors.length);
        char[][] goalsCopy = new char[goals.length][goals[0].length];
        for (int i = 0; i < goals.length; i++) {
            goalsCopy[i] = Arrays.copyOf(goals[i], goals[i].length);
        }
        int[][] agentsCopy = new int[agents.length][agents[0].length];
        for (int i = 0; i < agents.length; i++) {
            agentsCopy[i] = Arrays.copyOf(agents[i], agents[i].length);
        }
        return new State(agentRowsCopy, agentColsCopy, agentColorsCopy, wallsCopy, boxesCopy, boxColorsCopy, goalsCopy, agentsCopy);
    }

    
    public static void updateGoalSchedule(int[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;
    boolean foundOne = false;

    // Check for any 1s in the matrix
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            if (matrix[row][col] == 1) {
                foundOne = true;
                break;
            }
        }
        if (foundOne) {
            break;
        }
    }

    // If no 1s were found, decrement all values greater than 1
    if (!foundOne) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (matrix[row][col] > 1) {
                    matrix[row][col]--;
                }
            }
        }
    }
}

    public State wallifyState(State state){
        // Given a state, returns another state with unmovable boxes turned into walls
        // TODO: also wallify a box when it cannot be accessed by an agent of the same color (eg. if the box and agent are in 2 seperate rooms)

        // Lists all the the unmovable colors
        ArrayList<Color> unmovable_colors = new ArrayList<Color>();
        for (Color color : state.agentColors){
            if(!Arrays.asList(state.boxColors).contains(color)){
                unmovable_colors.add(color);
            }
        }

        // Runs through the state boxes and walls attributes in order to detect unmovable boxes and turn them into walls
        for(int i = 0; i<state.boxes.length; ++i){
            for(int j = 0; i<state.boxes[0].length; ++i){
                char box = state.boxes[i][j];
                if(box!=0){
                    int box_index = box -'A';
                    if(unmovable_colors.contains(state.boxColors[box_index])){
                        state.walls[i][j] = false;
                        state.boxes[i][j] = 0;
                    }
                
                }
            }
        }
        return state;
    }

    public int g()
    {
        return this.g;
    }

    public boolean isGoalState()
    {
        for (int row = 1; row < this.goals.length - 1; row++)
        {
            for (int col = 1; col < this.goals[row].length - 1; col++)
            {
                char goal = this.goals[row][col];

                if ('A' <= goal && goal <= 'Z' && this.boxes[row][col] != goal)
                {
                    return false;
                }
                else if ('0' <= goal && goal <= '9' &&
                         !(this.agentRows[goal - '0'] == row && this.agentCols[goal - '0'] == col))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<State> getExpandedStates()
    {
        int numAgents = this.agentRows.length;

        // Determine list of applicable actions for each individual agent.
        Action[][] applicableActions = new Action[numAgents][];
        for (int agent = 0; agent < numAgents; ++agent)
        {
            ArrayList<Action> agentActions = new ArrayList<>(Action.values().length);
            for (Action action : Action.values())
            {
                if (this.isApplicable(agent, action))
                {
                    agentActions.add(action);
                }
            }
            applicableActions[agent] = agentActions.toArray(new Action[0]);
        }

        // Iterate over joint actions, check conflict and generate child states.
        Action[] jointAction = new Action[numAgents];
        int[] actionsPermutation = new int[numAgents];
        ArrayList<State> expandedStates = new ArrayList<>(16);
        while (true)
        {
            for (int agent = 0; agent < numAgents; ++agent)
            {
                jointAction[agent] = applicableActions[agent][actionsPermutation[agent]];
            }

            if (!this.isConflicting(jointAction))
            {
                expandedStates.add(new State(this, jointAction));
            }

            // Advance permutation
            boolean done = false;
            for (int agent = 0; agent < numAgents; ++agent)
            {
                if (actionsPermutation[agent] < applicableActions[agent].length - 1)
                {
                    ++actionsPermutation[agent];
                    break;
                }
                else
                {
                    actionsPermutation[agent] = 0;
                    if (agent == numAgents - 1)
                    {
                        done = true;
                    }
                }
            }

            // Last permutation?
            if (done)
            {
                break;
            }
        }

        Collections.shuffle(expandedStates, State.RNG);
        return expandedStates;
    }

    public ArrayList<State> getExpandedStatesHLA(GroupOfColors colorInfo)
    {

        int[][] actualGoalSchedule= Arrays.copyOf(this.goalSchedule, this.goalSchedule.length);
        int rows=this.goals.length;
        int cols=this.goals[0].length;
        ArrayList<Character> goalsIDArrayList = new ArrayList<>();
        ArrayList<Integer> goalsRowArrayList = new ArrayList<>();
        ArrayList<Integer> goalsColArrayList = new ArrayList<>();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(actualGoalSchedule[i][j]==1){
                    goalsIDArrayList.add(this.goals[i][j]);
                    goalsRowArrayList.add(i);
                    goalsColArrayList.add(j);
                }
            }
        }
        Character[] goalsID= new Character[goalsIDArrayList.size()];
        Integer[] goalsRow= new Integer[goalsRowArrayList.size()];
        Integer[] goalsCol= new Integer[goalsColArrayList.size()];

        goalsIDArrayList.toArray(goalsID);
        goalsRowArrayList.toArray(goalsRow);
        goalsColArrayList.toArray(goalsCol);

        //function that returns SolveGoal actions for each agent allowed

        HighLevelAction[][] applicableSolveGoal=this.isApplicableSolveGoalActions(colorInfo.agents,goalsID,goalsRow,goalsCol);
        // the SolveGoal actions are stored by agent index
        ArrayList<Integer> agentsAllowedGoToCellArrayList= new ArrayList<>();
        for(int id : colorInfo.agents){
            if(applicableSolveGoal[id].length==0) agentsAllowedGoToCellArrayList.add(id); 
        }
        //agents that can SolveGoals will not be allowed to GoToCell

        Integer[] agentsAllowedGoToCell= new Integer[agentsAllowedGoToCellArrayList.size()];
        agentsAllowedGoToCellArrayList.toArray(agentsAllowedGoToCell);
        //System.err.println("agentsAllowed size: "+agentsAllowedGoToCell.length);
        HighLevelAction[][] applicableGoToCell=this.isApplicableGoToCellActions(agentsAllowedGoToCell,goalsID,colorInfo);

        //applicableGoToCell and applicableSolveGoal have the actions stored by agent allowed
        /*
        System.err.println("ApplicableGoToCell:");
        for(int i=0;i<applicableGoToCell.length;i++){
            System.err.print("Agent "+i);
            for(int j=0;j<applicableGoToCell[0].length;j++){
                if( applicableGoToCell[i][j]!=null) System.err.print(" "+applicableGoToCell[i][j].name+" ");
            }
            System.err.println("");
        }*/
        ArrayList<HighLevelAction> agentApplicableActions= new ArrayList<>();

        int numAgents=colorInfo.agents.length;
        int solveGoalSize=applicableSolveGoal[0].length;
        int goToCellSize= applicableGoToCell[0].length;
        int totalNumAgents=this.agentRows.length;
        HighLevelAction[][] applicableHLA= new HighLevelAction[totalNumAgents][solveGoalSize+goToCellSize+1];
        int[] numberOfActionsAgent= new int[totalNumAgents];
        int indexActionAgent;

        for(int i=0;i<totalNumAgents;i++){
            indexActionAgent=0;
            for(int j=0;j<applicableGoToCell[0].length;j++){
                if( applicableGoToCell[i][j]==null) break;
                applicableHLA[i][indexActionAgent++]=applicableGoToCell[i][j];
            }
            numberOfActionsAgent[i]=indexActionAgent;
        }

        //add NoOp action to all agents
        for(int i=0;i<totalNumAgents;i++){
            String name ="No Action";
            HighLevelAction action = new HighLevelAction(name, HLA_Type.NoOp);
            applicableHLA[i][numberOfActionsAgent[i]++]=action;
        }

        
        for(int i=0;i<applicableHLA.length;i++){
            System.err.print("HLA Agent "+i);
            for(int j=0;j<applicableHLA[0].length;j++){
                if( applicableHLA[i][j]!=null) System.err.print(" "+applicableHLA[i][j].name);
            }
            System.err.println("");
        }

        //now we have all the HLA allowed stored by agents in applicableHLA
        //we need to create the possible states using permutation

        HighLevelAction[] jointHLA = new HighLevelAction[totalNumAgents];
        int[] actionsPermutation = new int[totalNumAgents];
        ArrayList<State> expandedStatesHLA = new ArrayList<>();

        System.err.println("Possible Actions color "+colorInfo.color.toString());        
        while (true)
        {
            for (int agent = 0; agent < totalNumAgents; ++agent)
            {
                jointHLA[agent] = applicableHLA[agent][actionsPermutation[agent]];
                
            }
            
            if (!this.isConflictingHLA(jointHLA))
            {
                System.err.println("");
                System.err.print("jointHLA:");
                for(int i=0;i<totalNumAgents;i++){
                    System.err.print(" "+jointHLA[i].name+"|");
                    
                }
                System.err.println("");
                
                //same HLA action can take to 4 different states
                //create function that returns the real possible actions

                expandedStatesHLA.add(new State(this, jointHLA));

            }

            // Advance permutation
            boolean done = false;
            for (int agent = 0; agent < totalNumAgents; ++agent)
            {
                if (actionsPermutation[agent] < numberOfActionsAgent[agent] - 1)
                {
                    ++actionsPermutation[agent];
                    break;
                }
                else
                {
                    actionsPermutation[agent] = 0;
                    if (agent == totalNumAgents - 1)
                    {
                        done = true;
                    }
                }
            }

            // Last permutation?
            if (done)
            {
                break;
            }
        }

        System.err.println("Number of Expanded States: "+expandedStatesHLA.size());
        return null;
    }
    private HighLevelAction[][] isApplicableGoToCellActions(Integer[] agentsID,Character[] goalsID, GroupOfColors colorInfo){
        //only GoToCell actions to the neighbourhood of boxes with priority id will be allowed

        int rows=this.boxes.length;
        int cols=this.boxes[0].length;
        ArrayList<HighLevelAction> applicableGoToCell=new ArrayList<>();

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                for(int h=0;h<goalsID.length;h++){
                    //System.err.println(this.boxes[i][j]+"|"+goalsID[h]);
                    if(this.boxes[i][j]!=goalsID[h]) continue;
                    if(this.boxHasNoAgentAsNeighbour(i,j,colorInfo.agents)){ //change function to check if has agents of this colour
                        //all agents can GoToCell neighbour of the box
                        // we need to find which neighbours of the box are free
                        int[][] array= {{1,0},{0,1},{-1,0},{0,-1}};
                        for(int n=0;n<4;n++){

                            if(!this.cellIsFree(i+array[n][0],j+array[n][1])) continue;
                            
                            int pos1=i+array[n][0];
                            int pos2=j+array[n][1];
                            for(int k=0;k<agentsID.length;k++){
                                String name= "GoToCell "+pos1+" "+pos2;
                                HighLevelAction action = new HighLevelAction(name, HLA_Type.GoToCell,agentsID[k] ,i+array[n][0],j+array[n][1]);
                                // later the actions will be stored by agent

                                applicableGoToCell.add(action);
                            }
                        }
                            
                    }
                }
            }
        }

        HighLevelAction[][] agentsGoToCell = new HighLevelAction[this.agentRows.length][applicableGoToCell.size()];
        int[] numberOfHLA= new int[this.agentRows.length];
        int agent;
        for(HighLevelAction action:applicableGoToCell){
            agent=action.agent;
            agentsGoToCell[agent][numberOfHLA[agent]]=action;
            numberOfHLA[agent]+=1;
        }
        
        return agentsGoToCell;

    }
    private HighLevelAction[][] isApplicableSolveGoalActions(Integer[] agentsID,Character[] goalsID,Integer[] goalsRow,Integer[] goalsCol){

        int numberOfAgents=agentsID.length;
        int agentRow,agentCol;
        ArrayList<HighLevelAction> applicableSolveGoal=new ArrayList<>();
        int[][] agentBoxNeighbours;
        for(int i=0;i<numberOfAgents;i++){
            agentRow=this.agentRows[agentsID[i]];
            agentCol=this.agentCols[agentsID[i]];
            for(int j=0;j<goalsID.length;j++){
                agentBoxNeighbours=this.getBoxAgentNeighboursAndNotAtGoal(agentRow,agentCol,goalsID[j]);
                if(agentBoxNeighbours.length!=0){
                    //SolveGoal is applicable
                    // need go get all possible boxes of that boxID 
                    String name= "SolveGoalBox "+goalsRow[j]+""+goalsCol[j];
                    for(int h=0;i<agentBoxNeighbours.length;h++){
                        HighLevelAction action = new HighLevelAction(name, HLA_Type.SolveGoalBox,agentsID[i] ,goalsRow[i], goalsCol[i],
                        goalsID[i],goalsRow[i], goalsCol[i], agentBoxNeighbours[h][0],agentBoxNeighbours[h][1]);
                        // later the actions will be stored by agent

                        applicableSolveGoal.add(action);
                    }
                    
                    
                }
            }
        }
        HighLevelAction[][] agentsSolveGoal = new HighLevelAction[this.agentRows.length][applicableSolveGoal.size()];
        int[] numberOfHLA= new int[this.agentRows.length];
        int agent;
        for(HighLevelAction action:applicableSolveGoal){
            agent=action.agent;
            agentsSolveGoal[agent][numberOfHLA[agent]]=action;
            numberOfHLA[agent]+=1;
        }

        return agentsSolveGoal;

    }
    private boolean boxHasNoAgentAsNeighbour(int xCell,int yCell, Integer[] agentsColor){
        int[][] array= {{1,0},{0,1},{-1,0},{0,-1}};
        
        for(int i=0;i<4;i++){
            for(int j=0;j<agentsColor.length;j++){ //only matters if it has agents of this group of color
                if(this.agents[xCell+array[i][0]][yCell+array[i][1]]==agentsColor[j]) return false;
            }
            
        }
        return true;
    }
    private int[][] getBoxAgentNeighboursAndNotAtGoal(int xCell, int yCell, char boxID) {
    int[][] array = {{1,0},{0,1},{-1,0},{0,-1}};
    int count = 0;
    int[][] positions = new int[4][2]; // new matrix to store positions
    
    for (int i = 0; i < 4; i++) {
        int x = xCell + array[i][0];
        int y = yCell + array[i][1];
        
        if (this.boxes[x][y] == boxID && this.goals[x][y] != boxID) {
            positions[count][0] = x; // save x position
            positions[count][1] = y; // save y position
            count++; // increment count of positions found
        }
    }
    
    int[][] result = new int[count][2]; // create new matrix with size of found positions
    for (int i = 0; i < count; i++) {
        result[i][0] = positions[i][0];
        result[i][1] = positions[i][1];
    }
    return result;
}

    private boolean isApplicable(int agent, Action action)
    {
        int agentRow = this.agentRows[agent];
        int agentCol = this.agentCols[agent];
        Color agentColor = this.agentColors[agent];
        int boxRow;
        int boxCol;
        char box;
        int destinationRow;
        int destinationCol;
        int boxDestinationRow;
        int boxDestinationCol;
        int box_index;
        switch (action.type)
        {
            case NoOp:
                return true;

            case Move:
                destinationRow = agentRow + action.agentRowDelta;
                destinationCol = agentCol + action.agentColDelta;
                return this.cellIsFree(destinationRow, destinationCol);

            case Push:
                destinationRow = agentRow + action.agentRowDelta;
                destinationCol = agentCol + action.agentColDelta;

                boxDestinationRow = destinationRow + action.boxRowDelta;
                boxDestinationCol = destinationCol + action.boxColDelta;

                box = this.boxes[destinationRow][destinationCol];

                if(box!=0){box_index = box -'A';}
                else{return false;}


                if(this.boxColors[box_index] == agentColor 
                && this.cellIsFree(boxDestinationRow, boxDestinationCol))
                {
                    return true;
                }


            case Pull:
                destinationRow = agentRow + action.agentRowDelta;
                destinationCol = agentCol + action.agentColDelta;

                boxDestinationRow =agentRow;
                boxDestinationCol =agentCol;
                //boxDestinationRow = agentRow - action.agentRowDelta + action.boxRowDelta;
                //boxDestinationCol = agentCol - action.agentColDelta + action.boxColDelta;

                box = this.boxes[boxDestinationRow- action.boxRowDelta][boxDestinationCol- action.boxColDelta];
                if(box!=0){box_index = box -'A';}
                else{return false;}


                if(this.boxColors[box_index] == agentColor 
                && this.cellIsFree(destinationRow, destinationCol))
                {
                    return true;
                }
            }

        // Unreachable:
        return false;
    }
    private boolean isConflictingHLA(HighLevelAction[] jointAction){
        //we can assume there is conflict if different agents have the same action

        for (int i = 0; i < jointAction.length; i++) {
            for (int j = i + 1; j < jointAction.length; j++) {
                //add condition to find conflicts for SolveGoalBox (name is not enough because there
                // is goal destination and box location 
                // Same box can't be solved for two different agents and same goal cell can't be solved
                // for two different agents)
                if(jointAction[i]!=null && jointAction[j]!=null)
                    if (jointAction[i].name.equals(jointAction[j].name)
                    && !jointAction[i].name.equals("No Action")) {
                        return true; // found a duplicate action
                    }
                /*when two agents can solve different goal cells with the same box*/
                if (jointAction[i].type==HLA_Type.SolveGoalBox 
                    && jointAction[j].type==HLA_Type.SolveGoalBox 
                    && jointAction[i].xCellOldBox==jointAction[j].yCellOldBox
                    && jointAction[i].yCellOldBox==jointAction[j].yCellOldBox) {
                        return true; // found a duplicate action
                    }
            }
        }
        return false; // no duplicate strings found
    }
    private boolean isConflicting(Action[] jointAction)
    {
        int numAgents = this.agentRows.length;

        int[] destinationRows = new int[numAgents]; // row of new cell to become occupied by action
        int[] destinationCols = new int[numAgents]; // column of new cell to become occupied by action
        int[] boxRows = new int[numAgents]; // current row of box moved by action
        int[] boxCols = new int[numAgents]; // current column of box moved by action

        // Collect cells to be occupied and boxes to be moved
        for (int agent = 0; agent < numAgents; ++agent)
        {
            Action action = jointAction[agent];
            int agentRow = this.agentRows[agent];
            int agentCol = this.agentCols[agent];
            int boxRow;
            int boxCol;

            switch (action.type)
            {
                case NoOp:
                    break;

                case Move:
                    destinationRows[agent] = agentRow + action.agentRowDelta;
                    destinationCols[agent] = agentCol + action.agentColDelta;
                    boxRows[agent] = agentRow; // Distinct dummy value
                    boxCols[agent] = agentCol; // Distinct dummy value
                    break;

                case Push:
                    destinationRows[agent] = agentRow + action.agentRowDelta;
                    destinationCols[agent] = agentCol + action.agentColDelta;
                    boxRows[agent] = agentRow + action.agentRowDelta; 
                    boxCols[agent] = agentCol + action.agentColDelta;

                case Pull:
                    destinationRows[agent] = agentRow + action.agentRowDelta;
                    destinationCols[agent] = agentCol + action.agentColDelta;
                    boxRows[agent] = agentRow - action.agentRowDelta;
                    boxCols[agent] = agentCol - action.agentColDelta;
           }
        }

        for (int a1 = 0; a1 < numAgents; ++a1)
        {
            if (jointAction[a1] == Action.NoOp)
            {
                continue;
            }

            for (int a2 = a1 + 1; a2 < numAgents; ++a2)
            {
                if (jointAction[a2] == Action.NoOp)
                {
                    continue;
                }

                // Moving into same cell?
                if (destinationRows[a1] == destinationRows[a2] && destinationCols[a1] == destinationCols[a2])
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean cellIsFree(int row, int col)
    {
        return !this.walls[row][col] && this.boxes[row][col] == 0 && this.agentAt(row, col) == 0;
    }

    private char agentAt(int row, int col)
    {
        for (int i = 0; i < this.agentRows.length; i++)
        {
            if (this.agentRows[i] == row && this.agentCols[i] == col)
            {
                return (char) ('0' + i);
            }
        }
        return 0;
    }

    public Action[][] extractPlan()
    {
        Action[][] plan = new Action[this.g][];
        State state = this;
        while (state.jointAction != null)
        {
            plan[state.g - 1] = state.jointAction;
            state = state.parent;
        }
        //System.out.println("----------------------"+Arrays.deepToString(plan));
        return plan;
    }

    @Override
    public int hashCode()
    {
        if (this.hash == 0)
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.agentColors);
            result = prime * result + Arrays.hashCode(this.boxColors);
            result = prime * result + Arrays.deepHashCode(this.walls);
            result = prime * result + Arrays.deepHashCode(this.goals);
            result = prime * result + Arrays.hashCode(this.agentRows);
            result = prime * result + Arrays.hashCode(this.agentCols);
            for (int row = 0; row < this.boxes.length; ++row)
            {
                for (int col = 0; col < this.boxes[row].length; ++col)
                {
                    char c = this.boxes[row][col];
                    if (c != 0)
                    {
                        result = prime * result + (row * this.boxes[row].length + col) * c;
                    }
                }
            }
            this.hash = result;
        }
        return this.hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        State other = (State) obj;
        return Arrays.equals(this.agentRows, other.agentRows) &&
               Arrays.equals(this.agentCols, other.agentCols) &&
               Arrays.equals(this.agentColors, other.agentColors) &&
               Arrays.deepEquals(this.walls, other.walls) &&
               Arrays.deepEquals(this.boxes, other.boxes) &&
               Arrays.equals(this.boxColors, other.boxColors) &&
               Arrays.deepEquals(this.goals, other.goals);
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < this.walls.length; row++)
        {
            for (int col = 0; col < this.walls[row].length; col++)
            {
                if (this.boxes[row][col] > 0)
                {
                    s.append(this.boxes[row][col]);
                }
                else if (this.walls[row][col])
                {
                    s.append("+");
                }
                else if (this.agentAt(row, col) != 0)
                {
                    s.append(this.agentAt(row, col));
                }
                else
                {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }
}
