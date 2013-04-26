package com.johhny.java.demo.Synchronized;

import java.util.concurrent.TimeUnit;

class Counter2 {
	static long count = 0;

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
}

class Counter2Thread extends Thread {

	protected Counter2 Counter2 = null;

	public Counter2Thread(Counter2 Counter2) {
		this.Counter2 = Counter2;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter2.add(i);
		}
	}
}

/**
 * 静态方法同步 ： 同步在该方法所在的类对象上，Java虚拟机中一个类只能对应一个类对象，
 * 所以同时只允许一个线程执行同一个类中的静态同步方法
 * 例一、例二都是同步执行，不会出现交叉现象
 * @author IBM
 *
 */
public class Example2 {

	public static void main(String[] args) {
		//例一
		Counter2 counter = new Counter2();
		Thread threadA = new Counter2Thread(counter);
		Thread threadB = new Counter2Thread(counter);

		threadA.start();
		threadB.start();
		
		
		//例二
		Counter2 Counter21 = new Counter2();
		Counter2 Counter22 = new Counter2();
		Thread threadC = new Counter2Thread(Counter21);
		Thread threadD = new Counter2Thread(Counter22);

		threadC.start();
		threadD.start();
	}
}