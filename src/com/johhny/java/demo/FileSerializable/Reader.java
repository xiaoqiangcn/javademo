/**
 * Project: FileSerializable
 * 
 * File Created at 2012-9-5上午10:57:50
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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * 类 Reader 的实现描述：TODO 类实现描述
 * 
 * @author wb_zhiqiang.xiezq 2012-9-5上午10:57:50
 */
public class Reader {
    public static void main(String[] args) {
        File dataFile = new File(System.getProperty("user.home") + "/tmp/FileSerializable/"
                + "user.dat");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(dataFile);
            User user = (User) new ObjectInputStream(fin).readObject();
            System.out.println(user);
        } catch (Exception e) {
            System.out.println("error when read file: " + dataFile.getAbsolutePath() + ", "
                    + e.toString() + e.getCause());
        } finally {
            if (null != fin) {
                try {
                    fin.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
}
