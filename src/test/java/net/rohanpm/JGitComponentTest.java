/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rohanpm;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class JGitComponentTest extends CamelTestSupport {

    @Test
    public void testJGit() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("timer:oneshot?delay=0&repeatCount=1")
                  .to("jgit://init?directory=/tmp/jgit-init-test&bare=true")
                  .to("jgit://fetch?remote=https://github.com/rohanpm/Gerrit-Client.git&refSpecs=refs/heads/*:refs/heads/*")
                  .to("mock:result");

                from("timer:oneshot?delay=0&repeatCount=1")
                        .to("jgit://lsRemoteRepository?remote=https://github.com/rohanpm/Gerrit-Client.git&heads=true&tags=true")
                        .to("mock:result2");
            }
        };
    }
}
