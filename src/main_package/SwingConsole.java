package main_package;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SwingConsole {
	public static void run(final JFrame f,
			final int width,final int height){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				f.setTitle(f.getClass().getSimpleName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
			}
		});
	}
        public static void main(String[] args) {
            FileWriter fw=null;
            BufferedWriter bfw=null;
            try {
                fw=new FileWriter("src/learn/weight.txt");
                bfw=new BufferedWriter(fw);
                String temp="";
                for(int i=0;i<225;i++){
                    temp="";
                    for(int j=0;j<3;j++){
                        temp+="1 ";
                    }
                    temp+="1\r\n";
                    bfw.write(temp);
                }
            } catch (IOException ex) {
                Logger.getLogger(SwingConsole.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                try {
                    bfw.close();
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(SwingConsole.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
}
