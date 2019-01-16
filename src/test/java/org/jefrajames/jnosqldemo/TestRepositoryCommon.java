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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.inject.se.SeContainer;
import javax.inject.Inject;
import static junit.framework.Assert.assertFalse;
import lombok.extern.java.Log;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This class contains the common parts of TestRepositorySE and TestRepositoryEE
 *
 * @author JF James
 */
@Log
public abstract class TestRepositoryCommon {

    private static final long START_TIME = System.currentTimeMillis();

    private static final List<String> PHONES = Arrays.asList("56789", "67890");

    private final static Address ADDRESS = new Address(42, "Mongo Street", "Repository City");

    protected static SeContainer container;

    // These CDI annotations are just ignored in Java SE mode
    @Inject
    @Database(DatabaseType.DOCUMENT)
    protected PersonRepository repo;

    protected static void printDuration() {
        long duration = System.currentTimeMillis() - START_TIME;
        log.info("Tests done in " + duration + " ms");
    }

    @Test
    public void testInsert() {
        String name = "testInsertWithRepository " + System.currentTimeMillis();
        long countBefore = repo.count();
        Person p = new Person(name, PHONES, ADDRESS);
        repo.save(p);
        long countAfter = repo.count();
        assertTrue(countAfter > countBefore);
    }

    @Test
    public void testFindByName() {
        String name = "testSelectByNameWithRepository " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        repo.save(p);
        Person p2 = repo.findByName(name).get(0);
        assertNotNull(p2);
    }

    @Test
    public void testFindById() {
        String name = "testSelectByIdWithRepository " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        repo.save(p);
        Optional<Person> p2 = repo.findById(p.getId());
        assertTrue(p2.isPresent());
    }

    @Test
    public void testFindAll() {
        List<Person> people = repo.findAll();
        assertTrue(people.size() == repo.count());
    }

    @Test
    public void testFindByPhones() {
        // Save 2 people
        List<Person> newPeople = new ArrayList(2);
        newPeople.add(new Person("testFindByPhones-1 " + System.currentTimeMillis(), PHONES, ADDRESS));
        newPeople.add(new Person("testFindByPhones-2 " + System.currentTimeMillis(), PHONES, ADDRESS));
        repo.save(newPeople);

        // At least 2 people with the given phones must be found
        Stream<Person> people = repo.findByPhones(PHONES);
        assertTrue(people.count() >= newPeople.size());
    }

    @Test
    public void testUpdate() {
        // Initial write
        String name = "testUpdateWithRepository " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        repo.save(p);

        // Read + update
        Optional<Person> p2 = repo.findById(p.getId());
        assertTrue(p2.isPresent());
        String oldName = p2.get().getName();
        String newName = oldName + " updated";
        p2.get().setName(newName);
        repo.save(p2.get()); // Save also used for update

        // Read after update
        Optional<Person> p3 = repo.findById(p.getId());
        assertTrue(p3.isPresent());
        assertEquals(p3.get().getName(), newName);
    }

    @Test
    public void testDelete() {
        // Initial write
        String name = "testDeleteWithRepository " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        repo.save(p);

        // Read: should exist
        Optional<Person> p2 = repo.findById(p.getId());
        assertTrue(p2.isPresent());

        // Delete
        repo.deleteById(p2.get().getId());

        // Read again: should not exist
        Optional<Person> p3 = repo.findById(p.getId());
        assertFalse(p3.isPresent());
    }

}
