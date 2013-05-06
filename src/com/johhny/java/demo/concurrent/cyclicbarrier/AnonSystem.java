package com.johhny.java.demo.concurrent.cyclicbarrier;


public class AnonSystem implements Runnable{

	boolean flag;
	int N;
	public AnonSystem(boolean flag,int N){
		this.flag=flag;
		this.N=N;
	}
	public void run() {
		// TODO Auto-generated method stub
		if(flag){
			system("经理:[工人"+N+"个，已经完成工作]");
		}else{
			system("经理:[工人"+N+"个，已经到了]");
			flag = true;
		}
	}

		private void system(String str) {
			System.out.println(str);
		}
	}


