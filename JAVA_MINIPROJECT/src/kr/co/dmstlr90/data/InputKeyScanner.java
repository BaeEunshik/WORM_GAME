package kr.co.dmstlr90.data;

import java.util.Scanner;

// Ű�Է¿� ���� Ŭ����
// ��ĳ��, 
public class InputKeyScanner extends Thread {

	Scanner scanner = new Scanner(System.in);
	private boolean run = true;
	private Keyboard key = null;
	
	@Override
	public void run() {
		while(run) {
			String direction = scanner.nextLine();
			key.KeyEvent(direction);
		}
	}
	
	public Scanner getScanner() {
		return scanner;
	}
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}
	public Keyboard getKey() {
		return key;
	}
	public void setKey(Keyboard key) {
		this.key = key;
	}
	
	
}
