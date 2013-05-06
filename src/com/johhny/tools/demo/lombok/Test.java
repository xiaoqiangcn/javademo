package com.johhny.tools.demo.lombok;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		User user = new User();
		user.setAddress("浙江杭州");
		user.setAge(1);
		user.setId(1);
		user.setSex(1);
		user.setName("name");
		System.out.println(user);
	}

}
