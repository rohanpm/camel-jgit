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
