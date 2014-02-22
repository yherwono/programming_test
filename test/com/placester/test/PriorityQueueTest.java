package com.placester.test;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

public class PriorityQueueTest {

	@Test
	public void test() throws InterruptedException
    {
        final ThreadSafePriorityQueue<QueueTestTask> q = new ThreadSafePriorityQueue<>();
        final Random rand = new Random();
        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        int max = 1000;
        for(int i = 0; i < max; i++)
        {
            final int idx = i;
            threadPool.submit(new Runnable(){
                @Override
                public void run()
                {
                    q.add(new Priority<QueueTestTask>(rand.nextInt(100), new QueueTestTask(idx)));
                    try
                    {
                        //make sure we actually hit multiple threads;
                        Thread.sleep(50);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }
                }});
        }
        
        threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
        
        int startPriority = 0;
        //Test that threadsafe insertion worked
        while(!q.isEmpty())
        {
            Priority<QueueTestTask> item = q.poll();
            Assert.assertTrue(item.priority >= startPriority);
        }
        
        Priority<QueueTestTask> item1 = new Priority<QueueTestTask>(666, new QueueTestTask(0));
        Priority<QueueTestTask> item2 = new Priority<QueueTestTask>(0, new QueueTestTask(0));
        Priority<QueueTestTask> item3 = new Priority<QueueTestTask>(42, new QueueTestTask(0));

        
        q.add(item1);
        q.add(item2);
        q.add(item3);

        Assert.assertTrue(q.contains(item1));
        Priority<QueueTestTask> item = q.poll();
        Assert.assertEquals(item, item2);
        item = q.poll();
        Assert.assertEquals(item, item3);
        item = q.poll();
        Assert.assertEquals(item, item1);

        
        q.add(item1);
        Assert.assertFalse(q.isEmpty());
        item = q.peek();
        Assert.assertEquals(item, item1);
        Assert.assertFalse(q.isEmpty());

        item = q.poll();
        Assert.assertEquals(item, item1);
        Assert.assertTrue(q.isEmpty());
    }

}
