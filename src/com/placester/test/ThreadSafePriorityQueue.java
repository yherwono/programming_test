package com.placester.test;

import java.util.Random;


public class ThreadSafePriorityQueue<X> implements SimpleQueue<Priority<X>>
{
	// Using algorithm from Sedgewick to handle the heap management
	private static final int DEFAULT_SIZE = 1000;
	
	private int nrOfNodes = 0;
	private Priority<X> [] tasks = null;
	private int currentHeapSize = 0;
	private final Object lock = new Object();
	
    public ThreadSafePriorityQueue()
    {
        initialize();
    }
    
    
    @SuppressWarnings("unchecked")
	public void initialize()
    {
        //TODO: put your initialization code here
    	tasks = (Priority<X>[]) new Priority<?>[DEFAULT_SIZE];
    	currentHeapSize = DEFAULT_SIZE;
    }
    
    @SuppressWarnings("unchecked")
	private void increaseHeap(){
    	Priority<X> [] newArray = (Priority<X>[]) new Priority<?>[tasks.length + DEFAULT_SIZE];
    	
    	for(int i=0; i<tasks.length;i++){
    		newArray[i] = tasks[i];
    	}
    	tasks = newArray;
    	currentHeapSize = tasks.length;
    	
    }
    @Override
    public int size()
    {
        return nrOfNodes;
    }

    @Override
    public boolean isEmpty()
    {
       return nrOfNodes == 0;
    }

    @Override
    public void clear()
    {
        nrOfNodes = 0;
        for(int i=0; i<tasks.length;i++){
    		tasks[i] = null;
    	}
        
    }
    
    private boolean greater(int i, int j){
    	return tasks[i].compareTo(tasks[j]) > 0;
    }
    
    private void exchange(int i, int j){
    	Priority<X> temp = tasks[i];
    	tasks[i] = tasks[j];
    	tasks[j] = temp;
    }

    private void swim(int k){
    	while(k > 1 && greater(k/2,k)){
    		exchange(k,k/2);
    		k = k/2;
    	}
    }
    
    private void sink(int k, int N){
    	while(2*k <= N){
    		int j= 2*k;
    		if(j<N && greater(j, j+1)) j++;
    		if(!greater(k,j)) break;
    		exchange(k,j);
    		k=j;
    	}
    }
    
    @Override
    public boolean add(Priority<X> e)
    {
        if( e == null){
        	throw new NullPointerException();
        }
        synchronized(lock){
        	if(nrOfNodes == currentHeapSize - 1){
        		increaseHeap();
        	}
        	tasks[++nrOfNodes] = e;
        	swim(nrOfNodes);
        }
        return false;
    }

    @Override
    public Priority<X> poll(){
    	synchronized(lock){
    		exchange(1,nrOfNodes);
    		sink(1, nrOfNodes -1);
    	}
        return tasks[nrOfNodes--];
    }

    @Override
    public Priority<X> peek()
    {
        return tasks[1];
    }

    @Override
    public boolean contains(Priority<X> x)
    {   boolean found = false;
    	if( x == null){
        	throw new NullPointerException();
        }
    	synchronized(lock){
    		for(int i=1; i<=nrOfNodes;i++){
    			Priority<X> p = tasks[i];
    			if(p.equals(x)){
    				found = true;
    				break;
    			}
    		}
    	}
        return found;
    }
    
    public static void main(String[] args){
    	ThreadSafePriorityQueue<QueueTestTask> q = new ThreadSafePriorityQueue<>();
    	final Random rand = new Random();
    	int max = 10;
        for(int i = 0; i < max; i++)
        {   int idx = i;
        	q.add(new Priority<QueueTestTask>(rand.nextInt(100), new QueueTestTask(idx)));
        
        }
        
        while(!q.isEmpty())
        {
            Priority<QueueTestTask> item = q.poll();
            System.out.println("Priority: " + item.priority);
        }
        
        Priority<QueueTestTask> item1 = new Priority<QueueTestTask>(666, new QueueTestTask(0));
        Priority<QueueTestTask> item2 = new Priority<QueueTestTask>(0, new QueueTestTask(0));
        Priority<QueueTestTask> item3 = new Priority<QueueTestTask>(42, new QueueTestTask(0));

        
        q.add(item1);
        q.add(item2);
        q.add(item3);

        System.out.println("Item 1: "  + q.contains(item1));
        Priority<QueueTestTask> item = q.poll();
        System.out.println("Item 2: "  + item.priority);
        
        item = q.poll();
        
        System.out.println("Item 3: "  + item.priority);
        
        item = q.poll();
        System.out.println("Item 1: "  + item.priority);

    }
}