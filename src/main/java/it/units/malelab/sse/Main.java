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
import java.util.ArrayList;
import java.util.List;
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

  public static void main(String[] args) {
    VirtualMachine vm = new VirtualMachine(4, 4, 400);
    List<Multimap<Boolean, String>> datasets = new ArrayList<>();
    ArrayListMultimap<Boolean, String> dataset0 = ArrayListMultimap.create();
    dataset0.put(Boolean.TRUE, "02/03/1979");
    dataset0.put(Boolean.TRUE, "07/02/2011");
    dataset0.put(Boolean.FALSE, "eric");
    dataset0.put(Boolean.FALSE, "alice");
    ArrayListMultimap<Boolean, String> dataset1 = ArrayListMultimap.create();
    dataset1.put(Boolean.TRUE, "@EricMedvetTs");
    dataset1.put(Boolean.TRUE, "@MaleLabTs");
    dataset1.put(Boolean.FALSE, "ecolo;");
    dataset1.put(Boolean.FALSE, "#petaloso");
    datasets.add(dataset0);
    datasets.add(dataset1);
    Evaluator evaluator = new Evaluator(vm, datasets);

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
