package com.johhny.java.demo.seda;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * SEDA���ܲ���
 * 
 * @authorzhanghan
 *
 * @version $Id$
 */
public class SedaTester {
    /**
     * ������ʼʱ��
     */
    public long start  = 0;
    /**
     * ��������ʱ��
     */
    public long end    = 0;

    /**
     * ĳ��ʱ�̵Ĳ�������ʱ��
     */
    public Long useSum = new Long(0);

    /**
     * ������ͨ�̳߳ص����
     * @author zhanghan 
     */
    class NormalComponent {

        /**
         * wait time �߳�����ʱ�䣨ģ����ʵ�ʵ��������������������ȴ���ʱ�䣩
         */
        int    wt;
        /**
         * service time �̷߳���ʱ��(ģ��������Ļ�Ծ״̬���̶߳�CPU��ʹ��ʱ��)
         */
        double st;

        public NormalComponent(int wt, double st) {
            this.wt = wt;
            this.st = st;
        }

        /**
         * �����ⲿ���õ�ҵ�񷽷�������ҵ���߼���������
         * @param msg
         */
        protected void doBusiness(String msg) {
            process();//����ҵ���߼�
        }

        /**
         * ҵ���߼��ľ��崦��
         */
        final protected void process() {
            try {
                //do something with the msg for the business
                keepCPUBusy(); //CPU����ʱ�� st ���룬�˴�ʡ�Ծ���ҵ���߼���ʹ��st��CPU����������
                Thread.sleep(this.wt); //����ʱ�� wt ����, �˴���sleep��ģ���߳������������õĵȴ�����
                markTime();//��¼����ʱ��
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * ���㵱ǰ���ĵ�ʱ�䣬����¼��useSum������
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
         * �˷��������Լst��CPU���㣬����ģ������ڷ������Ļ�Ծ״̬�¶�CPU��ʹ��
         */
        final protected void keepCPUBusy() {
            int defaultCount = 5000000;//5000000�ε�ѭ����������15ms��CPU����
            defaultCount = (int) (defaultCount * (st / 15));
            long sum = 0;
            for (int k = 0; k < defaultCount; k++) {
                sum += k;
            }
        }

    }

    /**
     * SEDA�����ģ����SEDA���̳߳غ��¼�����ģ��
     * @author zhanghan
     * @version $Id: SedaTester.java,v 0.1 2009-7-23 ����08:51:20 zhanghan Exp $
     */
    class SedaComponent extends NormalComponent {
        /**
         * �̳߳�
         */
        List<Thread>          threads    = new ArrayList<Thread>();
        /**
         * �¼�����
         */
        LinkedList<SedaEvent> eventQueue = new LinkedList<SedaEvent>();
        /**
         * �̳߳ش�С
         */
        int                   poolSize   = 100;

        public SedaComponent(int size, int wt, double st) {
            super(wt, st);
            this.poolSize = size;
            this.initPool();
        }

        /**
         * ��ʼ���̳߳�
         */
        private void initPool() {
            for (int k = 0; k < poolSize; k++) {
                SedaThread st = new SedaThread();
                threads.add(st);
                st.start();
            }
        }

        /**
         * ���ظ����doBusiness �����ⲿ���õ�ҵ�񷽷�������ҵ���߼���������
         * @param msg
         * @see SedaTester.NormalComponent#doBusiness(java.lang.String)
         */
        protected void doBusiness(String msg) {
            //�ⲿ���õ��̵߳��ô˷�����һ���µ��¼������������������
            synchronized (eventQueue) {
                //����ͨ��ͬ�������ķ�ʽʵ���¼��������̳߳�֮���Э��
                eventQueue.addLast(new SedaEvent(msg, this));
                eventQueue.notify();
            }

        }

        /**
         * �̳߳��е��̣߳�ͨ��ͬ�������ķ�ʽ���¼�����Э��
         * @author zhanghan
         * @version $Id: SedaTester.java,v 0.1 2009-7-23 ����08:58:06 zhanghan Exp $
         */
        class SedaThread extends Thread {
            /**
             * �Ƿ񱣳ֻ�Ծ״̬
             */
            boolean live = true;

            public void run() {
                while (isLive()) {
                    SedaEvent event = null;
                    synchronized (eventQueue) {
                        while (eventQueue.isEmpty()) {
                            try {
                                eventQueue.wait();//����Ϊ��ʱ�ȴ�ֱ�����µ��¼�
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        event = eventQueue.removeFirst();//�Ӷ����������ȳ���ȡ���¼�
                    }
                    if (event != null) {
                        //֪ͨ�¼����������ʼ����ҵ���߼�
                        event.notifyBusiness();
                    }
                }
            }

            public boolean isLive() {
                return live;
            }

            /**
             * �����߳�
             */
            public void kill() {
                this.live = false;
            }
        }

        /**
         * �¼������е�event�������¼�Ԫ���ݣ�ʵ�����֪ͨ
         * @author zhanghan
         * @version $Id: SedaTester.java,v 0.1 2009-7-23 ����09:00:18 zhanghan Exp $
         */
        class SedaEvent {
            String        msg;
            SedaComponent component;

            public SedaEvent(String msg, SedaComponent component) {
                this.msg = msg;
                this.component = component;
            }

            /**
             * ֪ͨ�������ҵ����
             */
            public void notifyBusiness() {
                component.process();//����ҵ���߼� 
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
     * ���ػ��߳�ÿ��1���м���һ�β���������ʱ��
     * @author zhanghan
     * @version $Id: SedaTester.java,v 0.1 2009-7-23 ����09:01:13 zhanghan Exp $
     */
    class CheckThread extends Thread {
        boolean live = true;

        public CheckThread() {
            this.setDaemon(true);
        }

        public void run() {
            while (isLive()) {
                synchronized (useSum) {
                    System.out.println("��ǰ��ʱ " + useSum + " ms");
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
     * ��һ�������Service Time
     */
    public double ST1           = 0;
    /**
     * ��һ�������Wait Time
     */
    public int    WT1           = 3;
    /**
     * �ڶ��������Service Time
     */
    public double ST2           = 0;
    /**
     * �ڶ��������Wait Time
     */
    public int    WT2           = 100;

    /**
     * ��һ��SEDA������̳߳ش�С
     */
    public int    sedaPoolSize1 = 38;
    /**
     * �ڶ���SEDA������̳߳ش�С
     */
    public int    sedaPoolSize2 = 962;
    /**
     * ��һ��������õĲ�����
     */
    public long   count1        = 5000;
    /**
     *  �ڶ���������õĲ�����
     */
    public long   count2        = 5000;

    /**
     * ��ͨ�̳߳صĲ���
     */
    public void testNormal() {

        final NormalComponent normal1 = new NormalComponent(WT1, ST1);
        final NormalComponent normal2 = new NormalComponent(WT2, ST2);
        System.out.println("Normal ��ʼ���� ");
        start = System.currentTimeMillis();
        new CheckThread().start();//��������ػ��߳�
        Executor exe = Executors.newScheduledThreadPool(sedaPoolSize1 + sedaPoolSize2);//�����̳߳������ܲ�������
        long total = count1 + count2;
        for (int k = 0; k < total; k++) {
            //Ϊ�˹�ƽ��������������Ե�һ���͵ڶ�������Ĳ�������
            if ((k % 2 == 0 && count1 > 0) || (k % 2 == 1 && count2 <= 0)) {
                //��ż�������ҵ�һ������Ĳ�����û�����ʱ������������
                //�����������ҵڶ�������Ĳ����Ѿ����ʱҲ������������
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
        System.out.println("SEDA ��ʼ���� ");
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
