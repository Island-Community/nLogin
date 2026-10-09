package com.nickuc.login.loader;

import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.LoadingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class MemClassLoader
extends URLClassLoader {
    private final Map<String, byte[]> classMap = new ConcurrentHashMap<String, byte[]>();
    private final Map<String, byte[]> resourceMap = new ConcurrentHashMap<String, byte[]>();
    private final ClassLoader parent;
    private Collection<String> excludeIsolation;

    public MemClassLoader(ClassLoader parent) throws LoadingException {
        super(new URL[]{}, parent.getParent() != null ? parent.getParent() : ClassLoader.getSystemClassLoader());
        this.parent = parent;
        this.init();
    }

    public ClassLoader getParentLoader() {
        return this.parent;
    }

    public void configureFilter(Collection<String> excludeIsolation) {
        this.excludeIsolation = excludeIsolation;
    }

    public void addJarToClasspath(URL url) {
        this.addURL(url);
    }

    public void addClass(String classPath, byte[] content) {
        this.classMap.put(classPath, content);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;
        if (this.excludeIsolation != null) {
            if (this.excludeIsolation.stream().anyMatch(name::startsWith)) {
                try {
                    clazz = this.parent.loadClass(name);
                    if (clazz != null) {
                        return clazz;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
        }
        try {
            clazz = super.loadClass(name, resolve);
            if (clazz != null) {
                return clazz;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        return this.parent.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        byte[] classBytes = this.classMap.remove(className);
        if (classBytes != null) {
            return this.defineClass(className, classBytes, 0, classBytes.length);
        }
        return super.findClass(className);
    }

    public InputStream getInJarResourceAsStream(String resourcePath) {
        if (resourcePath.isEmpty()) {
            throw new IllegalStateException("Resource path cannot be empty!");
        }
        byte[] resourceBytes = this.resourceMap.get(resourcePath);
        if (resourceBytes != null) {
            return new ByteArrayInputStream(resourceBytes);
        }
        return super.getResourceAsStream(resourcePath);
    }

    @Override
    public InputStream getResourceAsStream(String resourcePath) {
        return this.getInJarResourceAsStream(resourcePath);
    }

    public <T> LoaderBootstrap createLoader(String bootstrapClass, Class<T> loaderPluginType, T loaderPlugin) throws LoadingException {
        Constructor<LoaderBootstrap> constructor;
        Class<LoaderBootstrap> plugin;
        try {
            plugin = this.loadClass(bootstrapClass).asSubclass(LoaderBootstrap.class);
        }
        catch (ReflectiveOperationException e) {
            throw new LoadingException("Unable to load bootstrap class", e);
        }
        try {
            constructor = plugin.getConstructor(loaderPluginType);
        }
        catch (ReflectiveOperationException e) {
            throw new LoadingException("Unable to get bootstrap constructor", e);
        }
        try {
            return constructor.newInstance(loaderPlugin);
        }
        catch (ReflectiveOperationException e) {
            throw new LoadingException("Unable to create bootstrap plugin instance", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void init() throws LoadingException {
        try {
            InputStream resource = this.parent.getResourceAsStream(this.getClass().getPackage().getName().replace('.', '/') + "/plugin.jar");
            try {
                if (resource == null) {
                    throw new LoadingException("Corrupted jar!");
                }
                ZipInputStream zis = new ZipInputStream(resource);
                try {
                    ZipEntry zipEntry;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    while ((zipEntry = zis.getNextEntry()) != null) {
                        int length;
                        if (zipEntry.isDirectory()) continue;
                        while ((length = zis.read(buf)) > 0) {
                            out.write(buf, 0, length);
                        }
                        byte[] zipEntryBytes = out.toByteArray();
                        String entryName = zipEntry.getName();
                        boolean isClass = entryName.endsWith(".class");
                        if (isClass) {
                            String classPath = entryName.replace('/', '.').substring(0, entryName.length() - ".class".length());
                            this.addClass(classPath, zipEntryBytes);
                        } else {
                            this.resourceMap.put(entryName, zipEntryBytes);
                        }
                        out.reset();
                    }
                }
                finally {
                    if (Collections.singletonList(zis).get(0) != null) {
                        zis.close();
                    }
                }
            }
            finally {
                if (Collections.singletonList(resource).get(0) != null) {
                    resource.close();
                }
            }
        }
        catch (ZipException e) {
            throw new LoadingException("ZIP exception", e);
        }
        catch (IOException e) {
            throw new LoadingException("IO exception", e);
        }
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }
}

