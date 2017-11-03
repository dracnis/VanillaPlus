package fr.soreth.VanillaPlus.Utils;

public final class LiteEntry<K, V>{
    private K key;
    private V value;

    public LiteEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() {
        return key;
    }
    public V getValue() {
        return value;
    }
    public K setKey(K key) {
        K old = this.key;
        this.key = key;
        return old;
    }
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}