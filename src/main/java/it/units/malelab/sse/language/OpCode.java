/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse.language;

public enum OpCode {
  ADD("n[%3$d] = n[%1$d]+n[%2$d]"),
  SUBTRACT("n[%3$d] = n[%1$d]-n[%2$d]"),
  DIVIDE("n[%3$d] = n[%1$d]/n[%2$d]"),
  MULTIPLY("n[%3$d] = n[%1$d]*n[%2$d]"),
  JUMP_IF_GT("if n[%1$d]>n[%2$d] jump to %3$+d"),
  SET("n[%3$d] = %1$d"),
  SET_LIST_ELEMENT("l[%1$d].set(n[%3$d], n[%2$d])"),
  GET_LIST_ELEMENT("n[%3$d] = l[%1$d].get(n[%2$d])"),
  ADD_ELEMENT_TO_LIST("l[%1$d].add(n[%2$d])"),
  REMOVE_ELEMENT_FROM_LIST("l[%1$d].remove(n[%2$d])"),
  GET_LIST_SIZE("n[%3$d] = l[%1$d].size()"),
  GET_LIST_ELEMENT_INDEX("n[%3$d] = l[%1$d].indexOf(n[%2$d])");
  OpCode(String formatString) {
    this.formatString = formatString;
  }  
  private final String formatString;

  public String getFormatString() {
    return formatString;
  }
}
