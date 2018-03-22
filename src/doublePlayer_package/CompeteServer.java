/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doublePlayer_package;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class CompeteServer {

    private int port;
    private ServerSocket server;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private HashMap<String, Socket> hash;
    private HashMap<String, ObjectInputStream> inputs;
    private HashMap<String, ObjectOutputStream> outs;

    public CompeteServer(int port) {
        this.port = port;
        hash = new HashMap<String, Socket>();
        inputs = new HashMap<String, ObjectInputStream>();
        outs = new HashMap<String, ObjectOutputStream>();
    }

    public void startServer() {
        Socket socket;
        try {
            server = new ServerSocket(port);
            while (true) {
                socket = server.accept();
                reader = new ObjectInputStream(socket.getInputStream());
                writer = new ObjectOutputStream(socket.getOutputStream());
                String oj = (String) reader.readObject();
                System.out.println(oj);
                String[] info = oj.split(" ");
                hash.put(info[0], socket);
                inputs.put(info[0], reader);
                outs.put(info[0], writer);
                new ReadInfo(info[0], info[1]).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(CompeteServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompeteServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class ReadInfo extends Thread {

        private ObjectInputStream self;
        private ObjectOutputStream other;
        private String otherId;
        private String selfId;
        public ReadInfo(String selfId, String otherId) {
            this.selfId = selfId;
            this.otherId = otherId;
            self=inputs.get(selfId);
        }

        @Override
        public void run() {
            Object oj = null;
            try {
                while (true) {
                    oj = self.readObject();
                    if(String.class.isInstance(oj)){
                        String m=(String)oj;
                        if(m.equals("EXIT")||m.equals("OTHEREXIT")){
                            System.out.println(selfId+"退出");
                            hash.remove(selfId);
                            inputs.remove(selfId);
                            outs.remove(selfId);
                        }
                        other=outs.get(otherId);
                        if(other==null){
                            other=outs.get(selfId);
                            if(other!=null)
                                other.writeObject("OTHEREXIT");
                        }else
                            other.writeObject(oj);
                    }
                }
            } catch (IOException ex) {
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CompeteServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public static void main(String[] args) {
        CompeteServer c = new CompeteServer(7778);
        c.startServer();
    }
}
