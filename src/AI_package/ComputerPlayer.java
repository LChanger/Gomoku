package AI_package;

import main_package.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class ComputerPlayer {

    public static int INFINITY = 1000000000;
    public static int NONE = 0;
    public static int MAX = 1;
    public static int MIN = 2;
    //棋形得分
    public static int ONE = 1;
    public static int TWO = 50;
    public static int THREE = 300;
    public static int FOUR = 4000;
    public static int FIVE = 10000;
    public static double MAX_VALUE = 10000;
    public static double MIN_VALUE = -10000;
    //总的赢法数
    public static int WINS_METHODS = 572;
    //棋盘大小
    private int row;
    private int column;
    private Stack<Point> stack;// 栈，用于悔棋
    private boolean[][][] wins;// 赢法数组，记录了每一种赢法
    public int[][] chessBoard;// 当前棋盘，NONE代表空位，MAX代表AI，MIN代表对手
    private int count;// 记录一共有多少种赢法
    private boolean isEnded;// 是否结束标志
    //存放当前局势状况
    private MaxMin[] maxWin;
    private MaxMin[] minWin;
    private int[] minCount;
    private int[] maxCount;

    public ComputerPlayer(int row, int column) {
        this.row = row;
        this.column = column;
        isEnded = false;
        maxWin = new MaxMin[WINS_METHODS];
        minWin = new MaxMin[WINS_METHODS];
        stack = new Stack();
        wins = new boolean[row][column][WINS_METHODS];
        chessBoard = new int[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                chessBoard[i][j] = NONE;
            }
        }
        // 初始化wins赢法数组
        // 横向
        for (int i = 0; i < row; i++) {
            for (int j = 0; j <= column - 5; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[i][j + k][count] = true;
                }
                count++;
            }
        }

        // 纵向
        for (int i = 0; i <= row - 5; i++) {
            for (int j = 0; j < column; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j][count] = true;
                }
                count++;
            }
        }

        // 左斜
        for (int i = 0; i <= row - 5; i++) {
            for (int j = column - 1; j >= 4; j--) {
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j - k][count] = true;
                }
                count++;
            }
        }
        // 右斜
        for (int i = 0; i <= row - 5; i++) {
            for (int j = 0; j <= column - 5; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j + k][count] = true;
                }
                count++;
            }
        }
        
        for (int i = 0; i < count; i++) {
            maxWin[i] = new MaxMin();
            minWin[i] = new MaxMin();
        }
    }

    // 在第row行第colunm列下color颜色的棋子
    public void put(int row, int colunm, int color) {
        if (chessBoard[row][colunm] == NONE) {
            chessBoard[row][colunm] = color;
            stack.push(new Point(row, colunm, color));
            // 对赢法的情况进行更新
            for (int i = 0; i < count; i++) {
                if (wins[row][colunm][i] == true) {
                    if (color == MAX) {
                        maxWin[i].max++;
                        minWin[i].max++;
                    } else {
                        maxWin[i].min++;
                        minWin[i].min++;
                    }
                }
            }
            //如果棋盘满了，则结束
            if (stack.size() == this.row * this.column) {
                isEnded = true;
            }
        }
    }
    //落子回滚，用于悔棋与博弈树的递归
    public void rollback() {
        if (stack.size() > 0) {
            Point p = stack.pop();
            int x = p.getX();
            int y = p.getY();
            int color = p.getType();
            chessBoard[x][y] = NONE;
            //更新赢法状况
            for (int i = 0; i < count; i++) {
                if (wins[x][y][i] == true) {
                    if (color == MAX) {
                        maxWin[i].max--;
                        minWin[i].max--;
                    } else {
                        maxWin[i].min--;
                        minWin[i].min--;
                    }
                }
            }
            isEnded = false;
        }
    }

    // 判断此点是否合法
    private boolean isValid(int row, int column) {
        return row >= 0 && row < this.row && column >= 0 && column < this.column && chessBoard[row][column] == NONE;
    }

    // 获得一个点周围的点 返回一个list
    public ArrayList<Point> getNearPoints(Point P) {
        ArrayList<Point> points = new ArrayList();
        int row, column, x = P.getX(), y = P.getY();
        //在一个点周围半径为2范围内取点
        for (int i = -2; i <= 2; i++) {
            row = x + i;
            column = y + i;
            if (isValid(row, column)) {
                points.add(new Point(row, column));
            }

            row = x - i;
            column = y + i;
            if (isValid(row, column)) {
                points.add(new Point(row, column));
            }

            row = x;
            column = y + i;
            if (isValid(row, column)) {
                points.add(new Point(row, column));
            }

            row = x + i;
            column = y;
            if (isValid(row, column)) {
                points.add(new Point(row, column));
            }
        }
        return points;
    }

    // 筛选掉已下棋子的位置
    public ArrayList<Point> validPoints() {
        ArrayList<Point> points = new ArrayList();
        int centerRow = (row - 1) / 2;
        int centerColumn = (column - 1) / 2;
        int len = stack.size();
        // 如果黑棋下的不是中间位置或者AI先下棋
        if (len == 0 || (stack.size() == 1 && chessBoard[centerRow][centerColumn] == NONE)) {
            points.add(new Point(centerRow, centerColumn));
            return points;
        } else {
            if (len == 1) {
                Random random = new Random();
                int x = random.nextInt(3) - 1;
                int y = random.nextInt(3) - 1;
                while (x == 0 && y == 0) {
                    x = random.nextInt(3) - 1;
                    y = random.nextInt(3) - 1;
                }
                points.add(new Point(centerRow + x, centerColumn + y));
                return points;
            } else {
                // 用哈希表来判断是否有重复存在的点
                HashMap<String, Boolean> hash = new HashMap();
                for (int i = 0; i < stack.size(); i++) {
                    ArrayList<Point> nearpoints;
                    nearpoints = getNearPoints(stack.get(i));
                    for (Point np : nearpoints) {
                        if (!findchess(np.getX(), np.getY())) {
                            if (!hash.containsKey(np.getX() + " " + np.getY())) {
                                points.add(new Point(np.getX(), np.getY()));
                                hash.put(np.getX() + " " + np.getY(), true);
                            }
                        }
                    }
                }
                return points;
            }
        }
    }

    // 评估函数，分析当前棋盘局面，返回评估到的分数，turn为当前是谁落子
    public double evaluete(int turn) {
        double max, min;
        // 用两个数组记录当前场上黑白棋各自拥有的连子数情况
        // 索引0-4分别代表1-5颗连子
        maxCount = new int[5];
        minCount = new int[5];
        for (int i = 0; i < count; i++) {
            //如果5颗则返回最大值，代表已经胜利
            if (maxWin[i].max == 5 && maxWin[i].min == 0) {
                return MAX_VALUE;
            }
            if (minWin[i].min == 5 && minWin[i].max == 0) {
                return MIN_VALUE;
            }
            if (maxWin[i].max == 4 && maxWin[i].min == 0) {
                maxCount[3]++;
            }
            if (minWin[i].min == 4 && minWin[i].max == 0) {
                minCount[3]++;
            }
            if (maxWin[i].max == 3 && maxWin[i].min == 0) {
                maxCount[2]++;
            }
            if (minWin[i].min == 3 && minWin[i].max == 0) {
                minCount[2]++;
            }
            if (maxWin[i].max == 2 && maxWin[i].min == 0) {
                maxCount[1]++;
            }
            if (minWin[i].min == 2 && minWin[i].max == 0) {
                minCount[1]++;
            }
            if (maxWin[i].max == 1 && maxWin[i].min == 0) {
                maxCount[0]++;
            }
            if (minWin[i].min == 1 && minWin[i].max == 0) {
                minCount[0]++;
            }
        }

        int step = stack.size() - 1;
        max = maxCount[0] * ONE + maxCount[1] * TWO + maxCount[2] * THREE + maxCount[3] * FOUR;
        min = minCount[0] * ONE + minCount[1] * TWO + minCount[2] * THREE + minCount[3] * FOUR;
        //扩大当前方的优势
        if (turn == MAX) {
            max *= 3;
        }
        if (turn == MIN) {
            min *= 3;
        }
        return max - min;
    }
    //判断max是否胜利
    public boolean isMaxWin() {
        if (evaluete(MAX) == MAX_VALUE) {
            isEnded = true;
            return true;
        }
        return false;
    }
    //判断min是否胜利
    public boolean isMinWin() {
        if (evaluete(MIN) == MIN_VALUE) {
            isEnded = true;
            return true;
        }
        return false;
    }

    public boolean isEnded() {
        return isEnded;
    }
    //max方的博弈树递归算法
    public EvaluetePoint max(int depth, double beta) {
        int bestX = 0, bestY = 0;
        double alpha = -INFINITY;
        //如果到达最底层，则评估得分
        if (depth == 0) {
            alpha = evaluete(MAX);
            return new EvaluetePoint(alpha);
        } else {
            ArrayList<Point> validPoints = validPoints();
            //打乱有效落子点，防止棋型单一
            Collections.shuffle(validPoints);
            if (validPoints.size() > 0) {
                for (int i = 0; i < validPoints.size(); i++) {
                    Point p = validPoints.get(i);
                    put(p.getX(), p.getY(), MAX);
                    //如果胜利了，则停止循环，返回胜利落点
                    if (isMaxWin() == true) {
                        alpha = MAX_VALUE;
                        bestX = p.getX();
                        bestY = p.getY();
                        rollback();
                        break;
                    } else {
                        //进入博弈树下一层，预测对方的落子
                        EvaluetePoint temp = min(depth - 1, alpha);
                        rollback();
                        //如果点评估分数高，则更新
                        if (temp.getValue() > alpha) {
                            alpha = temp.getValue();
                            bestX = p.getX();
                            bestY = p.getY();
                        }
                        //Alpha-beta剪枝，去除没用的点
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
                return new EvaluetePoint(bestX, bestY, alpha);
            }
            return null;
        }
    }

    public EvaluetePoint min(int depth, double alpha) {
        int bestX = 0, bestY = 0;
        double beta = INFINITY;
        if (depth == 0) {
            beta = evaluete(MIN);
            return new EvaluetePoint(beta);
        } else {
            ArrayList<Point> validPoints = validPoints();
            Collections.shuffle(validPoints);
            if (validPoints.size() > 0) {
                for (int i = 0; i < validPoints.size(); i++) {
                    Point p = validPoints.get(i);
                    put(p.getX(), p.getY(), MIN);
                    if (isMinWin() == true) {
                        beta = MIN_VALUE;
                        bestX = p.getX();
                        bestY = p.getY();
                        rollback();
                        break;
                    } else {
                        EvaluetePoint temp = max(depth - 1, beta);
                        rollback();
                        if (temp.getValue() < beta) {
                            beta = temp.getValue();
                            bestX = p.getX();
                            bestY = p.getY();
                        }
                        if (alpha >= beta) {
                            break;
                        }
                    }
                }
                return new EvaluetePoint(bestX, bestY, beta);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        String temp = "";
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                temp = temp + chessBoard[i][j] + " ";
            }
            temp += "\n";
        }
        return temp;
    }

    public Stack<Point> getStack() {
        return stack;
    }

    public boolean findchess(int row, int col) {
        return chessBoard[row][col] != NONE;
    }
    //重置棋局状态，用于重开一局
    public void restart() {
        isEnded = false;
        stack.clear();
        for (int i = 0; i < count; i++) {
            maxWin[i] = new MaxMin();
            minWin[i] = new MaxMin();
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                chessBoard[i][j] = NONE;
            }
        }
    }
}
