package com.johhny.java.demo.concurrent.cyclicbarrier;


import java.util.concurrent.CyclicBarrier;

public class Driver {

		  public static  void main(String args[]) throws InterruptedException {
			  final int N= 10;
			    boolean flag = false;
			  CyclicBarrier cyclic = new CyclicBarrier(N,
						new AnonSystem(flag,N)

				);//设置屏障点，主要是为了执行这个方法
		      for (int i = 0; i < N; ++i){ // create and start threads
		    	  print("工人"+i+"到了..");
		        new Thread(new Worker(cyclic,"工人"+i)).start();
		      }
		    }

		private static void print(String message) {
			// TODO Auto-generated method stub
			System.out.println(message);
		}
}


