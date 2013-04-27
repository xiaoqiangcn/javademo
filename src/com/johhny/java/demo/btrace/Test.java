package com.johhny.java.demo.btrace;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Test {

	public static void main(String args[]) {
		System.out.println("start");
		Counter c = new Counter();
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			}
			System.out.println(c.add(new Random().nextInt(10)));
		}
	}

}