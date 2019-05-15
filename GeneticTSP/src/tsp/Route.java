package tsp;

/**  
 *  
 * @author lichuang  
 * ����һ��·�ߣ���������ܾ��� 
 */

public class Route {

	private City route[];
	private double distance = 0;
	
	/**
	 * ��ʼ��·��
	 * @param traveler
	 * 				�������˸���
	 * @param cities
	 * 				�����ĳ���
	 */
	public Route(Traveler traveler, City cities[]) {
		// ��ȡ�����Ⱦɫ��
		int chromosome[] = traveler.getChromosome();
		// ����·��
		this.route = new City[cities.length];
		for (int geneIndex = 0; geneIndex < chromosome.length; geneIndex++) {
			this.route[geneIndex] = cities[chromosome[geneIndex]];
		}
	}

	/**
	 * ��ȡ��·��
	 * @return double distance
	 * 				���е���·��
	 */
	public double getDistance() {
		// ����֪��·�̣��򷵻���·��
		if (this.distance > 0) {
			return this.distance;
		}
		
		// ������·��
		double totalDistance = 0;
		// ����City.distenceFrom(),���μ���·������������֮��ľ���
		for (int cityIndex = 0; cityIndex < this.route.length-1; cityIndex++) {
			// ����������е������е�·���ܺ�
			totalDistance += this.route[cityIndex].distanceFrom(this.route[cityIndex+1]);
		}
		// �����������г��в��ص��������е�·���ܺ�
		totalDistance += this.route[this.route.length - 1].distanceFrom(this.route[0]);
		
		return totalDistance;
	}
}
