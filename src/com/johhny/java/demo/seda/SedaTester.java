package com.johhny.java.demo.seda;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * SEDA性能测试
 * 
 * @authorzhanghan
 *
 * @version $Id$
 */
public class SedaTester {
    /**
     * 并发开始时间
     */
    public long start  = 0;
    /**
     * 并发结束时间
     */
    public long end    = 0;

    /**
     * 某个时刻的并发消耗时间
     */
    public Long useSum = new Long(0);

    /**
     * 采用普通线程池的组件
     * @author zhanghan 
     */
    class NormalComponent {

        /**
         * wait time 线程阻塞时间（模拟在实际调用中由于阻塞调用所等待的时间）
         */
        int    wt;
        /**
         * service time 线程服务时间(模拟非阻塞的活跃状态下线程对CPU的使用时间)
         */
        double st;

        public NormalComponent(int wt, double st) {
            this.wt = wt;
            this.st = st;
        }

        /**
         * 将被外部调用的业务方法，接收业务逻辑处理请求
         * @param msg
         */
        protected void doBusiness(String msg) {
            process();//处理业务逻辑
        }

        /**
         * 业务逻辑的具体处理
         */
        final protected void process() {
            try {
                //do something with the msg for the business
                keepCPUBusy(); //CPU运算时间 st 毫秒，此处省略具体业务逻辑，使用st的CPU运算来代替
                Thread.sleep(this.wt); //阻塞时间 wt 毫秒, 此处用sleep来模拟线程由于阻塞调用的等待过程
                markTime();//记录处理时间
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 计算当前消耗的时间，并记录到useSum变量中
         */
        final private void markTime() {
            end = System.currentTimeMillis();
            long sum = end - start;
            synchronized (useSum) {
                if (sum > useSum)
                    useSum = sum;
            }
        }

        /**
         * 此方法会进行约st的CPU运算，用来模拟组件在非阻塞的活跃状态下对CPU的使用
         */
        final protected void keepCPUBusy() {
            int defaultCount = 5000000;//5000000次的循环将会消耗15ms的CPU运算
            defaultCount = (int) (defaultCount * (st / 15));
            long sum = 0;
            for (int k = 0; k < defaultCount; k++) {
                sum += k;
            }
        }

    }

    /**
     * SEDA组件，模拟了SEDA的线程池和事件队列模型
     * @author zhanghan
     * @version $Id: SedaTester.java,v 0.1 2009-7-23 下午08:51:20 zhanghan Exp $
     */
    class SedaComponent extends NormalComponent {
        /**
         * 线程池
         */
        List<Thread>          threads    = new ArrayList<Thread>();
        /**
         * 事件队列
         */
        LinkedList<SedaEvent> eventQueue = new LinkedList<SedaEvent>();
        /**
         * 线程池大小
         */
        int                   poolSize   = 100;

        public SedaComponent(int size, int wt, double st) {
            super(wt, st);
            this.poolSize = size;
            this.initPool();
        }

        /**
         * 初始化线程池
         */
        private void initPool() {
            for (int k = 0; k < poolSize; k++) {
                SedaThread st = new SedaThread();
                threads.add(st);
                st.start();
            }
        }

        /**
         * 重载父类的doBusiness 将被外部调用的业务方法，接收业务逻辑处理请求
         * @param msg
         * @see SedaTester.NormalComponent#doBusiness(java.lang.String)
         */
        protected void doBusiness(String msg) {
            //外部调用的线程调用此方法后，一个新的事件将被产生并放入队列
            synchronized (eventQueue) {
                //这里通过同步加锁的方式实现事件队列与线程池之间的协作
                eventQueue.addLast(new SedaEvent(msg, this));
                eventQueue.notify();
            }

        }

        /**
         * 线程池中的线程，通过同步加锁的方式与事件队列协作
         * @author zhanghan
         * @version $Id: SedaTester.java,v 0.1 2009-7-23 下午08:58:06 zhanghan Exp $
         */
        class SedaThread extends Thread {
            /**
             * 是否保持活跃状态
             */
            boolean live = true;

            public void run() {
                while (isLive()) {
                    SedaEvent event = null;
                    synchronized (eventQueue) {
                        while (eventQueue.isEmpty()) {
                            try {
                                eventQueue.wait();//队列为空时等待直到有新的事件
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        event = eventQueue.removeFirst();//从队列中先入先出的取出事件
                    }
                    if (event != null) {
                        //通知事件接收组件开始处理业务逻辑
                        event.notifyBusiness();
                    }
                }
            }

            public boolean isLive() {
                return live;
            }

            /**
             * 结束线程
             */
            public void kill() {
                this.live = false;
            }
        }

        /**
         * 事件队列中的event，保存事件元数据，实现组件通知
         * @author zhanghan
         * @version $Id: SedaTester.java,v 0.1 2009-7-23 下午09:00:18 zhanghan Exp $
         */
        class SedaEvent {
            String        msg;
            SedaComponent component;

            public SedaEvent(String msg, SedaComponent component) {
                this.msg = msg;
                this.component = component;
            }

            /**
             * 通知组件进行业务处理
             */
            public void notifyBusiness() {
                component.process();//处理业务逻辑 
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public NormalComponent getComponent() {
                return component;
            }

            public void setComponent(SedaComponent component) {
                this.component = component;
            }

        }
    }

    /**
     * 此守护线程每隔1秒中检阅一次并发的消耗时间
     * @author zhanghan
     * @version $Id: SedaTester.java,v 0.1 2009-7-23 下午09:01:13 zhanghan Exp $
     */
    class CheckThread extends Thread {
        boolean live = true;

        public CheckThread() {
            this.setDaemon(true);
        }

        public void run() {
            while (isLive()) {
                synchronized (useSum) {
                    System.out.println("当前耗时 " + useSum + " ms");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isLive() {
            return live;
        }

        public void kill() {
            this.live = false;
        }
    }

    /**
     * 第一个组件的Service Time
     */
    public double ST1           = 0;
    /**
     * 第一个组件的Wait Time
     */
    public int    WT1           = 3;
    /**
     * 第二个组件的Service Time
     */
    public double ST2           = 0;
    /**
     * 第二个组件的Wait Time
     */
    public int    WT2           = 100;

    /**
     * 第一个SEDA组件的线程池大小
     */
    public int    sedaPoolSize1 = 38;
    /**
     * 第二个SEDA组件的线程池大小
     */
    public int    sedaPoolSize2 = 962;
    /**
     * 第一个组件调用的并发数
     */
    public long   count1        = 5000;
    /**
     *  第二个组件调用的并发数
     */
    public long   count2        = 5000;

    /**
     * 普通线程池的测试
     */
    public void testNormal() {

        final NormalComponent normal1 = new NormalComponent(WT1, ST1);
        final NormalComponent normal2 = new NormalComponent(WT2, ST2);
        System.out.println("Normal 开始并发 ");
        start = System.currentTimeMillis();
        new CheckThread().start();//启动监测守护线程
        Executor exe = Executors.newScheduledThreadPool(sedaPoolSize1 + sedaPoolSize2);//建立线程池来承受并发调用
        long total = count1 + count2;
        for (int k = 0; k < total; k++) {
            //为了公平，间隔的来产生对第一个和第二个组件的并发调用
            if ((k % 2 == 0 && count1 > 0) || (k % 2 == 1 && count2 <= 0)) {
                //在偶数次数且第一个组件的并发还没有完成时继续产生并发
                //在奇数次数且第二个组件的并发已经完成时也继续产生并发
                exe.execute(new Thread() {
                    public void run() {
                        normal1.doBusiness(null);
                    }
                });
                count1--;
            } else {
                exe.execute(new Thread() {
                    public void run() {
                        normal2.doBusiness(null);
                    }
                });
                count2--;
            }
        }
    }

    public void testSeda() {
        final NormalComponent seda1 = new SedaComponent(sedaPoolSize1, WT1, ST1);
        final NormalComponent seda2 = new SedaComponent(sedaPoolSize2, WT2, ST2);
        System.out.println("SEDA 开始并发 ");
        start = System.currentTimeMillis();
        new CheckThread().start();
        long total = count1 + count2;
        for (int k = 0; k < total; k++) {
            if ((k % 2 == 0 && count1 > 0) || (k % 2 == 1 && count2 <= 0)) {
                seda1.doBusiness(null);
                count1--;
            } else {
                seda2.doBusiness(null);
                count2--;
            }
        }
    }

    public static void main(String args[]) {

        SedaTester tester = new SedaTester();
        Properties pros = new Properties();
        boolean useSEDA = true;
        try {
            pros.load(new FileInputStream("seda.properties"));
            tester.ST1 = Double.parseDouble(pros.getProperty("ST1"));
            tester.ST2 = Double.parseDouble(pros.getProperty("ST2"));
            tester.WT1 = Integer.parseInt(pros.getProperty("WT1"));
            tester.WT2 = Integer.parseInt(pros.getProperty("WT2"));
            tester.sedaPoolSize1 = Integer.parseInt(pros.getProperty("sedaPoolSize1"));
            tester.sedaPoolSize2 = Integer.parseInt(pros.getProperty("sedaPoolSize2"));
            tester.count1 = Integer.parseInt(pros.getProperty("count1"));
            tester.count2 = Integer.parseInt(pros.getProperty("count2"));
            useSEDA = Boolean.parseBoolean(pros.getProperty("useSEDA"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!useSEDA)
            tester.testNormal();
        else
            tester.testSeda();

    }
}
