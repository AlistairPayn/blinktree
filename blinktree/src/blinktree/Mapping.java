package blinktree;

import java.util.Objects;

final public class Mapping<K extends Comparable<K>> implements Comparable<Mapping<K>> {
  K key;
  Object value;

  public Mapping(final K key) {
    this.key = key;
  }

  public Mapping(final Object value) {
    this.value = value;
  }

  public Mapping(final K key, final Object value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public int compareTo(final Mapping<K> other) {
    return key.compareTo(other.key);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    return compareTo((Mapping<K>) other) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }
}
