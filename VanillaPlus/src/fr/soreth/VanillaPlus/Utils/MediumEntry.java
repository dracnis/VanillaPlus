package fr.soreth.VanillaPlus.Utils;

public final class MediumEntry<I, J, K>{
    private I key;
    private J value;
    private K extraValue;

    public MediumEntry(I key, J value, K extraValue) {
        this.key = key;
        this.value = value;
        this.extraValue = extraValue;
    }
    public I getKey() {
        return key;
    }
    public J getValue() {
        return value;
    }
    public K getExtraValue() {
        return extraValue;
    }
    public J setValue(J value) {
        J old = this.value;
        this.value = value;
        return old;
    }
}