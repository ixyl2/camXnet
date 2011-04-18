package camxnet;

import java.util.*;
import java.util.Map.*;

class myHeap<K, V> {

    HashMap<K, V> innerMap;
    TreeMap<V, LinkedHashSet<K>> map;
    private V currentV;
    private K currentK;
    byte maxSize;

    myHeap(int maxSize, boolean asc) {
        //maxSize = 0 means no limit
        this.maxSize = (byte) maxSize;
        if (maxSize != 1) {
            innerMap = new HashMap<K, V>();
            if (asc) {
                map = new TreeMap<V, LinkedHashSet<K>>();
            }  else {
                map = new TreeMap<V, LinkedHashSet<K>>(new invertC());
            }
        }
    }

    void put(K k, V v) {
        if (maxSize == 1) {
            if (currentV == null || ((Comparable) v).compareTo(currentV) > 0) {
                currentV = v;
                currentK = k;
            }
        } else if ((maxSize != 0) && (size() >= maxSize)) {
            if (((Comparable) v).compareTo(lastValue()) > 0) {
                removeLast();
                put(k, v);
            }
        } else { //when maxsize is 0 :
            remove(k);

            innerMap.put(k, v);

            if (map.containsKey(v)) {
                map.get(v).add(k);
            } else {
                LinkedHashSet<K> newSet = new LinkedHashSet<K>();
                newSet.add(k);
                map.put(v, newSet);
            }
        }
//        System.out.println("Checking..");
//        for (V vx : map.keySet()) {
//            System.out.println(vx);
//        }
//        System.out.println("finish");
    }

    void remove(K k) {
        if (maxSize == 1) {
            if (currentV != null && currentK.equals(k)) {
                currentK = null;
                currentV = null;
            }
        } else if (innerMap.containsKey(k)) {
            map.get(innerMap.get(k)).remove(k);
            if (map.get(innerMap.get(k)).isEmpty()) {
                map.remove(innerMap.get(k));
            }

            innerMap.remove(k);
        }
    }

    K peek() {
        if (maxSize == 1) {
            return currentK;
        }

        return map.firstEntry().getValue().iterator().next();
    }

    K pop() {
        K k = peek();

        remove(k);

        return k;

    }

    V popValue() {
        if (maxSize == 1) {
            return currentV;
        }
        V v = map.firstKey();

        pop();

        return v;
    }

    V peekValue() {
        if (maxSize == 1) {
            return currentV;
        }
        return map.firstKey();
    }

    V getValue(K k) {
        if (maxSize == 1) {
            return currentV;
        }
        return innerMap.get(k);
    }

    private V lastValue() {
        return map.lastKey();
    }

    private void removeLast() {
        remove(map.get(lastValue()).iterator().next());
    }

    int size() { //checked
        if (maxSize == 1) {
            if (currentK != null) {
                return 1;
            } else {
                return 0;
            }
        }

        return innerMap.size();
    }

    Set<K> keySet() {
        return this.innerMap.keySet();
    }




}

class invertC implements Comparator {

    public int compare(Object o1, Object o2) {
        return -((Comparable) o1).compareTo(o2);
    }

}
