/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doublePlayer_package;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class Message implements Serializable{
    private String to;
    private String from;
    private String msg;
    private String date;

    public Message(String to, String from, String msg, String date) {
        this.to = to;
        this.from = from;
        this.msg = msg;
        this.date = date;
    }
    
    public String getTo() {
	return to;
    }
    public String getFrom() {
	return from;
    }
    public String getMsg() {
	return msg;
    }
    public String getDate() {
	return date;
    }
    
}
