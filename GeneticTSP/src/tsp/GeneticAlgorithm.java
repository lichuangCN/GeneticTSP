package tsp;

import java.security.KeyStore.Entry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 
 * @author lichuang
 * �Ŵ��㷨����Ĳ�������ķ����ͱ���
 * ��Ӣ��������Ⱥ����ǿ��ĳ�Ա��Ӧ�ô����ഫ�� 
 * ��������Ǿ�Ӣ֮һ��������������򽻲档
 */

public class GeneticAlgorithm {
	private int populationSize;		// ��Ⱥ��ģ
	private double mutationRate; 	// ������(�����ÿ������ı�����)
	private double crossoverRate; 	// ������
	private double elitismCount;	// ��Ӣ��Ա��(���������н���ͱ���)	
	
	private double temperature = 1.0;	// �¶�
	private double coolingRate;		// ��ȴ��
	
	// ��Ӧ��ֵɢ�б�
	private Map<Traveler, Double> fitnessMap = Collections.synchronizedMap(new LinkedHashMap<Traveler, Double>(){		
		protected boolean removeEldestEntry(Entry eldestEntry) {
			return this.size() > 1000;
		}
	});
	
	/**
	 * ��ʼ��GA����ĸ�������ֵ
	 * @param populationSize
	 * @param mutationRate
	 * @param crossoverRate
	 * @param elitismCount
	 */
	
	public GeneticAlgorithm (int populationSize, double mutationRate, 
			double crossoverRate, int elitismCount) {
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
	}
	
	/**
	 * ģ���˻��㷨 
	 * @param populationSize
	 * @param mutationRate
	 * @param crossoverRate
	 * @param elitismCount
	 * @param coolingRate
	 */
	public GeneticAlgorithm (int populationSize, double mutationRate, 
			double crossoverRate, int elitismCount, double coolingRate) {
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.coolingRate = coolingRate;
	}
	
	/**
	 * ��ʼ����Ⱥ(��Ⱥ��С����Ⱥ���򳤶�)
	 * @param chromosomeLength		
	 * 				��Ⱥ���򳤶�
	 * @return population
	 * 				��ʼ�������Ⱥ
	 */
	public Population initPopulation(int chromosomeLength) {
		Population population = new Population(this.populationSize, chromosomeLength);
		return population;
	}
	
	/**
	 * ��Ӧ���� (1/·���ܾ���)���� �����̵ľ��и��ߵ�����
	 * @param traveler
	 * @param cities
	 * @return
	 */
	public double calculateFitness(Traveler traveler, City cities[]) {
		
		// ��ȡ��ǰ�����·��
		Route route = new Route(traveler, cities);
		// ������Ӧ��
		double fitness = 1 / route.getDistance();		
		// ������Ӧ��
		traveler.setFitness(fitness);
		
		// ɢ�б�����Ӧ��ֵ
		//this.fitnessMap.put(traveler, fitness);
		
		return fitness;
	}
	
	/**
	 * �������еĸ��壬���㲢����ÿ���������Ӧ��
	 * ������Ⱥƽ����Ӧ��
	 * @param population
	 * @param cities
	 */
	public void evaluatePopulation(Population population, City cities[]) {
		
		// ���в���
		IntStream.range(0, population.size()).parallel()
		      .forEach(i -> this.calculateFitness(population.getTraveler(i), cities));
		
		double populationFitness = 0;		
		// ѭ������������Ӧ��
		for (Traveler traveler : population.getTravelers()) {
			populationFitness += this.calculateFitness(traveler, cities);
		}
	
		population.setPopulationFitness(populationFitness);
		// ƽ����Ⱥ��Ӧ��
		double populationAvgFitness = populationFitness / population.size();
		population.setPopulationAvgFitness(populationAvgFitness);
	}
	
