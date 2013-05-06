package com.johhny.java.demo.Thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runnable是执行工作的独立任务，不返回任何值，如果希望任务结束后能够返回一个值，应该实现Callable接口，
 *并且通过ExecutorService.submit()调用，并返回一个Future<T>对象
 *
 * @author wb_zhiqiang.xiezq
 *
 */
public class CallableTest {
	public static void main(String[] args) {
		ExecutorService exec = Executors.newCachedThreadPool();
		List<Future<String>> results = new ArrayList<Future<String>>();
		for(int i = 0; i < 10 ; i++){
			results.add(exec.submit(new CallableDemo(10)));
		}
		exec.shutdown();
		for (Future<String> future:results) {
			System.out.println(future.isDone());
			try {
				System.out.println(future.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}finally{
				exec.shutdown();
			}
		}
		
	}
}
