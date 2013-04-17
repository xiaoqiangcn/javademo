package com.johhny.java.demo.ReflectAsm;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * 依赖jar包 asm-4.0jar,asm-util-4.0.jar,reflectasm-1.05.jar
 * @author IBM
 *
 */
public class ReflectAsmTest {
	public static void main(String[] args) {
		User user = new User();
		//使用reflectasm生产User访问类
		MethodAccess access = MethodAccess.get(User.class);
		//invoke setName方法name值
		access.invoke(user, "setName", "张三");
		//invoke getName方法 获得值
		String name = (String)access.invoke(user, "getName", null);
		System.out.println(name);
	}
}