	/**
	 * ��ֹ���
	 * ʵ���ϲ���֪���������������ʲôʱ��õ�,������Լ����������������
	 * @param generationCount	
	 * 				�Ѿ����еĵ�������
	 * @param maxGenerations	
	 * 				��ֹ�����������
	 * @return true/false
	 * 				��ֹ����true�����򷵻�false
	 */
	public boolean isTerminalCondition(int generationCount, int maxGenerations) {
		return (generationCount > maxGenerations);
	}
	
	/**
	 * �������̶ĵķ�ʽѡ����н����˫��
	 * �ڴ���ʵ���в��÷����̶ķ�ʽ������ѡ��һ�����λ�ã�Ȼ��������Ū���ĸ�����λ�ڸ�λ��
	 * @param population
	 * @return
	 */
	public Traveler selectParentRoulett(Population population) {		
		// ��ȡ��ǰȺ��
		Traveler travelers[] = population.getTravelers();		
		// ��ת����
		double populationFitness = population.getPopulationFitness();
		// �����������е�λ��
		double rouletteWheelPosition = Math.random() * populationFitness;		
		// Ѱ��λ�ڸ�λ�õĸ���
		double spinWheel = 0;
		for (Traveler traveler : travelers) {
			// �����ۼӵķ�ʽѰ��Ŀ�����
			spinWheel += traveler.getFitness();
			if (spinWheel >= rouletteWheelPosition) {
				return traveler;
			}
		}		
		return travelers[population.size() - 1];
	}
	
