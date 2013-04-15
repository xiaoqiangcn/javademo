/**
 * Project: FileSerializable
 * 
 * File Created at 2012-9-5上午10:57:43
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 类 Write 的实现描述：TODO 类实现描述
 * 
 * @author wb_zhiqiang.xiezq 2012-9-5上午10:57:43
 */
public class Write {
    /**
     * @author wb_zhiqiang.xiezq 2012-9-4涓嬪崍4:59:38
     * @param args
     */
    public static void main(String[] args) {
        User u = new User();
        u.setId(11);
        u.setAge(11);
        u.setName("test");
        u.setSex(0);
        save(u);

    }

    static public void save(User user) {

        File dataFile = new File(System.getProperty("user.home") + "/tmp/FileSerializable/"
                + "user.dat");
        FileOutputStream fout = null;

        try {
            dataFile.getParentFile().mkdirs();
            dataFile.createNewFile();
            fout = new FileOutputStream(dataFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fout) {
                try {
                    fout.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
