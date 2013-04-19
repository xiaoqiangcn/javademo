package com.johhny.java.demo.Thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 
 * FutureTask 可取消的异步计算。利用开始和取消计算的方法、查询计算是否完成的方法和获取计算结果的方法,
 * 仅在计算完成时才能获取结果；如果计算尚未完成，则阻塞 get 方法。一旦计算完成，就不能再重新开始或取消计算
 * @author wb_zhiqiang.xiezq
 *
 */
public class FutureTaskSample {
    
    static FutureTask<String> future1 = new FutureTask(new Callable<String>(){
        public String call(){
            return getPageContent();
        }
    });
    
    static FutureTask<String> future2 = new FutureTask(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}, getPageContent());
    
    public static void main(String[] args) throws InterruptedException, ExecutionException{
        //Start a thread to let this thread to do the time exhausting thing
        new Thread(future1).start();
        //Main thread can do own required thing first
        doOwnThing();
        //At the needed time, main thread can get the result
        System.out.println(future1.isDone());
        System.out.println(future1.isCancelled());
        System.out.println(future1.get());
        future1.cancel(false);
        System.out.println(future1.isDone());
        System.out.println(future1.isCancelled());
        
        
        new Thread(future2).start();
        doOwnThing();
        System.out.println(future2.get());
    }
    
    public static String doOwnThing(){
        return "Do Own Thing";
    }
    public static String getPageContent(){
        return "testPageContent and provide that the operation is a time exhausted thing...";
    }
}
