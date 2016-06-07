package site.vici;

	import java.util.*;//引入类
	import java.awt.*;
	import java.awt.event.*;
	import javax.swing.*;
	class Move {//小蛇或食物位置(方位)移动
		int move_X;
		int move_Y;
		Move(int move_X, int move_Y) {//带参数构造方法进行初始化
			this.move_X = move_X;
			this.move_Y = move_Y;
		}
	}
	class MoveOperate extends Observable implements Runnable {//小蛇和食物移动操作的类
		public static final int LEFT = 1;//小蛇向左移动的标识
		public static final int UP = 2;//小蛇向上运动的标识
		public static final int RIGHT = 3;//小蛇向右运动的标识
		public static final int DOWN = 4;//小蛇向下运动的标识
		private boolean[][] isHave; //指示位置上是否有小蛇或食物
		private LinkedList snake = new LinkedList();//声明小蛇的双链表集合
		private Move aliment; //食物
		private int move_Direction = LEFT;//小蛇移动的方向向左
		private boolean running = false; // 运行状态
		private int timeSpace = 300; //时间间隔
		private double speedChange = 0.75; // 每次的速度变化率
		private boolean paused = false; // 暂停标志
		private int score = 0; //得分
		private int moveCount = 0; //吃到食物前移动的次数
		private int X;//横坐标
		private int Y;//纵坐标
		public MoveOperate(int X, int Y) {//带参数构造方法进行初始化
			this.X = X;
			this.Y = Y;
			resetGame();
		}
		public void run() {//实现Runnable接口必须实现的方法
			running = true;//标识设置为真
			while (running) {//根据标识进行循环
				try {
					Thread.sleep(timeSpace);//休眠0.3秒
				} catch (Exception e) {
					System.out.println("休眠出错："+e.getMessage());
					break;//跳出循环
				}
				if (!paused) {//小蛇正在移动时
					if (move()) {
						setChanged();//更新界面数据
						notifyObservers();
					} else {//弹出对话框显示游戏结束
						JOptionPane.showMessageDialog(null, "你失败了！",
								"Game Over", JOptionPane.INFORMATION_MESSAGE);
						break;//跳出循环
					}
				}
			}
			if (!running) {//小蛇暂停或停止
				JOptionPane.showMessageDialog(null, "你停止了游戏",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		public void resetGame() {//重置游戏
			move_Direction = MoveOperate.LEFT;//小蛇移动的方向向左
			timeSpace = 300;//时间间隔为0.3秒
			paused = false;//不暂停
			score = 0;
			moveCount = 0;//吃到食物前移动的次数为0
			isHave = new boolean[X][Y];//初始化小蛇活动的范围
			for (int i = 0; i < X; i++) {
				isHave[i] = new boolean[Y];
				Arrays.fill(isHave[i], false);//填充数据
			}
			int initLength = X > 20 ? 10 : X / 2;//初始化小蛇，如果横向位置超过20个，长度为10，否则为横向位置的一半
			snake.clear();
			int x = X / 2;//初始居中显示
			int y = Y / 2;
			for (int i = 0; i < initLength; i++) {
				snake.addLast(new Move(x, y));//添加蛇移动的位置
				isHave[x][y] = true;
				x++;
			}
			aliment = createAliment();//创建食物
			isHave[aliment.move_X][aliment.move_Y] = true;//在指定位置上有蛇或食物
		}
		public boolean move() {//蛇运行一步
			Move snakeHead = (Move) snake.getFirst();//获得蛇头的位置
			int headX = snakeHead.move_X;//获得蛇的横坐标
			int headY = snakeHead.move_Y;//获得蛇的纵坐标
			switch (move_Direction) {//分支循环判断蛇的运动
			case UP://向上移动
				headY--;//纵坐标减1
				break;//跳出支循环
			case DOWN://向下移动
				headY++;//纵坐标加1
				break;
			case LEFT://向左移动
				headX--;//横坐标减1
				break;
			case RIGHT://向上移动
				headX++;//横坐标加1
				break;
			}
			if ((0 <= headX && headX < X) && (0 <= headY && headY < Y)) {
				if (isHave[headX][headY]) {//如果指定位置有蛇或食物
					if (headX == aliment.move_X && headY == aliment.move_Y) {
						snake.addFirst(aliment);//蛇头增长表示吃到食物
						int scoreGet = (10000 - 200 * moveCount) / timeSpace;//分数与改变方向次数和速度有关
						score += scoreGet > 0 ? scoreGet : 10;
						moveCount = 0;
						aliment = createAliment(); //创建新的食物
						isHave[aliment.move_X][aliment.move_Y] = true;//设置食物所在位置
						return true;
					} else {
						return false;//小蛇吃到自己
					}
				} else {
					snake.addFirst(new Move(headX, headY));//添加小蛇的身体
					isHave[headX][headY] = true;
					snakeHead = (Move) snake.removeLast();//获得蛇头信息
					isHave[snakeHead.move_X][snakeHead.move_Y] = false;
					moveCount++;
					return true;
				}
			}
			return false;//碰到外壁失败
		}
		public void changeDirection(int dir) {//改变蛇运动的方向
			if (move_Direction % 2 != dir % 2) {//改变的方向不与原方向相同或相反
				move_Direction = dir;
			}
		}
		private Move createAliment() {// 创建食物
			int x = 0;
			int y = 0;
			do {
				Random r = new Random();//随机获取位置
				x = r.nextInt(X);
				y = r.nextInt(Y);
			} while (isHave[x][y]);
			return new Move(x, y);//返回食物的新位置
		}
		public void speedUp() {// 加速运行
			timeSpace *= speedChange;
		}
		public void speedDown() {// 减速运行
			timeSpace /= speedChange;
		}
		public void changePauseState() {// 改变暂停状态
			paused = !paused;
		}
		public boolean isRunning() {//判断是否运动
			return running;
		}
		public void setRunning(boolean running) {//设置运动标识
			this.running = running;
		}
		public LinkedList getMoveList() {//获得链表数据(蛇的身体)
			return snake;
		}
		public Move getAliment() {//获得食物
			return aliment;
		}
		public int getScore() {//获得得分
			return score;
		}
	}
	class SnakeFrame extends JFrame implements Observer {//贪吃蛇的视图，MVC中的View
		public static final int gridWidth = 10;//格子的宽度
		public static final int gridHeight = 10;//格子的高度
		private int gameWidth;//画面的宽度
		private int gameHeight;//画面的高度
		private int gameX = 0;//画面左上角横位置
		private int startY = 0;//画面左上角纵坐标
		JLabel score; //声明分数标签
		Canvas canvas; //声明画布
		public SnakeFrame() {//// 默认构造方法调用带参数的构造方法
			this(30, 40, 0, 0);
		}
		public SnakeFrame(int X, int Y) {// 带参数的构造方法进行初始化
			this(X, Y, 0, 0);
		}
		public SnakeFrame(int X, int Y, int startX, int startY) {// 带参数的构造方法进行初始化
			this.gameWidth = X * gridWidth;
			this.gameHeight = Y * gridHeight;
			this.gameX = startX;
			this.startY = startY;
			init();
		}
		private void init() {// 初始化游戏界面
			this.setTitle("贪吃的小蛇");//设置界面的标题
			this.setLocation(gameX, startY);//设置界面的位置(方位)
			Container cp = this.getContentPane();//获得一容器
			score = new JLabel("成绩：");
			cp.add(score, BorderLayout.SOUTH);//将成绩放在界面的南侧
			canvas = new Canvas();//创建中间的游戏显示区域
			canvas.setSize(gameWidth + 1, gameHeight + 1);//设置区域的大小
			cp.add(canvas, BorderLayout.CENTER);//区域放在界面的中间
			this.pack();//根据组件的最优尺寸来进行布局 
			this.setResizable(false);//窗体不能最小化
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认关闭操作
			this.setVisible(true);//可视
		}
		public void update(Observable observer, Object obj) {// 实现Observer接口定义的update方法
			MoveOperate operate = (MoveOperate)observer; //获取被监控的模型
			Graphics graphics = canvas.getGraphics();// 获得画笔
			graphics.setColor(Color.WHITE);//设置颜色为白色
			graphics.fillRect(0, 0, gameWidth, gameHeight);//填充一个矩形
			graphics.setColor(Color.BLACK);//重新设置颜色
			LinkedList list = operate.getMoveList();//获得蛇体
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Move move = (Move) it.next();
				drawMove(graphics, move);//调用方法画方格
			}
			graphics.setColor(Color.RED);//设置颜色表示食物
			Move move = operate.getAliment();//获得食物
			drawMove(graphics, move);
			this.updateScore(operate.getScore());//更新成绩
		}
		private void drawMove(Graphics g, Move n) {//根据移动路径画格子
			g.fillRect(n.move_X * gridWidth, n.move_Y * gridHeight, gridWidth - 1,
					gridHeight - 1);//填充一个矩形
		}
		public void updateScore(int score) {//更新成绩
			String s = "成绩： " + score;
			this.score.setText(s);//设置成绩标签的值
		}
	}
	class ControlSnake implements KeyListener {//控制蛇的运动
		private MoveOperate snake; //贪吃的小蛇模型
		private SnakeFrame frame; // 蛇的对象
		private int X;//蛇移动区域横坐标
		private int Y;//蛇移动区域纵坐标
		public ControlSnake() {// 默认构造方法进行初始化
			this.X = 30;
			this.Y = 40;
		}
		public ControlSnake(int X, int Y) {// 带参数的构造方法进行初始化
			this();
			if ((10 < X) && (X < 200) && (10 < Y) && (Y < 200)) {//判断移动区域
				this.X = X;
				this.Y = Y;
			} else {
				System.out.println("初始化参数出错！");
			}
			initSnake();//调用初始化方法
		}
		private void initSnake() {// 初始化
			this.snake = new MoveOperate(X, Y); //创建蛇模型
			this.frame = new SnakeFrame(X, Y, 500, 200);
			this.snake.addObserver(this.frame); //为模型添加对象
			this.frame.addKeyListener(this); // 添加键盘事件
			(new Thread(this.snake)).start();//启动线程蛇运动
		}
		public void keyPressed(KeyEvent e) {// 键盘按下事件
			int keyCode = e.getKeyCode();
			if (snake.isRunning()) {// 只有在贪吃蛇处于运行状态下，才处理的按键事件
				switch (keyCode) {
				case KeyEvent.VK_ADD://按下数字键盘上的+ 
				case KeyEvent.VK_PAGE_UP://按下PageUp键
					snake.speedUp();
					break;
				case KeyEvent.VK_SUBTRACT://按下数字键盘上的-
				case KeyEvent.VK_PAGE_DOWN://按下PageDown
					snake.speedDown();
					break;
				case KeyEvent.VK_SPACE://按下空格键
				case KeyEvent.VK_P://按下PauseBreak
					snake.changePauseState();
					break;
				case KeyEvent.VK_UP://按下向上键
					snake.changeDirection(MoveOperate.UP);
					break;
				case KeyEvent.VK_DOWN://按下向下键
					snake.changeDirection(MoveOperate.DOWN);
					break;
				case KeyEvent.VK_LEFT://按下向左键
					snake.changeDirection(MoveOperate.LEFT);
					break;
				case KeyEvent.VK_RIGHT://按下向右键
					snake.changeDirection(MoveOperate.RIGHT);
					break;
				default:
				}
			}
			if (keyCode == KeyEvent.VK_F || keyCode == KeyEvent.VK_J
					|| keyCode == KeyEvent.VK_ENTER) {//按下回车、F、J键开始游戏
				snake.resetGame();
			}
			if (keyCode == KeyEvent.VK_S) {//按下S键停止游戏
				snake.setRunning(false);
			}
		}
		public void keyReleased(KeyEvent e) {//键盘弹起事件
		}
		public void keyTyped(KeyEvent e) {//有字符被输入事件
		}
	}
	public class GreedySnake {//操作实现小蛇吃食物的操作
		public static void main(String[] args) {//java程序主入口处
			new ControlSnake(40,30);//实例化对象
		}
}

