import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.Collections.shuffle;
import static org.junit.jupiter.api.Assertions.*;

public class BLinkTreeFuzzTest {
  public static <K extends Comparable<K>, V> void verifyContainsAll(BLinkTree<K, V> tree, Collection<K> keys) {
    assertEquals(keys.size(), tree.size, "Incorrect BLinkTree size");
    keys.forEach(key -> assertNotNull(tree.get(key), "Key missing from BLinkTree " + key));
  }

  public static <K extends Comparable<K>, V> void verifyContainsNone(BLinkTree<K, V> tree, Collection<K> keys) {
    keys.forEach(key -> assertNull(tree.get(key), "Key not deleted from BLinkTree " + key));
  }

  public static <K extends Comparable<K>, V> void verifyContainsEntries(BLinkTree<K, V> tree, Map<K, V> entries) {
    assertEquals(entries.size(), tree.size, "Incorrect BLinkTree size");
    entries.forEach((key, value) -> assertEquals(value, tree.get(key), "Key missing from BLinkTree " + key));
  }

  public static <K extends Comparable<K>, V> void verifyLeafOrder(BLinkTree<K, V> tree) {
    var iterator = tree.iterator();
    if (!iterator.hasNext()) {
      return;
    }
    var prev = iterator.next();
    if (!iterator.hasNext()) {
      return;
    }
    var cur = iterator.next();
    assertTrue(
        prev.key.compareTo(cur.key) < 0,
        "BLinkTree LeafNode keys are not ascending, prev " + prev + ", next " + cur
    );
    while (iterator.hasNext()) {
      prev = cur;
      cur = iterator.next();
      assertTrue(
          prev.key.compareTo(cur.key) < 0,
          "BLinkTree LeafNode keys are not ascending, prev " + prev + ", next " + cur
      );
    }
  }

  public static void testRandomAddAllRemoveAll(long seed, int batchSize, int nodeSize) {
    Random rnd = new Random(seed);

    final var pairs = new ArrayList<Mapping<Integer>>();
    Stream.iterate(0, i -> ++i)
        .map(key -> {
          byte[] value = new byte[255];
          rnd.nextBytes(value);
          return new Mapping<>(key, value);
        })
        .limit(batchSize)
        .forEach(pairs::add);

    final var inserted = new HashSet<Integer>();
    final var deleted = new HashSet<Integer>();

    BLinkTree<Integer, Object> tree = new BLinkTree<>(nodeSize);

    shuffle(pairs, rnd);

    for (var p : pairs) {
      tree.put(p.key, p.value);
      inserted.add(p.key);
      verifyContainsAll(tree, inserted);
      verifyContainsNone(tree, deleted);
      verifyLeafOrder(tree);
    }

    shuffle(pairs, rnd);

    for (var p : pairs) {
      assertNotNull(tree.get(p.key), "Key missing from BLinkTree " + p.key);
    }

    for (var p : pairs) {
      assertTrue(tree.remove(p.key), "Could not find key to remove " + p.key);
      Object found = tree.get(p.key);
      assertNull(found, "Key not deleted from BLinkTree " + p.key + ", found " + found);
      deleted.add(p.key);
      inserted.remove(p.key);
      verifyContainsAll(tree, inserted);
      verifyContainsNone(tree, deleted);
      verifyLeafOrder(tree);
    }
  }

  public static void testRandomAddRemoveUnique(long seed, int batchSize, int nodeSize) {
    Random rnd = new Random(seed);

    final var inserted = new ArrayList<Integer>();
    final var deleted = new ArrayList<Integer>();
    final var numbers = new ArrayList<Integer>();

    Stream.iterate(0, i -> ++i).limit(batchSize).forEach(numbers::add);

    shuffle(numbers, rnd);

    final var tree = new BLinkTree<Integer, Integer>(nodeSize);

    for (int n : numbers) {
      if (rnd.nextInt(0, 10) < 6 || tree.isEmpty() || inserted.isEmpty()) {
        tree.put(n, n * 10);
        inserted.add(n);
      } else {
        final var index = rnd.nextInt(0, inserted.size());
        final var target = inserted.get(index);
        deleted.add(target);
        inserted.remove(index);
        tree.remove(target);
      }
      verifyContainsAll(tree, inserted);
      verifyContainsNone(tree, deleted);
      verifyLeafOrder(tree);
    }
  }

  public static void testRandomAddRemoveDuplicates(long seed, int batchSize, int nodeSize) {
    Random rnd = new Random(seed);

    final var inserted = new HashSet<Integer>();
    final var deleted = new HashSet<Integer>();
    final var numbers = new ArrayList<Integer>();

    Stream.generate(() -> rnd.nextInt(0, 100))
        .limit(batchSize)
        .forEach(numbers::add);

    shuffle(numbers, rnd);

    final var entries = new HashMap<Integer, Integer>();
    final var tree = new BLinkTree<Integer, Integer>(nodeSize);

    for (int n : numbers) {
      if (rnd.nextInt(0, 10) < 6 || tree.isEmpty() || inserted.isEmpty()) {
        int value = rnd.nextInt();
        inserted.add(n);
        deleted.remove(n);

        entries.put(n, value);
        tree.put(n, value);
      } else {
        final var index = rnd.nextInt(0, inserted.size());
        final var target = inserted.stream().skip(index).findFirst().get();
        inserted.remove(index);
        deleted.add(target);

        entries.remove(target);
        tree.remove(target);
      }

      verifyContainsNone(tree, deleted);
      verifyContainsEntries(tree, entries);
      verifyLeafOrder(tree);
    }
  }

  @TestFactory
  public Stream<DynamicTest> fuzzTest() {
    return Stream.generate(() -> ThreadLocalRandom.current().nextLong(-100000, 100000))
        .limit(100)
        .flatMap((seed) -> {
              final var batchSize = ThreadLocalRandom.current().nextInt(100, 1000);
              final var nodeSize = ThreadLocalRandom.current().nextInt(4, 100);
              return Stream.of(
                  DynamicTest.dynamicTest(
                      "Test random inserting stage followed by random delete stage"
                          + ", seed " + seed
                          + ", batchSize " + batchSize
                          + ", nodeSize " + nodeSize,
                      () -> testRandomAddAllRemoveAll(seed, batchSize, nodeSize)
                  ),
                  DynamicTest.dynamicTest(
                      "Test random unique inserts and deletes"
                          + ", seed " + seed
                          + ", batchSize " + batchSize
                          + ", nodeSize " + nodeSize,
                      () -> testRandomAddRemoveUnique(seed, batchSize, nodeSize)
                  ),
                  DynamicTest.dynamicTest(
                      "Test random duplicate inserts and deletes"
                          + ", seed " + seed
                          + ", batchSize " + batchSize
                          + ", nodeSize " + nodeSize,
                      () -> testRandomAddRemoveDuplicates(seed, batchSize, nodeSize)
                  )
              );
            }
        );
  }
}
