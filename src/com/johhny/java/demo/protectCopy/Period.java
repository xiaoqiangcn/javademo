package com.johhny.java.demo.protectCopy;

import java.util.Date;

/**
 * 必要的时候需要做保护性拷贝
 * 
 * @author wb_zhiqiang.xiezq
 * 
 */
public class Period {
	private final Date start;
	private final Date end;

	public Date getStart() {
		// 如果外部对象调用 getStart().setYear(1); 也会导致同样问题
		// 可改为 return new Date(start.getTime())
		return start;
	}

	public Date getEnd() {
		// 如果外部对象调用 getEnd().setYear(1); 也会导致同样问题
		// 可改为 return new Date(end.getTime())
		return end;
	}

	public Period(Date start, Date end) {
		if (start.compareTo(end) > 0) {
			throw new RuntimeException("开始时间不能大于结束时间");
		}
		this.start = start;
		this.end = end;// 会出现问题
		// this.start = new Date(start.getTime());
		// this.end = new Date(end.getTime());//使用拷贝不会出现问题

	}


	private void Main() {
		Date start = new Date();
		Date end = new Date();
		Period p = new Period(start, end);
		start.setYear(1);// 因为Period没有做拷贝，改set会导致开始时间不一定大于结束时间

	}
}
