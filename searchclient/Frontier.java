package searchclient;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

public interface Frontier
{
    void add(State state);
    State pop();
    boolean isEmpty();
    int size();
    boolean contains(State state);
    String getName();
}

class FrontierBFS
        implements Frontier
{
    private final ArrayDeque<State> queue = new ArrayDeque<>(65536);
    private final HashSet<State> set = new HashSet<>(65536);

    @Override
    public void add(State state)
    {
        this.queue.addLast(state);
        this.set.add(state);
    }

    @Override
    public State pop()
    {
        State state = this.queue.pollFirst();
        this.set.remove(state);
        return state;
    }

    @Override
    public boolean isEmpty()
    {
        return this.queue.isEmpty();
    }

    @Override
    public int size()
    {
        return this.queue.size();
    }

    @Override
    public boolean contains(State state)
    {
        return this.set.contains(state);
    }

    @Override
    public String getName()
    {
        return "breadth-first search";
    }
}

class FrontierDFS
        implements Frontier
{
    private final Stack<State> stk = new Stack<>();
    private final HashSet<State> set = new HashSet<>(65536);

    @Override
    public void add(State state)
    {
        this.stk.add(state);
        this.set.add(state);
        System.err.println(state.toString());
        //throw new NotImplementedException();
    }

    @Override
    public State pop()
    {
        State state= this.stk.pop();
        set.remove(state);
        return state;
        //throw new NotImplementedException();
    }

    @Override
    public boolean isEmpty()
    {

        //throw new NotImplementedException();
        return this.stk.isEmpty();
    }

    @Override
    public int size()
    {
        //
        // throw new NotImplementedException();
        return this.stk.size();
    }

    @Override
    public boolean contains(State state)
    {
        return set.contains(state);
        //throw new NotImplementedException();
    }

    @Override
    public String getName()
    {
        return "depth-first search";
    }
}

class FrontierBestFirst
        implements Frontier
{
    private int iter;
    private Heuristic heuristic;
    private PriorityQueue<State> pq;
    private final HashSet<State> set = new HashSet<>(65536);
    public FrontierBestFirst(Heuristic h)
    {
        this.heuristic = h;
        pq = new PriorityQueue<State>(65536, this.heuristic);
        iter=0;
    }

    @Override
    public void add(State state)
    {
        pq.add(state);
        set.add(state);
        //throw new NotImplementedException();
    }

    @Override
    public State pop()
    {
        iter++;
        State state= pq.remove();
        // System.err.println("IteR:"+iter);
        System.err.println("\n"+state.toString());
        // System.err.println("IteR:"+iter);
        set.remove(state);
        return state;
        //throw new NotImplementedException();
    }

    @Override
    public boolean isEmpty()
    {
        return this.pq.isEmpty();
        //throw new NotImplementedException();
    }

    @Override
    public int size()
    {
        return this.pq.size();
        //throw new NotImplementedException();
    }

    @Override
    public boolean contains(State state)
    {
        return this.set.contains(state);
        //throw new NotImplementedException();
    }

    @Override
    public String getName()
    {
        return String.format("best-first search using %s", this.heuristic.toString());
    }
}
