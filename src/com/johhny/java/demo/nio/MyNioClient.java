package com.johhny.java.demo.nio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

public class MyNioClient {

    private int BUFFERSIZE = 1024*10;
    private String CHARSET = "GBK";
    private Selector sel;

    public MyNioClient(int port) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);    // this operation need to be executed before socket.connnect, for OP_CONNECT event
        sc.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
        sel = Selector.open();
        sc.register(sel, SelectionKey.OP_CONNECT |SelectionKey.OP_READ);
    }

    public void startup() {
        System.out.println("Client start...");
        try {
            while (!Thread.interrupted()) {
                int keysCount = sel.select();
                System.out.println("Catched " + keysCount + " SelectionKeys");
                if (keysCount < 1) {
                    continue;
                }                
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    //printKeyInfo(key);
                    if (key.isConnectable()) {
                        System.out.println("Key isConnectable");
                        doConnect(key);
                    } else if (key.isValid() && key.isReadable()) {
                        System.out.println("Key isReadable");
                        doRead(key);
                    } else if (key.isValid() && key.isWritable()) {
                        System.out.println("Key isWritable");
                        doWrite(key);
                    }
                }
                selectedKeys.clear();
            }
            System.err.println("Program is interrupted.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client stop...");
        shutdown();
    }
    
    public void shutdown(){
        Set<SelectionKey> keys = sel.keys();
        for(SelectionKey key:keys){
            try {
                key.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            sel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printKeyInfo(SelectionKey key) {
        String keyStr = MessageFormat
                .format(
                        "IntOps:{0},ReadyOps:{1},isVal:{2},isAcc:{3},isCnn:{4},isRead:{5},isWrite:{6}",
                        key.interestOps(), key.readyOps(), key.isValid(), key
                                .isAcceptable(), key.isConnectable(), key
                                .isReadable(), key.isWritable());
        System.out.println(keyStr);
    }

    private void doConnect(SelectionKey key) {
        try {
            boolean flag = ((SocketChannel) key.channel()).finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("unregister OP_CONNECT from interestOps");
        key.interestOps(SelectionKey.OP_READ);
        key.attach(new LinkedList<ByteBuffer>());
        new Thread(new UserInteractive(key)).start();
    }

    private void doRead(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.allocate(BUFFERSIZE);
            StringBuffer sb = new StringBuffer();
            while (sc.read(bb) > 0) {
                bb.flip(); 
                sb.append(Charset.forName(CHARSET).decode(bb));
                bb.flip();
            }
            System.out.println("message received from server:" + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(key);
            System.exit(1);
        }
        System.out.println("now end readMessage");
    }

    private void doWrite(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        LinkedList<ByteBuffer> outseq = (LinkedList<ByteBuffer>) key
                .attachment();
        ByteBuffer bb = outseq.poll();
        if(bb == null){
            return;
        }
        try {
            while(bb.hasRemaining()){
                sc.write(bb);
            }            
        } catch (IOException e) {
            disconnect(key);
            e.printStackTrace();
        }
        if (outseq.size() == 0) {
            System.out.println("after all buffers wrote, unregister OP_WRITE from interestOps");
            key.interestOps(SelectionKey.OP_READ);
        }
        System.out.println("end doWrote");
    }

    private void disconnect(SelectionKey key) {
        try {
            key.channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class UserInteractive implements Runnable {

        SelectionKey key;

        public UserInteractive(SelectionKey key) {
            this.key = key;
        }

        public void run() {
            LinkedList<ByteBuffer> outseq = (LinkedList<ByteBuffer>) key
                    .attachment();
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                     String inputLine = br.readLine();
                    if ("quit".equalsIgnoreCase(inputLine)) {
                        key.channel().close();
                        System.exit(1);
                        break;
                    }
                    ByteBuffer bb = ByteBuffer.allocate(BUFFERSIZE);
                    bb = ByteBuffer.wrap(inputLine.getBytes());
                    outseq.offer(bb);
                    System.out
                    .println("after input, register OP_WRITE to interestOps and wakeup selector");
                    key.interestOps(SelectionKey.OP_READ
                            | SelectionKey.OP_WRITE);
                    sel.wakeup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            MyNioClient client = new MyNioClient(10001);
            client.startup();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception caught, program exiting...");
        }
    }

}