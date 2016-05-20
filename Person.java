package com.hellomicke89gmail.projektsmartlock;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Mikael on 2016-04-18.
 * Author
 * Klassen representerar ett personobjekt med id och name
 */


public class Person {
    String key;
    String name;

    /**
     * Person konstuktor med infom id och namn
     * @param key id för varje person
     * @param name namn för varje person
     */
    Person(String key, String name) {
        this.key = key;
        this.name = name;

    }

    /**
     * Hämtar ett id för en viss person
     * @return id för en viss person
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Hämtar ett namn för en person
     * @return namnet på en person
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sätter namn på en person
     * @param name namnet som skall sättas
     */
    public void setName(String name) {
        this.name = name;

    }


}
