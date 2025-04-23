package searchclient;
import java.util.ArrayList;

public class Level {


    public GroupOfColors[] colors ;
    public Agent[] agents ;
    public  Box[] boxes ;
    public int [][] priorityGoals;
    public State initialState;
    

    //no constructor

    public void setGroupOfColors(ArrayList<GroupOfColors> colorsArrayList){
        colors = new GroupOfColors[colorsArrayList.size()];
        colorsArrayList.toArray(colors);
    }
    public void setAgents(ArrayList<Agent> agentsArrayList){
        agents = new Agent[agentsArrayList.size()];
        agentsArrayList.toArray(agents);
    }
    public void setBoxes(ArrayList<Box> boxesArrayList){
        boxes = new Box[boxesArrayList.size()];
        boxesArrayList.toArray(boxes);
    }

    public int findAgentIndex(int agentId){

        int numberOfAgents=this.agents.length;

        for (int i=0;i<numberOfAgents;i++){
            Agent agent=this.agents[i];
            if(agent.id==agentId) return i;
        }
        return -1; //not found
    }
    
    public int findBoxIndex(char boxId){

        int numberOfBoxes=this.boxes.length;

        for (int i=0;i<numberOfBoxes;i++){
            Box box=this.boxes[i];
            if(box.id==boxId) return i;
        }
        return -1; //not found
    }
    public Integer[] getAgentsColor(Color c){
        int numberOfColors=this.colors.length;
        for(int i=0;i<numberOfColors;i++){
            if(this.colors[i].color==c) return this.colors[i].agents;
        }
        //color not found
        return null;
    }
    public void infoBoxes(){
        System.err.println("\n------Box Info-----------");
        int numberOfBoxes=this.boxes.length;

        for(int i=0;i<numberOfBoxes;i++){
            Box box= this.boxes[i];
            System.err.println("id:"+box.id);
            System.err.println("color:"+box.color.toString());
            System.err.println("init:"+box.xCellInit+" "+box.yCellInit);
            System.err.println("goal:"+box.xCellGoal+" "+box.yCellGoal);
            System.err.println("#######################");
        }
    }
    public void infoAgents(){
        System.err.println("\n------Agent Info-----------");
        int numberOfAgents=this.agents.length;

        for(int i=0;i<numberOfAgents;i++){
            Agent agent= this.agents[i];
            System.err.println("id:"+agent.id);
            System.err.println("init:"+agent.xCellInit+" "+agent.yCellInit);
            System.err.println("goal:"+agent.xCellGoal+" "+agent.yCellGoal);
            System.err.println("#######################");
        }
    }
    public void infoColors(){
        System.err.println("\n------Colors Info-----------");
        int numberOfColors=this.colors.length;
        for(int i = 0; i < numberOfColors; i++){
            GroupOfColors colorGroup = this.colors[i];
            System.err.println("color:" + colorGroup.color.toString());
            System.err.print("agents: ");
            for(int j = 0; j < colorGroup.agents.length; j++) 
                System.err.print(colorGroup.agents[j] + " ");
            System.err.print("\nboxes : ");
            for(int j = 0; j < colorGroup.boxes.length; j++) 
                System.err.print(colorGroup.boxes[j] + " ");
            System.err.println("\n#######################");
        }
    }
    public void infoGoals(){
        char[][] final_goals= this.initialState.goals;
        char c;
        System.err.println("\n------Goals Info from State-----------");
        for (int i = 0; i < final_goals.length; i++) {
            // Loop through each column in the current row
            for (int j = 0; j < final_goals[i].length; j++) {
                c=final_goals[i][j];
                if (('0' <= c && c <= '9') || ('A' <= c && c <= 'Z'))
                {
                    System.err.println(c+": "+i+" "+j);
                }
            }
        }
    }
}