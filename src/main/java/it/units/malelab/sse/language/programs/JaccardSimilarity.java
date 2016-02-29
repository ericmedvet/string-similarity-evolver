/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse.language.programs;

import it.units.malelab.sse.language.OpCode;
import it.units.malelab.sse.language.Operation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eric
 */
public class JaccardSimilarity {
  
  /*
  n[3] = l[0].size()          0 - 3   setta costanti
  n[4] = l[1].size()          1 - 4
  n[5] = 1                    1 - 5
  n[6] = -1                   -1 - 6
  n[1] = l[0].get(n[0])       0 0 1   costruisci intersezione
  n[2] = l[1].indexOf(n[1])   1 1 2
  if (n[9]>n[2]) jump +4      9 2 4
  n[2] = l[2].indexOf(n[1])   2 1 2
  if (n[2]>n[6]) jump +2      2 6 2
  l[2].add(n[1])              2 1 -
  n[0] = n[0]+n[5]            0 5 0
  if (n[3]>n[0]) jump -7      3 0 -7
  n[0] = 0                    0 - 0   set da lista 0
  n[1] = l[0].get(n[0])       0 0 1
  n[2] = l[3].indexOf(n[1])   3 1 2
  if (n[2]>n[6]) jump +2      2 6 2
  l[3].add(n[1])              3 1 -
  n[0] = n[0]+n[5]            0 5 0
  if (n[3]>n[0]) jump -5      3 0 -5
  n[0] = 0                    0 - 0   set aggiunto da lista 1
  n[1] = l[1].get(n[0])       1 0 1
  n[2] = l[3].indexOf(n[1])   3 1 2
  if (n[2]>n[6]) jump +2      2 6 2
  l[3].add(n[1])              3 1 -
  n[0] = n[0]+n[5]            0 5 0
  if (n[4]>n[0]) jump -5      4 0 -5
  n[1] = l[2].size()          2 - 1   fai rapporto tra size
  n[2] = l[3].size()          3 - 2
  n[0] = n[1]/n[2]            1 2 0
  */
  
  public static List<Operation> getOperations() {
    List<Operation> os = new ArrayList<>();
    os.add(new Operation(OpCode.GET_LIST_SIZE, 0, 0, 3));
    os.add(new Operation(OpCode.GET_LIST_SIZE, 1, 0, 4));
    os.add(new Operation(OpCode.SET, 1, 0, 5));
    os.add(new Operation(OpCode.SET, -1, 0, 6));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT, 0, 0, 1));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT_INDEX, 1, 1, 2));
    os.add(new Operation(OpCode.JUMP_IF_GT, 9, 2, 4));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT_INDEX, 2, 1, 2));
    os.add(new Operation(OpCode.JUMP_IF_GT, 2, 6, 2));
    os.add(new Operation(OpCode.ADD_ELEMENT_TO_LIST, 2, 1, 0));
    os.add(new Operation(OpCode.ADD, 0, 5, 0));
    os.add(new Operation(OpCode.JUMP_IF_GT, 3, 0, -7));
    os.add(new Operation(OpCode.SET, 0, 0, 0));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT, 0, 0, 1));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT_INDEX, 3, 1, 2));
    os.add(new Operation(OpCode.JUMP_IF_GT, 2, 6, 2));
    os.add(new Operation(OpCode.ADD_ELEMENT_TO_LIST, 3, 1, 0));
    os.add(new Operation(OpCode.ADD, 0, 5, 0));
    os.add(new Operation(OpCode.JUMP_IF_GT, 3, 0, -5));
    os.add(new Operation(OpCode.SET, 0, 0, 0));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT, 1, 0, 1));
    os.add(new Operation(OpCode.GET_LIST_ELEMENT_INDEX, 3, 1, 2));
    os.add(new Operation(OpCode.JUMP_IF_GT, 2, 6, 2));
    os.add(new Operation(OpCode.ADD_ELEMENT_TO_LIST, 3, 1, 0));
    os.add(new Operation(OpCode.ADD, 0, 5, 0));
    os.add(new Operation(OpCode.JUMP_IF_GT, 4, 0, -5));
    os.add(new Operation(OpCode.GET_LIST_SIZE, 2, 0, 1));
    os.add(new Operation(OpCode.GET_LIST_SIZE, 3, 0, 2));
    os.add(new Operation(OpCode.DIVIDE, 1, 2, 0));
    return os;
  }
  
}
