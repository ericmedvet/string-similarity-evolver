/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.units.malelab.sse.language.Operation;
import it.units.malelab.sse.language.VirtualMachine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.random.JDKRandomGenerator;

/**
 *
 * @author eric
 */
public class Main {

  public static void main(String[] args) throws IOException {
    Random random = new Random(1);
    VirtualMachine vm = new VirtualMachine(4, 4, 400);
    List<Map<Boolean, List<String>>> datasets = new ArrayList<>();
    datasets.add(Util.loadStrings("/home/eric/Documenti/esperimenti/datasets/Bills-Date.txt", random));
    datasets.add(Util.loadStrings("/home/eric/Documenti/esperimenti/datasets/Log-IP.txt", random));
    datasets.add(Util.loadStrings("/home/eric/Documenti/esperimenti/datasets/Twitter-URL.txt", random));
    
    Evaluator evaluator = new Evaluator(vm, datasets, 1, 10);

    MyGeneticAlgorithm ga = new MyGeneticAlgorithm(
            new OnePointCrossover<Integer>(),
            0.2,
            new BinaryMutation(),
            0.6,
            new TournamentSelection(10),
            evaluator
    );
    MyGeneticAlgorithm.setRandomGenerator(new JDKRandomGenerator(1));

    List<Chromosome> chromosomes = new ArrayList<>();
    for (int i = 0; i < 2000; i++) {
      chromosomes.add(new OperationsChromosome(evaluator));
    }
    Population population = new ElitisticListPopulation(chromosomes, chromosomes.size(), 0.99);
    Population finalPopulation = ga.evolve(population, new FixedGenerationCount(10000));
    List<Operation> operations = ((OperationsChromosome) finalPopulation.getFittestChromosome()).getOperations();
    for (int i = 0; i < operations.size(); i++) {
      System.out.printf("%4d: %s\n", i, operations.get(i));
    }

  }

}
