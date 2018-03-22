/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doublePlayer_package;

import UserManage_package.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class LobbyServer {

    private ServerSocket server;
    private int port;//端口号
    private ObjectInputStream read;
    private ObjectOutputStream writer;
    private HashMap<String, Socket> hash;//存放所有连入用户
    private HashMap<String,ObjectInputStream> inputs;
    private HashMap<String,ObjectOutputStream> outs;
    private HashMap<String, User> users;

    public LobbyServer(int port) {
        this.port = port;
        users = new HashMap<String, User>();
        hash = new HashMap<String, Socket>();
        inputs=new HashMap<String, ObjectInputStream>();
        outs=new HashMap<String, ObjectOutputStream>();
        startServer();
    }

    private void startServer() {
        Socket socket;
        try {
            server = new ServerSocket(port);
            while (true) {
                socket = server.accept();
                //接受链接用户信息
                read = new ObjectInputStream(socket.getInputStream());
                writer=new ObjectOutputStream(socket.getOutputStream());
                User user = (User) read.readObject();
                hash.put(user.getLogId(), socket);//将登录id作为key
                inputs.put(user.getLogId(), read);
                outs.put(user.getLogId(), writer);
                users.put(user.getLogId(), user);
                new ServerHandleInfo(writer, user.getLogId()).start();//创建处理用户的线程
                new ServerReadInfo(read, user.getLogId()).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(LobbyServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LobbyServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(server!=null)
                    server.close();
            } catch (IOException ex) {
                Logger.getLogger(LobbyServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        new LobbyServer(7777);
    }

    class ServerReadInfo extends Thread {

        private String logid;
        private ObjectInputStream reader;
        private ObjectOutputStream writer;
        
        public ServerReadInfo(ObjectInputStream reader, String logid) {
            this.reader = reader;
            this.logid = logid;
        }

        @Override
        public void run() {
            Object read = null;
            try {
                while(true){
                    read = reader.readObject();
                    //如果收到某客户端的发送信息请求
                    if(Message.class.isInstance(read)){
                        Message msg=(Message)read;
                        System.out.println(msg.getMsg());
                        //找到发送对象的socket
                        writer=outs.get(msg.getTo());
                        writer.writeObject(new Message(users.get(msg.getTo()).getUsername(), msg.getFrom(), msg.getMsg(), msg.getDate()));
                    }
                    if(String.class.isInstance(read)){
                        String msg=(String)read;
                        String[] spilts=msg.split("\n");
                        if(spilts[0].equals("CHALENGE:")){
                            writer=outs.get(spilts[3]);
                            writer.writeObject(msg);
                        }else if(spilts[0].equals("REFUSE:")||spilts[0].equals("ACCEPT:")){
                            writer=outs.get(spilts[1]);
                            writer.writeObject(msg);
                        }else if(spilts[0].equals("EXIT")){
                            throw new IOException();
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println(logid + " 退出2");
                hash.remove(logid);
                users.remove(logid);
                inputs.remove(logid);
                outs.remove(logid);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LobbyServer.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
//                try {
//                    if(reader!=null)
//                        reader.close();
//                    if(writer!=null)
//                        writer.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(LobbyServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }

    }

    class ServerHandleInfo extends Thread {

        private String logid;
        private ObjectOutputStream writer;
        private int temp_count;

        public ServerHandleInfo(ObjectOutputStream writer, String logid) {
            this.writer = writer;
            this.logid = logid;
            this.temp_count = 0;
        }

        @Override
        public void run() {
            //开始更新大厅人数表
            String send = null;
            try {
                while (true) {
                    //若发生变动则更新
                    System.out.print("");
                    if (users.size() != temp_count) {
                        temp_count = users.size();
                        send = "UPDATE:\n";
                        Iterator iter = users.entrySet().iterator();//开始遍历用户集合
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            User u = (User) entry.getValue();
                            send += u.getLogId() + " " + u.getUsername() + " " + u.getScore() + " " + score2level(u.getScore()) + "\n";
                        }
                        writer.writeObject(send);
                        writer.flush();
                    }
                }
            } catch (IOException ex) {

            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        private String score2level(int score) {
            if (score < 300) {
                return "低级";
            } else if (score < 700) {
                return "初级";
            } else if (score < 1200) {
                return "中级";
            } else {
                return "高级";
            }
        }

    }
}
