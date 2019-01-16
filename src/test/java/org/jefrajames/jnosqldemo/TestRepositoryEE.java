/*
 * Copyright 2019 JF James.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jefrajames.jnosqldemo;

import java.io.File;
import java.util.logging.Level;

import lombok.extern.java.Log;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test class makes use of a JNoSQL Template object in a Java SE context.
 * 
 * Arquillian is configured in remote mode, so a Payara instance must be started.
 * 
 * @author JF James
 */
@RunWith(Arquillian.class)
@Log
public class TestRepositoryEE extends TestRepositoryCommon {

    @Deployment
    public static WebArchive createDeployment() {

        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        // Non-Java EE libraries
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        archive.addAsLibraries(libs);

        // Application packages excluding test classes except AbstractTestRepository
        archive.addPackages(false, Filters.exclude(".*Test.*"), PersonRepository.class.getPackage());
        archive.addClass(TestRepositoryCommon.class);
        archive.addAsResource("diana-mongodb.properties");

        log.log(Level.INFO, "deploying webarchive: {0}", archive.toString(true));

        return archive;
    }

    @AfterClass
    public static void closeCDI() {
        printDuration();
    }

    @Test
    public void testConfig() {
        assertNotNull(repo);
    }

}