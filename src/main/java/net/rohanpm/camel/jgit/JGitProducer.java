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
package net.rohanpm.camel.jgit;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.IntrospectionSupport;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * The JGit producer.
 */
public class JGitProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(JGitProducer.class);
    private JGitEndpoint endpoint;

    public JGitProducer(JGitEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        // TODO: extract from headers as well
        final JGitReflect reflect = endpoint.getJGitReflect();
        final String commandName = endpoint.getCommand();
        final TypeConverter converter = endpoint.getCamelContext().getTypeConverter();

        Callable<?> command = reflect.getCommand(commandName);
        if (command == null) {
            Object repo = endpoint.getParameters().get("repo");
            if (repo == null) {
                repo = exchange.getIn().getBody();
            }
            command = reflect.getRepoCommand(commandName, converter.mandatoryConvertTo(Repository.class, repo));
        }
        LOG.trace("resolved {} to {}", endpoint.getCommand(), command);

        if (!IntrospectionSupport.setProperties(
                converter,
                command, endpoint.getParameters())) {
            throw new IllegalArgumentException("Invalid properties");
        }

        exchange.getIn().setBody(command.call());
    }

}
