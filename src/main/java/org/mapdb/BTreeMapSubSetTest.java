package org.mapdb;/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

import org.mapdb.jsr166Tests.JSR166TestCase;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class BTreeMapSubSetTest extends JSR166TestCase {

    static class MyReverseComparator implements Comparator {
        public int compare(Object x, Object y) {
            return ((Comparable)y).compareTo(x);
        }
    }

    /*
     * Returns a new set of given size containing consecutive
     * Integers 0 ... n.
     */
    private NavigableSet<Integer> populatedSet(int n) {
        NavigableSet<Integer> q =
            newNavigableSet();
        assertTrue(q.isEmpty());

        for (int i = n-1; i >= 0; i-=2)
            assertTrue(q.add(new Integer(i)));
        for (int i = (n & 1); i < n; i+=2)
            assertTrue(q.add(new Integer(i)));
        assertTrue(q.add(new Integer(-n)));
        assertTrue(q.add(new Integer(n)));
        NavigableSet s = q.subSet(new Integer(0), true, new Integer(n), false);
        assertFalse(s.isEmpty());
        assertEquals(n, s.size());
        return s;
    }

    abstract protected NavigableSet<Integer> newNavigableSet();

    /*
     * Returns a new set of first 5 ints.
     */
    private NavigableSet set5() {
        NavigableSet q = newNavigableSet();
        assertTrue(q.isEmpty());
        q.add(one);
        q.add(two);
        q.add(three);
        q.add(four);
        q.add(five);
        q.add(zero);
        q.add(seven);
        NavigableSet s = q.subSet(one, true, seven, false);
        assertEquals(5, s.size());
        return s;
    }


    private NavigableSet set0() {
        NavigableSet set = newNavigableSet();
        assertTrue(set.isEmpty());
        return set.tailSet(m1, true);
    }

    private NavigableSet dset0() {
        NavigableSet set = newNavigableSet();
        assertTrue(set.isEmpty());
        return set;
    }

    /*
     * A new set has unbounded capacity
     */
    public void testConstructor1() {
        assertEquals(0, set0().size());
    }

    /*
     * isEmpty is true before add, false after
     */
    public void testEmpty() {
        NavigableSet q = set0();
        assertTrue(q.isEmpty());
        q.add(new Integer(1));
        assertFalse(q.isEmpty());
        q.add(new Integer(2));
        q.pollFirst();
        q.pollFirst();
        assertTrue(q.isEmpty());
    }



    /*
     * add(null) throws NPE
     */
    public void testAddNull() {
        try {
            NavigableSet q = set0();
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * Add of comparable element succeeds
     */
    public void testAdd() {
        NavigableSet q = set0();
        assertTrue(q.add(six));
    }

    /*
     * Add of duplicate element fails
     */
    public void testAddDup() {
        NavigableSet q = set0();
        assertTrue(q.add(six));
        assertFalse(q.add(six));
    }

    /*
     * Add of non-Comparable throws CCE
     */
    public void testAddNonComparable() {
        try {
            NavigableSet q = set0();
            q.add(new Object());
            q.add(new Object());
            q.add(new Object());
            shouldThrow();
        } catch (ClassCastException success) {}
    }

    /*
     * addAll(null) throws NPE
     */
    public void testAddAll1() {
        try {
            NavigableSet q = set0();
            q.addAll(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * addAll of a collection with null elements throws NPE
     */
    public void testAddAll2() {
        try {
            NavigableSet q = set0();
            Integer[] ints = new Integer[SIZE];
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testAddAll3() {
        try {
            NavigableSet q = set0();
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE-1; ++i)
                ints[i] = new Integer(i+SIZE);
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * Set contains all elements of successful addAll
     */
    public void testAddAll5() {
        Integer[] empty = new Integer[0];
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = new Integer(SIZE-1- i);
        NavigableSet q = set0();
        assertFalse(q.addAll(Arrays.asList(empty)));
        assertTrue(q.addAll(Arrays.asList(ints)));
        for (int i = 0; i < SIZE; ++i)
            assertEquals(new Integer(i), q.pollFirst());
    }

    /*
     * poll succeeds unless empty
     */
    public void testPoll() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.pollFirst());
        }
        assertNull(q.pollFirst());
    }

    /*
     * remove(x) removes x and returns true if present
     */
    public void testRemoveElement() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.contains(i));
            assertTrue(q.remove(i));
            assertFalse(q.contains(i));
            assertTrue(q.contains(i-1));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.contains(i));
            assertTrue(q.remove(i));
            assertFalse(q.contains(i));
            assertFalse(q.remove(i+1));
            assertFalse(q.contains(i+1));
        }
        assertTrue(q.isEmpty());
    }

    /*
     * contains(x) reports true when elements added but not yet removed
     */
    public void testContains() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.pollFirst();
            assertFalse(q.contains(new Integer(i)));
        }
    }



    /*
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testContainsAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = set0();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(new Integer(i));
        }
        assertTrue(p.containsAll(q));
    }

    /*
     * retainAll(c) retains only those elements of c and reports true if changed
     */
    public void testRetainAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE-i, q.size());
            p.pollFirst();
        }
    }

    /*
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            NavigableSet q = populatedSet(SIZE);
            NavigableSet p = populatedSet(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE-i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer I = (Integer)(p.pollFirst());
                assertFalse(q.contains(I));
            }
        }
    }

    /*
     * lower returns preceding element
     */
    public void testLower() {
        NavigableSet q = set5();
        Object e1 = q.lower(three);
        assertEquals(two, e1);

        Object e2 = q.lower(six);
        assertEquals(five, e2);

        Object e3 = q.lower(one);
        assertNull(e3);

        Object e4 = q.lower(zero);
        assertNull(e4);
    }

    /*
     * higher returns next element
     */
    public void testHigher() {
        NavigableSet q = set5();
        Object e1 = q.higher(three);
        assertEquals(four, e1);

        Object e2 = q.higher(zero);
        assertEquals(one, e2);

        Object e3 = q.higher(five);
        assertNull(e3);

        Object e4 = q.higher(six);
        assertNull(e4);
    }

    /*
     * floor returns preceding element
     */
    public void testFloor() {
        NavigableSet q = set5();
        Object e1 = q.floor(three);
        assertEquals(three, e1);

        Object e2 = q.floor(six);
        assertEquals(five, e2);

        Object e3 = q.floor(one);
        assertEquals(one, e3);

        Object e4 = q.floor(zero);
        assertNull(e4);
    }

    /*
     * ceiling returns next element
     */
    public void testCeiling() {
        NavigableSet q = set5();
        Object e1 = q.ceiling(three);
        assertEquals(three, e1);

        Object e2 = q.ceiling(zero);
        assertEquals(one, e2);

        Object e3 = q.ceiling(five);
        assertEquals(five, e3);

        Object e4 = q.ceiling(six);
        assertNull(e4);
    }

    /*
     * toArray contains all elements in sorted order
     */
    public void testToArray() {
        NavigableSet q = populatedSet(SIZE);
        Object[] o = q.toArray();
        for (int i = 0; i < o.length; i++)
            assertEquals(o[i], q.pollFirst());
    }

    /*
     * toArray(a) contains all elements in sorted order
     */
    public void testToArray2() {
        NavigableSet<Integer> q = populatedSet(SIZE);
        Integer[] ints = new Integer[SIZE];
        Integer[] array = q.toArray(ints);
        assertSame(ints, array);
        for (int i = 0; i < ints.length; i++)
            assertEquals(ints[i], q.pollFirst());
    }

    /*
     * iterator iterates through all elements
     */
    public void testIterator() {
        NavigableSet q = populatedSet(SIZE);
        int i = 0;
        Iterator it = q.iterator();
        while (it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, SIZE);
    }

    /*
     * iterator of empty set has no elements
     */
    public void testEmptyIterator() {
        NavigableSet q = set0();
        int i = 0;
        Iterator it = q.iterator();
        while (it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(0, i);
    }

    /*
     * iterator.remove removes current element
     */
    public void testIteratorRemove() {
        final NavigableSet q = set0();
        q.add(new Integer(2));
        q.add(new Integer(1));
        q.add(new Integer(3));

        Iterator it = q.iterator();
        it.next();
        it.remove();

        it = q.iterator();
        assertEquals(it.next(), new Integer(2));
        assertEquals(it.next(), new Integer(3));
        assertFalse(it.hasNext());
    }

    /*
     * toString contains toStrings of elements
     */
    public void testToString() {
        NavigableSet q = populatedSet(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.contains(String.valueOf(i)));
        }
    }
