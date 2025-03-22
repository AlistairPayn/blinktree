import java.util.Iterator;
import java.util.NoSuchElementException;

public class BLinkTree<K extends Comparable<K>, V> {
  final int maxSize;
  final int minSize;
  int size = 0;

  BLinkTreeNode<K> root;

  public BLinkTree(final int maxSize) {
    this(maxSize / 2, maxSize);
  }

  public BLinkTree(final int minSize, final int maxSize) {
    if (maxSize < 4) {
      throw new RuntimeException("BTree node size must be greater than 4");
    }
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.root = new BLinkTreeNode<>(maxSize, false);
  }

  public boolean isEmpty() {
    return root == null;
  }

  @SuppressWarnings("unchecked")
  public V get(final K key) {
    return (V) root.get(key);
  }

  public void put(final K key, final V value) {
    boolean insertedKey;
    if (root.isInternal) {
      insertedKey = root.putInternal(key, value);
    } else {
      insertedKey = root.putLeaf(key, value);
    }

    if (insertedKey) {
      ++size;
    }

    if (root.isOvercapacity()) { // when the root is full grow the tree by creating a new root
      final var promoted = root.split();
      final var node = new BLinkTreeNode<K>(maxSize, true);
      node.setMappingAt(0, new Mapping<>(promoted.key, root));
      node.setMappingAt(1, new Mapping<>(null, promoted.value));
      node.size = 2;
      root = node;
    }
  }

  @SuppressWarnings("unchecked")
  public boolean remove(final K key) {
    // TODO: Finish implementation.
    boolean removed;
    if (root.isInternal) {
      removed = root.removeInternal(key);
    } else {
      removed = root.removeLeaf(key);
    }

    if (removed) {
      --size;
    }

    if (root.size == 1 && root.isInternal) {
      root = (BLinkTreeNode<K>) root.getMappingAt(0).value;
    }

    return removed;
  }

  @SuppressWarnings("unchecked")
  public MappingIterator iterator() {
    BLinkTreeNode<K> node = root;
    while (node != null && node.isInternal) {
      node = (BLinkTreeNode<K>) node.getMappingAt(0).value;
    }
    return new MappingIterator(node);
  }

  public class MappingIterator implements Iterator<Mapping<K>> {
    BLinkTreeNode<K> node;
    int index = 0;

    MappingIterator(BLinkTreeNode<K> node) {
      this.node = node;
    }

    @Override
    public boolean hasNext() {
      return node != null && index < node.size;
    }

    @Override
    public Mapping<K> next() {
      if (node == null || index == node.size) {
        throw new NoSuchElementException("End of iterator.");
      }

      final var mapping = node.getMappingAt(index++);
      if (index == node.size) {
        index = 0;
        node = node.right;
      }

      return mapping;
    }
  }
}
