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

import javax.enterprise.inject.se.SeContainerInitializer;
import org.jnosql.artemis.DatabaseQualifier;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This test class makes use of a JNoSQL Template object in a Java SE context.
 *
 * No use of Arquillian, the CDI container is manually started.
 *
 * Weld is use as CDI implementation.
 *
 * @author JF James
 */
public class TestRepositorySE extends TestRepositoryCommon {

    @BeforeClass
    public static void initializeCDI() {
        // See CDI 2.0 spec Chapt 15.1
        System.setProperty("javax.enterprise.inject.scan.implicit", "true");
        container = SeContainerInitializer.newInstance().initialize();
    }

    @AfterClass
    public static void closeCDI() {
        if (container != null) {
            container.close();
        }
        printDuration();
    }

    @Before
    public void initRepo() {
        repo = container.select(PersonRepository.class).select(DatabaseQualifier.ofDocument()).get();
    }

}
