package tsp;

/**  
 *  
 * @author lichuang  
 * ����������һ�����У������㵽��һ�����е���̾���
 */

public class City {
		private double x;
		private double y;
		
		public City(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * ���㵱ǰ���о��������еľ���
		 * ŷ�Ͼ���: a^2 + b^2 = c^2
		 * @param city
		 * 				��������
		 * @return	double distance
		 * 				���������еľ���
		 */
		public double distanceFrom(City city) {
			// 
			double deltaXSq = Math.pow((city.getX() - this.getX()), 2);
			double deltaYSq = Math.pow((city.getY() - this.getY()), 2);
			
			// ������������֮��ľ���
			double distance = Math.sqrt(Math.abs(deltaXSq + deltaYSq));
			return distance;
		}

		/**
		 * ���ظó��еĺ�����
		 * @return double X
		 */
		public double getX() {
			return x;
		}

		/**
		 * ���ظó��е�������
		 * @return int y
		 */
		public double getY() {
			return y;
		}
}
