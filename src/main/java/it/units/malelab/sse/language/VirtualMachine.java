/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author eric
 */
public class VirtualMachine {

  //state
  private final Map<Integer, Float> numbers = new HashMap<>();
  private final Map<Integer, List<Float>> lists = new HashMap<>();

  //stats
  private final Set<Integer> numbersReadIndexes = new HashSet<>();
  private final Set<Integer> listsReadIndexes = new HashSet<>();
  private int ops = 0;
  private ErrorCode errorCode;
  private int maxOverallListSizes = 0;

  //parameters
  private final int numbersSize;
  private final int listsSize;
  private final int maxOps;

  public enum ErrorCode {

    DIVISION_BY_ZERO, WRONG_LIST_INDEX, WRONG_INDEX, MAX_OPS_EXCEEDED
  }

  public final class VMException extends RuntimeException {

    private final ErrorCode causeErrorCode;

    public VMException(ErrorCode causeErrorCode) {
      this.causeErrorCode = causeErrorCode;
    }

    public ErrorCode getCauseErrorCode() {
      return causeErrorCode;
    }

  }

  public VirtualMachine(int numbersSize, int listsSize, int maxOps) {
    this.numbersSize = numbersSize;
    this.listsSize = listsSize;
    this.maxOps = maxOps;
  }

  public void reset() {
    numbers.clear();
    lists.clear();
    numbersReadIndexes.clear();
    listsReadIndexes.clear();
    ops = 0;
    errorCode = null;
    maxOverallListSizes = 0;
  }

  public float execute(List<Operation> operations, String s0, String s1, boolean debug) {
    int programCounter = 0;
    //prepare input
    List<Float> l0 = new ArrayList<>();
    for (int i = 0; i < s0.length(); i++) {
      l0.add((float) s0.codePointAt(i));
    }
    List<Float> l1 = new ArrayList<>();
    for (int i = 0; i < s1.length(); i++) {
      l1.add((float) s1.codePointAt(i));
    }
    lists.put(0, l0);
    lists.put(1, l1);
    updateMaxOverallListSize();
    //execute
    try {
      while (true) {
        if (programCounter >= operations.size()) {
          break;
        }
        if (programCounter<0) {
          programCounter = 0;
        }
        Operation o = operations.get(programCounter);
        ops = ops + 1;
        if (ops > maxOps) {
          throw new VMException(ErrorCode.MAX_OPS_EXCEEDED);
        }
        programCounter = programCounter + 1;
        if (o.getOpCode().equals(OpCode.ADD)
                || o.getOpCode().equals(OpCode.SUBTRACT)
                || o.getOpCode().equals(OpCode.DIVIDE)
                || o.getOpCode().equals(OpCode.MULTIPLY)) {
          float num0 = getNumber(o.getI0());
          float num1 = getNumber(o.getI1());
          if (o.getOpCode().equals(OpCode.ADD)) {
            setNumber(o.getI2(), num0 + num1);
          } else if (o.getOpCode().equals(OpCode.SUBTRACT)) {
            setNumber(o.getI2(), num0 - num1);
          } else if (o.getOpCode().equals(OpCode.MULTIPLY)) {
            setNumber(o.getI2(), num0 * num1);
          } else if (o.getOpCode().equals(OpCode.DIVIDE)) {
            if (num1 == 0) {
              throw new VMException(ErrorCode.DIVISION_BY_ZERO);
            }
            setNumber(o.getI2(), num0 / num1);
          }
        } else if (o.getOpCode().equals(OpCode.SET)) {
          setNumber(o.getI2(), (float) o.getI0());
        } else if (o.getOpCode().equals(OpCode.JUMP_IF_GT)) {
          float num0 = getNumber(o.getI0());
          float num1 = getNumber(o.getI1());
          if (num0 > num1) {
            programCounter = programCounter - 1 + o.getI2();
          }
        } else if (o.getOpCode().equals(OpCode.SET_LIST_ELEMENT)) {
          List<Float> list = getList(o.getI0());
          float num0 = Math.round(getNumber(o.getI1()));
          float num1 = getNumber(o.getI2());
          if ((num0<0)||(num0>=list.size())) {
            throw new VMException(ErrorCode.WRONG_LIST_INDEX);
          }
          list.set((int)num0, num1);
        } else if (o.getOpCode().equals(OpCode.GET_LIST_ELEMENT)) {
          List<Float> list = getList(o.getI0());
          float num0 = Math.round(getNumber(o.getI1()));
          if ((num0<0)||(num0>=list.size())) {
            throw new VMException(ErrorCode.WRONG_LIST_INDEX);
          }
          setNumber(o.getI2(), list.get((int)num0));
        } else if (o.getOpCode().equals(OpCode.ADD_ELEMENT_TO_LIST)) {
          List<Float> list = getList(o.getI0());
          float num0 = getNumber(o.getI1());
          if (list.isEmpty()) {
            lists.put(o.getI0(), list);
          }
          list.add(num0);
          updateMaxOverallListSize();
        } else if (o.getOpCode().equals(OpCode.REMOVE_ELEMENT_FROM_LIST)) {
          List<Float> list = getList(o.getI0());
          float num0 = Math.round(getNumber(o.getI1()));
          if ((num0<0)||(num0>=list.size())) {
            throw new VMException(ErrorCode.WRONG_LIST_INDEX);
          }
          list.remove((int)num0);
        } else if (o.getOpCode().equals(OpCode.GET_LIST_SIZE)) {
          List<Float> list = getList(o.getI0());
          setNumber(o.getI2(), list.size());
        } else if (o.getOpCode().equals(OpCode.GET_LIST_ELEMENT_INDEX)) {
          List<Float> list = getList(o.getI0());
          float num0 = getNumber(o.getI1());
          setNumber(o.getI2(), list.indexOf(num0));
        }
      }
    } catch (VMException e) {
      errorCode = e.getCauseErrorCode();
      if (debug) {
        System.out.printf("pc=%d error=%s\n", programCounter, errorCode);
        for (int i = 0; i<programCounter; i++) {
          System.out.printf("%4d: %s\n", i, operations.get(i));
        }
      }
      return 0;
    }
    return getNumber(0);
  }

