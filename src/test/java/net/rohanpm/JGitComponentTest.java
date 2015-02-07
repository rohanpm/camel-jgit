/**
 * Copyright 2015 Rohan McGovern <rohan@mcgovern.id.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rohanpm;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;

public class JGitComponentTest extends CamelTestSupport {
    @Produce(uri="direct:test-bad-parameter")
    protected ProducerTemplate produceBadParameter;

    @Produce(uri="direct:test-init")
    protected ProducerTemplate produceInit;

    private File tempDirectory;

    @Override
    protected void doPreSetup() throws Exception {
        tempDirectory = Files.createTempDirectory("jgit-test").toFile();
        tempDirectory.deleteOnExit();
        System.setProperty("jgit-test.tmpdir", tempDirectory.getAbsolutePath());
    }

    @After
    public void deleteTempDir() throws IOException {
        FileUtils.deleteDirectory(tempDirectory);
    }

    @Test
    public void testBadParameter() throws Exception {
        final MockEndpoint errorMock = getMockEndpoint("mock:error-bad-parameter");
        final MockEndpoint resultMock = getMockEndpoint("mock:result-bad-parameter");

        resultMock.expectedMessageCount(0);
        errorMock.expectedMessageCount(1);

        errorMock.expectedMessagesMatches(
                header(Exchange.EXCEPTION_CAUGHT).method("getMessage")
                        .contains("Invalid properties for org.eclipse.jgit.api.FetchCommand: [badParameter]"));

        produceBadParameter.sendBody(mock(Repository.class));

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInit() throws InterruptedException {
        final MockEndpoint mock = getMockEndpoint("mock:result-init");

        mock.expectedMessageCount(1);
        mock.expectedMessagesMatches(
                body().isInstanceOf(Repository.class));
        mock.expectedMessagesMatches(
                body().method("isBare").isEqualTo(true));
        mock.expectedMessagesMatches(
                body().method("getDirectory").isEqualTo(tempDirectory.toPath().resolve("some-repo").toFile()));

        produceInit.sendBody(null);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:test-bad-parameter")
                        .onException(Throwable.class)
                            .handled(true)
                            .to("mock:error-bad-parameter")
                        .end()
                        .to("jgit://fetch?remote=git://git.example.com/repo&badParameter=quux")
                        .to("mock:result-bad-parameter");

                from("direct:test-init")
                        .to("jgit:init?directory={{jgit-test.tmpdir}}/some-repo&bare=true")
                        .convertBodyTo(Repository.class)
                        .to("mock:result-init");
            }
        };
    }
}
