package tsp;

/**
 * 
 * @author lichuang
 * �����ɸ�����ɵ�һ�����飬�ܹ�ͨ���෽���������
 * �洢��Ⱥ��������Ӧ��
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
	
	private Traveler population[];	// ��Ⱥ���и���
	private double populationFitness = -1;	// ������Ӧ��
	private double populationAvgFitness = -1;	// ����ƽ����Ӧ��

	/**
	 * ��ʼ���յ���Ⱥ
	 * ���ڳ�ʼ�����������Ⱥ
	 * @param populationSize
	 * 				��Ⱥ��ģ
	 */
	public Population(int populationSize) {
		this.population = new Traveler[populationSize];
	}
	
	/**
	 * ��ʼ���յ���Ⱥ
	 * @param populationSize
	 * 				��Ⱥ��ģ
	 * @param chromosomeLength
	 * 				�����Ⱦɫ�峤��
	 */
	public Population(int populationSize, int chromosomeLength) {
		this.population = new Traveler[populationSize];
		
		// ������Ⱥ�е�ÿһ������
		for (int travelerCount = 0; travelerCount < populationSize; travelerCount++) {
			Traveler traveler = new Traveler(chromosomeLength);
			this.population[travelerCount] = traveler;
		}
	}
	
	/**
	 * ��ȡ��ǰ��Ⱥȫ��
	 * @return population
	 * 				��ǰ��Ⱥȫ��
	 */
	public Traveler[] getTravelers() {
		return this.population;
	}
	
	/**
	 * ��ȡ��Ⱥ������Ӧ��
	 * @return populationAvgFitness
	 * 				������Ӧ��
	 */
	public double getPopulationAvgFitness() {
		return populationAvgFitness;
	}

	/**
	 * ����������Ӧ��
	 * @param populationAvgFitness
	 * 				������Ӧ��
	 */
	public void setPopulationAvgFitness(double populationAvgFitness) {
		this.populationAvgFitness = populationAvgFitness;
	}
	
	/**
	 * ͨ���ʺ϶�(����)������Ⱥ�еĸ���,������������ҵ�������ǿ�ĸ���
	 * @param offset
	 * 				�����ƫ����������Ӧ������ 0����ǿ��population.length  -  1�������ġ�
	 * @return
	 * 				Ŀ�����
	 */
	public Traveler getFittest(int offset) {
		Arrays.parallelSort(this.population, new Comparator<Traveler>() {

			@Override
			public int compare(Traveler o1, Traveler o2) {
				if (o1.getFitness() > o2.getFitness()) {
					return -1;
				} else if (o1.getFitness() < o2.getFitness()) {
					return 1;
				}
				return 0;
			}
		});
		return this.population[offset];
	}

	/**
	 * ��ȡ��Ⱥ������Ӧ��
	 * @return populationFitness
	 * 				��Ⱥ������Ӧ��
	 */
	public double getPopulationFitness() {
		return populationFitness;
	}

	/**
	 * ������Ⱥ��Ӧ��
	 * @param populationFitness
	 */
	public void setPopulationFitness(double populationFitness) {
		this.populationFitness = populationFitness;
	}
	
	/**
	 * ��ȡ��Ⱥ��ģ
	 * @return population.size
	 * 				��Ⱥ��ģ
	 */
	public int size() {
		return this.population.length;
	}
	
	/**
	 * �����ض�λ�õĸ���
	 * @param offset
	 * @param traveler
	 * @return traveler
	 */
	public Traveler setTraveler(int offset, Traveler traveler) {
		return population[offset] = traveler;
	}
	
	/**
	 * ��ȡ�ض�λ�õĸ���
	 * @param offset
	 * 				�ض�λ��
	 * @return population
	 * 				����
	 */
	public Traveler getTraveler(int offset) {
		return population[offset];
	}
	
	/**
	 * ����Ⱥ���и������������У�������Ⱥ���и������ϴ�ƣ�
	 */
	public void shuffle() {
		Random rnd = new Random();
		for (int i = population.length-1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			Traveler traveler = population[index];
			population[index] = population[i];
			population[i] = traveler;
		}
	}
	
}
