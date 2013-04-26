package com.johhny.java.demo.Synchronized;

import java.util.concurrent.TimeUnit;

class Counter1 {
	long count = 0;

	public synchronized void add(long value) {
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

class Counter1Thread extends Thread {

	protected Counter1 Counter1 = null;

	public Counter1Thread(Counter1 Counter1) {
		this.Counter1 = Counter1;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			Counter1.add(i);
		}
	}
}

/**
 * 测试实例方法同步 ： 同步是同步在拥有该方法的对象上
 * 1. 对于相同实例处在不同线程中，类中的同步方法只能有一个在访问 见例一
 * 2. 如果不同实例在不同线程中，则类的同步方法间不受到阻塞 见例二
 * @author IBM
 *
 */
public class Example1 {

	public static void main(String[] args) {
		//例一
		Counter1 Counter1 = new Counter1();
		Thread threadA = new Counter1Thread(Counter1);
		Thread threadB = new Counter1Thread(Counter1);

		threadA.start();
		threadB.start();
		
		
		//例二
		Counter1 Counter11 = new Counter1();
		Counter1 Counter12 = new Counter1();
		Thread threadC = new Counter1Thread(Counter11);
		Thread threadD = new Counter1Thread(Counter12);

		threadC.start();
		threadD.start();
	}
}