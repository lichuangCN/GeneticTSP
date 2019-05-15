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
 * 遗传算法本身的操作所需的方法和变量
 * 精英主义是种群中最强大的成员，应该代代相传。 
 * 如果个体是精英之一，它不会参与变异或交叉。
 */

public class GeneticAlgorithm {
	private int populationSize;		// 种群规模
	private double mutationRate; 	// 变异率(个体的每个基因的变异率)
	private double crossoverRate; 	// 交叉率
	private double elitismCount;	// 精英成员数(保留不进行交叉和变异)	
	
	private double temperature = 1.0;	// 温度
	private double coolingRate;		// 冷却率
	
	// 适应度值散列表
	private Map<Traveler, Double> fitnessMap = Collections.synchronizedMap(new LinkedHashMap<Traveler, Double>(){		
		protected boolean removeEldestEntry(Entry eldestEntry) {
			return this.size() > 1000;
		}
	});
	
	/**
	 * 初始化GA对象的各个属性值
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
	 * 模拟退火算法 
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
	 * 初始化种群(种群大小，种群基因长度)
	 * @param chromosomeLength		
	 * 				种群基因长度
	 * @return population
	 * 				初始化后的种群
	 */
	public Population initPopulation(int chromosomeLength) {
		Population population = new Population(this.populationSize, chromosomeLength);
		return population;
	}
	
	/**
	 * 适应度由 (1/路线总距离)计算 ：更短的距有更高的评分
	 * @param traveler
	 * @param cities
	 * @return
	 */
	public double calculateFitness(Traveler traveler, City cities[]) {
		
		// 获取当前个体的路径
		Route route = new Route(traveler, cities);
		// 计算适应度
		double fitness = 1 / route.getDistance();		
		// 保存适应度
		traveler.setFitness(fitness);
		
		// 散列表保存适应度值
		//this.fitnessMap.put(traveler, fitness);
		
		return fitness;
	}
	
	/**
	 * 评估所有的个体，计算并保存每个个体的适应度
	 * 保存种群平均适应度
	 * @param population
	 * @param cities
	 */
	public void evaluatePopulation(Population population, City cities[]) {
		
		// 并行策略
		IntStream.range(0, population.size()).parallel()
		      .forEach(i -> this.calculateFitness(population.getTraveler(i), cities));
		
		double populationFitness = 0;		
		// 循环计算总体适应度
		for (Traveler traveler : population.getTravelers()) {
			populationFitness += this.calculateFitness(traveler, cities);
		}
	
		population.setPopulationFitness(populationFitness);
		// 平均种群适应度
		double populationAvgFitness = populationFitness / population.size();
		population.setPopulationAvgFitness(populationAvgFitness);
	}
	
	/**
	 * 终止检查
	 * 实际上并不知道完美解决方案是什么时候得到,故设置约束是世代数的上限
	 * @param generationCount	
	 * 				已经进行的的世代数
	 * @param maxGenerations	
	 * 				截止的最大世代数
	 * @return true/false
	 * 				截止返回true，否则返回false
	 */
	public boolean isTerminalCondition(int generationCount, int maxGenerations) {
		return (generationCount > maxGenerations);
	}
	
	/**
	 * 采用轮盘赌的方式选择进行交叉的双亲
	 * 在代码实现中采用反轮盘赌方式，即先选择一个随机位置，然后反向工作，弄清哪个个体位于该位置
	 * @param population
	 * @return
	 */
	public Traveler selectParentRoulett(Population population) {		
		// 获取当前群体
		Traveler travelers[] = population.getTravelers();		
		// 旋转轮盘
		double populationFitness = population.getPopulationFitness();
		// 先设置轮盘中的位置
		double rouletteWheelPosition = Math.random() * populationFitness;		
		// 寻找位于该位置的个体
		double spinWheel = 0;
		for (Traveler traveler : travelers) {
			// 采用累加的方式寻找目标个体
			spinWheel += traveler.getFitness();
			if (spinWheel >= rouletteWheelPosition) {
				return traveler;
			}
		}		
		return travelers[population.size() - 1];
	}
	
