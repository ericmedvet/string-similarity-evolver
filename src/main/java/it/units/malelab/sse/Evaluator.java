/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import it.units.malelab.sse.language.Operation;
import it.units.malelab.sse.language.VirtualMachine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eric
 */
public class Evaluator {

  private final VirtualMachine vm;
  private final List<Map<Boolean, List<String>>> datasets;
  private final int minStringsPerDataset;
  private final int maxStringsPerDataset;
  
  private int evaluatedCount = 0;
  private int stringsPerDataset;
  private final Multiset<VirtualMachine.ErrorCode> errorCodes = HashMultiset.create();

  public enum ResultType {
    OVERLAPNESS, AVG_OPS, ERROR_RATIO, AVG_FOOTPRINT, SIZE
  }

  public Evaluator(VirtualMachine vm, List<Map<Boolean, List<String>>> datasets, int minStringsPerDataset, int maxStringsPerDataset) {
    this.vm = vm;
    this.datasets = datasets;
    this.minStringsPerDataset = minStringsPerDataset;
    this.maxStringsPerDataset = maxStringsPerDataset;
    stringsPerDataset = minStringsPerDataset;
  }

  public EnumMap<ResultType, Double> evaluate(List<Operation> operations) {
    double count = 0;
    EnumMap<ResultType, Double> stats = new EnumMap<>(ResultType.class);
    stats.put(ResultType.AVG_FOOTPRINT, 0d);
    stats.put(ResultType.AVG_OPS, 0d);
    stats.put(ResultType.ERROR_RATIO, 0d);
    stats.put(ResultType.OVERLAPNESS, 0d);
    for (Map<Boolean, List<String>> dataset : datasets) {
      List<Float> ttSimilarities = new ArrayList<>();
      List<Float> tfSimilarities = new ArrayList<>();
      for (String string1 : dataset.get(Boolean.TRUE).subList(0, Math.min(stringsPerDataset, dataset.get(Boolean.TRUE).size()))) {
        for (String string2 : dataset.get(Boolean.TRUE).subList(0, Math.min(stringsPerDataset, dataset.get(Boolean.TRUE).size()))) {
          count = count + 1;
          ttSimilarities.add(execute(operations, string1, string2, stats));
        }
        for (String string2 : dataset.get(Boolean.FALSE).subList(0, Math.min(stringsPerDataset, dataset.get(Boolean.FALSE).size()))) {
          count = count + 1;
          tfSimilarities.add(execute(operations, string1, string2, stats));
        }
      }
      double overlapness100 = computeOverlapness(ttSimilarities, tfSimilarities, 1);
      double overlapness90 = computeOverlapness(ttSimilarities, tfSimilarities, 0.9f);
      stats.put(ResultType.OVERLAPNESS, stats.get(ResultType.OVERLAPNESS) + (overlapness100 + overlapness90) / 2);
    }
    stats.put(ResultType.OVERLAPNESS, stats.get(ResultType.OVERLAPNESS) / datasets.size());
    stats.put(ResultType.AVG_FOOTPRINT, stats.get(ResultType.AVG_FOOTPRINT) / count);
    stats.put(ResultType.AVG_OPS, stats.get(ResultType.AVG_OPS) / count);
    stats.put(ResultType.ERROR_RATIO, stats.get(ResultType.ERROR_RATIO) / count);
    return stats;
  }

  private double computeOverlapness(List<Float> ttSimilarities, List<Float> tfSimilarities, float percentile) {
    Collections.sort(ttSimilarities);
    Collections.sort(tfSimilarities);
    float lastTfSimilarity = tfSimilarities.get((int) Math.min(Math.floor(tfSimilarities.size() * percentile), tfSimilarities.size() - 1));
    float firstTtSimilarity = ttSimilarities.get((int) Math.floor(ttSimilarities.size() * (1 - percentile)));
    int overlapping = 0;
    for (float ttSimilarity : ttSimilarities) {
      if ((ttSimilarity >= firstTtSimilarity) && (ttSimilarity <= lastTfSimilarity)) {
        overlapping = overlapping + 1;
      }
    }
    for (float tfSimilarity : tfSimilarities) {
      if ((tfSimilarity >= firstTtSimilarity) && (tfSimilarity <= lastTfSimilarity)) {
        overlapping = overlapping + 1;
      }
    }
    return (double) (overlapping) / (double) (ttSimilarities.size() + tfSimilarities.size());
  }

  private float execute(List<Operation> operations, String string1, String string2, EnumMap<ResultType, Double> stats) {
    vm.reset();
    float s = vm.execute(operations, string1, string2, false);
    evaluatedCount = evaluatedCount+1;
    stats.put(ResultType.AVG_OPS, stats.get(ResultType.AVG_OPS) + vm.getOps());
    stats.put(ResultType.AVG_FOOTPRINT, stats.get(ResultType.AVG_FOOTPRINT) + vm.getNumbersWriteFootprint() + vm.getMaxOverallListSizes());
    if (vm.getErrorCode() != null) {
      stats.put(ResultType.ERROR_RATIO, stats.get(ResultType.ERROR_RATIO) + 1);
      errorCodes.add(vm.getErrorCode());
    }
    return s;
  }

  public Multiset<VirtualMachine.ErrorCode> getErrorCodes() {
    return errorCodes;
  }

  public int getEvaluatedCount() {
    return evaluatedCount;
  }

}
