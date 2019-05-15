package tsp;

/**  
 *  
 * @author lichuang  
 * 保存一个路线，并计算出总距离 
 */

public class Route {

	private City route[];
	private double distance = 0;
	
	/**
	 * 初始化路径
	 * @param traveler
	 * 				旅行商人个体
	 * @param cities
	 * 				经过的城市
	 */
	public Route(Traveler traveler, City cities[]) {
		// 获取个体的染色体
		int chromosome[] = traveler.getChromosome();
		// 创建路径
		this.route = new City[cities.length];
		for (int geneIndex = 0; geneIndex < chromosome.length; geneIndex++) {
			this.route[geneIndex] = cities[chromosome[geneIndex]];
		}
	}

	/**
	 * 获取总路程
	 * @return double distance
	 * 				旅行的总路程
	 */
	public double getDistance() {
		// 若已知总路程，则返回总路程
		if (this.distance > 0) {
			return this.distance;
		}
		
		// 计算总路程
		double totalDistance = 0;
		// 调用City.distenceFrom(),依次计算路径中两个城市之间的距离
		for (int cityIndex = 0; cityIndex < this.route.length-1; cityIndex++) {
			// 计算出发城市到最后城市的路程总和
			totalDistance += this.route[cityIndex].distanceFrom(this.route[cityIndex+1]);
		}
		// 计算周游所有城市并回到出发城市的路程总和
		totalDistance += this.route[this.route.length - 1].distanceFrom(this.route[0]);
		
		return totalDistance;
	}
}
