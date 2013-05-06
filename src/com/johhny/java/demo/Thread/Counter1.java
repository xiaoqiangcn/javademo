package com.johhny.java.demo.Thread;

/**
 * 倒计时类
 * 
 * Thread.yield(); 在run方法中执行上面方法 可以使线程回到可执行状态,排程器从可执行状态的线程中重新安排线程执行，
 *
 * 2010-4-19 下午09:12:02
 */
public class Counter1 implements Runnable {
	
	private int countNum;
	private static int taskCount = 0;//线程id
	private final int taskId = taskCount++;
	
	public Counter1(int countNum) {
		this.countNum = countNum;
	}
	
	public String show(){
		return "Id[" + taskId + "] countNum:" + countNum + "  ";
	}
	@Override
	public void run() {
		while(countNum-- > 0){
			System.out.print(show());
			Thread.yield();
		}
	}
	

}

