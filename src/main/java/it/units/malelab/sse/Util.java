/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.units.malelab.sse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author eric
 */
public class Util {
  
  public static Map<Boolean, List<String>> loadStrings(String fileName, Random random) throws FileNotFoundException, IOException {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    Pattern pattern = Pattern.compile(br.readLine());
    StringBuilder sb = new StringBuilder();
    while (true) {
      String line = br.readLine();
      if (line==null) {
        break;
      }
      sb.append(line+"\n");
    }
    Matcher matcher = pattern.matcher(sb.toString());
    int lastEnd = 0;
    Set<String> negativesSet = new LinkedHashSet<>();
    Set<String> positivesSet = new LinkedHashSet<>();
    while (matcher.find()) {
      int start = matcher.start(1);
      if (start>lastEnd) {
        negativesSet.add(sb.substring(lastEnd, start));
      }
      positivesSet.add(matcher.group(1));
      lastEnd = matcher.end(1);
    }
    List<String> positives = new ArrayList<>(positivesSet);
    List<String> negatives = new ArrayList<>();
    for (String negative : negativesSet) {
      int length = positives.get(random.nextInt(positives.size())).length();
      if (negative.length()>length) {
        int start = random.nextInt(negative.length()-length);
        negatives.add(negative.substring(start, length+start));
      } else {
        negatives.add(negative);
      }
    }
    Map<Boolean, List<String>> strings = new HashMap<>();
    strings.put(Boolean.TRUE, positives);
    strings.put(Boolean.FALSE, negatives);
    return strings;
  }
  
}
