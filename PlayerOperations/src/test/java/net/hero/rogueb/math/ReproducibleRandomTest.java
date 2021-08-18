package net.hero.rogueb.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReproducibleRandomTest {

    @Test
    void test1(){
        ReproducibleRandom random = new ReproducibleRandom(1);
        assertEquals(-1155869325, random.next(), "first");
        assertEquals(431529176, random.next(), "second");
        assertEquals(1761283695, random.next(), "third");
        assertEquals(1749940626, random.next(), "fourth");
        assertEquals(892128508, random.next(), "fifth");
        assertEquals(155629808, random.next(), "sixth");
        assertEquals(1429008869, random.next(), "seventh");
        assertEquals(-1465154083, random.next(), "eighth");
        assertEquals(-138487339, random.next(), "ninth");
        assertEquals(-1242363800, random.next(), "tenth");
        assertEquals(26273138, random.next(), "eleventh");
    }
}