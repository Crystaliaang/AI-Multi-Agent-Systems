package searchclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Collections;


import javax.print.attribute.standard.MediaSize.Other;

public class SearchClient2
{   
    /*To save in objects the whole information provided from the 
    level file is needed to return :
        -array with groupOfColors objects;
            groupOfColors:
                *color
                *agents id's
                *boxes id's
        -array with agents objects;
            agents:
                *id
                *color
                *init loc
                *goal loc
        -array with box objects;
            boxes:
                *id
                *color
                *init loc
                *goal loc
                
    Not like this parseLevel function, our implementation will not
    return the initialState, we can built it later easily with 
    all this information saved in objects*/
    public static Level parseLevel(BufferedReader serverMessages)
    throws IOException
    {
        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colors
        serverMessages.readLine(); // #colors
        Color[] agentColors = new Color[10];
        Color[] boxColors = new Color[26];
        String line = serverMessages.readLine();
        /*save each group of colors
        in a class called groupOfColors. here we save the
        identiyfing Color of the group, the ids of the agents 
        and boxes the ids of the boxes
        that belong to the group */

        Level levelInfo = new Level();

        ArrayList<Agent> agentsArrayList= new ArrayList<>(); 
        ArrayList<Box> boxesArrayList= new ArrayList<>();
        ArrayList<GroupOfColors> groupOfColorsArrayList= new ArrayList<>();

        while (!line.startsWith("#")) 
        {

            Agent agent = null;
            Box box = null;
            String[] split = line.split(":");
            Color color = Color.fromString(split[0].strip());
            ArrayList<Integer> agentsID= new ArrayList<>();
            ArrayList<Character> boxesID= new ArrayList<>();

            GroupOfColors colorGroup = new GroupOfColors(color);

            String[] entities = split[1].split(",");

            for (String entity : entities)
            {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9')
                {
                    agent= new Agent(c-'0',colorGroup.color);   
                    agentsID.add(c-'0'); 
                    agentsArrayList.add(agent);
                    agentColors[c - '0'] = color;
                }
                else if ('A' <= c && c <= 'Z')
                {
                    box= new Box(c,colorGroup.color);
                    boxesID.add(c);
                    boxesArrayList.add(box);
                    //levelInfo.addBox(box);
                    boxColors[c - 'A'] = color;
                }
            }
            colorGroup.setAgentsID(agentsID);
            /*
            System.err.print("boxes id: ");
            for(char id : boxesID) System.err.print(id+" ");
            System.err.println("");*/
            colorGroup.setBoxesID(boxesID);
            groupOfColorsArrayList.add(colorGroup);
            
            line = serverMessages.readLine();
        }
        
        levelInfo.setAgents(agentsArrayList);
        levelInfo.setBoxes(boxesArrayList);
        levelInfo.setGroupOfColors(groupOfColorsArrayList);
        // At this point, the color groups are stored with all the information needed
        // At this point the agents are stored but only with color and id info
        // At this point the boxes are stored but only with color and id info

        // We should keep the way to save the level like this
        // Read initial state
        // line is currently "#initial"
        int numRows = 0;
        int numCols = 0;
        ArrayList<String> levelLines = new ArrayList<>(64);
        line = serverMessages.readLine();
        while (!line.startsWith("#"))
            {
                levelLines.add(line);
                numCols = Math.max(numCols, line.length());
                ++numRows;
                line = serverMessages.readLine();
        }

        // numRows has the number of rows
        // numCols has the number of cols
        // levelLines has the level saved

        int numAgents = 0;
        int[] agentRows = new int[10];
        int[] agentCols = new int[10];
        boolean[][] walls = new boolean[numRows][numCols];
        char[][] boxes = new char[numRows][numCols];
        int[][] agents= new int[numRows][numCols];
        
        for (int row = 0; row < numRows; ++row)
        {
            line = levelLines.get(row);
            for (int col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);
                agents[row][col]=-1;
                if ('0' <= c && c <= '9')
                {
                    int index= levelInfo.findAgentIndex(c-'0');
                    //System.err.println("Index: "+index);
                    if(index==-1) continue; //agent not found
                    levelInfo.agents[index].setInitial(row,col);
                    agentRows[c - '0'] = row;
                    agentCols[c - '0'] = col;
                    ++numAgents;
                    agents[row][col]=c-'0';
                }
                else if ('A' <= c && c <= 'Z')
                {
                    
                    int index= levelInfo.findBoxIndex(c);
                    
                    if(index==-1) continue; //agent not found
                    levelInfo.boxes[index].setInitial(row,col);
                    boxes[row][col] = c;
                }
                else if (c == '+')
                {
                    walls[row][col] = true;
                }
            }
        }
        agentRows = Arrays.copyOf(agentRows, numAgents);
        agentCols = Arrays.copyOf(agentCols, numAgents);
        
