package net.rohanpm.camel.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class JGitReflect {
    private static final Logger LOG = LoggerFactory.getLogger(JGitReflect.class);

    private final Map<String,Method> staticCommands;
    private final Map<String,Method> repoCommands;

    public JGitReflect() {
        final Map<String,Method> staticCommands = new HashMap<>();
        final Map<String,Method> repoCommands = new HashMap<>();

        final Method[] methods = Git.class.getDeclaredMethods();
        for (Method m : methods) {
            final String methodName = m.getName();
            final boolean returnsCallable = Callable.class.isAssignableFrom(m.getReturnType());
            LOG.trace("{} returns Callable: {}", methodName, returnsCallable);

            if (!returnsCallable) {
                continue;
            }

            if (Modifier.isStatic(m.getModifiers())) {
                staticCommands.put(methodName, m);
            } else {
                repoCommands.put(methodName, m);
            }
        }

        this.repoCommands = Collections.unmodifiableMap(repoCommands);
        this.staticCommands = Collections.unmodifiableMap(staticCommands);
    }

    public boolean hasCommand(String name) {
        return staticCommands.containsKey(name);
    }

    public boolean hasRepoCommand(String name) {
        return repoCommands.containsKey(name);
    }

    public Callable<?> getCommand(String name) {
        final Method method = staticCommands.get(name);
        if (method == null) {
            return null;
        }
        return invoke(name, method, null);
    }

    public Callable<?> getRepoCommand(String name, Repository repository) {
        final Method method = repoCommands.get(name);
        if (method == null) {
            return null;
        }
        final Git git = new Git(repository);
        return invoke(name, method, git);
    }

    private Callable<?> invoke(String name, Method m, Object o) {
        try {
            return (Callable<?>) m.invoke(o);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new UnsupportedOperationException(name + " cannot be invoked", e);
        }

    }
}
