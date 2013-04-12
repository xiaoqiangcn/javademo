package com.johhny.java.demo.beanInit;

public class BuildBean {
	private int id;
	private String name;
	private int age;

	private BuildBean(Build build) {
		this.id = build.getId();
		this.name = build.getName();
		this.age = build.getAge();
	}

	private BuildBean() {
	}

	// 1.使用继承则必须匪类有一个没有参数的构造函数
	// 2.不使用继承就必须将所有的属性复制一份
	static class Build extends BuildBean {

		public Build id(int id) {
			setId(id);
			return this;
		}

		public Build age(int age) {
			setAge(age);
			return this;
		}

		public Build name(String name) {
			setName(name);
			return this;
		}

		private BuildBean build() {
			return new BuildBean(this);
		}
	}

	public static void main(String[] args) {
		BuildBean bb = new BuildBean.Build().id(1).name("333").build();
		System.out.println(bb.getId() + "," + bb.getAge() + "," + bb.getName());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
