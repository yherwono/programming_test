package com.placester.test;

public class Priority<X> implements Comparable<Priority<X>> {

	protected final X item;
    protected final int priority;
    
    public Priority(int priority, X item)
    {
        this.priority = priority;
        this.item = item;
    }
    
    public X item(){return item;}
    public int priority(){return  priority;}

	
	@Override
	public int compareTo(Priority<X> o) {
		return this.priority - o.priority;
	}

}
