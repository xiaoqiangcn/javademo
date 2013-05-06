package com.johhny.tools.demo.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
//@Data 会生成get set、hashcode、toString 等
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
	
}