	/**
	 * 对个体进行交叉，采用锦标赛选择进行交叉的双亲
	 * 非自适应遗传算法
	 * @param population
	 * 				当前种群
	 * @return newPopulation
	 * 				后代种群
	 */
	public Population noAdaptiveCrossoverPopulation(Population population) {
		// 创建后代群体
		Population newPopulation = new Population(population.size());		
		// 依据适应度(降序)循环当前群体
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// 获取第一个双亲
			Traveler parent1 = population.getFittest(populationIndex);

			// 判断是否要进行交叉,即判断是否满足交叉率和是否为精英群体
			if (this.crossoverRate > Math.random() && populationIndex >= elitismCount) {
				// 寻找第二个双亲
				Traveler parent2 = this.selectParentRoulett(population);
				
				// 创建一个后代的染色体
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// 初始化后代染色体
				Traveler offspring = new Traveler(offspringChromosome);
				
				// 获取双亲染色体子集
				// 第一个节点
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// 第二个节点
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				
				// 小的作为开始节点，大的作为结束节点
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// 循环将第一个双亲的该片段基因填充到后代
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// 循环遍历第二个双亲的周游城市
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// 先遍历第二个双亲的第二节基因(该片段基因第一个位置)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// 即第二节基因遍历结束，返回第一节基因(第一个位置)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// 如果后代中没有当前基因(城市)，则添入后代
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// 循环寻找空白基因位置
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// 空白位置找到，添加基因
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));
								break;
							}
						}
					}
				}
				// 将后代添加到后代种群
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// 不进行交叉直接加入后代种群
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// 返回后代种群
		return newPopulation;
	}

	/**
	 * 对个体进行交叉，采用锦标赛选择进行交叉的双亲
	 * 采用自遗传算法，动态调整交叉率
	 * @param population
	 * 				当前种群
	 * @return newPopulation
	 * 				后代种群
	 */
	public Population adaptiveCrossoverPopulation(Population population) {
		// 创建后代群体
		Population newPopulation = new Population(population.size());		
		// 依据适应度(降序)循环当前群体
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// 获取第一个双亲
			Traveler parent1 = population.getFittest(populationIndex);	
			
			// 获取种群中最佳个体的适应度
			double bestFitness = population.getFittest(0).getFitness();				
			// 计算自适应交叉率
			double adaptiveCrossoverRate = this.crossoverRate;			
			// 若当前个体的适应度大于总体平均适应度,调整交叉率			
			if (parent1.getFitness() > population.getPopulationAvgFitness()) {
				double fitnessDelta1 = bestFitness - parent1.getFitness();
				double fitnessDelta2 = bestFitness - population.getPopulationAvgFitness();
				adaptiveCrossoverRate = (fitnessDelta1 / fitnessDelta2) * this.crossoverRate;	//	降低交叉率
			} 
			
			// 判断是否要进行交叉,即判断是否满足交叉率和是否为精英群体
			if (adaptiveCrossoverRate > Math.random() && populationIndex >= elitismCount) {
				// 寻找第二个双亲
				Traveler parent2 = this.selectParentRoulett(population);				
				// 创建一个后代的染色体
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// 初始化后代染色体
				Traveler offspring = new Traveler(offspringChromosome);
				
				// 获取双亲染色体子集
				// 第一个节点
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// 第二个节点
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				// 小的作为开始节点，大的作为结束节点
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// 循环将第一个双亲的该片段基因填充到后代
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// 循环遍历第二个双亲的周游城市
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// 先遍历第二个双亲的第二节基因(该片段基因第一个位置)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// 即第二节基因遍历结束，返回第一节基因(第一个位置)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// 如果后代中没有当前基因(城市)，则添入后代
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// 循环寻找空白基因位置
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// 空白位置找到，添加基因
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));								
								break;	// 跳出当前循环
							}
						}
					}
				}
				// 将后代添加到后代种群
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// 不进行交叉直接加入后代种群
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// 返回后代种群
		return newPopulation;
	}
	
	/**
	 * 对个体进行交叉，采用锦标赛选择进行交叉的双亲
	 * 非自适应遗传算法
	 * @param population
	 * 				当前种群
	 * @return newPopulation
	 * 				后代种群
	 */
	public Population coolCrossoverPopulation(Population population) {
		// 创建后代群体
		Population newPopulation = new Population(population.size());		
		// 依据适应度(降序)循环当前群体
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// 获取第一个双亲
			Traveler parent1 = population.getFittest(populationIndex);

			// 判断是否要进行交叉,即判断是否满足交叉率和是否为精英群体
			if ((this.crossoverRate * this.getTemperature())> Math.random() && populationIndex >= elitismCount) {
				// 寻找第二个双亲
				Traveler parent2 = this.selectParentRoulett(population);				
				// 创建一个后代的染色体
				int offspringChromosome[] = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);	// 初始化后代染色体
				Traveler offspring = new Traveler(offspringChromosome);
				
				// 获取双亲染色体子集
				// 第一个节点
				int substrPos1 = (int)(Math.random() * parent1.getChromosomeLength());
				// 第二个节点
				int substrPos2 = (int)(Math.random() * parent1.getChromosomeLength());
				
				// 小的作为开始节点，大的作为结束节点
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);
				
				// 循环将第一个双亲的该片段基因填充到后代
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}			
				// 循环遍历第二个双亲的周游城市
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					// 先遍历第二个双亲的第二节基因(该片段基因第一个位置)
					int parent2GeneIndex = i + endSubstr;
					if (parent2GeneIndex >= parent2.getChromosomeLength()) {
						// 即第二节基因遍历结束，返回第一节基因(第一个位置)
						parent2GeneIndex -= parent2.getChromosomeLength();
					}
					
					// 如果后代中没有当前基因(城市)，则添入后代
					if (offspring.containsGene(parent2.getGene(parent2GeneIndex)) == false) {
						// 循环寻找空白基因位置
						for (int j = 0; j < offspring.getChromosomeLength(); j++) {
							// 空白位置找到，添加基因
							if (offspring.getGene(j) == -1) {
								offspring.setGene(j, parent2.getGene(parent2GeneIndex));
								break;
							}
						}
					}
				}
				// 将后代添加到后代种群
				newPopulation.setTraveler(populationIndex, offspring);				
			}else {
				// 不进行交叉直接加入后代种群
				newPopulation.setTraveler(populationIndex, parent1);
			}
		}
		// 返回后代种群
		return newPopulation;
	}
	
	/**
	 * 遍历每个个体的染色体，基于变异率，考虑每个基因是否进行位交换变异
	 * 非自适应遗传
	 * @param population
	 * 				当前个体
	 * @return newPopulation
	 * 				变异后的后代群体
	 */
	public Population noAdaptiveMutatePopulation(Population population) {
		// 创建新的后代种群
		Population newPopulation = new Population(this.populationSize);
		
		// 依据适应度(降序)循环当前种群
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			
			// 如果属于精英群体,则跳过
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// 循环当前个体基因
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					// 判断是否进行变异
					if (this.mutationRate > Math.random()) {
						// 获取变异基因位置
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// 进行交换的基因
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// 交换基因
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}					
				}
			}
			// 添加至后代群体
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// 返回完成变异的群体
		return newPopulation;
	}
	
	/**
	 * 遍历每个个体的染色体，基于变异率，考虑每个基因是否进行位交换变异
	 * 模拟退火算法
	 * @param population
	 * 				当前个体
	 * @return newPopulation
	 * 				变异后的后代群体
	 */
	public Population coolMutatePopulation(Population population) {
		// 创建新的后代种群
		Population newPopulation = new Population(this.populationSize);
		
		// 依据适应度(降序)循环当前种群
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			
			// 如果属于精英群体,则跳过
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// 循环当前个体基因
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					// 判断是否进行变异
					if ((this.mutationRate *this.getTemperature()) > Math.random()) {
						// 获取变异基因位置
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// 进行交换的基因
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// 交换基因
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}					
				}
			}
			// 添加至后代群体
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// 返回完成变异的群体
		return newPopulation;
	}
	

	/**
	 * 遍历每个个体的染色体，基于变异率，考虑每个基因是否进行位交换变异
	 * 自适应遗传
	 * @param population
	 * 				当前个体
	 * @return newPopulation
	 * 				变异后的后代群体
	 */
	public Population adaptiveMutatePopulation(Population population) {
		// 创建新的后代种群
		Population newPopulation = new Population(this.populationSize);
		
		// 依据适应度(降序)循环当前种群
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {			
			Traveler traveler = population.getFittest(populationIndex);
			// 获取种群中最佳个体的适应度
			double bestFitness = population.getFittest(0).getFitness();
			
			// 计算自适应变异率
			double adaptieMutationRate = this.mutationRate;
			
			// 若当前个体的适应度大于总体平均适应度,调整变异率			
			if (traveler.getFitness() > population.getPopulationAvgFitness()) {
				double fitnessDelta1 = bestFitness - traveler.getFitness();
				double fitnessDelta2 = bestFitness - population.getPopulationAvgFitness();
				adaptieMutationRate = (fitnessDelta1 / fitnessDelta2) * this.mutationRate;	//	降低变异率
			} 
			
			// 如果属于精英群体,则跳过
			if (populationIndex >= this.elitismCount) {
			    // System.out.println("Mutating population member " + populationIndex);
				// 循环当前个体基因
				for (int geneIndex = 0; geneIndex < traveler.getChromosomeLength(); geneIndex++) {
					if (adaptieMutationRate > Math.random()) {
						// 获取变异基因位置
						int newGenePos = (int)(Math.random() * traveler.getChromosomeLength());
						// 进行交换的基因
						int gene1 = traveler.getGene(newGenePos);
						int gene2 = traveler.getGene(geneIndex);
						// 交换基因
						traveler.setGene(geneIndex, gene1);
						traveler.setGene(newGenePos, gene2);
					}
				}
			}
			// 添加至后代群体
			newPopulation.setTraveler(populationIndex, traveler);
		}
		// 返回完成变异的群体
		return newPopulation;
	}
	
	/**
	 * 调整温度
	 */
	public void coolTemperature() {
		this.temperature *= (1 - this.coolingRate);
	}
	
	public double getTemperature() {
		return temperature;
	}
}
