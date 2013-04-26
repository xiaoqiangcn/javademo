package com.johhny.java.demo.Thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {
	private static Lock lock = new ReentrantLock();

	private static Condition condition1 = lock.newCondition();
	private static Condition condition2 = lock.newCondition();
	private static Condition condition3 = lock.newCondition();

	private static Integer flag = 1;

	public static void main(String[] args) throws Exception {
		
		//老二执行
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					while (true) {
						while (flag != 2) {// 不是2号 执行
							try {
								condition2.await();// 则使自己等待
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
							// 执行 老二代码
							for (int i = 0; i < 10; i++) {
								System.out.println("---------老二执行   ： " + i);
								TimeUnit.MILLISECONDS.sleep(500);
							}
							// 执行完 通知老三执行
							condition3.signal();
							flag = 3;
						}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}

			}
		}).start();

		// 老三线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					while (true) {
						while (flag != 3) {// 不是3号 执行
							try {
								condition3.await();// 则使自己等待

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 执行 老3代码
						for (int i = 0; i < 10; i++) {
							System.out.println("---------------------老三执行   ： "+ i);
							TimeUnit.MILLISECONDS.sleep(500);
						}
						// 执行完 通知老大执行
						condition1.signal();
						flag = 1;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}

			}
		}).start();
		
		//老大执行
		/**
		 * 这段代码  即老大执行的这段程序必须放在  thread1 thread2 的后面
		 *   因为在老大的代码中  有condition2.signal();  
		 *   若是放在thread1前面的话  程序必不会执行 condition2.await();
		 *   所以会报错！
		 */
				lock.lock();
				try {
					while (true) {
						while (flag != 1) {// 不是1号 执行
							try {
								condition1.await();// 则使自己等待
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
							// 执行 老大代码
							for (int i = 0; i < 10; i++) {
								System.out.println("-老大执行   ： " + i);
								TimeUnit.MILLISECONDS.sleep(500);
							}
							// 执行完 通知老二执行
							flag = 2;
							condition2.signal();
						}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
	}
}