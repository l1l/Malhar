/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.demos.chart;

import com.malhartech.api.Context.OperatorContext;
import com.malhartech.api.DAG;
import com.malhartech.api.DAG.StreamMeta;
import com.malhartech.demos.yahoofinance.StockTickInput;
import com.malhartech.lib.chart.TimeSeriesAverageChartOperator;
import com.malhartech.lib.chart.TimeSeriesHighLowChartOperator;
import com.malhartech.lib.util.KeyValPair;
import org.apache.hadoop.conf.Configuration;

/**
 *
 * @author David Yan <davidyan@malhar-inc.com>
 */
public class YahooFinanceApplication extends com.malhartech.demos.yahoofinance.Application
{
  public static class YahooFinanceTimeSeriesAverageChartOperator extends TimeSeriesAverageChartOperator
  {
    public String ticker;

    @Override
    public Number convertTupleToNumber(Object tuple)
    {
      KeyValPair kvp = (KeyValPair<String, Double>)tuple;
      if (kvp.getKey().equals(ticker)) {
        return (Number)kvp.getValue();
      }
      else {
        return null;
      }
    }

  }

  public static class YahooFinanceTimeSeriesHighLowChartOperator extends TimeSeriesHighLowChartOperator
  {
    public String ticker;

    @Override
    public Number convertTupleToNumber(Object tuple)
    {
      KeyValPair kvp = (KeyValPair<String, Double>)tuple;
      if (kvp.getKey().equals(ticker)) {
        return (Number)kvp.getValue();
      }
      else {
        return null;
      }
    }

  }

  TimeSeriesAverageChartOperator getAverageChartOperator(String name, DAG dag, final String ticker)
  {
    YahooFinanceTimeSeriesAverageChartOperator op = new YahooFinanceTimeSeriesAverageChartOperator();
    op.ticker = ticker;
    return dag.addOperator(name, op);
  }

  TimeSeriesHighLowChartOperator getHighLowChartOperator(String name, DAG dag, final String ticker)
  {
    YahooFinanceTimeSeriesHighLowChartOperator op = new YahooFinanceTimeSeriesHighLowChartOperator();
    op.ticker = ticker;
    return dag.addOperator(name, op);
  }

  @Override
  public DAG getApplication(Configuration conf)
  {
    this.tickers = new String[] {"AAPL"};
    DAG dag = new DAG(conf);

    dag.getAttributes().attr(DAG.STRAM_WINDOW_SIZE_MILLIS).set(streamingWindowSizeMilliSeconds);

    StockTickInput tick = getStockTickInputOperator("StockTickInput", dag);
    tick.setOutputEvenIfZeroVolume(true);
    StreamMeta stream = dag.addStream("price", tick.price);
    for (String ticker: tickers) {
      TimeSeriesAverageChartOperator averageChartOperator = getAverageChartOperator("AverageChart_" + ticker, dag, ticker);
      TimeSeriesHighLowChartOperator highLowChartOperator = getHighLowChartOperator("HighLowChart_" + ticker, dag, ticker);
      dag.getOperatorMeta(averageChartOperator).getAttributes().attr(OperatorContext.APPLICATION_WINDOW_COUNT).set(5); // 5 seconds
      dag.getOperatorMeta(highLowChartOperator).getAttributes().attr(OperatorContext.APPLICATION_WINDOW_COUNT).set(5); // 5 seconds
      stream.addSink(averageChartOperator.in1);
      stream.addSink(highLowChartOperator.in1);
      dag.addStream("averageDummyStream_" + ticker, averageChartOperator.chart);
      dag.addStream("highLowDummyStream_" + ticker, highLowChartOperator.chart);
    }
    return dag;
  }

}