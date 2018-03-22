package AI_package;

import UserManage_package.User;
import UserManage_package.UserManager;
import doublePlayer_package.Board;
import main_package.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
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
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AIBoard extends JPanel implements MouseListener, MouseMotionListener {

    public static final int ROWS = 15;
    public static final int COLUMNS = 15;
    public static final int MARGIN = 50;//边距
    public static final int GRID_SPAN = 35;//网格间距
    private ComputerPlayer cm;
    private int AIColor;
    private int depth;
    private int flag;
    private Image img;
    private User user;
    private boolean isEnd;
    public AIBoard(int depth, int AIColor, User user) {
        this.user = user;
        isEnd=false;
        img = Toolkit.getDefaultToolkit().getImage("src/images/Board.jpg");
        flag = 1;
        this.depth = depth;
        this.AIColor = AIColor;
        cm = new ComputerPlayer(ROWS, COLUMNS);
        addMouseListener(this);
        addMouseMotionListener(this);
        System.out.println(getWidth() + " " + getHeight());
    }

    public void AIFirst() {
        if (AIColor == ComputerPlayer.MAX) {
            EvaluetePoint best = cm.max(depth, ComputerPlayer.INFINITY);
            cm.put(best.getX(), best.getY(), ComputerPlayer.MAX);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int img_width = img.getWidth(this);
        int img_height = img.getHeight(this);
        int panel_width = getWidth();
        int panel_height = getHeight();
        int y = (panel_height - img_height) / 2;
        int x = (panel_width - img_width) / 2;
        g.drawImage(img, x, y, null);

        //画棋盘
        g2.setStroke(new BasicStroke((float) 1.5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < ROWS; i++) {
            g2.drawLine(MARGIN, MARGIN + i * GRID_SPAN, MARGIN + (COLUMNS - 1) * GRID_SPAN, MARGIN + i * GRID_SPAN);
        }
        for (int i = 0; i < COLUMNS; i++) {
            g2.drawLine(MARGIN + i * GRID_SPAN, MARGIN, MARGIN + i * GRID_SPAN, MARGIN + (ROWS - 1) * GRID_SPAN);
        }

        //画棋子
        Stack<Point> stack = cm.getStack();
        if (flag == 1 && stack.size() == 0) {
            AIFirst();
            flag = 0;
        }
        for (int i = 0; i < stack.size(); i++) {
            Point p = stack.get(i);
            int xPos = p.getX() * GRID_SPAN + MARGIN;
            int yPos = p.getY() * GRID_SPAN + MARGIN;
            Color color = p.getType() == ComputerPlayer.MAX ? Color.BLACK : Color.WHITE;
            g.setColor(color);
            if (color == Color.BLACK) {
                RadialGradientPaint paint = new RadialGradientPaint(xPos - Point.DIAMETER / 2 + 25, yPos - Point.DIAMETER / 2 + 10, 20, new float[]{0f, 1f},
                        new Color[]{Color.WHITE, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            } else if (color == Color.WHITE) {
                RadialGradientPaint paint = new RadialGradientPaint(xPos - Point.DIAMETER / 2 + 25, yPos - Point.DIAMETER / 2 + 10, 70, new float[]{0f, 1f},
                        new Color[]{Color.WHITE, Color.BLACK});
                ((Graphics2D) g).setPaint(paint);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            }
            Ellipse2D e = new Ellipse2D.Float(xPos - Point.DIAMETER / 2, yPos - Point.DIAMETER / 2, 34, 35);
            ((Graphics2D) g).fill(e);
            //标记最后一个棋子的红矩形框  

            if (i == stack.size() - 1) {//如果是最后一个棋子  
                g.setColor(Color.red);
                g.drawRect(xPos - Point.DIAMETER / 2, yPos - Point.DIAMETER / 2, 34, 35);
            }
        }
    }

    public void rollback() {
        if (cm.getStack().size() >= 2) {
            cm.rollback();
            cm.rollback();
            repaint();
        }
    }

    public static void main(String[] args) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    private void judge(int type) {
        UserManager um = new UserManager();
        if(type==ComputerPlayer.MIN){
            if (cm.isMaxWin()) {
                JOptionPane.showMessageDialog(this, "你被AI打败了!");
                if (user.getScore() >= 25) {
                    user.updataScore(user.getScore() - 25);
                } else {
                    user.updataScore(0);
                }
                um.update(user);
                isEnd=true;
            }
            if (cm.isMinWin()) {
                JOptionPane.showMessageDialog(this, "恭喜你，你打败了AI!");
                user.updataScore(user.getScore() + 50);
                um.update(user);
                isEnd=true;
            }
        }else if(type==ComputerPlayer.MAX){
            if (cm.isMaxWin()) {
                JOptionPane.showMessageDialog(this, "恭喜你，你打败了AI!");
                user.updataScore(user.getScore() + 50);
                um.update(user);
                isEnd=true;
            }
            if (cm.isMinWin()) {
                JOptionPane.showMessageDialog(this, "你被AI打败了!");
                if (user.getScore() >= 25) {
                    user.updataScore(user.getScore() - 25);
                } else {
                    user.updataScore(0);
                }
                um.update(user);
                isEnd=true;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (!cm.isEnded()) {
            int x = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;
            int y = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;

            if (x < 0 || x >= ROWS || y < 0 || y >= COLUMNS || cm.findchess(x, y)) {
                return;
            }

            if (AIColor == ComputerPlayer.MAX) {
                cm.put(x, y, ComputerPlayer.MIN);
                repaint();
                double value=cm.evaluete(ComputerPlayer.MIN);
                if (value == ComputerPlayer.MAX_VALUE || value == ComputerPlayer.MIN_VALUE) {
                    judge(ComputerPlayer.MIN);
                    return;
                }
                EvaluetePoint point = cm.max(depth, ComputerPlayer.INFINITY);
                cm.put(point.getX(), point.getY(), ComputerPlayer.MAX);
                repaint();
                if (cm.isEnded()) {
                    JOptionPane.showMessageDialog(this, "你与AI打成平手!");
                }
                if (point.getValue() == ComputerPlayer.MAX_VALUE || point.getValue() == ComputerPlayer.MIN_VALUE) {
                    judge(ComputerPlayer.MIN);
                }
            } else if (AIColor == ComputerPlayer.MIN) {
                cm.put(x, y, ComputerPlayer.MAX);
                repaint();
                double value=cm.evaluete(ComputerPlayer.MAX);
                if (value == ComputerPlayer.MAX_VALUE || value == ComputerPlayer.MIN_VALUE) {
                    judge(ComputerPlayer.MAX);
                    return;
                }
                EvaluetePoint point = cm.min(depth, -ComputerPlayer.INFINITY);
                cm.put(point.getX(), point.getY(), ComputerPlayer.MIN);
                repaint();
                if (cm.isEnded()) {
                    JOptionPane.showMessageDialog(this, "你与AI打成平手!");
                }
                if (point.getValue() == ComputerPlayer.MAX_VALUE || point.getValue() == ComputerPlayer.MIN_VALUE) {
                    judge(ComputerPlayer.MAX);
                }
            }

        }
    }

    public void restart() {
        cm.restart();
        repaint();
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setAIColor(int type) {
        this.AIColor = type;
    }

    public void save(String url) {
        Stack<Point> stack = cm.getStack();
        File f = new File(url);
        OutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = new FileOutputStream(f);
            oos = new ObjectOutputStream(os);
            oos.writeObject(new Point(-2, -2, AIColor));
            for (int i = 0; i < stack.size(); i++) {
                Point point = stack.get(i);
                oos.writeObject(point);
            }
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void reload(String url) {
        flag = 0;
        File f = new File(url);
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = new FileInputStream(f);
            ois = new ObjectInputStream(is);
            ois.readObject();
            for (int i = 1;; i++) {
                Point p = (Point) ois.readObject();
                cm.put(p.getX(), p.getY(), p.getType());
            }
        } catch (EOFException e) {

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AIBoard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AIBoard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AIBoard.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(AIBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
            repaint();
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x1 = (int) (Math.floor(1.0 * (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN));
        int y1 = (int) (Math.floor(1.0 * (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN));
        //判断不能下的情况
        if (x1 < 0 || x1 >= ROWS || y1 < 0 || y1 >= COLUMNS || cm.isEnded() || cm.findchess(x1, y1)) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

    }
    public boolean getIsEnd(){
        return isEnd;
    }
    public void resetIsEnd(){
        isEnd=false;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