	/**
	 * �Ը�����н��棬���ý�����ѡ����н����˫��
	 * ������Ӧ�Ŵ��㷨
	 * @param population
	 * 				��ǰ��Ⱥ
	 * @return newPopulation
	 * 				�����Ⱥ
	 */
	public Population noAdaptiveCrossoverPopulation(Population population) {
		// �������Ⱥ��
		Population newPopulation = new Population(population.size());		
		// ������Ӧ��(����)ѭ����ǰȺ��
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// ��ȡ��һ��˫��
			Traveler parent1 = population.getFittest(populationIndex);

			// �ж��Ƿ�Ҫ���н���,���ж��Ƿ����㽻���ʺ��Ƿ�Ϊ��ӢȺ��
			if (this.crossoverRate > Math.random() && populationIndex >= elitismCount) {
				// Ѱ�ҵڶ���˫��
				Traveler parent2 = this.selectParentRoulett(population);
				
				// ����һ�������Ⱦɫ��
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// ��ʼ�����Ⱦɫ��
				Traveler offspring = new Traveler(offspringChromosome);
				
				// ��ȡ˫��Ⱦɫ���Ӽ�
				// ��һ���ڵ�
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// �ڶ����ڵ�
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				
				// С����Ϊ��ʼ�ڵ㣬�����Ϊ�����ڵ�
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// ѭ������һ��˫�׵ĸ�Ƭ�λ�����䵽���
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// ѭ�������ڶ���˫�׵����γ���
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// �ȱ����ڶ���˫�׵ĵڶ��ڻ���(��Ƭ�λ����һ��λ��)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// ���ڶ��ڻ���������������ص�һ�ڻ���(��һ��λ��)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// ��������û�е�ǰ����(����)����������
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// ѭ��Ѱ�ҿհ׻���λ��
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// �հ�λ���ҵ�����ӻ���
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));
								break;
							}
						}
					}
				}
				// �������ӵ������Ⱥ
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// �����н���ֱ�Ӽ�������Ⱥ
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// ���غ����Ⱥ
		return newPopulation;
	}

	/**
	 * �Ը�����н��棬���ý�����ѡ����н����˫��
	 * �������Ŵ��㷨����̬����������
	 * @param population
	 * 				��ǰ��Ⱥ
	 * @return newPopulation
	 * 				�����Ⱥ
	 */
	public Population adaptiveCrossoverPopulation(Population population) {
		// �������Ⱥ��
		Population newPopulation = new Population(population.size());		
		// ������Ӧ��(����)ѭ����ǰȺ��
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// ��ȡ��һ��˫��
			Traveler parent1 = population.getFittest(populationIndex);	
			
			// ��ȡ��Ⱥ����Ѹ������Ӧ��
			double bestFitness = population.getFittest(0).getFitness();				
			// ��������Ӧ������
			double adaptiveCrossoverRate = this.crossoverRate;			
			// ����ǰ�������Ӧ�ȴ�������ƽ����Ӧ��,����������			
			if (parent1.getFitness() > population.getPopulationAvgFitness()) {
				double fitnessDelta1 = bestFitness - parent1.getFitness();
				double fitnessDelta2 = bestFitness - population.getPopulationAvgFitness();
				adaptiveCrossoverRate = (fitnessDelta1 / fitnessDelta2) * this.crossoverRate;	//	���ͽ�����
			} 
			
			// �ж��Ƿ�Ҫ���н���,���ж��Ƿ����㽻���ʺ��Ƿ�Ϊ��ӢȺ��
			if (adaptiveCrossoverRate > Math.random() && populationIndex >= elitismCount) {
				// Ѱ�ҵڶ���˫��
				Traveler parent2 = this.selectParentRoulett(population);				
				// ����һ�������Ⱦɫ��
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// ��ʼ�����Ⱦɫ��
				Traveler offspring = new Traveler(offspringChromosome);
				
				// ��ȡ˫��Ⱦɫ���Ӽ�
				// ��һ���ڵ�
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// �ڶ����ڵ�
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				// С����Ϊ��ʼ�ڵ㣬�����Ϊ�����ڵ�
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// ѭ������һ��˫�׵ĸ�Ƭ�λ�����䵽���
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// ѭ�������ڶ���˫�׵����γ���
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// �ȱ����ڶ���˫�׵ĵڶ��ڻ���(��Ƭ�λ����һ��λ��)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// ���ڶ��ڻ���������������ص�һ�ڻ���(��һ��λ��)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// ��������û�е�ǰ����(����)����������
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// ѭ��Ѱ�ҿհ׻���λ��
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// �հ�λ���ҵ�����ӻ���
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));								
								break;	// ������ǰѭ��
							}
						}
					}
				}
				// �������ӵ������Ⱥ
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// �����н���ֱ�Ӽ�������Ⱥ
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// ���غ����Ⱥ
		return newPopulation;
	}
	
	/**
	 * �Ը�����н��棬���ý�����ѡ����н����˫��
	 * ������Ӧ�Ŵ��㷨
	 * @param population
	 * 				��ǰ��Ⱥ
	 * @return newPopulation
	 * 				�����Ⱥ
	 */
	public Population coolCrossoverPopulation(Population population) {
		// �������Ⱥ��
		Population newPopulation = new Population(population.size());		
		// ������Ӧ��(����)ѭ����ǰȺ��
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// ��ȡ��һ��˫��
			Traveler parent1 = population.getFittest(populationIndex);

			// �ж��Ƿ�Ҫ���н���,���ж��Ƿ����㽻���ʺ��Ƿ�Ϊ��ӢȺ��
			if ((this.crossoverRate * this.getTemperature())> Math.random() && populationIndex >= elitismCount) {
				// Ѱ�ҵڶ���˫��
				Traveler parent2 = this.selectParentRoulett(population);				
				// ����һ�������Ⱦɫ��
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// ��ʼ�����Ⱦɫ��
				Traveler offspring = new Traveler(offspringChromosome);
				
				// ��ȡ˫��Ⱦɫ���Ӽ�
				// ��һ���ڵ�
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// �ڶ����ڵ�
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				
				// С����Ϊ��ʼ�ڵ㣬�����Ϊ�����ڵ�
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// ѭ������һ��˫�׵ĸ�Ƭ�λ�����䵽���
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// ѭ�������ڶ���˫�׵����γ���
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// �ȱ����ڶ���˫�׵ĵڶ��ڻ���(��Ƭ�λ����һ��λ��)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// ���ڶ��ڻ���������������ص�һ�ڻ���(��һ��λ��)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// ��������û�е�ǰ����(����)����������
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// ѭ��Ѱ�ҿհ׻���λ��
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// �հ�λ���ҵ�����ӻ���
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));
								break;
							}
						}
					}
				}
				// �������ӵ������Ⱥ
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// �����н���ֱ�Ӽ�������Ⱥ
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// ���غ����Ⱥ
		return newPopulation;
	}
	
	/**
	 * ����ÿ�������Ⱦɫ�壬���ڱ����ʣ�����ÿ�������Ƿ����λ��������
	 * ������Ӧ�Ŵ�
	 * @param population
	 * 				��ǰ����
	 * @return newPopulation
	 * 				�����ĺ��Ⱥ��
	 */
	public Population noAdaptiveMutatePopulation(Population population) {
		// �����µĺ����Ⱥ
		Population newPopulation = new Population(this.populationSize);
		
		// ������Ӧ��(����)ѭ����ǰ��Ⱥ
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			
			// ������ھ�ӢȺ��,������
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// ѭ����ǰ�������
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					// �ж��Ƿ���б���
					if (this.mutationRate > Math.random()) {
						// ��ȡ�������λ��
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// ���н����Ļ���
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// ��������
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}					
				}
			}
			// ��������Ⱥ��
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// ������ɱ����Ⱥ��
		return newPopulation;
	}
	
	/**
	 * ����ÿ�������Ⱦɫ�壬���ڱ����ʣ�����ÿ�������Ƿ����λ��������
	 * ģ���˻��㷨
	 * @param population
	 * 				��ǰ����
	 * @return newPopulation
	 * 				�����ĺ��Ⱥ��
	 */
	public Population coolMutatePopulation(Population population) {
		// �����µĺ����Ⱥ
		Population newPopulation = new Population(this.populationSize);
		
		// ������Ӧ��(����)ѭ����ǰ��Ⱥ
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			
			// ������ھ�ӢȺ��,������
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// ѭ����ǰ�������
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					// �ж��Ƿ���б���
					if ((this.mutationRate *this.getTemperature()) > Math.random()) {
						// ��ȡ�������λ��
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// ���н����Ļ���
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// ��������
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}					
				}
			}
			// ��������Ⱥ��
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// ������ɱ����Ⱥ��
		return newPopulation;
	}
	

	/**
	 * ����ÿ�������Ⱦɫ�壬���ڱ����ʣ�����ÿ�������Ƿ����λ��������
	 * ����Ӧ�Ŵ�
	 * @param population
	 * 				��ǰ����
	 * @return newPopulation
	 * 				�����ĺ��Ⱥ��
	 */
	public Population adaptiveMutatePopulation(Population population) {
		// �����µĺ����Ⱥ
		Population newPopulation = new Population(this.populationSize);
		
		// ������Ӧ��(����)ѭ����ǰ��Ⱥ
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			// ��ȡ��Ⱥ����Ѹ������Ӧ��
			double bestFitness = population.getFittest(0).getFitness();
			
			// ��������Ӧ������
			double adaptieMutationRate = this.mutationRate;
			
			// ����ǰ�������Ӧ�ȴ�������ƽ����Ӧ��,����������			
			if (traveler.getFitness() > population.getPopulationAvgFitness()) {
				double fitnessDelta1 = bestFitness - traveler.getFitness();
				double fitnessDelta2 = bestFitness - population.getPopulationAvgFitness();
				adaptieMutationRate = (fitnessDelta1 / fitnessDelta2) * this.mutationRate;	//	���ͱ�����
			} 
			
			// ������ھ�ӢȺ��,������
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// ѭ����ǰ�������
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					if (adaptieMutationRate > Math.random()) {
						// ��ȡ�������λ��
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// ���н����Ļ���
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// ��������
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}
				}
			}
			// ��������Ⱥ��
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// ������ɱ����Ⱥ��
		return newPopulation;
	}
	
	/**
	 * �����¶�
	 */
	public void coolTemperature() {
		this.temperature *= (1 - this.coolingRate);
	}
	
	public double getTemperature() {
		return temperature;
	}
}
