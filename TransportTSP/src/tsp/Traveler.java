package tsp;

import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author lichuang
 * 该类代表一个候选解，负责存储和操作一条染色体
 */

public class Traveler {

	private int[] chromosome;		// 个体染色体
	private double fitness = -1;	// 个体适应度
	
	/**
	 * 接受一个数组，作为染色体
	 * @param chromosome
	 */
	public Traveler(int[] chromosome) {
		this.chromosome = chromosome;
	}
	
	/**
	 * 接受一个整数（染色体长度），初始化时创建一条顺序染色体
	 * @param chromosomeLength
	 * 				染色体长度
	 */
	public Traveler(int chromosomeLength) {
		// 创建个体染色体
		int[] travelerChromosome;
		travelerChromosome = new int[chromosomeLength];
		// 在TSP问题中,不能出现路途中城市重复现象,故在创建旅行商人个体时,顺序创建染色体
		// 即顺序从第一个城市到达最后一个城市
		for (int gene = 0; gene < chromosomeLength; gene++) {
			travelerChromosome[gene] = gene;
		}
		this.chromosome = travelerChromosome;
	}

	/**
	 * 获取个该个体的染色体
	 * @return chromosome
	 * 				个体染色体
	 */
	public int[] getChromosome() {
		return chromosome;
	}
	
	/**
	 * 获取个体染色体长度
	 * @return chromosome.length
	 * 				染色体长度
	 */
	public int getChromosomeLength() {
		return this.chromosome.length;
	}

	/**
	 * 获取个体的适应度
	 * @return fitness
	 * 				个体适应度
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * 保存个体的适应度
	 * @param fitness
	 * 				个体适应度
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * 设置个体特定位置的基因
	 * @param offset
	 * 				染色体的特定位置
	 * @param gene
	 * 				设置该位置的基因
	 */
	public void setGene(int offset, int gene) {
		this.chromosome[offset] = gene;
	}
	
	/**
	 * 获取个体特定位置的基因
	 * @return gene
	 * 				个体特定位置的基因
	 */
	public int getGene(int offset) {
		return this.chromosome[offset];
	}
	
	/**
	 * 追踪个体适应度的值，将自己打印为字符串s
	 * @return output
	 * 				个体的适应度的值
	 */
	public String toString() {
		String output = "";
		for (int gene = 0; gene < chromosome.length; gene++) {
			output += this.chromosome[gene];
		}
		return output;
	}
	
	/**
	 * 检查特定基因是否存在
	 * @param gene
	 * 				特定基因
	 * @return Boolean
	 * 				存在返回true,否则返回false
	 */
	public boolean containsGene(int gene) {
		for (int i = 0; i < chromosome.length; i++) {
			if (this.chromosome[i] == gene) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断路径是否合法
	 */
	public int isLegalRoute(int[] chromosome, int[][] noArrive) {
		for (int geneIndex=0; geneIndex<chromosome.length-1; geneIndex++) {
			if (noArrive[geneIndex][chromosome[geneIndex+1]] == 0) {
				return geneIndex;	//不合法，返回错误位置
			}			
		}
		return 0;	// 合法返回0
	}
	
	/**
	 * 随机交换两个位置
	 */
	public void changePostions(int[] chromosome, int geneIndex) {
		int index = (int)(Math.random() *chromosome.length);
		int temp = chromosome[geneIndex];
		chromosome[geneIndex] = chromosome[index];
		chromosome[index] = temp;
	}
}
