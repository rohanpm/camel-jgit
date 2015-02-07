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
package net.rohanpm.camel.jgit;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Represents the component that manages {@link JGitEndpoint}.
 */
public class JGitComponent extends DefaultComponent {

    private static final Logger LOG = LoggerFactory.getLogger(JGitComponent.class);

    private JGitReflect jGitReflect = new JGitReflect();

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        LOG.debug("createEndPoint {} {} {}", new Object[]{uri, remaining, parameters});
        final JGitEndpoint endpoint = new JGitEndpoint(uri, this);
        setProperties(endpoint, parameters);
        endpoint.setCommand(remaining);
        endpoint.setParameters(parameters);
        endpoint.setJGitReflect(jGitReflect);
        endpoint.check();
        return endpoint;
    }
}
