package main_package;

 
import java.io.File;  
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;  
import javax.sound.sampled.AudioInputStream;  
import javax.sound.sampled.AudioSystem;  
import javax.sound.sampled.DataLine;  
import javax.sound.sampled.SourceDataLine;  
   
public class AudioFilePlayer extends Thread{  
	public boolean start=true;
	private boolean exit=false;
	private String url;
	public AudioFilePlayer(String url) {
		super();
		this.url=url;
	}
    public static void main(String[] args) {  
        final AudioFilePlayer player = new AudioFilePlayer ("E:/音乐2/BEYOND - 冷雨夜.mp3");  
        player.start();
        System.out.println(1);
        Scanner scanner=new Scanner(System.in);
        while(true){
	        int x=scanner.nextInt();
	        if(x==3){
				player.start=false;
	        }else if(x==4){
	        	player.start=true;
	        }else if(x==5){
	        	player.exit();
	        	
	        }
        }
    }  
    @Override
    public void run() {
    	while(!exit){
    		this.play(url);
    		if(Thread.currentThread().isInterrupted()){
    			System.out.println(exit);
    			return;
    			
    		}
    	}
    }
    public void exit(){
    	this.exit=true;
    }
    public void play(String filePath) {  
        final File file = new File(filePath);  
   
        try {  
            final AudioInputStream in = AudioSystem.getAudioInputStream(file);  
               
            final AudioFormat outFormat = getOutFormat(in.getFormat());  
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);  
   
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);  
   
            if (line != null) {  
                line.open(outFormat);  
                line.start();  
                stream(AudioSystem.getAudioInputStream(outFormat, in), line);  
                line.drain();  
                line.stop();  
            }  
   
        } catch (Exception e) {  
            throw new IllegalStateException(e);  
        }  
    }  
   
    private AudioFormat getOutFormat(AudioFormat inFormat) {  
        final int ch = inFormat.getChannels();  
        final float rate = inFormat.getSampleRate();  
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);  
    }  
   
    private void stream(AudioInputStream in, SourceDataLine line)  
        throws IOException {  
        final byte[] buffer = new byte[65536];  
        int flag=1;
        for (int n = 0; n != -1; ) { 
        	System.out.print("");
        	if(this.exit==true){
        		return;
        	}
        	if(!start){
        		if(flag==0){
        			System.out.println("停止");
        			flag=1;
        		}
        	}
        	else{
        		if(flag==1){
        			System.out.println("播放");
        			flag=0;
        		}
        		line.write(buffer, 0, n);  
        		n = in.read(buffer, 0, buffer.length);
        	}
        }  
    }  
}  
