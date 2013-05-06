package com.johhny.java.demo.Thread;

import java.util.concurrent.Callable;

/**
 * 计数类2可以返回值
 *
 *
 *
 * 2010-4-19 下午09:55:12
 */
public class CallableDemo implements Callable<String> {
	private int countNum;
	private static int taskCount = 0;//线程id
	private final int taskId = taskCount++;
	private StringBuffer sb = new StringBuffer();
	
	public CallableDemo(int countNum) {
		this.countNum = countNum;
	}
	
	public String show(){
		return "Id[" + taskId + "] countNum:" + countNum + "  ";
	}
	@Override
	public String call() throws Exception {
		while(countNum-- > 0){
			sb.append(show());
			Thread.yield();
		}
		return sb.toString();
	}

}
