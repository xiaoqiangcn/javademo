package com.johhny.tools.demo.lombok;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor

public class User {
	@Setter @Getter
	private int id;
	@Setter @Getter
	private int age;
	@Setter @Getter
	private int sex;
	@Setter @Getter
	private String name;
	@Setter @Getter
	private String address;
	
	@Override public String toString() {
		 return String.format("%s (age: %d) %d", name, age, sex);
		}
}
