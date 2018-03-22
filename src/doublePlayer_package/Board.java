package doublePlayer_package;

import UserManage_package.User;
import UserManage_package.UserManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main_package.Point;
import main_package.SwingConsole;

public class Board extends JPanel implements MouseListener {

    public static final int MARGIN = 50;//边距
    public static final int GRID_SPAN = 35;//网格间距
    public static final int ROWS = 15;//行数
    public static final int COLS = 15;//列数
    public static final int POST = 7778;
    public static final String IP = "localhost";

    Point[] chessList = new Point[(ROWS + 1) * (COLS + 1)];//每个元素数组为null
    boolean isBlack = true;//黑棋先手
    boolean gameOver = false;
    int chessCount;//当前棋盘棋子数
    int x_index, y_index;//刚下的棋子位置

    Image img;
    Color colortemp;
    private boolean turn;//是否轮到你下棋
    private int color;//执棋颜色
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private String selfId;
    private String otherId;
    private boolean win;
    /*
    user_info信息格式
    INFO:
    selfid
    otherid
     */
    public Board(String user_info, int color) {
        String[] spilts = user_info.split("\n");
        selfId = spilts[0];
        otherId = spilts[1];
        this.color = color;//0代表黑棋、1代表白棋
        //初始化顺序
        if (color == 0) {
            turn = true;
        } else {
            turn = false;
        }
        win=false;
        //初始化背景图片
        img = Toolkit.getDefaultToolkit().getImage("src/images/Board.jpg");
        //添加鼠标事件
        addMouseListener(this);
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                int x1 = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
                int y1 = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
                //判断不能下的情况
                if (x1 < 0 || x1 >= ROWS || y1 < 0 || y1 >= COLS || gameOver || findChess(x1, y1) || !turn) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        });
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(IP, POST);
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());
            //向服务器发送自己的id与对方的id
            writer.writeObject(selfId + " " + otherId);
            new ReadInfo(reader, writer).start();
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //画背景图
        Graphics2D g2 = (Graphics2D) g;
        int img_width = img.getWidth(this);
        int img_height = img.getHeight(this);
        int frame_width = getWidth();
        int frame_height = getHeight();
        int y = (frame_height - img_height) / 2;
        int x = (frame_width - img_width) / 2;
        g.drawImage(img, x, y, null);
        //画棋盘
        g2.setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < ROWS; i++) {
            g2.drawLine(MARGIN, MARGIN + i * GRID_SPAN, MARGIN + (COLS - 1) * GRID_SPAN, MARGIN + i * GRID_SPAN);
        }
        for (int i = 0; i < COLS; i++) {
            g2.drawLine(MARGIN + i * GRID_SPAN, MARGIN, MARGIN + i * GRID_SPAN, MARGIN + (ROWS - 1) * GRID_SPAN);
        }

        //画棋子
        for (int i = 0; i < chessCount; i++) {
            int xPos = chessList[i].getX() * GRID_SPAN + MARGIN;
            int yPos = chessList[i].getY() * GRID_SPAN + MARGIN;
            g.setColor(chessList[i].getColor());
            colortemp = chessList[i].getColor();
            if (colortemp.equals(Color.black)) {
                RadialGradientPaint paint = new RadialGradientPaint(xPos - Point.DIAMETER / 2 + 25, yPos - Point.DIAMETER / 2 + 10, 20, new float[]{0f, 1f},
                        new Color[]{Color.WHITE, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            } else if (colortemp.equals(Color.white)) {
                RadialGradientPaint paint = new RadialGradientPaint(xPos - Point.DIAMETER / 2 + 25, yPos - Point.DIAMETER / 2 + 10, 70, new float[]{0f, 1f},
                        new Color[]{Color.WHITE, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            }
            Ellipse2D e = new Ellipse2D.Float(xPos - Point.DIAMETER / 2, yPos - Point.DIAMETER / 2, 34, 35);
            ((Graphics2D) g).fill(e);
            //标记最后一个棋子的红矩形框  

            if (i == chessCount - 1) {//如果是最后一个棋子  
                g.setColor(Color.red);
                g.drawRect(xPos - Point.DIAMETER / 2, yPos - Point.DIAMETER / 2,
                        34, 35);
            }
        }
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        if (gameOver) {
            return;
        }
        if (!turn) {
            return;
        }

        x_index = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
        y_index = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;

        if (x_index < 0 || x_index >= ROWS || y_index < 0 || y_index >= COLS) {
            return;
        }

        if (findChess(x_index, y_index)) {
            return;
        }

        Point ch = new Point(x_index, y_index, color == 0 ? Color.black : Color.white);
        chessList[chessCount++] = ch;
        try {
            //向服务器发送落子信息
            writer.writeObject("PUTCHESS:\n" + x_index + " " + y_index + " " + color);
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        repaint();

        if (isWin()) {
            String msg = String.format("%s获胜", color == 0 ? "黑棋" : "白棋");
            JOptionPane.showMessageDialog(this, msg);
            gameOver = true;
            //更新用户积分
            UserManager um = new UserManager();
            User user = um.findUser(selfId);
            user.updataScore(user.getScore() + 50);
            um.update(user);
        }
        isBlack = !isBlack;
        turn = false;
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub

    }

    private boolean findChess(int x, int y) {
        for (Point p : chessList) {
            if (p != null && p.getX() == x && p.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isWin() {
        int count = 1;

        //横向向西寻找  
        for (int x = x_index - 1; x >= 0; x--) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y_index, c) != null) {
                count++;
            } else {
                break;
            }
        }
        //横向向东寻找  
        for (int x = x_index + 1; x <= COLS; x++) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y_index, c) != null) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }

        //继续另一种搜索纵向  
        //向上搜索  
        for (int y = y_index - 1; y >= 0; y--) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x_index, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        //纵向向下寻找  
        for (int y = y_index + 1; y <= ROWS; y++) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x_index, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }

        //继续另一种情况的搜索：斜向  
        //东北寻找  
        for (int x = x_index + 1, y = y_index - 1; y >= 0 && x <= COLS; x++, y--) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        //西南寻找  
        for (int x = x_index - 1, y = y_index + 1; x >= 0 && y <= ROWS; x--, y++) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }

        //继续另一种情况的搜索：斜向  
        //西北寻找  
        for (int x = x_index - 1, y = y_index - 1; x >= 0 && y >= 0; x--, y--) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        //东南寻找  
        for (int x = x_index + 1, y = y_index + 1; x <= COLS && y <= ROWS; x++, y++) {
            Color c = isBlack ? Color.black : Color.white;
            if (getChess(x, y, c) != null) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        } else {
            count = 1;
        }
        return false;
    }

    private Point getChess(int xIndex, int yIndex, Color color) {
        for (Point p : chessList) {
            if (p != null && p.getX() == xIndex && p.getY() == yIndex
                    && p.getColor() == color) {
                return p;
            }
        }
        return null;
    }

    public void restart() {
        try {
            writer.writeObject("RESTART");
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void restartGame() {
        if (color == 0) {
            turn = true;
        } else {
            turn = false;
        }
        //清除棋子  
        for (int i = 0; i < chessList.length; i++) {
            chessList[i] = null;
        }
        //恢复游戏相关的变量值  
        isBlack = true;
        gameOver = false; //游戏是否结束  
        chessCount = 0; //当前棋盘棋子个数  
        repaint();
    }

    //悔棋  
    public void goback() {
        if (!gameOver) {
            if (chessCount == 0) {
                return;
            }
            if(turn)
                return;
            try {
                //向对方请求
                writer.writeObject("ROLLBACK");
            } catch (IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //矩形Dimension  
    public Dimension getPreferredSize() {
        return new Dimension(MARGIN * 2 + GRID_SPAN * COLS, MARGIN * 2
                + GRID_SPAN * ROWS);
    }

    public void exit() {
        if(!gameOver){
            try {
                writer.writeObject("EXIT");
            } catch (IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
            UserManager um = new UserManager();
            User user = um.findUser(selfId);
            user.updataScore(user.getScore() - 25);
            um.update(user);
        }else{
            try {
                writer.writeObject("OTHEREXIT");
            } catch (IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
//    public void save(String url) {
//        File f = new File(url);
//        OutputStream os = null;
//        ObjectOutputStream oos = null;
//        try {
//            os = new FileOutputStream(f);
//            oos = new ObjectOutputStream(os);
//            oos.writeObject(new Point(-1, -1));
//            int length = chessCount, i = 0;
//            while (i < chessCount) {
//                Point point = chessList[i++];
//                oos.writeObject(point);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                oos.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//    }
//
//    public void reload(String url) {
//        File f = new File(url);
//        InputStream is = null;
//        ObjectInputStream ois = null;
//        try {
//            is = new FileInputStream(f);
//            ois = new ObjectInputStream(is);
//            int i = 0;
//            ois.readObject();
//            while (true) {
//                Point p = (Point) ois.readObject();
//                chessList[i++] = p;
//                chessCount++;
//            }
//        } catch (EOFException e) {
//            System.err.println("已读取完毕");
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                ois.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            repaint();
//        }
//    }

    class ReadInfo extends Thread {

        private ObjectInputStream reader;
        private ObjectOutputStream writer;

        public ReadInfo(ObjectInputStream reader, ObjectOutputStream writer) {
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        public void run() {
            Object oj = null;
            try {
                while (true) {
                    oj = reader.readObject();
                    if (String.class.isInstance(oj)) {
                        String msg = (String) oj;
                        String[] spilts = msg.split("\n");
                        //如果接收到对方落子的信息
                        if (spilts[0].equals("PUTCHESS:")) {
                            String[] chess_info = spilts[1].split(" ");
                            Point ch = new Point(Integer.parseInt(chess_info[0]), Integer.parseInt(chess_info[1]),
                                    Integer.parseInt(chess_info[2]) == 0 ? Color.black : Color.white);
                            chessList[chessCount++] = ch;
                            x_index = ch.getX();
                            y_index = ch.getY();
                            //重画棋盘
                            repaint();
                            if (isWin()) {
                                String m = String.format("%s获胜", color == 1 ? "黑棋" : "白棋");
                                JOptionPane.showMessageDialog(Board.this, m);
                                gameOver = true;
                                UserManager um = new UserManager();
                                User user = um.findUser(selfId);
                                user.updataScore(user.getScore() - 25);
                                um.update(user);
                            }
                            //轮到自己下
                            turn = true;
                            isBlack = !isBlack;
                        } else if (spilts[0].equals("ROLLBACK")) {
                            int i = JOptionPane.showConfirmDialog(Board.this, "对方请求悔棋，是否同意？", "悔棋请求", JOptionPane.YES_NO_OPTION);
                            if (i == 0) {
                                writer.writeObject("ROLLBACK:YES");
                                chessList[chessCount - 1] = null;
                                chessCount--;
                                if (chessCount > 0) {
                                    x_index = chessList[chessCount - 1].getX();
                                    y_index = chessList[chessCount - 1].getY();
                                }
                                repaint();
                                turn = !turn;
                                isBlack=!isBlack;
                            } else {
                                writer.writeObject("ROLLBACK:NO");
                            }
                        } else if (spilts[0].equals("ROLLBACK:YES")) {
                            chessList[chessCount - 1] = null;
                            chessCount--;
                            if (chessCount > 0) {
                                x_index = chessList[chessCount - 1].getX();
                                y_index = chessList[chessCount - 1].getY();
                            }
                            repaint();
                            turn = !turn;
                            isBlack=!isBlack;
                        } else if (spilts[0].equals("ROLLBACK:NO")) {
                            JOptionPane.showMessageDialog(Board.this, "对方拒绝了您的请求！");
                        } else if (spilts[0].equals("RESTART")) {
                            int i = JOptionPane.showConfirmDialog(Board.this, "对方请求重开一局，是否同意？", "重开请求", JOptionPane.YES_NO_OPTION);
                            if (i == 0) {
                                writer.writeObject("RESTART:YES");
                                restartGame();
                            } else {
                                writer.writeObject("RESTART:NO");
                            }
                        } else if (spilts[0].equals("RESTART:YES")) {
                            restartGame();
                        } else if (spilts[0].equals("RESTART:NO")) {
                            JOptionPane.showMessageDialog(Board.this, "对方拒绝了您的请求！");
                        } else if (spilts[0].equals("EXIT")) {
                            JOptionPane.showMessageDialog(Board.this, "对方退出了对局！");
                            gameOver=true;
                            UserManager um = new UserManager();
                            User user = um.findUser(selfId);
                            user.updataScore(user.getScore() + 50);
                            um.update(user);
                        }else if(spilts[0].equals("OTHEREXIT"))
                            JOptionPane.showMessageDialog(Board.this, "对方已经离开！");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args) {
//        Board board = new Board("老贾\n老王", 1);
//        JFrame frame = new JFrame();
//        frame.add(board);
//        SwingConsole.run(frame, 500, 400);
        Board board1 = new Board("老王\n老贾", 0);
        JFrame frame1 = new JFrame();
        frame1.add(board1);
        SwingConsole.run(frame1, 500, 400);
    }
}
