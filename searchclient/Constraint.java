package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class Constraint
{
    public int agent1;
    public int agent2;
    public int stateNumber;
    public int typeOfCosntraint;

    /* typeOfConstraint
     * 1. Vertex Conflict 
     * 2. Swap-Edge Conflict
     * 3. Follow Conflict
     * 4. Box is blocking the plan
     */
   
    public Constraint(int agent1, int agent2, int stateNumber, int typeOfCosntraint)
    {
        this.agent1 = agent1;
        this.agent2= agent2;
        this.stateNumber= stateNumber;
        this.typeOfCosntraint = typeOfCosntraint;
    }
       
}
