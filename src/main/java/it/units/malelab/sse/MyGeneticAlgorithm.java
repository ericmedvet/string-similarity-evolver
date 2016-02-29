/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.SelectionPolicy;
import org.apache.commons.math3.genetics.StoppingCondition;

/**
 *
 * @author eric
 */
public class MyGeneticAlgorithm extends GeneticAlgorithm {

  private int generationsEvolved;
  private final Evaluator evaluator;

  public MyGeneticAlgorithm(CrossoverPolicy crossoverPolicy, double crossoverRate, MutationPolicy mutationPolicy, double mutationRate, SelectionPolicy selectionPolicy, Evaluator evaluator) throws OutOfRangeException {
    super(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
    this.evaluator = evaluator;
  }

  @Override
  public Population evolve(Population initial, StoppingCondition condition) {
    Population current = initial;
    generationsEvolved = 0;
    while (!condition.isSatisfied(current)) {
      current = nextGeneration(current);
      generationsEvolved++;
      //obtain stats
      List<EnumMap<Evaluator.ResultType, Double>> statsList = new ArrayList<>(current.getPopulationSize());
      Iterator<Chromosome> iterator = current.iterator();
      while(iterator.hasNext()) {
        EnumMap<Evaluator.ResultType, Double> stats = ((OperationsChromosome)iterator.next()).getStats();
        if (stats!=null) {
          statsList.add(stats);
        }
      }
      Collections.sort(statsList, new Comparator<EnumMap<Evaluator.ResultType, Double>>() {
        @Override
        public int compare(EnumMap<Evaluator.ResultType, Double> stats1, EnumMap<Evaluator.ResultType, Double> stats2) {
          return Double.compare(stats1.get(Evaluator.ResultType.OVERLAPNESS), stats2.get(Evaluator.ResultType.OVERLAPNESS));
        }
      });
      EnumMap<Evaluator.ResultType, Double> bestStats = statsList.get(0);
      EnumMap<Evaluator.ResultType, Double> top10Stats = mean(statsList.subList(0, 10));
      EnumMap<Evaluator.ResultType, Double> allStats = mean(statsList);
      System.out.printf("ovp=%5.3f/%5.3f/%5.3f   ", bestStats.get(Evaluator.ResultType.OVERLAPNESS), top10Stats.get(Evaluator.ResultType.OVERLAPNESS), allStats.get(Evaluator.ResultType.OVERLAPNESS));
      System.out.printf("ops=%4.0f/%4.0f/%4.0f   ", bestStats.get(Evaluator.ResultType.AVG_OPS), top10Stats.get(Evaluator.ResultType.AVG_OPS), allStats.get(Evaluator.ResultType.AVG_OPS));
      System.out.printf("mfp=%4.0f/%4.0f/%4.0f   ", bestStats.get(Evaluator.ResultType.AVG_FOOTPRINT), top10Stats.get(Evaluator.ResultType.AVG_FOOTPRINT), allStats.get(Evaluator.ResultType.AVG_FOOTPRINT));
      System.out.printf("err=%5.3f/%5.3f/%5.3f   ", bestStats.get(Evaluator.ResultType.ERROR_RATIO), top10Stats.get(Evaluator.ResultType.ERROR_RATIO), allStats.get(Evaluator.ResultType.ERROR_RATIO));
      System.out.printf("evals=%8d\n", evaluator.getEvaluatedCount());
      System.out.println(evaluator.getErrorCodes());
    }
    return current;
  }
  
  private EnumMap<Evaluator.ResultType, Double> mean(List<EnumMap<Evaluator.ResultType, Double>> statsList) {
    EnumMap<Evaluator.ResultType, Double> meanStats = new EnumMap<>(Evaluator.ResultType.class);
    for (Evaluator.ResultType type : Evaluator.ResultType.values()) {
      double s = 0;
      double c = 0;
      for (EnumMap<Evaluator.ResultType, Double> stats : statsList) {
        if (stats.containsKey(type)) {
          s = s+stats.get(type);
          c = c+1;
        }
      }
      if (c>0) {
        meanStats.put(type, s/c);
      }
    }
    return meanStats;
  }

}
