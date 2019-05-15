package tsp;

/**  
 * @author lichuang  
 * ������������
 * �趨һЩ�����ڵ�ͼ��
 */

public class TSP {

	public static int maxGenerations = 1200;
	
	public static void main(String[] args) {
		
		// ��ʼʱ��
		//long startTime = System.currentTimeMillis();
		
		// ��ʼ���ó�������		
		int citiesPos[][] = {{9, 86}, {36, 16}, {97, 52}, {90, 75}, {32, 14},
							{45, 81}, {38, 94}, {13, 87}, {64, 34}, {79, 43}, 
							{99, 18}, {2, 79}, {31, 53}, {11, 37}, {79, 10}, 
							{71, 78}, {73, 19}, {94, 58}, {64, 65}, {92, 98}, 
							{68, 52}, {94, 42}, {23, 98}, {56, 7}, {78, 85}, 
							{76, 14}, {23, 65}, {17, 35}, {33, 97}, {7, 61}};
								
		City cities[] = new City[citiesPos.length];		// ��������
		
		// ��ʼ�����е�λ��
		for (int cityIndex = 0; cityIndex < citiesPos.length; cityIndex++) {
			double xPos = citiesPos[cityIndex][0];
			double yPos = citiesPos[cityIndex][1];			
			cities[cityIndex] = new City(xPos, yPos);	
		}
		
		int time = 1;
		while(time <= 5) {
			
			// ����GA���� 		��Ա��; ������; ������;  ��Ӣ��;
			GeneticAlgorithm ga = new GeneticAlgorithm(150, 0.008, 0.9, 25);	
			// ģ���˻��㷨  			��Ա��; ������; ������; ��Ӣ��; ��ȴ��
			//GeneticAlgorithm ga = new GeneticAlgorithm(150, 0.01, 0.9, 25, 0.05);			
			// ������ʼ��Ⱥ
			Population population = ga.initPopulation(cities.length);		
			// ��Ⱥ��ʼ����Ӧ��ֵ����
			ga.evaluatePopulation(population, cities);		
			// ��ǰ������
			int generation = 1;
			
			// ��ʼѭ������
			while (ga.isTerminalCondition(generation, maxGenerations) == false) {
			
				// ÿһ����ӡ����Ѹ���l
				//Route route = new Route(population.getFittest(0), cities);
				// �����Ⱥ����Ӧ����
				//System.out.println(route.getDistance());
				// �����Ѹ�����Ӧ��
				//System.out.println(population.getFittest(0).getFitness());
				// �����Ⱥ��������Ӧ��
				//System.out.println("; " + population.getPopulationFitness());
				
				// �������
				ga.noAdaptiveCrossoverPopulation(population);
				//ga.adaptiveCrossoverPopulation(population);
				
				// �������
				ga.noAdaptiveMutatePopulation(population);
				//ga.adaptiveMutatePopulation(population);
				
				// ��Ⱥ��Ӧ�ȼ���
				ga.evaluatePopulation(population, cities);
				
				// ����������
				generation ++;	
				// �����¶�
				//ga.coolTemperature();
			}
			
			// ��ӡ���ս��
			//System.out.println("Stopped after " + maxGenerations + " generations.");
			Route route = new Route(population.getFittest(0), cities);
			//System.out.println("Best distance: " + route.getDistance());
			System.out.println("T" + time + ": " + route.getDistance() + "; " + population.getFittest(0).getFitness());
			//System.out.println("T" + time + ": " + route.getDistance());
			time ++;
		}
		// ����ʱ��
		//long endTime = System.currentTimeMillis();
		//System.out.println("Running time: " + (endTime - startTime));
	}

}