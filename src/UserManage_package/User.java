/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserManage_package;

import doublePlayer_package.Message;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class User implements Serializable{
    private String username;
    private String logId;
    private String password;
    private int score;
    public User(String logId,String password){
        this.logId=logId;
        this.password=password;
    }
    public User(String username, String logId, String password) {
        this.username = username;
        this.logId = logId;
        this.password = password;
        this.score = 0;
    }
    public User(String username,String logId,String password,int score){
        this.username = username;
        this.logId = logId;
        this.password = password;
        this.score=score;
    }
    public String getUsername(){
        return this.username;
    }
    public String getLogId(){
        return this.logId;
    }
    public String getPassword(){
        return this.password;
    }
    public int getScore(){
        return this.score;
    }
    public void updataScore(int score){
        this.score=score;
    }

    @Override
    public String toString() {
        return getUsername()+" "+getLogId()+" "+getPassword()+" "+getScore(); //To change body of generated methods, choose Tools | Templates.
    }
    public static void main(String[] args){
        try {
            ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("src/User_Manage/1.txt"));
            out.writeObject(new String("我是老贾"));
            out.writeObject(new Message("niye", "老贾", "老贾我干你", "2017-3-2"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            ObjectInputStream in=new ObjectInputStream(new FileInputStream("src/User_Manage/1.txt"));
            while(true){
                Object oj=in.readObject();
                if(String.class.isInstance(oj)){
                    oj=(String)oj;
                    System.out.println(oj);
                }else if(Message.class.isInstance(oj)){
                    Message msg=(Message)oj;
                    System.out.println(msg.getTo()+" "+msg.getMsg());
                }
            }
        }catch(EOFException e){ 
            
        }catch (FileNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date date=new Date();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(new Date().getTime()-date.getTime());
        String s="UPDATE:\n倪烨 123 456 789\n老贾 2678 3789 389";
        String[] spilts=s.split("\n");
        for(int i=0;i<spilts.length;i++){
            System.out.println(spilts[i]);
        }
    }
}
