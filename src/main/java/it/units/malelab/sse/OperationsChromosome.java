/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse;

import it.units.malelab.sse.language.OpCode;
import it.units.malelab.sse.language.Operation;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.BinaryChromosome;
import org.apache.commons.math3.genetics.Chromosome;

/**
 *
 * @author eric
 */
public class OperationsChromosome extends BinaryChromosome {
  
  private static final int N_OPS = 100;
  private static final int MAX_INDEX = 10;
  private static final int BITS;
  private static final int BITS_PER_OP;
  private static final int BITS_PER_OPCODE;
  private static final int BITS_PER_INDEX;
  static {
    BITS_PER_OPCODE = (int)Math.ceil(Math.log(OpCode.values().length)/Math.log(2));
    BITS_PER_INDEX = (int)Math.ceil(Math.log(MAX_INDEX)/Math.log(2));
    BITS_PER_OP = 1+BITS_PER_OPCODE+BITS_PER_INDEX*3;
    BITS = N_OPS*BITS_PER_OP;
  }
  
  private final Evaluator evaluator;
  private EnumMap<Evaluator.ResultType, Double> stats;
  private double fitness;

  public OperationsChromosome(Evaluator evaluator) {
    super(BinaryChromosome.randomBinaryRepresentation(BITS));
    this.evaluator = evaluator;
  }

  @Override
  public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
    return new OperationsChromosome(evaluator);
  }

  @Override
  public double fitness() {
    if (stats==null) {
      stats = evaluator.evaluate(getOperations());
      fitness = stats.get(Evaluator.ResultType.OVERLAPNESS);
    }
    return fitness;
  }
  
  public List<Operation> getOperations() {
    List<Operation> operations = new ArrayList<>();
    for (int i = 0; i<N_OPS; i++) {
      Operation operation = getOperation(i);
      if (operation!=null) {
        operations.add(operation);
      }
    }
    return operations;
  }
  
  private Operation getOperation(int index) {
    List<Integer> bits = getRepresentation().subList(index*BITS_PER_OP, (index+1)*BITS_PER_OP);
    if (bits.get(0)==0) {
      return null;
    }
    int opCodeIndex = toInt(bits.subList(1, 1+BITS_PER_OPCODE), OpCode.values().length);
    int index0 = toInt(bits.subList(1+BITS_PER_OPCODE+BITS_PER_INDEX*0, 1+BITS_PER_OPCODE+BITS_PER_INDEX*1), MAX_INDEX);
    int index1 = toInt(bits.subList(1+BITS_PER_OPCODE+BITS_PER_INDEX*1, 1+BITS_PER_OPCODE+BITS_PER_INDEX*2), MAX_INDEX);
    int index2 = toInt(bits.subList(1+BITS_PER_OPCODE+BITS_PER_INDEX*2, 1+BITS_PER_OPCODE+BITS_PER_INDEX*3), MAX_INDEX);
    OpCode opCode = OpCode.values()[opCodeIndex];
    if (opCode.equals(OpCode.JUMP_IF_GT)) {
      index2 = index2-MAX_INDEX/2;
    }
    return new Operation(opCode, index0, index1, index2);
  }
  
  private int toInt(List<Integer> bits, int maxValue) {
    int sum = 0;
    for (int i = 0; i<bits.size(); i++) {
      sum = sum+bits.get(i)*(int)Math.pow(2, i);
    }
    return sum%maxValue;
  }

  public EnumMap<Evaluator.ResultType, Double> getStats() {
    return stats;
  }

  @Override
  public int compareTo(Chromosome another) {
    int fitnessComparison = -Double.compare(getFitness(), another.getFitness());
    int errorRatioComparison = -Double.compare(getStats().get(Evaluator.ResultType.ERROR_RATIO), ((OperationsChromosome)another).getStats().get(Evaluator.ResultType.ERROR_RATIO));
    int opsComparison = Double.compare(getStats().get(Evaluator.ResultType.AVG_OPS), ((OperationsChromosome)another).getStats().get(Evaluator.ResultType.AVG_OPS));
    if (fitnessComparison!=0) {
      return fitnessComparison;
    }
    if (errorRatioComparison!=0) {
      return errorRatioComparison;
    }
    return opsComparison;
  }
  
}
