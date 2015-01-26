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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import java.util.Map;

/**
 * Represents a JGit endpoint.
 */
public class JGitEndpoint extends DefaultEndpoint {

    private String command;
    private Map<String,Object> parameters;
    private JGitReflect jGitReflect;

    public JGitEndpoint(String uri, JGitComponent component) {
        super(uri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new JGitProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("JGit component cannot be used as a consumer");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public JGitReflect getJGitReflect() {
        return jGitReflect;
    }

    public void setJGitReflect(JGitReflect jGitReflect) {
        this.jGitReflect = jGitReflect;
    }

    public void check() {
        if (jGitReflect.hasCommand(command) || jGitReflect.hasRepoCommand(command)) {
            return;
        }

        throw new IllegalArgumentException(command + " is not a valid jgit command");
    }
}
