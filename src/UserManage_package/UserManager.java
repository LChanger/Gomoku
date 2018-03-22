/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserManage_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class UserManager {
    public static String REG_SUCCESS="注册成功";
    public static String LOG_SUCCESS="登录成功";
    public static String LOG_FALSE="登录失败";
    public static String ERR_PASSWORD="密码错误";
    public static String REP_LOGID="登录账户已存在";
    public static String REP_USERNAME="用户名已存在";
    public static String ERR_LOGID="登录账户不存在";
//    public static String 
    public UserManager() {
        super();
    }
    public String register(User user){
        File f=new File("src/UserManage_package/user.txt");
        FileWriter fw=null;
        FileReader fr=null;
        BufferedReader br=null;
        String info=null;
        
        //判断输入是否合理
        if(!user.getUsername().matches("^[\\S]{1,9}$"))
            return "用户名包含非法字符并且长度为1-9位";
        if(!user.getLogId().matches("^[a-zA-Z_0-9]{6,11}$"))
            return "登陆账号只能包含字母、数字和下划线，长度为6-11位";
        if(!user.getPassword().matches("^[a-zA-Z_0-9]{6,16}$"))
            return "密码只能包含字母、数字和下划线，长度为6-16位";
        try {
            String line=null;
            fr=new FileReader(f);
            br=new BufferedReader(fr);
            
            while((line=br.readLine())!=null&&!line.equals("")){
                if(user.getLogId().equals(line.split(" ")[1]))
                    return REP_LOGID;
                if(user.getUsername().equals(line.split(" ")[0]))
                    return REP_USERNAME;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            info=user.getUsername()+" "+user.getLogId()+" "+user.getPassword()+" "+user.getScore();
            fw=new FileWriter(f,true);
            fw.write(info);
            fw.write("\r\n");
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return REG_SUCCESS;
    }
    public String login(User user){
        File f=new File("src/UserManage_package/user.txt");
        FileReader fr=null;
        BufferedReader br=null;
        boolean log_flag=false;//logid标识
        boolean pwd_flag=false;//密码标识
        String username=null;
        String score=null;
        
        try {
            String line=null;
            fr=new FileReader(f);
            br=new BufferedReader(fr);
            
            while((line=br.readLine())!=null&&!line.equals("")){
                String[] splits=line.split(" ");
                if(user.getLogId().equals(splits[1])){
                    log_flag=true;
                    if(user.getPassword().equals(splits[2])){
                        pwd_flag=true;
                        username=splits[0];
                        score=splits[3];
                    }
                }
                
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(!log_flag){
            return ERR_LOGID; 
        }else if(!pwd_flag){
            return ERR_PASSWORD;
        }
        return LOG_SUCCESS+" "+username+" "+score;
    }
    public User findUser(String logid){
        File f=new File("src/UserManage_package/user.txt");
        FileReader fr=null;
        BufferedReader br=null;
        
        try {
            String line=null;
            fr=new FileReader(f);
            br=new BufferedReader(fr);
            
            while((line=br.readLine())!=null&&!line.equals("")){
                String[] splits=line.split(" ");
                if(logid.equals(splits[1])){
                    return new User(splits[0], splits[1], splits[2],Integer.valueOf(splits[3]));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    public void update(User user){
        FileWriter fw=null;
        FileReader fr=null;
        BufferedReader br=null;
        StringBuilder sb=new StringBuilder();
        try {
            fr=new FileReader("src/UserManage_package/user.txt");
            br=new BufferedReader(fr);
            String line=null;
            while((line=br.readLine())!=null){
                sb.append(line+"\r\n");
            }
            System.out.println(sb);
            int pos=sb.indexOf(user.getLogId());
            if(pos==-1){
                System.out.println("未查找到!");
                return;
            }else{
                int beg=sb.lastIndexOf("\r\n", pos);
                int end=sb.indexOf("\r\n", pos);
                if(beg==-1){
                    beg=-2;
                }
                sb.replace(beg+2, end, user.getUsername()+" "+user.getLogId()+" "+
                        user.getPassword()+" "+user.getScore());
            } 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            fw=new FileWriter("src/UserManage_package/user.txt");
            fw.write(sb.toString());
            System.out.println("更新成功!"+user);
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void main(String[] args){
        UserManager um=new UserManager();
        um.update(new User("123", "niye96", "12345678", 800));
    }
}
