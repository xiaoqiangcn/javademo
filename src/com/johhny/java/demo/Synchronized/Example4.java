package com.johhny.java.demo.Synchronized;

import java.util.concurrent.TimeUnit;

class Counter4 {
	static long count = 0;
	private static Object object = new Object();
	public static  void add(long value) {
		synchronized (Counter4.class) {
			System.out.println("--------------in");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count += value;
			System.out.println("--------------out");
		}
	}
}

class Counter4Thread extends Thread {

	protected Counter4 Counter4 = null;

	public Counter4Thread(Counter4 Counter4) {
		this.Counter4 = Counter4;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter4.add(i);
		}
	}
}

class Counter41Thread extends Thread {

	protected Counter4 Counter4 = null;

	public Counter41Thread(Counter4 Counter4) {
		this.Counter4 = Counter4;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter4.add(i);
		}
	}
}

/**
 * 静态方法中的同步块 : 与静态方法同步效果等同 
 * @author IBM
 *
 */
public class Example4 {

	public static void main(String[] args) {
		//例一
		Counter4 Counter4 = new Counter4();
		Thread threadA = new Counter4Thread(Counter4);
		Thread threadB = new Counter41Thread(Counter4);

		threadA.start();
		threadB.start();
		
		
		//例二
		Counter4 Counter41 = new Counter4();
		Counter4 Counter42 = new Counter4();
		Thread threadC = new Counter4Thread(Counter41);
		Thread threadD = new Counter41Thread(Counter42);

		threadC.start();
		threadD.start();
	}
}