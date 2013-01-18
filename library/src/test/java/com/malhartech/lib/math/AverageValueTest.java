/**
 * Copyright (c) 2012-2012 Malhar, Inc. All rights reserved.
 */
package com.malhartech.lib.math;

import com.malhartech.api.Sink;
import com.malhartech.engine.Tuple;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Functional tests for {@link com.malhartech.lib.math.AverageValue}<p>
 *
 */
public class AverageValueTest
{
  private static Logger log = LoggerFactory.getLogger(AverageValueTest.class);

  class TestSink implements Sink
  {
    List<Object> collectedTuples = new ArrayList<Object>();

    @Override
    public void process(Object payload)
    {
      if (payload instanceof Tuple) {
      }
      else {
        collectedTuples.add(payload);
      }
    }
  }

  /**
   * Test operator logic emits correct results.
   */
  @Test
  public void testNodeProcessing()
  {
    SumValue<Double> doper = new SumValue<Double>();
    SumValue<Float> foper = new SumValue<Float>();
    SumValue<Integer> ioper = new SumValue<Integer>();
    SumValue<Long> loper = new SumValue<Long>();
    SumValue<Short> soper = new SumValue<Short>();
    doper.setType(Double.class);
    foper.setType(Float.class);
    ioper.setType(Integer.class);
    loper.setType(Long.class);
    soper.setType(Short.class);

    testNodeSchemaProcessing(doper);
    testNodeSchemaProcessing(foper);
    testNodeSchemaProcessing(ioper);
    testNodeSchemaProcessing(loper);
    testNodeSchemaProcessing(soper);
  }

  public void testNodeSchemaProcessing(SumValue oper)
  {
    TestSink averageSink = new TestSink();
    oper.average.setSink(averageSink);

    oper.beginWindow(0); //

    Double a = new Double(2.0);
    Double b = new Double(20.0);
    Double c = new Double(1000.0);

    oper.data.process(a);
    oper.data.process(b);
    oper.data.process(c);

    a = 1.0;
    oper.data.process(a);
    a = 10.0;
    oper.data.process(a);
    b = 5.0;
    oper.data.process(b);

    b = 12.0;
    oper.data.process(b);
    c = 22.0;
    oper.data.process(c);
    c = 14.0;
    oper.data.process(c);

    a = 46.0;
    oper.data.process(a);
    b = 2.0;
    oper.data.process(b);
    a = 23.0;
    oper.data.process(a);

    oper.endWindow(); //

    Assert.assertEquals("number emitted tuples", 1, averageSink.collectedTuples.size());
    for (Object o: averageSink.collectedTuples) { // count is 12
      Integer val = ((Number)o).intValue();
      Assert.assertEquals("emitted average value was was ", new Integer(1157 / 12), val);
    }
  }
}
