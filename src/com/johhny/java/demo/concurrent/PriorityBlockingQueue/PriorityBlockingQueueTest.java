package com.johhny.java.demo.concurrent.PriorityBlockingQueue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {
	private static int COUNT = 5;
	private static int THREAD_NUM = 2;

	static class Producer extends Thread {
		private BlockingQueue queue;
		private Random rnd = new Random();

		public Producer(BlockingQueue queue) {
			this.queue = queue;
		}

		public void run() {
			for (int i = 0; i < COUNT; i++) {

				try {
					int n = rnd.nextInt(COUNT);
					queue.put(new Entity(n));
					System.err.println("Producer.run()" + n);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class Customer extends Thread {
		private BlockingQueue queue;

		public Customer(BlockingQueue queue) {
			this.queue = queue;
		}

		public void run() {
			for (int i = 0; i < COUNT; i++) {
				try {
					Entity e = (Entity) queue.take();
					System.out.println("Customer.run()" + e.getSize());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class Entity implements Comparable<Entity> {
		private int size;

		public Entity(int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}

		public int compareTo(Entity o) {
			return this.size > o.size ? 1 : ((this.size == o.size) ? 0 : -1);
		}

	}

	private static PriorityBlockingQueue<Entity> queue = new PriorityBlockingQueue<Entity>();

	public static long getQueuePerformanceTest(BlockingQueue queue,
			int threadNum) throws InterruptedException {

		long start = System.nanoTime();
		Thread[] producers = new Producer[threadNum];
		Thread[] customers = new Customer[threadNum];

		for (int i = 0; i < threadNum; i++) {
			producers[i] = new Producer(queue);
			producers[i].start();
			customers[i] = new Customer(queue);
			customers[i].start();
		}

		for (int i = 0; i < threadNum; i++) {
			producers[i].join();
			customers[i].join();
		}

		long end = System.nanoTime();
		return (end - start);
	}

	public static void main(String[] args) {
		try {
			getQueuePerformanceTest(queue, THREAD_NUM);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}