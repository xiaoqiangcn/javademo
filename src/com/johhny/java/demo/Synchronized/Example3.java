package com.johhny.java.demo.Synchronized;

import java.util.concurrent.TimeUnit;

class Counter3 {
	long count = 0;
	private Object object= new Object();
	public  void add(long value) {
		synchronized (this) {
			System.out.println("--------------in");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.count += value;
			System.out.println("--------------out");
		}
		
	}
	
}

class Counter3Thread extends Thread {

	protected Counter3 Counter3 = null;

	public Counter3Thread(Counter3 Counter3) {
		this.Counter3 = Counter3;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter3.add(i);
		}
	}
}

/**
 * 实例方法中的同步块 : 
 * synchronized (this),synchronized (object)效果和实例方法同步效果等同  Example1
 * synchronized (Counter3.class)效果静态方法同步效果等同 Example2
 * @author IBM
 *
 */
public class Example3 {

	public static void main(String[] args) {
		//例一
		Counter3 Counter3 = new Counter3();
		Thread threadA = new Counter3Thread(Counter3);
		Thread threadB = new Counter3Thread(Counter3);

		threadA.start();
		threadB.start();
		
		
		//例二
		Counter3 Counter31 = new Counter3();
		Counter3 Counter32 = new Counter3();
		Thread threadC = new Counter3Thread(Counter31);
		Thread threadD = new Counter3Thread(Counter32);

		threadC.start();
		threadD.start();
	}
}