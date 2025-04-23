 package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GraphSearchHLA {

    public static State[][] search(State initialState,GroupOfColors colorInfo,
     Frontier frontier)
    {
        boolean failedToFindSolution = true;
        
        int[][] goalSchedule=colorInfo.filteredGoalSchedule.clone();
        // During the search we will update goalSchedule and the states added to the frontier
        // will respect that goalSchedule  
        
        State initialStateColor=initialState.copy();

        initialStateColor.goalSchedule=goalSchedule;
        //Part 2:
        //Now try to implement the Graph-Search algorithm from R&N figure 3.7
        //In the case of "failure to find a solution" you should return null.
        //Some useful methods on the state class which you will need to use are:
        //state.isGoalState() - Returns true if the state is a goal state.
        //state.extractPlan() - Returns the Array of actions used to reach this state.
        //state.getExpandedStates() - Returns an ArrayList<State> containing the states reachable from the current state.
        //You should also take a look at Frontier.java to see which methods the Frontier interface exposes
        //
        //printSearchStates(expanded, frontier): As you can see below, the code will print out status 
        //(#expanded states, size of the frontier, #generated states, total time used) for every 10000th node generated.
        //You should also make sure to print out these stats when a solution has been found, so you can keep 
        //track of the exact total number of states generated!!

            
        int iterations = 0;
        Action[][] resultPlan= new Action[][]{};
        frontier.add(initialState);
        
        HashSet<State> expanded = new HashSet<>();
        
        initialStateColor.getExpandedStatesHLA(colorInfo);

        /*
        while(!frontier.isEmpty()) {
            
            //Print a status message every 10000 iteration
            if (++iterations % 10000 == 0) {
                printSearchStatus(expanded, frontier);
            }

            //Your code here... Don't forget to print out the stats when a solution has been found (see above)
            
            State s = frontier.pop();            // State s = frontier.pop();
            if (s.isGoalState()) {            //
                //System.out.println("Found solution");
                failedToFindSolution= false;
                resultPlan= s.extractPlan();
                //return resultPlan;
                return null;
            } 
            expanded.add(s); //
            for (State t : s.getExpandedStatesHLA(colorInfo)) {  //
                if (!frontier.contains(t) && !expanded.contains(t)) 
                {
                    frontier.add(t); //
                }
            }
        } */
        
        return null;
    
       
    }

    private static long startTime = System.nanoTime();

    private static void printSearchStatus(HashSet<State> expanded, Frontier frontier)
    {
        String statusTemplate = "#Expanded: %,8d, #Frontier: %,8d, #Generated: %,8d, Time: %3.3f s\n%s\n";
        double elapsedTime = (System.nanoTime() - startTime) / 1_000_000_000d;
        System.err.format(statusTemplate, expanded.size(), frontier.size(), expanded.size() + frontier.size(),
                          elapsedTime, Memory.stringRep());
    }
}
