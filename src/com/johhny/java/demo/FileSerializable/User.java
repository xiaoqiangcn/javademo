/**
 * Project: FileSerializable
 * 
 * File Created at 2012-9-5上午10:55:07
 * $Id$
 * 
 * Copyright 1999-2012 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.johhny.java.demo.FileSerializable;

import java.io.Serializable;

/**
 * 类 User 的实现描述：TODO 类实现描述
 * 
 * @author wb_zhiqiang.xiezq 2012-9-5上午10:55:07
 */
public class User implements Serializable {

    /**   **/
    private static final long serialVersionUID = 8346606041831556733L;

    private Integer           id;
    private String            name;
    private int               sex;
    private int               age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @author wb_zhiqiang.xiezq 2012-9-5上午10:56:14
     */
    @Override
    public String toString() {
        return "{id :" + id + ", name:" + name + ",sex:" + sex + ",age:" + age + "}";
    }
}