  private float getNumber(int index) {
    if ((index < 0) || (index >= numbersSize)) {
      throw new VMException(ErrorCode.WRONG_INDEX);
    }
    numbersReadIndexes.add(index);
    Float value = numbers.get(index);
    if (value == null) {
      return 0;
    }
    return value;
  }

  private float setNumber(int index, float value) {
    if ((index < 0) || (index >= numbersSize)) {
      throw new VMException(ErrorCode.WRONG_INDEX);
    }
    numbers.put(index, value);
    return value;
  }

  private List<Float> getList(int index) {
    if ((index < 0) || (index >= listsSize)) {
      throw new VMException(ErrorCode.WRONG_INDEX);
    }
    listsReadIndexes.add(index);
    List<Float> value = lists.get(index);
    if (value == null) {
      return new ArrayList<>();
    }
    return value;
  }

  private void updateMaxOverallListSize() {
    int v = 0;
    for (List<Float> list : lists.values()) {
      v = v + list.size();
    }
    if (maxOverallListSizes < v) {
      maxOverallListSizes = v;
    }
  }

  public int getOps() {
    return ops;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public int getMaxOverallListSizes() {
    return maxOverallListSizes;
  }

  public int getNumbersReadFootprint() {
    return numbersReadIndexes.size();
  }

  public int getListsReadFootprint() {
    return listsReadIndexes.size();
  }

  public int getNumbersWriteFootprint() {
    return numbers.size();
  }

  public int getListsWriteFootprint() {
    return lists.size();
  }

  public int getNumbersReadWriteFootprint() {
    Set<Integer> indexes = new HashSet<>(numbers.keySet());
    indexes.retainAll(numbersReadIndexes);
    return indexes.size();
  }

  public int getListsReadWriteFootprint() {
    Set<Integer> indexes = new HashSet<>(lists.keySet());
    indexes.retainAll(listsReadIndexes);
    return indexes.size();
  }

}