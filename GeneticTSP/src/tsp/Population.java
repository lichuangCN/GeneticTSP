package tsp;

/**
 * 
 * @author lichuang
 * 保存由个体组成的一个数组，能够通过类方法方便访问
 * 存储种群的总体适应度
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
	
	private Traveler population[];	// 种群所有个体
	private double populationFitness = -1;	// 总体适应度
	private double populationAvgFitness = -1;	// 总体平均适应度

	/**
	 * 初始化空的种群
	 * 用于初始化创建后代种群
	 * @param populationSize
	 * 				种群规模
	 */
	public Population(int populationSize) {
		this.population = new Traveler[populationSize];
	}
	
	/**
	 * 初始化空的种群
	 * @param populationSize
	 * 				种群规模
	 * @param chromosomeLength
	 * 				个体的染色体长度
	 */
	public Population(int populationSize, int chromosomeLength) {
		this.population = new Traveler[populationSize];
		
		// 创建种群中的每一个个体
		for (int travelerCount = 0; travelerCount < populationSize; travelerCount++) {
			Traveler traveler = new Traveler(chromosomeLength);
			this.population[travelerCount] = traveler;
		}
	}
	
	/**
	 * 获取当前种群全体
	 * @return population
	 * 				当前种群全体
	 */
	public Traveler[] getTravelers() {
		return this.population;
	}
	
	/**
	 * 获取种群总体适应度
	 * @return populationAvgFitness
	 * 				总体适应度
	 */
	public double getPopulationAvgFitness() {
		return populationAvgFitness;
	}

	/**
	 * 设置总体适应度
	 * @param populationAvgFitness
	 * 				总体适应度
	 */
	public void setPopulationAvgFitness(double populationAvgFitness) {
		this.populationAvgFitness = populationAvgFitness;
	}
	
	/**
	 * 通过适合度(降序)查找种群中的个体,这个可以用来找到单个最强的个体
	 * @param offset
	 * 				个体的偏移量，按适应度排序。 0是最强，population.length  -  1是最弱的。
	 * @return
	 * 				目标个体
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
	 * 获取种群总体适应度
	 * @return populationFitness
	 * 				种群总体适应度
	 */
	public double getPopulationFitness() {
		return populationFitness;
	}

	/**
	 * 设置种群适应度
	 * @param populationFitness
	 */
	public void setPopulationFitness(double populationFitness) {
		this.populationFitness = populationFitness;
	}
	
	/**
	 * 获取种群规模
	 * @return population.size
	 * 				种群规模
	 */
	public int size() {
		return this.population.length;
	}
	
	/**
	 * 设置特定位置的个体
	 * @param offset
	 * @param traveler
	 * @return traveler
	 */
	public Traveler setTraveler(int offset, Traveler traveler) {
		return population[offset] = traveler;
	}
	
	/**
	 * 获取特定位置的个体
	 * @param offset
	 * 				特定位置
	 * @return population
	 * 				个体
	 */
	public Traveler getTraveler(int offset) {
		return population[offset];
	}
	
	/**
	 * 对种群所有个体进行随机排列（即对种群所有个体进行洗牌）
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
