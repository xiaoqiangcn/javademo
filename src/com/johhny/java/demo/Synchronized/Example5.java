package com.johhny.java.demo.Synchronized;

import java.util.concurrent.TimeUnit;

class Counter5 {
	static long count = 0;
	private static Object object = new Object();
	public static synchronized void add(long value) {
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
	public static void add1(long value) {
		synchronized (Counter5.class) {
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

class Counter5Thread extends Thread {

	protected Counter5 Counter5 = null;

	public Counter5Thread(Counter5 Counter5) {
		this.Counter5 = Counter5;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter5.add(i);
		}
	}
}

class Counter51Thread extends Thread {

	protected Counter5 Counter5 = null;

	public Counter51Thread(Counter5 Counter5) {
		this.Counter5 = Counter5;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter5.add1(i);
		}
	}
}

/**
 * 静态方法中的同步和静态方法内的同步快：
 * 如果同步快为 synchronized (Counter5.class)  和 俩者都是静态同步块一致
 * 如果同步快为 synchronized (object)  和 方法同步块效果一致
 * @author IBM
 *
 */
public class Example5 {

	public static void main(String[] args) {
		//例一
		Counter5 Counter5 = new Counter5();
		Thread threadA = new Counter5Thread(Counter5);
		Thread threadB = new Counter51Thread(Counter5);

		threadA.start();
		threadB.start();
		
		
		//例二
		Counter5 Counter51 = new Counter5();
		Counter5 Counter52 = new Counter5();
		Thread threadC = new Counter5Thread(Counter51);
		Thread threadD = new Counter51Thread(Counter52);

		threadC.start();
		threadD.start();
	}
}