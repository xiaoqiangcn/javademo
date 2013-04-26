package com.johhny.java.demo.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread.yield(); 在run方法中执行上面方法 可以使线程回到可执行状态
 * @author wb_zhiqiang.xiezq
 *
 */
public class Counter1Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			ExecutorService exec = Executors.newCachedThreadPool();
			for(int i = 0; i < 10 ; i++){
				exec.execute(new Counter1(10));
			}
			exec.shutdown();

	}

}
