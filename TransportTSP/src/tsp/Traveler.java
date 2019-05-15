package tsp;

import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author lichuang
 * �������һ����ѡ�⣬����洢�Ͳ���һ��Ⱦɫ��
 */

public class Traveler {

	private int[] chromosome;		// ����Ⱦɫ��
	private double fitness = -1;	// ������Ӧ��
	
	/**
	 * ����һ�����飬��ΪȾɫ��
	 * @param chromosome
	 */
	public Traveler(int[] chromosome) {
		this.chromosome = chromosome;
	}
	
	/**
	 * ����һ��������Ⱦɫ�峤�ȣ�����ʼ��ʱ����һ��˳��Ⱦɫ��
	 * @param chromosomeLength
	 * 				Ⱦɫ�峤��
	 */
	public Traveler(int chromosomeLength) {
		// ��������Ⱦɫ��
		int[] travelerChromosome;
		travelerChromosome = new int[chromosomeLength];
		// ��TSP������,���ܳ���·;�г����ظ�����,���ڴ����������˸���ʱ,˳�򴴽�Ⱦɫ��
		// ��˳��ӵ�һ�����е������һ������
		for (int gene = 0; gene < chromosomeLength; gene++) {
			travelerChromosome[gene] = gene;
		}
		this.chromosome = travelerChromosome;
	}

	/**
	 * ��ȡ���ø����Ⱦɫ��
	 * @return chromosome
	 * 				����Ⱦɫ��
	 */
	public int[] getChromosome() {
		return chromosome;
	}
	
	/**
	 * ��ȡ����Ⱦɫ�峤��
	 * @return chromosome.length
	 * 				Ⱦɫ�峤��
	 */
	public int getChromosomeLength() {
		return this.chromosome.length;
	}

	/**
	 * ��ȡ�������Ӧ��
	 * @return fitness
	 * 				������Ӧ��
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * ����������Ӧ��
	 * @param fitness
	 * 				������Ӧ��
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	/**
	 * ���ø����ض�λ�õĻ���
	 * @param offset
	 * 				Ⱦɫ����ض�λ��
	 * @param gene
	 * 				���ø�λ�õĻ���
	 */
	public void setGene(int offset, int gene) {
		this.chromosome[offset] = gene;
	}
	
	/**
	 * ��ȡ�����ض�λ�õĻ���
	 * @return gene
	 * 				�����ض�λ�õĻ���
	 */
	public int getGene(int offset) {
		return this.chromosome[offset];
	}
	
	/**
	 * ׷�ٸ�����Ӧ�ȵ�ֵ�����Լ���ӡΪ�ַ���s
	 * @return output
	 * 				�������Ӧ�ȵ�ֵ
	 */
	public String toString() {
		String output = "";
		for (int gene = 0; gene < chromosome.length; gene++) {
			output += this.chromosome[gene];
		}
		return output;
	}
	
	/**
	 * ����ض������Ƿ����
	 * @param gene
	 * 				�ض�����
	 * @return Boolean
	 * 				���ڷ���true,���򷵻�false
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
	 * �ж�·���Ƿ�Ϸ�
	 */
	public int isLegalRoute(int[] chromosome, int[][] noArrive) {
		for (int geneIndex=0; geneIndex<chromosome.length-1; geneIndex++) {
			if (noArrive[geneIndex][chromosome[geneIndex+1]] == 0) {
				return geneIndex;	//���Ϸ������ش���λ��
			}			
		}
		return 0;	// �Ϸ�����0
	}
	
	/**
	 * �����������λ��
	 */
	public void changePostions(int[] chromosome, int geneIndex) {
		int index = (int)(Math.random() *chromosome.length);
		int temp = chromosome[geneIndex];
		chromosome[geneIndex] = chromosome[index];
		chromosome[index] = temp;
	}
}
