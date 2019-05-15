package randomPos;

import java.util.Random;

/**  
 *  
 * @author lichuang  
 * TODO  
 */

public class RandomPositions {

	public static void main(String[] args) {
		
		/*
		Random rnd1 = new Random();
		Random rnd2 = new Random();
		for (int i = 0; i < 10; i++) {
			System.out.print("{" + (rnd1.nextInt(100)+1) + ", " + (rnd2.nextInt(100)+1) + "}" + ", ");
		}
		*/
		// 设置不可直达城市
		int noArrive[][] = new int[30][30];
		for (int i = 0; i < noArrive.length; i++) {
			for (int j = 0; j < noArrive.length; j++) {
				if ((i+j)%2 == 0) {
					noArrive[i][j] = 0;		// 0为不可以直达
				} else {
					noArrive[i][j] = 1;		//1为可以直达
				}
			}
		}
		
		for (int i = 0; i < noArrive.length; i++) {
			for (int j = 0; j < noArrive.length; j++) {
				System.out.print(noArrive[i][j]);
			}
			System.out.println();
		}
	}

}
