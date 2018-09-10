package kr.co.dmstlr90.data;
// 지렁이 클래스 : 
// 	현재 방향 ( 좌우상하 )
// 	현재 위치 좌표 ( x, y ) 
//	지렁이 캐릭터 
public class Worm{
	private String direction;
	private int y;
	private int x;
	private String ch;
	
	public Worm(int y, int x, String ch) {
		this.y = y;
		this.x = x;
		this.ch = ch;
	}
	public void move() {
		if(this.direction.equals(Move.UP)) {
			this.up();
		}else if(this.direction.equals(Move.DOWN)) {
			this.down();
		}else if(this.direction.equals(Move.LEFT)) {
			this.left();
		}else if(this.direction.equals(Move.RIGHT)) {
			this.right();
		}
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getCh() {
		return ch;
	}
	public void setCh(String ch) {
		this.ch = ch;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void up() {
		this.y--;	
	}
	public void down() {
		this.y++;	
	}
	public void left() {
		this.x--;	
	}
	public void right() {
		this.x++;	
	}
}