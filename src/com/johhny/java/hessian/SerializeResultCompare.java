package com.johhny.java.hessian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;

public class SerializeResultCompare {
	
	public static void main(String[] args) {
		String msg = "test";
		byte[] reuseSerialHessian = serializerWithHessian(msg);
		printByte(reuseSerialHessian);
		printChar(reuseSerialHessian);

		byte[] reuseSerialJava = serializerWithJava(msg);
		printByte(reuseSerialJava);
		printChar(reuseSerialJava);
		
	}
	
	private static byte[] serializerWithHessian(Object obj){
		ByteArrayOutputStream binary = new ByteArrayOutputStream();
		SerializerFactory factory = new SerializerFactory();
		HessianOutput hout = new HessianOutput(binary);
		hout.setSerializerFactory(factory);
		try {
			hout.writeObject(obj);
			hout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return binary.toByteArray();
	}

	private static byte[] serializerWithJava(Object obj){
		ByteArrayOutputStream binary = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(binary);
			oos.writeObject(obj);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return binary.toByteArray();
	}
	
	private static void printByte(byte[] reuseSerial){
		System.out.println();
		for (byte b : reuseSerial) {
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.println();
		
	}
	
	private static void printChar(byte[] reuseSerial){
		System.out.println();
		for (byte b : reuseSerial) {
			System.out.print((char)b);
			System.out.print(" ");
		}
		System.out.println();
	}
	
}
