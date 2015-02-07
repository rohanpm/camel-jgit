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

import org.apache.camel.Converter;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JGitConverters {

    @Converter
    public static Repository toRepository(String path) {
        try {
            return new FileRepository(path);
        } catch (IOException e) {
            throw new GenericFileOperationFailedException("Can't open repository " + path, e);
        }
    }

    @Converter
    public static Repository toRepository(Git git) {
        return git.getRepository();
    }

    @Converter
    public static RefSpec[] toRefSpecs(String str) {
        final List<RefSpec> out = new ArrayList<>();
        for (String refSpecStr : str.split(",")) {
            out.add(new RefSpec(refSpecStr));
        }
        return out.toArray(new RefSpec[out.size()]);
    }
}
