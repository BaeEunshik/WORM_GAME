package kr.co.dmstlr90.main;

import java.util.ArrayList;
import java.util.Scanner;

import kr.co.dmstlr90.data.Food;
import kr.co.dmstlr90.data.InputKeyScanner;
import kr.co.dmstlr90.data.Keyboard;
import kr.co.dmstlr90.data.Move;
import kr.co.dmstlr90.data.Worm;

public class Main {
	
	private static int MAPHEIGHT = 20;	
	private static int MAPWIDTH = 30;	
	private static int INITNUM = -1;
	private static int WORMAREA = 1;
	private static int EMPTYAREA = 0;
	private static boolean PLAYING = true;	
	private static boolean ISCLEAR = false;	
	private static boolean ISDEAD = false;	
	
	public static void main(String[] args) {
		
		/************** initialize **************/
		int[][] map = new int[MAPHEIGHT][MAPWIDTH];	
		ArrayList<Worm> wormArr = new ArrayList<>(); 
		ArrayList<int[]> savePosArr = new ArrayList<>();
		
		// ���� �ӵ� 
		int sleepTime = 200;
		
		// ������ġ 
		Food food = new Food();
		food.setPosY(INITNUM);
		food.setPosX(INITNUM);
		
		// ������ 
		Worm wormHead = new Worm(0,0,"��");
		wormHead.setDirection(Move.RIGHT);
		wormArr.add(wormHead);
		/****************************************/			
		
		// Ű�Է� ������
		InputKeyScanner inputKey = new InputKeyScanner();
		inputKey.start();
		inputKey.setKey(new Keyboard() {
			
			// Ű�Է� ������ ��ü�� ������ - ������ ��ü�� ��� �������̽��� - main ���� ����
			@Override
			public void KeyEvent(String key) {
				
				if(!isReverse(wormHead, key)) {
					if (key.equals(Move.LEFT)) {
						wormHead.setDirection(Move.LEFT);
					} else if (key.equals(Move.RIGHT)) {
						wormHead.setDirection(Move.RIGHT);
					} else if (key.equals(Move.DOWN)) {
						wormHead.setDirection(Move.DOWN);
					} else if (key.equals(Move.UP)) {
						wormHead.setDirection(Move.UP);
					}
				}
				// timing bugFix
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		//���ӽ���
		while(PLAYING) {
			
			// ���� üũ
			meal(wormArr, food, map);
			
			// ���� Ŭ���� üũ
			clearCheck(wormArr);
			
			// ������ ������ (�ٽɷ���) 
			savePosition(wormArr, savePosArr);					
			moveHeadWithCrashCheck(wormArr, wormHead, map);		
			moveBody(wormArr, savePosArr, map);					
			
			// ȭ�����
			System.out.println();
			printMap(map, wormArr, food);
			System.out.println("a:left d:right w:up s:down");	
			System.out.print("������ ���� : " + wormArr.size());
			
			// ���� �÷��� ���� üũ
			if(!PLAYING) {
				inputKey.setRun(false);
			}
			if(ISCLEAR) {
				System.out.println("CREAR!!");
			}
			if(ISDEAD) {
				System.out.println("GAME_OVER");
			}
			
			// ���� �ִϸ��̼� ������
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ���� Ŭ���� üũ
	public static void clearCheck(ArrayList<Worm> wormArr) {
		if(wormArr.size() == ( MAPWIDTH * MAPHEIGHT )) {
			PLAYING = false;
			ISCLEAR = true;
		}
	}
	
	// ����üũ �Լ�
	public static void meal( ArrayList<Worm> wormArr, Food food, int[][] map) {
		int headY = wormArr.get(0).getY();
		int headX = wormArr.get(0).getX();
		// ������
		if(headY == food.getPosY() && headX == food.getPosX()) {
			makeFood(food, map);
			growUp(wormArr);
		}
		// ���̰� (��ġ��) ���� ���
		if( food.getPosY() == INITNUM || food.getPosX() == INITNUM) {
			makeFood(food, map);
		}
	}
	
	// ������ ���� �Լ�
	public static void growUp(ArrayList<Worm> wormArr) {
		Worm tmpTail = wormArr.get(wormArr.size()-1);		
		int y = tmpTail.getY();
		int x = tmpTail.getX();
		String direction = tmpTail.getDirection();			
		
		if(direction == Move.UP) {							
			y+=1;
		}else if(direction == Move.DOWN) {
			y-=1;
		}else if(direction == Move.LEFT) {
			x+=1;
		}else if(direction == Move.RIGHT) {
			x-=1;
		}
		wormArr.add(new Worm(y,x,"��"));
	}
	
	// ���� ���� �Լ�
	public static void makeFood(Food food, int[][] map) {
		ArrayList<int[]> tmpArr = new ArrayList<>();

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if(map[i][j] == 0) {
					int[] tmpPosition = {i,j}; // map �� value �� 0�� ���� ��ǥ�� ��´�.
					tmpArr.add(tmpPosition);
				}
			}
		}
		int[] feedPos = tmpArr.get((int)(Math.random()*tmpArr.size()));	// ���� ��ǥ�� �ϳ� ����
		food.setPosY(feedPos[0]);	// ����
		food.setPosX(feedPos[1]);
	}
	
	// �� �浹 üũ �Լ�
	public static void moveHeadWithCrashCheck(ArrayList<Worm> wormArr, Worm wormHead, int[][] map) {
		wormHead.move();
		if(!isWall(wormHead) && !isBody(wormArr)) {
			
			map[wormHead.getY()][wormHead.getX()] = WORMAREA;
			
		} else {
			PLAYING = false;
			ISDEAD = true;
		}
	}
	
	// �� �浹 üũ �Լ�
	public static boolean isWall(Worm wormHead) {
		int headY = wormHead.getY();
		int headX = wormHead.getX();
		if(headY >= MAPHEIGHT || headY < 0 || headX >= MAPWIDTH || headX < 0) {
			return true;
		}
		return false;
	}
	
	// �Ӹ�-�� �浹 üũ �Լ�
	public static boolean isBody(ArrayList<Worm> wormArr) {
		int headY = wormArr.get(0).getY();
		int headX = wormArr.get(0).getX();
		int bodyY;
		int bodyX;
		
		for (int i = 1; i < wormArr.size(); i++) {
			bodyY = wormArr.get(i).getY();
			bodyX = wormArr.get(i).getX();
			if(headY == bodyY && headX == bodyX) {
				return true;
			}
		}
		return false;
	}
	
	// �ݴ������ �Է��ߴ��� üũ �Լ�
	public static boolean isReverse(Worm wormHead, String changeDir) {
		String postDir = wormHead.getDirection();
		if (postDir.equals(Move.UP) && changeDir.equals(Move.DOWN)){
			return true;
		} else if (postDir.equals(Move.DOWN) && changeDir.equals(Move.UP)){
			return true;
		} else if (postDir.equals(Move.LEFT) && changeDir.equals(Move.RIGHT)){
			return true;
		} else if (postDir.equals(Move.RIGHT) && changeDir.equals(Move.LEFT)){
			return true;
		}
		return false;
	}
	
	// ���� ��ġ ���� �Լ�
	public static void savePosition(ArrayList<Worm> wormArr, ArrayList<int[]> savePosArr) {
		//��ġ�迭 �ʱ�ȭ
		savePosArr.clear();
		//��ġ�迭 ���Ҵ�
		for (int i = 0; i < wormArr.size(); i++) {
			int wormY = wormArr.get(i).getY();
			int wormX = wormArr.get(i).getX();
			int[] wormPosition = {wormY, wormX}; 
			savePosArr.add(i,wormPosition);
		}
	}
	
	// ��(�Ӹ�����) �̵� �Լ�
	public static void moveBody(ArrayList<Worm> wormArr, ArrayList<int[]> savePosArr, int[][] map) {
		// ����
		int deleteY = wormArr.get(wormArr.size()-1).getY();	
		int deleteX = wormArr.get(wormArr.size()-1).getX();
		
		// ������ ARR �� �Ӹ� ���� ���� �ִٸ�
		if(wormArr.size() > 1) {
			// �� �󿡼� ������ġ VAL : 0
			map[deleteY][deleteX] = EMPTYAREA;
		}
		
		//������ �̵� 
		Worm tmpWorm;
		int postY, postX, y, x;
		for (int i = 1; i < wormArr.size(); i++) {	// �Ӹ��� ������ ������ �迭��ŭ ����
			
			// �� ��ġ
			tmpWorm = wormArr.get(i);
			postY = tmpWorm.getY();	
			postX = tmpWorm.getX();
			
			// �ϳ��� ���
			y = savePosArr.get(i-1)[0];
			x = savePosArr.get(i-1)[1];
			
			// �ǽð� ������ ���� üũ
			if(postY > y) {
				tmpWorm.setDirection(Move.UP);
			}else if(postY < y) {
				tmpWorm.setDirection(Move.DOWN);
			}else if (postX > x) {
				tmpWorm.setDirection(Move.LEFT);
			}else if(postX < x) {
				tmpWorm.setDirection(Move.RIGHT);
			}
			
			tmpWorm.setY(y);
			tmpWorm.setX(x);
			
			// map�� ���� ��ġ ǥ��
			map[y][x] = WORMAREA;
		}
	}
	
	// ȭ�� ��� �Լ�
	public static void printMap(int[][] map, ArrayList<Worm> wormArr, Food food) {
		
		System.out.print("��");
		for (int i = 0; i < MAPWIDTH; i++) {
			System.out.print("��");
		}
		System.out.println("��");

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				
				//���� ��
				if(j == 0) {
					System.out.print("��");
				}
				boolean isWorm = false;
				for (int k = 0; k < wormArr.size(); k++) {
					if (wormArr.get(k).getY() == i && wormArr.get(k).getX() == j) {
						System.out.print(wormArr.get(k).getCh());
						isWorm = true;
					}
				}
				if(!isWorm) {
					if(food.getPosY() == i && food.getPosX() == j){
						System.out.print("��");
					} else {
						System.out.print(" ");
					}
				}
				//���� ��
				if(j == map[i].length-1) {
					System.out.print("��");
				}
			}
			System.out.println();
		}
		System.out.print("��");
		for (int i = 0; i < MAPWIDTH; i++) {
			System.out.print("��");
		}
		System.out.println("��");
	}
} 
