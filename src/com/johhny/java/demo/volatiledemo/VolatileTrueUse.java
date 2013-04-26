package com.johhny.java.demo.volatiledemo;

import java.util.concurrent.TimeUnit;

/**
 * Volatile 正确使用
 * 
 * @author wb_zhiqiang.xiezq
 * 
 */
public class VolatileTrueUse {
	/*
	 * 此处不加volatile 关键字，下面的线程中的while可能（我的机器没有测试出来）不会停止,
	 * 原因看具体虚拟机解析代码的结果：
	 * <code>
	 * while (!isStop) {
	 *				 i ++;
	 *			}
	 * </code>
	 * 可能会被解析成：
	 * <code>
	 * if(!isStop)
	 * 		while (true) {
	 *				 i ++;
	 *			}
	 *  }
	 * </code>
	 */
	private static volatile boolean isStop;

	public static void main(String[] args) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int i = 0;
				while (!isStop) {
					 i ++;
				}
				System.out.println(i);
			}
		}).start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isStop = true;
	}

}
