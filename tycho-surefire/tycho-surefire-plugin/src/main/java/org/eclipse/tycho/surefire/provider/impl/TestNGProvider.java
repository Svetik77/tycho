/*******************************************************************************
 * Copyright (c) 2016 Bachmann electronic GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bachmann electronic GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.tycho.surefire.provider.impl;

import static java.util.Collections.singletonList;
import static org.eclipse.tycho.surefire.provider.impl.ProviderHelper.newDependency;

import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.tycho.classpath.ClasspathEntry;
import org.eclipse.tycho.surefire.provider.spi.TestFrameworkProvider;
import org.osgi.framework.Version;

@Component(role = TestFrameworkProvider.class, hint = "testng")
public class TestNGProvider implements TestFrameworkProvider {

    private static final Version VERSION = Version.parseVersion("6.9.10");

    @Override
    public String getType() {
        return "testng";
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public String getSurefireProviderClassName() {
        return "org.apache.maven.surefire.testng.TestNGProvider";
    }

    @Override
    public boolean isEnabled(List<ClasspathEntry> testBundleClassPath, Properties surefireProperties) {
        //TODO: Find a way to automatically enable this provider 
        // For now the user must provide the tycho surefire parameter 
        // <providerHint>testng</providerHint> 
        // in order to enable this testprovider
        return false;
    }

    @Override
    public List<Dependency> getRequiredBundles() {
        return singletonList(newDependency("org.eclipse.tycho", "org.eclipse.tycho.surefire.testng"));
    }

    @Override
    public Properties getProviderSpecificProperties() {
        Properties properties = new Properties();
        properties.setProperty("testng.configurator", "org.apache.maven.surefire.testng.conf.TestNG652Configurator");
        return properties;
    }

}
