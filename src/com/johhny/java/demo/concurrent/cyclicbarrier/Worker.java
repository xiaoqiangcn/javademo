package com.johhny.java.demo.concurrent.cyclicbarrier;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {

	private String worker;
	private final CyclicBarrier cyclic;

	Worker(CyclicBarrier cyclic,String worker) {
		this.cyclic = cyclic;
		this.worker = worker;
	}

	public void run() {
		try {
			cyclic.await();
			doWork();
			cyclic.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	void doWork() {
		System.out.println(worker+":劳动完成");
	}
}

