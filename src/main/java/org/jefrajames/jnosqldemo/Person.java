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

import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

/**
 *
 * @author JF James
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity("Person")
public class Person {
    
    private static final Random RANDOM = new Random();
    
    public static long generateId() {
        return RANDOM.nextLong();
    }
    
    @Id("_id")
    private long id;
    
    @Column
    private String name;
    
    @Column
    private List<String> phones;
    
    @Column
    private Address address;
    
    public Person(String name, List<String> phones, Address address) {
        this(generateId(), name, phones, address);
    }
    
}
