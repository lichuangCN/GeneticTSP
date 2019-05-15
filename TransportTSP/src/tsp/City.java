package tsp;

/**  
 *  
 * @author lichuang  
 * 创建并保存一个城市，并计算到另一个城市的最短距离
 */

public class City {
		private double x;
		private double y;
		
		public City(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * 计算当前城市距其他城市的距离
		 * 欧氏距离: a^2 + b^2 = c^2
		 * @param city
		 * 				其他城市
		 * @return	double distance
		 * 				距其他城市的距离
		 */
		public double distanceFrom(City city) {
			// 
			double deltaXSq = Math.pow((city.getX() - this.getX()), 2);
			double deltaYSq = Math.pow((city.getY() - this.getY()), 2);
			
			// 计算两个城市之间的距离
			double distance = Math.sqrt(Math.abs(deltaXSq + deltaYSq));
			return distance;
		}

		/**
		 * 返回该城市的横坐标
		 * @return double X
		 */
		public double getX() {
			return x;
		}

		/**
		 * 返回该城市的纵坐标
		 * @return int y
		 */
		public double getY() {
			return y;
		}
}
