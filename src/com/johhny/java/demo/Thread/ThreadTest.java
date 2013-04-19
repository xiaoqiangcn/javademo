package com.johhny.java.demo.Thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {
	private static volatile AtomicInteger ai =  new AtomicInteger(0);
	private static Map<Integer, Integer> map=  new HashMap<Integer, Integer>();
	static class Count implements Runnable {

		@Override
		public void run() {
			int i = incrementAndGet();
			while(!map.containsKey(i)){
				map.put(i, i);
				System.out.println(i);
				i = incrementAndGet();
			}
			
			System.out.println("重复---------" + i);
			
			
		}
		
	} 
	
	public static final int incrementAndGet() {
        return ai.incrementAndGet();
    }
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		for(int i = 0; i < 10 ; i++){
			executor.execute(new Count());
		}
		System.out.println("开始执行---");
		executor.shutdown();
	}
}
