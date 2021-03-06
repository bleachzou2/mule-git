/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.tools.maven2.bundleplugin;
 
import aQute.lib.osgi.Analyzer;
import aQute.lib.osgi.Builder;
import aQute.lib.osgi.EmbeddedResource;
import aQute.lib.osgi.FileResource;
import aQute.lib.osgi.Jar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
 
/**
 * 
 * @goal bundle
 * @phase package
 * @requiresDependencyResolution runtime
 * @description build an OSGi bundle jar
 */
public class BundlePlugin extends AbstractMojo {
 
 /**
  * @parameter expression="${project.build.outputDirectory}"
  * @required
  * @readonly
  */
 File     outputDirectory;
 
 /**
  * The directory for the pom
  * 
  * @parameter expression="${basedir}"
  * @required
  */
 private File   baseDir;
 
 /**
  * The directory for the generated JAR.
  * 
  * @parameter expression="${project.build.directory}"
  * @required
  */
 private String   buildDirectory;
 
 /**
  * The Maven project.
  * 
  * @parameter expression="${project}"
  * @required
  * @readonly
  */
 private MavenProject project;
 
 /**
  * The name of the generated JAR file.
  * 
  * @parameter
  */
 private Map    instructions = new HashMap();

 // Taken from the old Felix plug-in.
 private static final java.util.regex.Pattern versionPattern =
     java.util.regex.Pattern.compile("^(\\w+(\\.\\w+(\\.\\w+)?)?)-");
 private static final String[] versionCompleters =
     new String[] { ".0.0", ".0" };

 public void execute() throws MojoExecutionException {
  try {
   File jarFile = new File(buildDirectory, project.getBuild()
     .getFinalName()
     + ".jar");
 
   // Setup defaults
   String bsn = project.getGroupId() + "." + project.getArtifactId();
   Properties properties = new Properties();
   properties.put(Analyzer.BUNDLE_SYMBOLICNAME, bsn);
   properties.put(Analyzer.IMPORT_PACKAGE, "*");
   if (!instructions.containsKey(Analyzer.PRIVATE_PACKAGE)) {
     properties.put(Analyzer.EXPORT_PACKAGE, bsn + ".*");
   }
   String version = fixBundleVersion(project.getVersion());
   properties.put(Analyzer.BUNDLE_VERSION, version);
   header(properties, Analyzer.BUNDLE_DESCRIPTION, project
     .getDescription());
   header(properties, Analyzer.BUNDLE_LICENSE, printLicenses(project
     .getLicenses()));
   header(properties, Analyzer.BUNDLE_NAME, project.getName());
   
   if (project.getOrganization() != null) {
     header(properties, Analyzer.BUNDLE_VENDOR, project
       .getOrganization().getName());
     if (project.getOrganization().getUrl() != null) {
       header(properties, Analyzer.BUNDLE_DOCURL, project
         .getOrganization().getUrl());
     }
   }

   if (new File(baseDir, "src/main/resources").exists()) {
     header(properties, Analyzer.INCLUDE_RESOURCE, "src/main/resources/");
   }
 
   properties.putAll(project.getProperties());
   properties.putAll(project.getModel().getProperties());
   properties.putAll( getProperies("project.build.", project.getBuild()));
   properties.putAll( getProperies("pom.", project.getModel()));
   properties.putAll( getProperies("project.", project));
   properties.put("project.baseDir", baseDir );
   properties.put("project.build.directory", buildDirectory );
   properties.put("project.build.outputdirectory", outputDirectory );
   
   properties.putAll(instructions);
 
   Builder builder = new Builder();
   builder.setBase(baseDir);
   Jar[] cp = getClasspath();
   builder.setProperties(properties);
   builder.setClasspath(cp);
 
   builder.build();
   Jar jar = builder.getJar();
   doMavenMetadata(jar);
   builder.setJar(jar);
 
   List errors = builder.getErrors();
   List warnings = builder.getWarnings();
 
   if (errors.size() > 0) {
    jarFile.delete();
    for (Iterator e = errors.iterator(); e.hasNext();) {
     String msg = (String) e.next();
     getLog().error(msg);
    }
    throw new MojoFailureException("Found errors, see log");
   }
   else {
    jarFile.getParentFile().mkdirs();
    builder.getJar().write(jarFile);
    project.getArtifact().setFile(jarFile);
   }
   for (Iterator w = warnings.iterator(); w.hasNext();) {
    String msg = (String) w.next();
    getLog().warn(msg);
   }
 
  }
  catch (Exception e) {
   e.printStackTrace();
   throw new MojoExecutionException("Unknown error occurred", e);
  }
 }
 
