/*
 * Copyright (c) 2013 Malhar Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.lib.algo;

import com.datatorrent.lib.algo.FilterKeyVals;
import com.datatorrent.lib.testbench.CountTestSink;

import java.util.HashMap;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Performance tests for {@link com.datatorrent.lib.algo.FilterKeyVals}<p>
 *
 */
public class FilterKeyValsBenchmark
{
  private static Logger log = LoggerFactory.getLogger(FilterKeyValsBenchmark.class);

  /**
   * Test node logic emits correct results
   */
  @Test
  @SuppressWarnings( {"SleepWhileInLoop", "rawtypes", "unchecked"})
  @Category(com.datatorrent.lib.annotation.PerformanceTestCategory.class)
  public void testNodeProcessing() throws Exception
  {
    FilterKeyVals<String, Number> oper = new FilterKeyVals<String, Number>();

    CountTestSink sortSink = new CountTestSink<HashMap<String, Number>>();
    oper.filter.setSink((CountTestSink<Object>)sortSink);
    HashMap<String, Number> filter = new HashMap<String, Number>();
    filter.put("b", 2);
    oper.setKeyVals(filter);
    oper.clearKeys();

    filter.clear();
    filter.put("e", 200);
    filter.put("f", 2);
    filter.put("blah", 2);
    oper.setKeyVals(filter);
    filter.clear();
    filter.put("a", 2);
    oper.setKeyVals(filter);

    oper.beginWindow(0);
    int numTuples = 10000000;
    HashMap<String, Number> input = new HashMap<String, Number>();
    for (int i = 0; i < numTuples; i++) {
      oper.setInverse(false);
      input.put("a", 2);
      input.put("b", 5);
      input.put("c", 7);
      input.put("d", 42);
      input.put("e", 202);
      input.put("e", 200);
      input.put("f", 2);
      oper.data.process(input);

      input.clear();
      input.put("a", 5);
      oper.data.process(input);
      input.clear();
      input.put("a", 2);
      input.put("b", 33);
      input.put("f", 2);
      oper.data.process(input);

      input.clear();
      input.put("b", 6);
      input.put("a", 2);
      input.put("j", 6);
      input.put("e", 2);
      input.put("dd", 6);
      input.put("blah", 2);
      input.put("another", 6);
      input.put("notmakingit", 2);
      oper.data.process(input);

      input.clear();
      input.put("c", 9);
      oper.setInverse(true);
      oper.data.process(input);
    }
    oper.endWindow();
    log.debug(String.format("\nBenchmarked %d tuples with %d emitted", numTuples * 20, sortSink.getCount()));
  }
}