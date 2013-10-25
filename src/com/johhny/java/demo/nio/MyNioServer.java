package com.johhny.java.demo.nio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class MyNioServer {

    private int BUFFERSIZE = 1024*10;
    private String CHARSET = "GBK";
    private Selector sel;

    public MyNioServer(int port) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(
                new InetSocketAddress(InetAddress.getLocalHost(), port));
        sel = Selector.open();
        ssc.register(sel, SelectionKey.OP_ACCEPT);
    }

    public void startup() {
        System.out.println("Server start...");
        try {
            while (!Thread.interrupted()) {
                int keysCount = sel.select();
                System.out.println("Catched " + keysCount + " SelectionKeys");
                if (keysCount < 1) {
                    continue;
                }
                Set<SelectionKey> set = sel.selectedKeys();
                Iterator<SelectionKey> it = set.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        System.out.println("Key isAcceptable");
                        doAccept(key);
                    }
                    if (key.isValid() && key.isReadable()) {
                        System.out.println("Key isReadable");
                        doRead(key);
                    }
                    if (key.isValid() && key.isWritable()) {
                        System.out.println("Key isWritable");
                        doWrite(key);
                    }
                }
                set.clear();
            }
            System.err.println("Program is interrupted.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stop...");
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

    private void doAccept(SelectionKey key) {
        try {
            SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
            sc.configureBlocking(false);
            SelectionKey newkey = sc.register(sel, SelectionKey.OP_READ);
            newkey.attach(new LinkedList<ByteBuffer>());
            new Thread(new UserInteractive(newkey)).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to accept new client.");
        }
        System.out.println("end doAccept");
    }

    // TODO buffersize performance testing
    private void doRead(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.allocate(BUFFERSIZE);
            StringBuffer sb = new StringBuffer();
            int count = 0;
            while ( (count = sc.read(bb)) > 0) {
                bb.flip();
                sb.append(Charset.forName(CHARSET).decode(bb));
                bb.flip();
            }
            //if client disconnected, read return -1
            if(count == -1){
                System.out.println("client disconnected");
                disconnect(key);    
            } else {
                System.out.println("message received from client:" + sb.toString());
            }
        } catch (IOException e) {
            disconnect(key);
            e.printStackTrace();
        }
        System.out.println("end doRead");
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

    //TODO find out how to shutdown
    private class UserInteractive implements Runnable {

        SelectionKey key;

        public UserInteractive(SelectionKey key) {
            this.key = key;
        }

        public void run() {
            System.out.println("UserInteractive thread start...");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            while (true) {
                try {
                    String inputLine = br.readLine();
                    ByteBuffer bb = ByteBuffer.allocate(BUFFERSIZE);
                    bb = ByteBuffer.wrap(inputLine.getBytes());
                    ((LinkedList<ByteBuffer>) key.attachment()).offer(bb);
                    System.out
                            .println("after input, register OP_WRITE to interestOps and wakeup selector");
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    key.selector().wakeup();
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
            MyNioServer server = new MyNioServer(10001);
            server.startup();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception caught, program exiting…");
        }
    }
}
