/*******************************************************************************
 * Copyright (c) 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/

package org.eclipse.tycho.core.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BuildPropertiesImpl implements BuildProperties {

    private String javacSource;
    private String javacTarget;
    private String jreCompilationProfile;
    private String forceContextQualifier;
    private boolean rootFilesUseDefaultExcludes;

    private List<String> binIncludes;
    private List<String> binExcludes;
    private List<String> sourceIncludes;
    private List<String> sourceExcludes;
    private List<String> jarsExtraClasspath;
    private List<String> jarsCompileOrder;

    private Map<String, List<String>> jarToSourceFolderMap;
    private Map<String, List<String>> jarToExtraClasspathMap;
    private Map<String, String> jarToJavacDefaultEncodingMap;
    private Map<String, String> jarToOutputFolderMap;
    private Map<String, String> jarToManifestMap;
    private Map<String, String> rootEntries;

    @SuppressWarnings("unchecked")
    public BuildPropertiesImpl(Properties properties) {
        javacSource = safeTrimValue("javacSource", properties);
        javacTarget = safeTrimValue("javacTarget", properties);
        forceContextQualifier = safeTrimValue("forceContextQualifier", properties);
        jreCompilationProfile = safeTrimValue("jre.compilation.profile", properties);
        rootFilesUseDefaultExcludes = Boolean
                .parseBoolean(properties.getProperty("rootFiles.useDefaultExcludes", "true"));

        sourceIncludes = splitAndTrimCommaSeparated(properties.getProperty("src.includes"));
        sourceExcludes = splitAndTrimCommaSeparated(properties.getProperty("src.excludes"));
        binIncludes = splitAndTrimCommaSeparated(properties.getProperty("bin.includes"));
        binExcludes = splitAndTrimCommaSeparated(properties.getProperty("bin.excludes"));
        jarsExtraClasspath = splitAndTrimCommaSeparated(properties.getProperty("jars.extra.classpath"));
        jarsCompileOrder = splitAndTrimCommaSeparated(properties.getProperty("jars.compile.order"));

        HashMap<String, List<String>> jarTosourceFolderTmp = new LinkedHashMap<>();
        HashMap<String, List<String>> jarToExtraClasspathTmp = new LinkedHashMap<>();
        HashMap<String, String> jarToJavacDefaultEncodingTmp = new LinkedHashMap<>();
        HashMap<String, String> jarToOutputFolderMapTmp = new LinkedHashMap<>();
        HashMap<String, String> jarToManifestMapTmp = new LinkedHashMap<>();
        HashMap<String, String> rootEntriesTmp = new LinkedHashMap<>();

        List<String> sortedKeys = new ArrayList(properties.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            String trimmedKey = key.trim();
            String value = properties.getProperty(key);
            if (trimmedKey.startsWith("source.")) {
                String jarName = trimmedKey.substring("source.".length());
                jarTosourceFolderTmp.put(jarName, splitAndTrimCommaSeparated(value));
            } else if (trimmedKey.startsWith("extra.")) {
                String jarName = trimmedKey.substring("extra.".length());
                jarToExtraClasspathTmp.put(jarName, splitAndTrimCommaSeparated(value));
            } else if (trimmedKey.startsWith("javacDefaultEncoding.")) {
                String jarName = trimmedKey.substring("javacDefaultEncoding.".length());
                jarToJavacDefaultEncodingTmp.put(jarName, value);
            } else if (trimmedKey.startsWith("output.")) {
                String jarName = trimmedKey.substring("output.".length());
                jarToOutputFolderMapTmp.put(jarName, value);
            } else if (trimmedKey.startsWith("manifest.")) {
                String jarName = trimmedKey.substring("manifest.".length());
                jarToManifestMapTmp.put(jarName, value);
            } else if (trimmedKey.startsWith("root.") || trimmedKey.equals("root")) {
                rootEntriesTmp.put(trimmedKey, value);
            }
        }
        jarToSourceFolderMap = unmodifiableMap(jarTosourceFolderTmp);
        jarToExtraClasspathMap = unmodifiableMap(jarToExtraClasspathTmp);
        jarToJavacDefaultEncodingMap = unmodifiableMap(jarToJavacDefaultEncodingTmp);
        jarToOutputFolderMap = unmodifiableMap(jarToOutputFolderMapTmp);
        jarToManifestMap = unmodifiableMap(jarToManifestMapTmp);
        rootEntries = unmodifiableMap(rootEntriesTmp);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map unmodifiableMap(Map map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }

    private static String safeTrimValue(String key, Properties buildProperties) {
        String value = buildProperties.getProperty(key);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    private static List<String> splitAndTrimCommaSeparated(String rawValue) {
        List<String> result = new ArrayList<>();
        if (rawValue != null) {
            for (String element : rawValue.split(",")) {
                result.add(element.trim());
            }
        }
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<String> getBinIncludes() {
        return binIncludes;
    }

    @Override
    public List<String> getBinExcludes() {
        return binExcludes;
    }

    @Override
    public List<String> getSourceIncludes() {
        return sourceIncludes;
    }

    @Override
    public List<String> getSourceExcludes() {
        return sourceExcludes;
    }

    @Override
    public Map<String, List<String>> getJarToSourceFolderMap() {
        return jarToSourceFolderMap;
    }

    @Override
    public List<String> getJarsExtraClasspath() {
        return jarsExtraClasspath;
    }

    @Override
    public String getJavacSource() {
        return javacSource;
    }

    @Override
    public String getJavacTarget() {
        return javacTarget;
    }

    @Override
    public List<String> getJarsCompileOrder() {
        return jarsCompileOrder;
    }

    @Override
    public Map<String, List<String>> getJarToExtraClasspathMap() {
        return jarToExtraClasspathMap;
    }

    @Override
    public Map<String, String> getJarToJavacDefaultEncodingMap() {
        return jarToJavacDefaultEncodingMap;
    }

    @Override
    public Map<String, String> getJarToOutputFolderMap() {
        return jarToOutputFolderMap;
    }

    @Override
    public Map<String, String> getJarToManifestMap() {
        return jarToManifestMap;
    }

    @Override
    public String getJreCompilationProfile() {
        return jreCompilationProfile;
    }

    @Override
    public String getForceContextQualifier() {
        return forceContextQualifier;
    }

    @Override
    public boolean isRootFilesUseDefaultExcludes() {
        return rootFilesUseDefaultExcludes;
    }

    @Override
    public Map<String, String> getRootEntries() {
        return rootEntries;
    }

}
