/*
 * Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
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
package com.datatorrent.contrib.siteops;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import com.datatorrent.api.LocalMode;
import com.datatorrent.contrib.siteops.ApacheAccessLogAnalaysis;
/**
 */
public class ApacheAccessLogAnalysisTest
{
  @Test
  public void testApplication() throws Exception
  {
  	//LocalMode.runApp(new ApacheAccessLogAnalaysis(), 10000);
  	ApacheAccessLogAnalaysis app = new ApacheAccessLogAnalaysis();
    LocalMode lma = LocalMode.newInstance();
    app.populateDAG(lma.getDAG(), new Configuration(false));
    LocalMode.Controller lc = lma.getController();
    lc.setHeartbeatMonitoringEnabled(false);
    lc.run();
  }
}
