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
		
		// 게임 속도 
		int sleepTime = 200;
		
		// 먹이위치 
		Food food = new Food();
		food.setPosY(INITNUM);
		food.setPosX(INITNUM);
		
		// 지렁이 
		Worm wormHead = new Worm(0,0,"●");
		wormHead.setDirection(Move.RIGHT);
		wormArr.add(wormHead);
		/****************************************/			
		
		// 키입력 스레드
		InputKeyScanner inputKey = new InputKeyScanner();
		inputKey.start();
		inputKey.setKey(new Keyboard() {
			
			// 키입력 스레드 객체의 동작을 - 스레드 객체의 멤버 인터페이스로 - main 에서 결정
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
		
		//게임시작
		while(PLAYING) {
			
			// 먹이 체크
			meal(wormArr, food, map);
			
			// 게임 클리어 체크
			clearCheck(wormArr);
			
			// 지렁이 움직임 (핵심로직) 
			savePosition(wormArr, savePosArr);					
			moveHeadWithCrashCheck(wormArr, wormHead, map);		
			moveBody(wormArr, savePosArr, map);					
			
			// 화면출력
			System.out.println();
			printMap(map, wormArr, food);
			System.out.println("a:left d:right w:up s:down");	
			System.out.print("지렁이 길이 : " + wormArr.size());
			
			// 게임 플레이 상태 체크
			if(!PLAYING) {
				inputKey.setRun(false);
			}
			if(ISCLEAR) {
				System.out.println("CREAR!!");
			}
			if(ISDEAD) {
				System.out.println("GAME_OVER");
			}
			
			// 게임 애니메이션 스레드
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 게임 클리어 체크
	public static void clearCheck(ArrayList<Worm> wormArr) {
		if(wormArr.size() == ( MAPWIDTH * MAPHEIGHT )) {
			PLAYING = false;
			ISCLEAR = true;
		}
	}
	
	// 먹이체크 함수
	public static void meal( ArrayList<Worm> wormArr, Food food, int[][] map) {
		int headY = wormArr.get(0).getY();
		int headX = wormArr.get(0).getX();
		// 먹으면
		if(headY == food.getPosY() && headX == food.getPosX()) {
			makeFood(food, map);
			growUp(wormArr);
		}
		// 먹이가 (위치가) 없는 경우
		if( food.getPosY() == INITNUM || food.getPosX() == INITNUM) {
			makeFood(food, map);
		}
	}
	
	// 지렁이 성장 함수
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
		wormArr.add(new Worm(y,x,"●"));
	}
	
	// 먹이 생성 함수
	public static void makeFood(Food food, int[][] map) {
		ArrayList<int[]> tmpArr = new ArrayList<>();

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if(map[i][j] == 0) {
					int[] tmpPosition = {i,j}; // map 중 value 가 0인 곳의 좌표를 담는다.
					tmpArr.add(tmpPosition);
				}
			}
		}
		int[] feedPos = tmpArr.get((int)(Math.random()*tmpArr.size()));	// 담은 좌표중 하나 선택
		food.setPosY(feedPos[0]);	// 적용
		food.setPosX(feedPos[1]);
	}
	
	// 벽 충돌 체크 함수
	public static void moveHeadWithCrashCheck(ArrayList<Worm> wormArr, Worm wormHead, int[][] map) {
		wormHead.move();
		if(!isWall(wormHead) && !isBody(wormArr)) {
			
			map[wormHead.getY()][wormHead.getX()] = WORMAREA;
			
		} else {
			PLAYING = false;
			ISDEAD = true;
		}
	}
	
	// 벽 충돌 체크 함수
	public static boolean isWall(Worm wormHead) {
		int headY = wormHead.getY();
		int headX = wormHead.getX();
		if(headY >= MAPHEIGHT || headY < 0 || headX >= MAPWIDTH || headX < 0) {
			return true;
		}
		return false;
	}
	
	// 머리-몸 충돌 체크 함수
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
	
	// 반대방향을 입력했는지 체크 함수
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
	
	// 현재 위치 저장 함수
	public static void savePosition(ArrayList<Worm> wormArr, ArrayList<int[]> savePosArr) {
		//위치배열 초기화
		savePosArr.clear();
		//위치배열 재할당
		for (int i = 0; i < wormArr.size(); i++) {
			int wormY = wormArr.get(i).getY();
			int wormX = wormArr.get(i).getX();
			int[] wormPosition = {wormY, wormX}; 
			savePosArr.add(i,wormPosition);
		}
	}
	
	// 몸(머리제외) 이동 함수
	public static void moveBody(ArrayList<Worm> wormArr, ArrayList<int[]> savePosArr, int[][] map) {
		// 꼬리
		int deleteY = wormArr.get(wormArr.size()-1).getY();	
		int deleteX = wormArr.get(wormArr.size()-1).getX();
		
		// 지렁이 ARR 에 머리 외의 것이 있다면
		if(wormArr.size() > 1) {
			// 맵 상에서 꼬리위치 VAL : 0
			map[deleteY][deleteX] = EMPTYAREA;
		}
		
		//몸통의 이동 
		Worm tmpWorm;
		int postY, postX, y, x;
		for (int i = 1; i < wormArr.size(); i++) {	// 머리를 제외한 지렁이 배열만큼 돌며
			
			// 목 위치
			tmpWorm = wormArr.get(i);
			postY = tmpWorm.getY();	
			postX = tmpWorm.getX();
			
			// 하나씩 당김
			y = savePosArr.get(i-1)[0];
			x = savePosArr.get(i-1)[1];
			
			// 실시간 몸통의 방향 체크
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
			
			// map에 현재 위치 표시
			map[y][x] = WORMAREA;
		}
	}
	
	// 화면 출력 함수
	public static void printMap(int[][] map, ArrayList<Worm> wormArr, Food food) {
		
		System.out.print("┌");
		for (int i = 0; i < MAPWIDTH; i++) {
			System.out.print("─");
		}
		System.out.println("┐");

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				
				//좌측 벽
				if(j == 0) {
					System.out.print("│");
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
						System.out.print("○");
					} else {
						System.out.print(" ");
					}
				}
				//우측 벽
				if(j == map[i].length-1) {
					System.out.print("│");
				}
			}
			System.out.println();
		}
		System.out.print("└");
		for (int i = 0; i < MAPWIDTH; i++) {
			System.out.print("─");
		}
		System.out.println("┘");
	}
} 