        // Read goal state
        // line is currently "#goal"
        char[][] goals = new char[numRows][numCols];
        line = serverMessages.readLine();
        int row = 0;
        while (!line.startsWith("#"))
        {
            for (int col = 0; col < line.length(); ++col)
            {
                char c = line.charAt(col);

                if ('0' <= c && c <= '9' )
                {
                    int index= levelInfo.findAgentIndex(c-'0');
                    if(index==-1) continue; //agent not found
                    levelInfo.agents[index].setGoal(row,col);
                    goals[row][col] = c;
                }
                else if ('A' <= c && c <= 'Z')
                {
                    int index= levelInfo.findBoxIndex(c);
                    if(index==-1) continue; //agent not found
                    levelInfo.boxes[index].setGoal(row,col);
                    goals[row][col] = c;
                }
            }

            ++row;
            line = serverMessages.readLine();
        }

        // End
        // line is currently "#end"

        levelInfo.initialState= new State(agentRows, agentCols, agentColors, walls, boxes, boxColors, goals,agents);
        return levelInfo;
    }
    /* getGoalsFiltered is responsible to :
        -filter goals map for each group of agents (color)
        -returns the filtered goal priorites
        
        Should we create a thread for each agent
    */
   
    public static int[][] simulateGoalPriority(Level levelInfo){
        char[][] goals= levelInfo.initialState.goals;
        int[][] priorityGoals= new int[goals.length][goals[0].length];
        
        /*  Then we need to filter the priority_goals by agent 
        and make the HLA plan for each agent*/
        int numberOfAgents=levelInfo.agents.length;
        int numberOfBoxes=levelInfo.boxes.length;

        int rows= priorityGoals.length;
        int cols=priorityGoals[0].length;

        //every element that is not a goal will be -1
        
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                priorityGoals[i][j]=-1;
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c= goals[i][j];
                //if it's not a goal it's not even worth it the search
                if (c=='\0') continue;
                //search for Agents
                for(int h=0;h<numberOfAgents;h++){
                    if ((c-'0') == levelInfo.agents[h].id) {
                        priorityGoals[i][j]=c-'0'+1;
                        break;
                    }
                }
                //search for Boxes
                for(int h=0;h<numberOfBoxes;h++){
                    if (c == levelInfo.boxes[h].id) {
                        priorityGoals[i][j]=c-'A'+1;
                        break;
                    }
                }
            }
        }

        return priorityGoals;
    }
    /* preProcessBoxAgreement :
        This function will be extremely important to make an agreement between the agents
        about who is reponsible for each box. Our reasoning is that, it's not needed
        to make the agreement about all boxes at the beggining, we can make it 
        when we are making the HLA plan and we can decide just for the highest priority
        boxes in that moment
        -receives the positions of the boxes with highest priority at the moment
        -receives the colors of each box with the same index
        -returns a matrix where the index is in sync with the index of the arrays received
    */
    public static int[][] sortMatrix(int[][] matrix) {
        ArrayList<Integer> values = new ArrayList<Integer>();
        int priority = 1;

        // Get all unique values from matrix
        for (int[] row : matrix) {
            for (int value : row) {
                if (value != -1 && !values.contains(value)) {
                    values.add(value);
                }
            }
        }

        // Sort the values in ascending order
        Collections.sort(values);

        // Replace matrix positions with their priority value
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int value = matrix[i][j];
                if (value != -1) {
                    int priorityValue = values.indexOf(value) + 1;
                    matrix[i][j] = priorityValue;
                }
            }
        }
        return matrix;
    }


    public static ArrayList<int[]> boxGoalList(Level levelInfo){
        //returns a list of all box goals coordinates
        char[][] goals= levelInfo.initialState.goals;
        int rows = goals.length;
        int cols = goals[0].length;
        ArrayList<int[]> goalList= new ArrayList<int[]>();

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(goals[i][j] >='A'){ //Box goal
                    goalList.add(new int[]{i, j});
                }
            }
        }
        return goalList;
            
    }


    public static int[][] getScheduleFiltered(GroupOfColors colorInfo, int[][] goalSchedule, 
    char[][] goals)
    {
        /* algorithm is search each agent/box goal priority, store the values
        at the goals_schedule_filtered (should we standardize the priority values?)
        */
        Integer[] agents=colorInfo.agents; 
        Character[] boxes=colorInfo.boxes;
        int[][] goalScheduleFiltered = new int[goalSchedule.length][goalSchedule[0].length];
        // agents and boxes will be changed for arrays in color class

        
        /*   search for each agent and box id in goals and save the priority which is
        in that position in goal_schedule at goal_schedule filtered.
        */

        int numberOfAgents=agents.length;
        int numberOfBoxes=boxes.length;
        int rows = goals.length;
        int cols = goals[0].length;

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                goalScheduleFiltered[i][j]=-1;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                
                //if it's not a goal it's not even worth it the search
                if (goals[i][j]=='\0') continue;

                //search for Agents
                for(int h=0;h<numberOfAgents;h++){
                    if ((goals[i][j]-'0') == agents[h]) {
                        goalScheduleFiltered[i][j]=goalSchedule[i][j];
                        break;
                    }
                }
                //search for Boxes
                for(int h=0;h<numberOfBoxes;h++){
                    if (goals[i][j] == boxes[h]) {
                        goalScheduleFiltered[i][j]=goalSchedule[i][j];
                        break;
                    }
                }
            }
        }
        
        //store the values
        

        return sortMatrix(goalScheduleFiltered);
    }
    /* search is responsible to :
        1)search HLA action plan for each group of colors
        2)search primitive actions plan for each agent
        3)solve conflicts
        4)returns the overall plan
        
    */
    public static void /*Action[][]*/ search(Level levelInfo)
    {
        //System.err.format("Starting %s.\n", frontier.getName());
        //search for each group of color the HLA plan

        int numberOfColors=levelInfo.colors.length;
        Frontier frontier;        
        frontier = new FrontierBestFirst(new HeuristicAStar(levelInfo.initialState));

        for(int i=0;i<numberOfColors;i++){
            
            GraphSearchHLA.search(levelInfo.initialState,levelInfo.colors[i],frontier);
        }


        //return GraphSearch.search(levelInfo.initialState, new Frontier());
    }
    public static int[][] goalPriority(Level levelInfo){
        char[][] goals= levelInfo.initialState.goals;
        int[][] priorityGoals= new int[goals.length][goals[0].length];

        int rows= priorityGoals.length;
        int cols=priorityGoals[0].length;

        //every element that is not a goal will be -1
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                priorityGoals[i][j]=-1;
            }
        }

        int numberOfAgents=levelInfo.agents.length;
        int numberOfBoxes=levelInfo.boxes.length;


        // Prioritize box goals
        State initialState = levelInfo.initialState;
        ArrayList<int[]> goalsLeft =  boxGoalList(levelInfo);
        while(!goalsLeft.isEmpty()){
            for(int[] goal:goalsLeft){
                State searchState = initialState.copy(); // create a copy of initialState;

                // Filter out all agent goals
                for (Agent agent : levelInfo.agents){
                    if(agent.xCellGoal!=-1){
                        searchState.goals[agent.xCellGoal][agent.yCellGoal]=0;
                    }
                }


                // Filter out all boxes with different type
                char box_index = goals[goal[0]][goal[1]];
                for (Box box : levelInfo.boxes){
                    if(box.id != box_index){
                        for(int i=0;i<box.xCellInit.size();i++){
                            searchState.boxes[box.xCellInit.get(i)][box.xCellInit.get(i)] = 0;
                        }
                    }
                }

                // Turn the other box goals into walls
                for (int[] otherGoal : goalsLeft) {
                    if (otherGoal != goal) {
                        searchState.walls[otherGoal[0]][otherGoal[1]] = true;
                        searchState.goals[otherGoal[0]][otherGoal[1]] = 0;
                    }
                }

                // Try to solve goal
                Action[][] searchAttempt = GraphSearch.search(searchState, null);

               
                if(searchAttempt !=null){
                    // If solution found, goal priority is the length of goalsLeft
                    priorityGoals[goal[0]][goal[1]] = goalsLeft.size();
                    // Remove goal from goalsLeft
                    goalsLeft.remove(goal);

                }
            }
        }


        // Agent goal prioritiziation
        for(GroupOfColors colorgroup:levelInfo.colors){

            // Determine the highest priority of the boxes for the given color
            int agentPriority=0;

            for(Box box :levelInfo.boxes){
                if(colorgroup.color == box.color){
                    for(int i = 0; i<box.xCellGoal.size();i++){
                        if(priorityGoals[box.xCellGoal.get(i)][box.yCellGoal.get(i)]>=agentPriority){
                            agentPriority = priorityGoals[box.xCellGoal.get(i)][box.yCellGoal.get(i)];
                        }
                    }

                }
            }

            // Apply the obtained priority +1 to all the agents with the given color
            for(Agent agent:levelInfo.agents){
                if(colorgroup.color == agent.color){
                    priorityGoals[agent.xCellGoal][agent.yCellGoal]=agentPriority+1;
                }
            }
        }
       
        return priorityGoals;
    }
    public static void main(String[] args)
    throws IOException
    {
        // Use stderr to print to the console.
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Send client name to server.
        System.out.println("SearchClient");

        // We can also print comments to stdout by prefixing with a #.
        System.out.println("#This is a comment.");

        // Parse the level.
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.US_ASCII));
        Level levelInfo = SearchClient2.parseLevel(serverMessages);
        
        levelInfo.infoColors();

        //levelInfo.infoAgents();
        //levelInfo.infoBoxes();
        //levelInfo.infoGoals();
        
        
        int[][] priorityGoals= simulateGoalPriority(levelInfo);
        
        levelInfo.priorityGoals=priorityGoals;

        int rows= priorityGoals.length;
        int cols= priorityGoals[0].length;
        
        /*
        int[][] agents=levelInfo.initialState.agents;

        System.err.println("agents:");
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(agents[i][j]==-1) System.err.print("-");
                else System.err.print(agents[i][j]);
            }
            System.err.println("");
        }
        char[][] goals=levelInfo.initialState.goals;
        System.err.println("goals:");
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(goals[i][j]=='\0') System.err.print("-");
                else System.err.print(goals[i][j]);
            }
            System.err.println("");
        }

        System.err.println("priorityGoals:");
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(priorityGoals[i][j]==-1) System.err.print("-");
                else System.err.print(priorityGoals[i][j]);
            }
            System.err.println("");
        }*/

        GroupOfColors[] colorsInfo=levelInfo.colors ;
        int numberOfColors=colorsInfo.length;
        char[][] goals=levelInfo.initialState.goals;
        for(int i=0;i<numberOfColors;i++){
            
            int[][] filteredGoalSchedule= getScheduleFiltered(colorsInfo[i],priorityGoals,goals);
            
            colorsInfo[i].filteredGoalSchedule=filteredGoalSchedule;

            /* code to print filteredGoalSchedule*/
            
            rows=filteredGoalSchedule.length;
            cols=filteredGoalSchedule[0].length;
            System.err.println(colorsInfo[i].color.toString()+" :");
            for(int h=0;h<rows;h++){
                for(int j=0;j<cols;j++){
                    if(levelInfo.colors[i].filteredGoalSchedule[h][j]==-1) System.err.print("-");
                    else System.err.print(levelInfo.colors[i].filteredGoalSchedule[h][j]+"");
                }
                System.err.println("");
            }
        
        }

        search(levelInfo);


        /*
        // Select search strategy.
        Frontier frontier;        
        frontier = new FrontierBestFirst(new HeuristicAStar(initialState));

        // Search for a plan.
        Action[][] plan;
        try
        {
            plan = SearchClient.search(initialState, frontier);
        }
        catch (OutOfMemoryError ex)
        {
            System.err.println("Maximum memory usage exceeded.");
            plan = null;
        }

        // Print plan to server.
        if (plan == null)
        {
            System.err.println("Unable to solve level.");
            System.exit(0);
        }
        else
        {
            System.err.format("Found solution of length %,d.\n", plan.length);

            for (Action[] jointAction : plan)
            {
                System.out.print(jointAction[0].name);
                for (int action = 1; action < jointAction.length; ++action)
                {
                    System.out.print("|");
                    System.out.print(jointAction[action].name);
                }
                System.out.println();
                // We must read the server's response to not fill up the stdin buffer and block the server.
                serverMessages.readLine();
            }
        }*/
    }
}
