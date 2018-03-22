/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_package;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Administrator
 */
public class MyFilefilter extends FileFilter{


        @Override
        public boolean accept(File f) {
            if(f.getName().toUpperCase().endsWith("CHESS")||f.isDirectory())
                return true;
            return false;
        }

        @Override
        public String getDescription() {
            return "chess 文件(*.chess)";
        }
}