 private Map getProperies(String prefix, Object model) {
  Map properties = new HashMap();
  Method methods[] = Model.class.getDeclaredMethods();
  for (int i = 0; i < methods.length; i++) {
   String name = methods[i].getName();
   if ( name.startsWith("get") ) {
    try {
     Object v = methods[i].invoke(project.getModel(), null );
     if ( v != null ) {
      name = prefix + Character.toLowerCase(name.charAt(3)) + name.substring(4);
      if ( v.getClass().isArray() )
       properties.put( name, Arrays.asList((Object[])v).toString() );
      else
       properties.put( name, v );
       
     } 
    }
    catch (Exception e) {
     // too bad
    }
   }
  }
  return properties;
 }
 
 private StringBuffer printLicenses(List licenses) {
  if (licenses == null || licenses.size() == 0)
   return null;
  StringBuffer sb = new StringBuffer();
  String del = "";
  for (Iterator i = licenses.iterator(); i.hasNext();) {
   License l = (License) i.next();
   String url = l.getUrl();
   sb.append(del);
   sb.append(url);
   del = ", ";
  }
  return sb;
 }
 
 /**
  * @param jar
  * @throws IOException
  */
 private void doMavenMetadata(Jar jar) throws IOException {
  String path = "META-INF/maven/" + project.getGroupId() + "/"
    + project.getArtifactId();
  File pomFile = new File(baseDir, "pom.xml");
  jar.putResource(path + "/pom.xml", new FileResource(pomFile));
 
  Properties p = new Properties();
  p.put("version", project.getVersion());
  p.put("groupId", project.getGroupId());
  p.put("artifactId", project.getArtifactId());
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  p.store(out, "Generated by org.apache.felix.plugin.bundle");
  jar.putResource(path + "/pom.properties", new EmbeddedResource(out
    .toByteArray()));
 }
 
 /**
  * @return
  * @throws ZipException
  * @throws IOException
  */
 private Jar[] getClasspath() throws ZipException, IOException {
  List list = new ArrayList();
  
  if (outputDirectory != null && outputDirectory.exists()) {
    list.add(new Jar(".", outputDirectory));
  }
 
  Set artifacts = project.getArtifacts();
  for (Iterator it = artifacts.iterator(); it.hasNext();) {
   Artifact artifact = (Artifact) it.next();
   if (Artifact.SCOPE_COMPILE.equals(artifact.getScope()) 
     || Artifact.SCOPE_SYSTEM.equals(artifact.getScope()) 
     || Artifact.SCOPE_PROVIDED.equals(artifact.getScope())) {
    Jar jar = new Jar(artifact.getArtifactId(), artifact.getFile());
    list.add(jar);
   }
  }
  Jar[] cp = new Jar[list.size()];
  list.toArray(cp);
  return cp;
 }
 
 private void header(Properties properties, String key, Object value) {
  if (value == null)
   return;
 
  if (value instanceof Collection && ((Collection) value).isEmpty())
   return;
 
  properties.put(key, value.toString());
 }

 // Taken from the old Felix plugin.
 public static String fixBundleVersion(String version) {
     // Maven uses a '-' to separate the version qualifier, while
     // OSGi uses a '.', so we need to convert the first '-' to a
     // '.' and fill in any missing minor or micro version
     // components if necessary.
     final java.util.regex.Matcher matcher = versionPattern.matcher(version);
     if (!matcher.lookingAt())
         return version;

     // Leave extra space for worst-case additional insertion:
     final StringBuffer sb = new StringBuffer(version.length() + 4);
     sb.append(matcher.group(1));

     if (null == matcher.group(3)) {
        final int count = null != matcher.group(2) ? 2 : 1;
        sb.append(versionCompleters[count - 1]);
     }

     sb.append('.');
     sb.append(version.substring(matcher.end(), version.length()));
     return sb.toString();
 } 
}
