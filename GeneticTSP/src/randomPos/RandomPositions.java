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
		// ���ò���ֱ�����
		int noArrive[][] = new int[30][30];
		for (int i = 0; i < noArrive.length; i++) {
			for (int j = 0; j < noArrive.length; j++) {
				if ((i+j)%2 == 0) {
					noArrive[i][j] = 0;		// 0Ϊ������ֱ��
				} else {
					noArrive[i][j] = 1;		//1Ϊ����ֱ��
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
