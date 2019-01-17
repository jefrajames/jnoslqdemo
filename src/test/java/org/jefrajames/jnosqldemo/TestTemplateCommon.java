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
import javax.enterprise.inject.se.SeContainer;
import javax.inject.Inject;
import static junit.framework.Assert.assertFalse;
import lombok.extern.java.Log;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * This class makes use of JNoSQL and CDI in a Java SE context.
 *
 * No use of Arquillian, the CDI container is manually started.
 *
 * Weld is use as CDI implementation.
 *
 * @author JF James
 */
@Log
public abstract class TestTemplateCommon {

    private static final long START_TIME = System.currentTimeMillis();

    protected static SeContainer container;

    @Inject // This annotation is just ignored in SE mode
    protected DocumentTemplate template;

    private static final List<String> PHONES = Arrays.asList("12345", "23456");

    private final static Address ADDRESS = new Address(42, "Mongo Street", "Template City");

    protected static void printDuration() {
        long duration = System.currentTimeMillis() - START_TIME;
        log.info("Tests done in " + duration + " ms");
    }

    @Test
    public void testConfig() {
        assertNotNull(template);
    }

    @Test
    public void testInsert() {
        String name = "testInsertWithTemplate " + System.currentTimeMillis();
        long countBefore = template.count(Person.class);
        Person p = new Person(name, PHONES, ADDRESS);
        template.insert(p);
        long countAfter = template.count(Person.class);
        assertTrue(countAfter > countBefore);
    }

    @Test
    public void testSelectByName() {
        String name = "testSelectByNameWithTemplate " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        template.insert(p);
        DocumentQuery query = select().from("Person").where("name").eq(name).build();
        Optional<Person> p2 = template.singleResult(query);
        assertTrue(p2.isPresent());
    }

    @Test
    public void testSelectById() {
        String name = "testSelectByIdWithTemplate " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        template.insert(p);
        DocumentQuery query = select().from("Person").where("_id").eq(p.getId()).build();
        Optional<Person> p2 = template.singleResult(query);
        assertTrue(p2.isPresent());
    }

    @Test
    public void testSelectAll() {
        // Save 2 people
        List<Person> newPeople = new ArrayList(2);
        newPeople.add(new Person("testFindAllWithTemplate-1 " + System.currentTimeMillis(), PHONES, ADDRESS));
        newPeople.add(new Person("testFindAllWithTemplate-2 " + System.currentTimeMillis(), PHONES, ADDRESS));
        template.insert(newPeople);

        DocumentQuery query = select().from("Person").build();
        List<Person> allPeople = template.select(query);
        assertTrue(allPeople.size() >= newPeople.size());
    }

    @Test
    public void testSelectAllWithSkipLimit() {
        DocumentQuery query = select().from("Person").skip(1).limit(2).build();
        List<Person> people = template.select(query);
        assertTrue(people.size() <= 2);
    }

    @Test
    public void testUpdate() {
        // Initial write
        String name = "testUpdateWithTemplate " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        template.insert(p);

        // Read + update
        DocumentQuery query = select().from("Person").where("_id").eq(p.getId()).build();
        Optional<Person> p2 = template.singleResult(query);
        assertTrue(p2.isPresent());
        String oldName = p2.get().getName();
        String newName = oldName + " updated";
        p2.get().setName(newName);
        template.update(p2.get());

        // Read after update
        Optional<Person> p3 = template.singleResult(query);
        assertTrue(p3.isPresent());
        assertEquals(p3.get().getName(), newName);
    }

    @Test
    public void testDelete() {
        // Initial write
        String name = "testDeleteWithTemplate " + System.currentTimeMillis();
        Person p = new Person(name, PHONES, ADDRESS);
        template.insert(p);

        // Read: should exist
        DocumentQuery query = select().from("Person").where("_id").eq(p.getId()).build();
        Optional<Person> p2 = template.singleResult(query);
        assertTrue(p2.isPresent());

        // Delete
        template.delete(Person.class, p2.get().getId());

        // Read again: should not exist
        Optional<Person> p3 = template.singleResult(query);
        assertFalse(p3.isPresent());
    }

}
