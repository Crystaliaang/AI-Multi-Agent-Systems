package searchclient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
public abstract class Heuristic
        implements Comparator<State>
{
    int boxCount = 0;

    //TODO define the goal table
    
    HashMap<Character, ArrayList<Integer>> agentGoalsDict  = new  HashMap<>();
    HashMap<Character, ArrayList<Integer>> boxGoalsDict  = new  HashMap<>();

    public Heuristic(State initialState)
    {
        // Here's a chance to pre-process the static parts of the level.
        // Counting the number of boxes in the state
        
        char[][] boxes = initialState.boxes;

        
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[i].length; j++) {
                if (boxes[i][j] != 0) {
                    boxCount++;  
                }
            }
        }

        //TODO fill the goal table
             for (int row = 0; row < initialState.goals.length; row++) {
              for (int col = 0; col <initialState.goals[row].length; col++) {
                char goal = initialState.goals[row][col];
                //System.out.println(goal);
                if (goal >= 'A' && goal <= 'Z'){
                   ArrayList<Integer> boxPostions = new ArrayList<>();
                    boxPostions.add(row);
                    boxPostions.add(col);
                    boxGoalsDict.put(goal, boxPostions); 
                }
                else if (goal>=48) { //char '0' corresponds to a value of 48
                    ArrayList<Integer> postions = new ArrayList<>();
                    postions.add(row);
                    postions.add(col);
                    agentGoalsDict.put(goal, postions);
                    //System.out.println(goal);
                }
              }
            }

            // To print everything in the dictionary
        for (Character goal : agentGoalsDict.keySet()) {
            ArrayList<Integer> position = agentGoalsDict.get(goal);
            System.out.println("Goal: " + goal + ", Position: (" + position.get(0) + ", " + position.get(1) + ")");
        }
        for (Character goal : boxGoalsDict.keySet()) {
            ArrayList<Integer> position = boxGoalsDict.get(goal);
            System.out.println("Goal: " + goal + ", Position: (" + position.get(0) + ", " + position.get(1) + ")");
        }

        System.out.println("initial H:"+ calculateManhattan(initialState));

    }

    public int goalCount(State s){

        int agent_number = s.agentCols.length;
        int h_value = agent_number + boxCount;

        for(int i = 1; i < agent_number ; i++)
        {
            final int row = s.agentRows[i];
            final int col = s.agentCols[i];

            if (!String.valueOf(s.goals[row][col]).isEmpty() 
                && String.valueOf(s.goals[row][col]).equals(String.valueOf(i)))
            {
                h_value --;
            }
            
        }
        
        //Box count
        for (int i = 0; i < s.goals.length; i++) {
            for (int j = 0; j < s.goals[i].length; j++) {
                if (s.goals[i][j]!=0
                && s.goals[i][j] == s.boxes[i][j])
                {
                    h_value--;
                }
            }
        }

        return h_value;

    }

    public int calculateManhattan(State s){

        int agent_number = s.agentCols.length;
        int h_value = 0;
        //System.out.println(s);
        for(int i = 0; i < agent_number ; i++)
                {
                    ArrayList<Integer> goal_position = agentGoalsDict.get((char)(i+48)); //char '0' corresponds to a value of 48
                    if(goal_position != null){
                        int goal_row =goal_position.get(0);
                        int goal_col = goal_position.get(1);

                        int agent_row = s.agentRows[i];
                        int agent_col = s.agentCols[i];
                        
                        h_value += Math.abs(goal_row-agent_row) + Math.abs(goal_col-agent_col);
                        //System.out.println("INCREMENTED");
                  
                    }
            

            }
        
            //Calculate Manhattan distance for each box
            for (int i = 0; i < s.goals.length; i++) 
            {
                for (int j = 0; j < s.goals[i].length; j++) 
                {
                    if(s.boxes[i][j] !=0)
                    {
                        ArrayList<Integer> goal_position = boxGoalsDict.get(s.boxes[i][j]);
                        
                        if(goal_position != null){
                            int goal_row =goal_position.get(0);
                            int goal_col = goal_position.get(1);

                            int box_row = i;
                            int box_col = j;
                            
                            h_value += Math.abs(goal_row-box_row) + Math.abs(goal_col-box_col);
                            
                        }
                    }
                }
            }
        // System.out.println(h_value);
        return h_value;    
    }

    public int h(State s)
    {

        //Agent count
        int h_value = 0;
        

        int count_h = goalCount(s);
        int manhattan_h = calculateManhattan(s);


        int w1,w2;

       //w1 and w2 are the weighting factors for each heuristic
        w1 = 1;
        w2 = 10;
        //f(n) = w1 * h1(n) + w2 * h2(n)
        h_value = w1*count_h + w2*manhattan_h;

        //System.out.println("h_value:" + h_value);
        //System.out.println(s);

        return h_value;
        
    }

    public int h_HLA(State s)
    {
        int rows = s.goals.length;
        int cols = s.goals[0].length;
        int agent_number =s.agentCols.length;
        int h_value = 0;
        if(s.parent != null){
            for(int i = 0; i<agent_number;i++){
                int agent_row = s.agentRows[i];
                int agent_col = s.agentCols[i];
                Color agentColor = s.agentColors[i];
                char box = s.boxes[agent_row][agent_col];

                // If a box is at the agent's position, add the distance from the box to its goal
                if(box != 0){
                    Color boxColor = s.boxColors[box -'A'];
                    // Box is of the same color and not at its goal
                    if(boxColor==agentColor && s.goals[agent_row][agent_col] != box){
                        //Add distance to closest goal
                        // TODO: optimize closest goal search
                        int min_distance_to_goal=999999;
                        int distance=0;
                        for(int j=0;j<rows;j++){
                            for(int k=0;k<cols;k++){
                                if(s.goals[j][k] == box){
                                    distance = Math.abs(agent_row-j)+Math.abs(agent_col-k);
                                    if(distance<=min_distance_to_goal){
                                        min_distance_to_goal = distance;
                                    }
                                }
                            }
                        }
                        if(min_distance_to_goal!=999999){ // At least one goal found
                            h_value += min_distance_to_goal;
                        }
                    }
                }
                
                // Add distance from agent in parent state to agent in current state
                if(s.parent != null){
                    int parent_row = s.parent.agentRows[i];
                    int parent_col = s.parent.agentCols[i];
                    h_value += Math.abs(parent_row-agent_row) + Math.abs(parent_col-agent_col);
                }

                // Add distance from agent to its goal
                // TODO: optimize search for goal
                for(int j=0;j<rows;j++){
                    for(int k=0;k<cols;k++){
                        if(s.goals[j][k] -'0' == i){
                            h_value += Math.abs(j-agent_row) + Math.abs(k-agent_col);
                            break;
                        }
                    }
                }

            }
        }else{
            h_value=999999;
        }

        return h_value;
    }

    //public int h(State s){return 0;}

    public abstract int f(State s);

    @Override
    public int compare(State s1, State s2)
    {
        return this.f(s1) - this.f(s2);
    }
}

class HeuristicAStar
        extends Heuristic
{
    public HeuristicAStar(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State s)
    {
        return s.g() + this.h(s);
    }

    @Override
    public String toString()
    {
        return "A* evaluation";
    }
}

class HeuristicWeightedAStar
        extends Heuristic
{
    private int w;

    public HeuristicWeightedAStar(State initialState, int w)
    {
        super(initialState);
        this.w = w;
    }

    @Override
    public int f(State s)
    {
        return s.g() + this.w * this.h(s);
    }

    @Override
    public String toString()
    {
        return String.format("WA*(%d) evaluation", this.w);
    }
}

class HeuristicGreedy
        extends Heuristic
{
    public HeuristicGreedy(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State s)
    {
        return this.h(s);
    }

    @Override
    public String toString()
    {
        return "greedy evaluation";
    }
}
