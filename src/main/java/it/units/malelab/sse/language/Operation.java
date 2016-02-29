/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse.language;

/**
 *
 * @author eric
 */
public class Operation {
  
  private final OpCode opCode;
  private final int i0;
  private final int i1;
  private final int i2;

  public Operation(OpCode opCode, int i0, int i1, int i2) {
    this.opCode = opCode;
    this.i0 = i0;
    this.i1 = i1;
    this.i2 = i2;
  }

  public OpCode getOpCode() {
    return opCode;
  }

  public int getI0() {
    return i0;
  }

  public int getI1() {
    return i1;
  }

  public int getI2() {
    return i2;
  }

  @Override
  public String toString() {
    return String.format(opCode.getFormatString(), i0, i1, i2);
  }
  
}