//
//    /*
//     * A deserialized serialized set has same elements
//     */
//    public void testSerialization() throws Exception {
//        NavigableSet x = populatedSet(SIZE);
//        NavigableSet y = serialClone(x);
//
//        assertTrue(x != y);
//        assertEquals(x.size(), y.size());
//        assertEquals(x, y);
//        assertEquals(y, x);
//        while (!x.isEmpty()) {
//            assertFalse(y.isEmpty());
//            assertEquals(x.pollFirst(), y.pollFirst());
//        }
//        assertTrue(y.isEmpty());
//    }

    /*
     * subSet returns set with keys in requested range
     */
    public void testSubSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.subSet(two, four);
        assertEquals(two, sm.first());
        assertEquals(three, sm.last());
        assertEquals(2, sm.size());
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        assertFalse(i.hasNext());
        Iterator j = sm.iterator();
        j.next();
        j.remove();
        assertFalse(set.contains(two));
        assertEquals(4, set.size());
        assertEquals(1, sm.size());
        assertEquals(three, sm.first());
        assertEquals(three, sm.last());
        assertTrue(sm.remove(three));
        assertTrue(sm.isEmpty());
        assertEquals(3, set.size());
    }

    public void testSubSetContents2() {
        NavigableSet set = set5();
        SortedSet sm = set.subSet(two, three);
        assertEquals(1, sm.size());
        assertEquals(two, sm.first());
        assertEquals(two, sm.last());
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertFalse(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        assertFalse(i.hasNext());
        Iterator j = sm.iterator();
        j.next();
        j.remove();
        assertFalse(set.contains(two));
        assertEquals(4, set.size());
        assertEquals(0, sm.size());
        assertTrue(sm.isEmpty());
        assertFalse(sm.remove(three));
        assertEquals(4, set.size());
    }

    /*
     * headSet returns set with keys in requested range
     */
    public void testHeadSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.headSet(four);
        assertTrue(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertFalse(sm.contains(four));
        assertFalse(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(one, k);
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        assertFalse(i.hasNext());
        sm.clear();
        assertTrue(sm.isEmpty());
        assertEquals(2, set.size());
        assertEquals(four, set.first());
    }

    /*
     * tailSet returns set with keys in requested range
     */
    public void testTailSetContents() {
        NavigableSet set = set5();
        SortedSet sm = set.tailSet(two);
        assertFalse(sm.contains(one));
        assertTrue(sm.contains(two));
        assertTrue(sm.contains(three));
        assertTrue(sm.contains(four));
        assertTrue(sm.contains(five));
        Iterator i = sm.iterator();
        Object k;
        k = (Integer)(i.next());
        assertEquals(two, k);
        k = (Integer)(i.next());
        assertEquals(three, k);
        k = (Integer)(i.next());
        assertEquals(four, k);
        k = (Integer)(i.next());
        assertEquals(five, k);
        assertFalse(i.hasNext());

        SortedSet ssm = sm.tailSet(four);
        assertEquals(four, ssm.first());
        assertEquals(five, ssm.last());
        assertTrue(ssm.remove(four));
        assertEquals(1, ssm.size());
        assertEquals(3, sm.size());
        assertEquals(4, set.size());
    }

//    /*
//     * size changes when elements added and removed
//     */
//    public void testDescendingSize() {
//        NavigableSet q = populatedSet(SIZE);
//        for (int i = 0; i < SIZE; ++i) {
//            assertEquals(SIZE-i, q.size());
//            q.pollFirst();
//        }
//        for (int i = 0; i < SIZE; ++i) {
//            assertEquals(i, q.size());
//            q.add(new Integer(i));
//        }
//    }

    /*
     * add(null) throws NPE
     */
    public void testDescendingAddNull() {
        try {
            NavigableSet q = dset0();
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * Add of comparable element succeeds
     */
    public void testDescendingAdd() {
        NavigableSet q = dset0();
        assertTrue(q.add(m6));
    }

    /*
     * Add of duplicate element fails
     */
    public void testDescendingAddDup() {
        NavigableSet q = dset0();
        assertTrue(q.add(m6));
        assertFalse(q.add(m6));
    }

    public static class SerializableNonComparable implements Serializable {

    }

    /*
     * Add of non-Comparable throws CCE
     */
    public void testDescendingAddNonComparable() {
        try {
            NavigableSet q = dset0();
            q.add(new SerializableNonComparable());
            q.add(new SerializableNonComparable());
            q.add(new SerializableNonComparable());
            shouldThrow();
        } catch (ClassCastException success) {}
    }

    /*
     * addAll(null) throws NPE
     */
    public void testDescendingAddAll1() {
        try {
            NavigableSet q = dset0();
            q.addAll(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * addAll of a collection with null elements throws NPE
     */
    public void testDescendingAddAll2() {
        try {
            NavigableSet q = dset0();
            Integer[] ints = new Integer[SIZE];
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testDescendingAddAll3() {
        try {
            NavigableSet q = dset0();
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE-1; ++i)
                ints[i] = new Integer(i+SIZE);
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /*
     * Set contains all elements of successful addAll
     */
    public void testDescendingAddAll5() {
        Integer[] empty = new Integer[0];
        Integer[] ints = new Integer[SIZE];
        for (int i = 0; i < SIZE; ++i)
            ints[i] = new Integer(SIZE-1- i);
        NavigableSet q = dset0();
        assertFalse(q.addAll(Arrays.asList(empty)));
        assertTrue(q.addAll(Arrays.asList(ints)));
        for (int i = 0; i < SIZE; ++i)
            assertEquals(new Integer(i), q.pollFirst());
    }

    /*
     * poll succeeds unless empty
     */
    public void testDescendingPoll() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.pollFirst());
        }
        assertNull(q.pollFirst());
    }

    /*
     * remove(x) removes x and returns true if present
     */
    public void testDescendingRemoveElement() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
            assertFalse(q.remove(new Integer(i+1)));
        }
        assertTrue(q.isEmpty());
    }

    /*
     * contains(x) reports true when elements added but not yet removed
     */
    public void testDescendingContains() {
        NavigableSet q = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.pollFirst();
            assertFalse(q.contains(new Integer(i)));
        }
    }

    /*
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testDescendingContainsAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = dset0();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(new Integer(i));
        }
        assertTrue(p.containsAll(q));
    }

    /*
     * retainAll(c) retains only those elements of c and reports true if changed
     */
    public void testDescendingRetainAll() {
        NavigableSet q = populatedSet(SIZE);
        NavigableSet p = populatedSet(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE-i, q.size());
            p.pollFirst();
        }
    }

    /*
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testDescendingRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            NavigableSet q = populatedSet(SIZE);
            NavigableSet p = populatedSet(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE-i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer I = (Integer)(p.pollFirst());
                assertFalse(q.contains(I));
            }
        }
    }

    /*
     * toArray contains all elements
     */
    public void testDescendingToArray() {
        NavigableSet q = populatedSet(SIZE);
        Object[] o = q.toArray();
        Arrays.sort(o);
        for (int i = 0; i < o.length; i++)
            assertEquals(o[i], q.pollFirst());
    }

    /*
     * toArray(a) contains all elements
     */
    public void testDescendingToArray2() {
        NavigableSet q = populatedSet(SIZE);
        Integer[] ints = new Integer[SIZE];
        assertSame(ints, q.toArray(ints));
        Arrays.sort(ints);
        for (int i = 0; i < ints.length; i++)
            assertEquals(ints[i], q.pollFirst());
    }

    /*
     * iterator iterates through all elements
     */
    public void testDescendingIterator() {
        NavigableSet q = populatedSet(SIZE);
        int i = 0;
        Iterator it = q.iterator();
        while (it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, SIZE);
    }

    /*
     * iterator of empty set has no elements
     */
    public void testDescendingEmptyIterator() {
        NavigableSet q = dset0();
        int i = 0;
        Iterator it = q.iterator();
        while (it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(0, i);
    }

    /*
     * iterator.remove removes current element
     */
    public void testDescendingIteratorRemove() {
        final NavigableSet q = dset0();
        q.add(new Integer(2));
        q.add(new Integer(1));
        q.add(new Integer(3));

        Iterator it = q.iterator();
        it.next();
        it.remove();

        it = q.iterator();
        assertEquals(it.next(), new Integer(2));
        assertEquals(it.next(), new Integer(3));
        assertFalse(it.hasNext());
    }

    /*
     * toString contains toStrings of elements
     */
    public void testDescendingToString() {
        NavigableSet q = populatedSet(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.contains(String.valueOf(i)));
        }
    }

//    /*
//     * A deserialized serialized set has same elements
//     */
//    public void testDescendingSerialization() throws Exception {
//        NavigableSet x = dset5();
//        NavigableSet y = serialClone(x);
//
//        assertTrue(x != y);
//        assertEquals(x.size(), y.size());
//        assertEquals(x, y);
//        assertEquals(y, x);
//        while (!x.isEmpty()) {
//            assertFalse(y.isEmpty());
//            assertEquals(x.pollFirst(), y.pollFirst());
//        }
//        assertTrue(y.isEmpty());
//    }

}
