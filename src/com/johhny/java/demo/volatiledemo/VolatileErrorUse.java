package com.johhny.java.demo.volatiledemo;

public class VolatileErrorUse {

	public volatile static int count = 0;

	public static void inc() {

		// 这里延迟1毫秒，使得结果明显
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}

		count++;
	}

	public static void main(String[] args) {

		// 同时启动1000个线程，去进行i++计算，看看实际结果

		for (int i = 0; i < 1000; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					VolatileErrorUse.inc();
				}
			}).start();
		}

		// 这里每次运行的值都有可能不同,可能为1000
		/**
		 * 原因： jvm运行时刻内存的分配。其中有一个内存区域是jvm虚拟机栈，每一个线程运行时都有一个线程栈，
		 * 线程栈保存了线程运行时候变量值信息。当线程访问某一个对象时候值的时候，首先通过对象的引用找到对应在堆内存的变量的值，然后把堆内存
		 * 变量的具体值load到线程本地内存中，建立一个变量副本，之后线程就不再和对象在堆内存变量值有任何关系，而是直接修改副本变量的值，
		 * 在修改完之后的某一个时刻（线程退出之前），自动把线程变量副本的值回写到对象在堆中变量
		 */
		System.out.println("运行结果:Counter.count=" + VolatileErrorUse.count);
	}
}
