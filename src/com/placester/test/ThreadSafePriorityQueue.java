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
        return true;
    }

    @Override
    public Priority<X> poll(){
    	synchronized(lock){
    		if(nrOfNodes >= 1){
    			exchange(1,nrOfNodes);
    			sink(1, nrOfNodes -1);
    			return tasks[nrOfNodes--];
    		}
    		else{
    			return null;
    		}
    	}
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
    
}