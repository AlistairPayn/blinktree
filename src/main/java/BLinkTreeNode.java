import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BLinkTreeNode<K extends Comparable<K>> {
  final Object[] array;
  final boolean isInternal;
  BLinkTreeNode<K> left;
  BLinkTreeNode<K> right;
  int size;

  protected BLinkTreeNode(final int arraySize, final boolean isInternal) {
    this.array = new Object[arraySize];
    this.size = 0;
    this.isInternal = isInternal;
  }

  public int getMinSize() {
    return (array.length + 1) / 2;
  }

  public int getMaxSize() {
    return array.length;
  }

  @SuppressWarnings("unchecked")
  public K getKeyAt(final int index) {
    return ((Mapping<K>) array[index]).key;
  }

  @SuppressWarnings("unchecked")
  public void setKeyAt(final int index, final K key) {
    ((Mapping<K>) array[index]).key = key;
  }

  public void setMappingAt(final int index, final Mapping<K> mapping) {
    array[index] = mapping;
  }

  @SuppressWarnings("unchecked")
  public Mapping<K> getMappingAt(final int index) {
    return (Mapping<K>) array[index];
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isOvercapacity() {
    return size >= getMaxSize();
  }

  public boolean isUnderCapacity() {
    return size < getMinSize();
  }

  public int search(final K key) {
    int end = size;
    if (isInternal) {
      --end;
    }
    int index = Arrays.binarySearch(array, 0, end, new Mapping<>(key, null));
    if (index < 0) {
      return -(index + 1);
    }

    if (isInternal) {
      return index + 1;
    }

    return index;
  }

  @SuppressWarnings("unchecked")
  public Object get(final K key) {
    int index = search(key);
    if (isInternal) {
      return ((BLinkTreeNode<K>) getMappingAt(index).value).get(key);
    }
    if (index >= array.length) {
      return null;
    }
    Mapping<K> mapping = getMappingAt(index);
    if (mapping == null || !mapping.key.equals(key)) {
      return null;
    }
    return mapping.value;
  }

  @SuppressWarnings("unchecked")
  public boolean putInternal(final K key, final Object value) {
    boolean insertedKey;

    int index = search(key);
    final var child = ((BLinkTreeNode<K>) getMappingAt(index).value);
    if (child.isInternal) {
      insertedKey = child.putInternal(key, value);
    } else {
      insertedKey = child.putLeaf(key, value);
    }

    if (!child.isOvercapacity()) { // the child is not full return
      return insertedKey;
    }

    if (child.left != null && index > 0) { // redistributed the child with the child's left sibling
      final var separator = child.left.tryTakeFromRightSibling(getKeyAt(index - 1));
      if (separator != null) {
        setKeyAt(index - 1, separator);
        return insertedKey;
      }
    }

    if (child.right != null && index < size - 1) { // redistributed the child with the child's right sibling
      final var separator = child.right.tryTakeFromLeftSibling(getKeyAt(index));
      if (separator != null) {
        setKeyAt(index, separator);
        return insertedKey;
      }
    }

    var promoted = child.split(); // when no redistribution is possible split the child
    insert(promoted.key, promoted.value, index + 1); // +1 because the node resulting from the split is greater keys

    return insertedKey;
  }

  public boolean putLeaf(final K key, final Object value) {
    int index = search(key);
    Mapping<K> mapping = getMappingAt(index);
    if (mapping != null && mapping.key.equals(key)) { // overwrite value of an existing key
      mapping.value = value;
      return false;
    } else { // insert a new key value pair
      insert(key, value, index);
      return true;
    }
  }

  public void insert(final K key, final Object value, final int index) {
    System.arraycopy(array, index, array, index + 1, size - index);
    setMappingAt(index, new Mapping<>(key, value));
    if (isInternal) {
      Mapping<K> left = getMappingAt(index - 1);
      Mapping<K> right = getMappingAt(index);
      K temp = left.key;
      left.key = right.key;
      right.key = temp;
    }
    ++size;
  }

  public Mapping<K> split() {
    BLinkTreeNode<K> other = new BLinkTreeNode<>(array.length, isInternal);

    other.size = size / 2;
    size -= other.size;

    System.arraycopy(array, size, other.array, 0, other.size);
    Arrays.fill(array, size, size + other.size, null);

    other.left = this;
    other.right = right;
    if (right != null) {
      right.left = other;
    }
    right = other;

    if (isInternal) {
      K separator = getKeyAt(size - 1);
      setKeyAt(size - 1, null);
      other.setKeyAt(other.size - 1, null);
      return new Mapping<>(separator, other);
    }

    return new Mapping<>(other.getKeyAt(0), other);
  }

  @SuppressWarnings("unchecked")
  public boolean removeInternal(final K key) {
    boolean removed;
    int index = search(key);
    final var child = ((BLinkTreeNode<K>) getMappingAt(index).value);
    if (child.isInternal) {
      removed = child.removeInternal(key);
    } else {
      removed = child.removeLeaf(key);
    }

    if (!child.isUnderCapacity()) { // the child is not full return
      return removed;
    }

    if (child.left != null && index > 0) { // redistributed the child with the child's left sibling
      final var separator = child.tryTakeFromLeftSibling(getKeyAt(index - 1));
      if (separator != null) {
        setKeyAt(index - 1, separator);
        return removed;
      }
    }

    if (child.right != null && index < size - 1) { // redistributed the child with the child's right sibling
      final var separator = child.tryTakeFromRightSibling(getKeyAt(index));
      if (separator != null) {
        setKeyAt(index, separator);
        return removed;
      }
    }

    if (child.left != null && index > 0) { // when no redistribution is possible merge the child with its left sibling
      K separator = getKeyAt(index - 1);
      if (child.left.merge(separator)) {
        delete(index);
        return removed;
      }
    }

    if (child.right != null && index < size - 1) { // when no redistribution is possible merge the child with its right sibling
      if (child.merge(getKeyAt(index))) {
        delete(index + 1);
        return removed;
      }
    }

    return removed;
  }

  public boolean removeLeaf(final K key) {
    int index = search(key);
    Mapping<K> mapping = getMappingAt(index);
    if (mapping != null && mapping.key.equals(key)) {
      delete(index);
      return true;
    }
    return false;
  }

  public void delete(final int index) {
    if (isInternal) {
      if (index < size - 1) {
        setKeyAt(index - 1, getKeyAt(index));
      } else {
        setKeyAt(index - 1, null);
      }
    }
    if (index < size - 1) {
      System.arraycopy(array, index + 1, array, index, size - index - 1);
    } else if (isInternal) {
      setKeyAt(index - 1, null);
    }
    setMappingAt(size - 1, null);
    --size;
  }

  public boolean merge(final K separator) {
    if (size + right.size >= getMaxSize()) { // can't merge nodes if resulting node will be overcapacity
      return false;
    }

    if (isInternal) {
      setKeyAt(size - 1, separator);
    }

    System.arraycopy(right.array, 0, array, size, right.size);
    Arrays.fill(right.array, 0, right.size, null);

    size += right.size;
    right.size = 0;

    right = right.right;
    if (right != null) {
      right.left = this;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  public K tryTakeFromRightSibling(final K separator) {
    if (size >= getMaxSize() - 1 || right.size <= right.getMinSize()) {
      return null;
    }

    if (isInternal) {
      setKeyAt(size - 1, separator);
    }

    int delta = right.size - (size + right.size) / 2;
    System.arraycopy(right.array, 0, array, size, delta);
    System.arraycopy(right.array, delta, right.array, 0, right.size - delta);
    Arrays.fill(right.array, right.size - delta, right.size, null);

    size += delta;
    right.size -= delta;

    if (isInternal) {
      K key = getKeyAt(size - 1);
      setKeyAt(size - 1, null);
      return key;
    }

    return right.getKeyAt(0);
  }

  @SuppressWarnings("unchecked")
  public K tryTakeFromLeftSibling(final K separator) {
    if (size >= getMaxSize() - 1 || left.size <= left.getMinSize()) {
      return null;
    }

    if (isInternal) {
      left.setKeyAt(left.size - 1, separator);
    }

    int delta = left.size - (size + left.size) / 2;
    System.arraycopy(array, 0, array, delta, size);
    System.arraycopy(left.array, left.size - delta, array, 0, delta);
    Arrays.fill(left.array, left.size - delta, left.size, null);

    size += delta;
    left.size -= delta;

    if (isInternal) {
      return left.getKeyAt(left.size - 1);
    }

    return getKeyAt(0);
  }
}
