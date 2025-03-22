import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BLinkTreeNodeTest {

  @Test
  void testInternalNodeSearchFixedEven() {
    final var size = 4;

    final var node = new BLinkTreeNode<Integer>(size, true);
    node.size = size;
    node.array[0] = new Mapping<>(1, 0);
    node.array[1] = new Mapping<>(3, 2);
    node.array[2] = new Mapping<>(5, 4);
    node.array[3] = new Mapping<>(null, 6);

    assertEquals(0, node.search(0));
    assertEquals(1, node.search(1));
    assertEquals(1, node.search(2));
    assertEquals(2, node.search(3));
    assertEquals(2, node.search(4));
    assertEquals(3, node.search(5));
    assertEquals(3, node.search(6));
  }

  @Test
  void testLeafNodeSearchFixedEven() {
    final var size = 4;

    final var node = new BLinkTreeNode<Integer>(size, false);
    node.size = size;
    node.array[0] = new Mapping<>(1, 10);
    node.array[1] = new Mapping<>(2, 20);
    node.array[2] = new Mapping<>(3, 30);
    node.array[3] = new Mapping<>(4, 40);

    assertEquals(0, node.search(0));
    assertEquals(0, node.search(1));
    assertEquals(1, node.search(2));
    assertEquals(2, node.search(3));
    assertEquals(3, node.search(4));
    assertEquals(4, node.search(5));
  }

  @Test
  void testInternalNodeSearchEven() {
    final var size = 4;

    final var node = new BLinkTreeNode<Integer>(size, true);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i);
      ++node.size;
      assertEquals(0, node.search(-1));
      assertEquals(i, node.search(i));
      assertEquals(i, node.search(i + 1));
    }
  }

  @Test
  void testLeafNodeSearchEven() {
    final var size = 4;

    final var node = new BLinkTreeNode<Integer>(size, false);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i);
      ++node.size;
      assertEquals(0, node.search(-1));
      assertEquals(i, node.search(i));
      assertEquals(i + 1, node.search(i + 1));
    }
  }

  @Test
  void testInternalNodeSearchOdd() {
    final var size = 5;

    final var node = new BLinkTreeNode<Integer>(size, true);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i);
      ++node.size;
      assertEquals(0, node.search(-1));
      assertEquals(i, node.search(i));
      assertEquals(i, node.search(i + 1));
    }
  }

  @Test
  void testLeafNodeSearchOdd() {
    final var size = 5;

    final var node = new BLinkTreeNode<Integer>(size, false);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i);
      ++node.size;
      assertEquals(0, node.search(-1));
      assertEquals(i, node.search(i));
      assertEquals(i + 1, node.search(i + 1));
    }
  }

  @Test
  void testInternalNodeSplitEven() {
    final var size = 4;

    final var node = new BLinkTreeNode<Integer>(size, true);
    node.size = size;
    node.array[0] = new Mapping<>(1, 0);
    node.array[1] = new Mapping<>(3, 2);
    node.array[2] = new Mapping<>(5, 4);
    node.array[3] = new Mapping<>(null, 6);

    final var right = node.split();

    assertEquals(size / 2, node.size);
    assertEquals(1, node.getKeyAt(0));
    assertEquals(0, node.getMappingAt(0).value);
    assertNull(node.getKeyAt(1));
    assertEquals(2, node.getMappingAt(1).value);

    assertEquals(right.key, 3);
    final var rightNode = (BLinkTreeNode<Integer>) right.value;

    assertEquals(size / 2, rightNode.size);
    assertEquals(5, rightNode.getKeyAt(0));
    assertEquals(4, rightNode.getMappingAt(0).value);
    assertNull(rightNode.getKeyAt(1));
    assertEquals(6, rightNode.getMappingAt(1).value);
  }

  @Test
  void testInternalNodeSplitOdd() {
    final var size = 5;

    final var node = new BLinkTreeNode<Integer>(size, true);
    node.size = size;
    node.array[0] = new Mapping<>(1, 0);
    node.array[1] = new Mapping<>(3, 2);
    node.array[2] = new Mapping<>(5, 4);
    node.array[3] = new Mapping<>(7, 6);
    node.array[4] = new Mapping<>(null, 8);

    final var right = node.split();

    assertEquals((size + 1) / 2, node.size);
    assertEquals(1, node.getKeyAt(0));
    assertEquals(0, node.getMappingAt(0).value);
    assertEquals(3, node.getKeyAt(1));
    assertEquals(2, node.getMappingAt(1).value);
    assertNull(node.getKeyAt(2));
    assertEquals(4, node.getMappingAt(2).value);

    assertEquals(right.key, 5);
    final var rightNode = (BLinkTreeNode<Integer>) right.value;

    assertEquals(size / 2, rightNode.size);
    assertEquals(7, rightNode.getKeyAt(0));
    assertEquals(6, rightNode.getMappingAt(0).value);
    assertNull(rightNode.getKeyAt(1));
    assertEquals(8, rightNode.getMappingAt(1).value);
  }

  @Test
  void testLeafNodeSplitEven() {
    final var size = 4;
    final var half = size / 2;

    final var node = new BLinkTreeNode<Integer>(size, false);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i, i * 10);
      ++node.size;
    }

    final var right = node.split();

    assertEquals(half, node.size);
    for (int i = 0; i < half; ++i) {
      assertEquals(i, node.getKeyAt(i));
      assertEquals(i * 10, node.getMappingAt(i).value);
    }

    assertEquals(right.key, 2);
    final var rightNode = (BLinkTreeNode<Integer>) right.value;

    assertEquals(half, rightNode.size);
    for (int i = 0; i < half; ++i) {
      assertEquals(i + half, rightNode.getKeyAt(i));
      assertEquals((i + half) * 10, rightNode.getMappingAt(i).value);
    }
  }

  @Test
  void testLeafNodeSplitOdd() {
    final var size = 4;
    final var half = size / 2;

    final var node = new BLinkTreeNode<Integer>(size, false);
    for (int i = 0; i < size; ++i) {
      node.array[i] = new Mapping<>(i, i * 10);
      ++node.size;
    }

    final var right = node.split();

    assertEquals(half, node.size);
    for (int i = 0; i < half; ++i) {
      assertEquals(i, node.getKeyAt(i));
      assertEquals(i * 10, node.getMappingAt(i).value);
    }

    assertEquals(right.key, 2);
    final var rightNode = (BLinkTreeNode<Integer>) right.value;

    assertEquals(half, rightNode.size);
    for (int i = 0; i < half; ++i) {
      assertEquals(i + half, rightNode.getKeyAt(i));
      assertEquals((i + half) * 10, rightNode.getMappingAt(i).value);
    }
  }

  @TestFactory
  Stream<DynamicTest> testInternalNodeInsert() {
    /* Note that the internal not inserts only every occur to the right of the search index after a node split.
     * This means position can only ever be > 0 and the position of the key inserted is position + 1.
     */
    return Stream.iterate(4, i -> i + 1)
        .limit(8)
        .flatMap(size -> Stream.iterate(0, i -> ++i)
            .limit(size - 1)
            .map(position ->
                DynamicTest.dynamicTest("Position " + position + "/" + (size - 1), () -> {
                  final var node = new BLinkTreeNode<Integer>(size, true);

                  int insertKey = 15;
                  int insertValue = 14;
                  for (int i = 0, k = 1, v = 0; i < size - 1; ++i, k += 2, v += 2) {
                    if (i == position) {
                      insertKey = k;
                      insertValue = v;
                      k += 2;
                      v += 2;
                    }
                    node.array[i] = new Mapping<>(k, v);
                    ++node.size;
                  }

                  node.insert(insertKey, insertValue, position + 1);
                  assertEquals(size, node.size);

                  for (int i = 0, k = 1, v = 0; i < size; ++i, k += 2, v += 2) {
                    if (i == position) {
                      assertEquals(k, node.getKeyAt(i));
                      assertEquals(v, node.getMappingAt(i + 1).value);
                    } else if (i == position + 1) {
                      assertEquals(k, node.getKeyAt(i));
                      assertEquals(v, node.getMappingAt(i - 1).value);
                    } else {
                      assertEquals(k, node.getKeyAt(i));
                      assertEquals(v, node.getMappingAt(i).value);
                    }
                  }
                })
            )
        );
  }

  @TestFactory
  Stream<DynamicTest> testLeafNodeInsert() {
    return Stream.iterate(4, i -> i + 1)
        .limit(8)
        .flatMap(size -> Stream.iterate(0, i -> ++i)
            .limit(size - 1)
            .map(position ->
                DynamicTest.dynamicTest("Position " + position + "/" + (size - 1), () -> {
                  final var node = new BLinkTreeNode<Integer>(size, false);

                  int insertKey = 7;
                  int insertValue = 70;
                  for (int i = 0, k = 0; i < size - 1; ++i, ++k) {
                    if (i == position) {
                      insertKey = k;
                      insertValue = k * 10;
                      ++k;
                    }
                    node.array[i] = new Mapping<>(k, k * 10);
                    ++node.size;
                  }

                  node.insert(insertKey, insertValue, position);
                  assertEquals(size, node.size);

                  for (int i = 0; i < size; ++i) {
                    assertEquals(i, node.getKeyAt(i));
                    assertEquals(i * 10, node.getMappingAt(i).value);
                  }
                })
            )
        );
  }

  @TestFactory
  Stream<DynamicTest> testInternalNodeDelete() {
    /* Note that the internal not inserts only every occur to the right of the search index after a node split.
     * This means position can only ever be > 0 and the position of the key inserted is position + 1.
     */
    return Stream.iterate(4, i -> i + 1)
        .limit(8)
        .flatMap(size -> Stream.iterate(0, i -> ++i)
            .limit(size)
            .map(position ->
                DynamicTest.dynamicTest("Position " + position + "/" + (size - 1), () -> {
                  final var node = new BLinkTreeNode<Integer>(size, true);

                  for (int i = 0, k = 1, v = 0; i < size; ++i, k += 2, v += 2) {
                    node.array[i] = new Mapping<>(k, v);
                    ++node.size;
                  }

                  node.delete(position);
                  assertEquals(size - 1, node.size);

                  for (int i = 0, k = 1, v = 0; i < size - 1; ++i, k += 2, v += 2) {
                    if (i < position) {
                      if (i == node.size - 1) {
                        assertNull(node.getKeyAt(i));
                      } else {
                        assertEquals(k, node.getKeyAt(i));
                      }
                      assertEquals(v, node.getMappingAt(i).value);
                    } else {
                      assertEquals(k + 2, node.getKeyAt(i));
                      assertEquals(v + 2, node.getMappingAt(i).value);
                    }
                  }
                })
            )
        );
  }

  @TestFactory
  Stream<DynamicTest> testLeafNodeDelete() {
    return Stream.iterate(4, i -> i + 1)
        .limit(8)
        .flatMap(size -> Stream.iterate(0, i -> ++i)
            .limit(size - 1)
            .map(position ->
                DynamicTest.dynamicTest("Position " + position + "/" + (size - 1), () -> {
                  final var node = new BLinkTreeNode<Integer>(size, false);

                  for (int i = 0, k = 0; i < size; ++i, ++k) {
                    node.array[i] = new Mapping<>(k, k * 10);
                    ++node.size;
                  }

                  node.delete(position);
                  assertEquals(size - 1, node.size);

                  for (int i = 0; i < size - 1; ++i) {
                    if (i < position) {
                      assertEquals(i, node.getKeyAt(i));
                      assertEquals(i * 10, node.getMappingAt(i).value);
                    } else {
                      assertEquals(i + 1, node.getKeyAt(i));
                      assertEquals((i + 1) * 10, node.getMappingAt(i).value);
                    }
                  }
                })
            )
        );
  }

  @Test
  void testLeftTryTakeFromRightSiblingBothMaxSize() {
    final var size = 4;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = size;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(5, 4);
    left.array[3] = new Mapping<>(null, 6);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = size;
    right.array[0] = new Mapping<>(7, 6);
    right.array[1] = new Mapping<>(9, 8);
    right.array[2] = new Mapping<>(11, 10);
    right.array[3] = new Mapping<>(null, 12);

    right.left = left;
    left.right = right;

    assertNull(left.tryTakeFromRightSibling(7));

    assertEquals(size, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertEquals(5, left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);
    assertNull(left.getMappingAt(3).key);
    assertEquals(6, left.getMappingAt(3).value);

    assertEquals(size, right.size);
    assertEquals(7, right.getMappingAt(0).key);
    assertEquals(6, right.getMappingAt(0).value);
    assertEquals(9, right.getMappingAt(1).key);
    assertEquals(8, right.getMappingAt(1).value);
    assertEquals(11, right.getMappingAt(2).key);
    assertEquals(10, right.getMappingAt(2).value);
    assertNull(left.getMappingAt(3).key);
    assertEquals(12, right.getMappingAt(3).value);
  }


  @Test
  void testRightTryTakeFromLeftSiblingBothMaxSize() {
    final var size = 4;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = size;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(5, 4);
    left.array[3] = new Mapping<>(null, 6);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = size;
    right.array[0] = new Mapping<>(7, 6);
    right.array[1] = new Mapping<>(9, 8);
    right.array[2] = new Mapping<>(11, 10);
    right.array[3] = new Mapping<>(null, 12);

    right.left = left;
    left.right = right;

    assertNull(right.tryTakeFromLeftSibling(7));

    assertEquals(size, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertEquals(5, left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);
    assertNull(left.getMappingAt(3).key);
    assertEquals(6, left.getMappingAt(3).value);

    assertEquals(size, right.size);
    assertEquals(7, right.getMappingAt(0).key);
    assertEquals(6, right.getMappingAt(0).value);
    assertEquals(9, right.getMappingAt(1).key);
    assertEquals(8, right.getMappingAt(1).value);
    assertEquals(11, right.getMappingAt(2).key);
    assertEquals(10, right.getMappingAt(2).value);
    assertNull(left.getMappingAt(3).key);
    assertEquals(12, right.getMappingAt(3).value);
  }

  @Test
  void testLeftTryTakeFromRightSiblingBothMinSize() {
    final var size = 4;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = size - 1;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(null, 4);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = size - 1;
    right.array[0] = new Mapping<>(7, 6);
    right.array[1] = new Mapping<>(9, 8);
    right.array[2] = new Mapping<>(null, 10);

    right.left = left;
    left.right = right;

    assertNull(left.tryTakeFromRightSibling(7));

    assertEquals(size - 1, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertNull(left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);

    assertEquals(size - 1, right.size);
    assertEquals(7, right.getMappingAt(0).key);
    assertEquals(6, right.getMappingAt(0).value);
    assertEquals(9, right.getMappingAt(1).key);
    assertEquals(8, right.getMappingAt(1).value);
    assertNull(right.getMappingAt(2).key);
    assertEquals(10, right.getMappingAt(2).value);
  }


  @Test
  void testRightTryTakeFromLeftSiblingBothMinSize() {
    final var size = 4;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = size - 1;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(null, 4);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = size - 1;
    right.array[0] = new Mapping<>(7, 6);
    right.array[1] = new Mapping<>(9, 8);
    right.array[2] = new Mapping<>(null, 10);

    right.left = left;
    left.right = right;

    assertNull(right.tryTakeFromLeftSibling(5));

    assertEquals(size - 1, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertNull(left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);

    assertEquals(size - 1, right.size);
    assertEquals(7, right.getMappingAt(0).key);
    assertEquals(6, right.getMappingAt(0).value);
    assertEquals(9, right.getMappingAt(1).key);
    assertEquals(8, right.getMappingAt(1).value);
    assertNull(right.getMappingAt(2).key);
    assertEquals(10, right.getMappingAt(2).value);
  }

  @Test
  void testLeftTryTakeFromRightSibling() {
    final var size = 7;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = 3;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(null, 4);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = 6;
    right.array[0] = new Mapping<>(7, 6);
    right.array[1] = new Mapping<>(9, 8);
    right.array[2] = new Mapping<>(11, 10);
    right.array[3] = new Mapping<>(13, 12);
    right.array[4] = new Mapping<>(15, 14);
    right.array[5] = new Mapping<>(null, 16);

    right.left = left;
    left.right = right;

    assertEquals(9, left.tryTakeFromRightSibling(5));

    assertEquals(5, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertEquals(5, left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);
    assertEquals(7, left.getMappingAt(3).key);
    assertEquals(6, left.getMappingAt(3).value);
    assertNull(left.getMappingAt(4).key);
    assertEquals(8, left.getMappingAt(4).value);

    assertEquals(4, right.size);
    assertEquals(11, right.getMappingAt(0).key);
    assertEquals(10, right.getMappingAt(0).value);
    assertEquals(13, right.getMappingAt(1).key);
    assertEquals(12, right.getMappingAt(1).value);
    assertEquals(15, right.getMappingAt(2).key);
    assertEquals(14, right.getMappingAt(2).value);
    assertNull(right.getMappingAt(3).key);
    assertEquals(16, right.getMappingAt(3).value);
  }

  @Test
  void testMergeInternal() {
    final var size = 8;

    final var left = new BLinkTreeNode<Integer>(size, true);
    left.size = 4;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(5, 4);
    left.array[3] = new Mapping<>(null, 6);

    final var right = new BLinkTreeNode<Integer>(size, true);
    right.size = 4;
    right.array[0] = new Mapping<>(9, 8);
    right.array[1] = new Mapping<>(11, 10);
    right.array[2] = new Mapping<>(13, 12);
    right.array[3] = new Mapping<>(null, 14);

    right.left = left;
    left.right = right;

    left.merge(7);

    assertEquals(size, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertEquals(5, left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);
    assertEquals(7, left.getMappingAt(3).key);
    assertEquals(6, left.getMappingAt(3).value);
    assertEquals(9, left.getMappingAt(4).key);
    assertEquals(8, left.getMappingAt(4).value);
    assertEquals(11, left.getMappingAt(5).key);
    assertEquals(10, left.getMappingAt(5).value);
    assertEquals(13, left.getMappingAt(6).key);
    assertEquals(12, left.getMappingAt(6).value);
    assertNull(left.getMappingAt(7).key);
    assertEquals(14, left.getMappingAt(7).value);
  }

  @Test
  void testMergeLeaf() {
    final var size = 8;

    final var left = new BLinkTreeNode<Integer>(size, false);
    left.size = 4;
    left.array[0] = new Mapping<>(1, 0);
    left.array[1] = new Mapping<>(3, 2);
    left.array[2] = new Mapping<>(5, 4);
    left.array[3] = new Mapping<>(7, 6);

    final var right = new BLinkTreeNode<Integer>(size, false);
    right.size = 4;
    right.array[0] = new Mapping<>(9, 8);
    right.array[1] = new Mapping<>(11, 10);
    right.array[2] = new Mapping<>(13, 12);
    right.array[3] = new Mapping<>(15, 14);

    right.left = left;
    left.right = right;

    left.merge(9);

    assertEquals(size, left.size);
    assertEquals(1, left.getMappingAt(0).key);
    assertEquals(0, left.getMappingAt(0).value);
    assertEquals(3, left.getMappingAt(1).key);
    assertEquals(2, left.getMappingAt(1).value);
    assertEquals(5, left.getMappingAt(2).key);
    assertEquals(4, left.getMappingAt(2).value);
    assertEquals(7, left.getMappingAt(3).key);
    assertEquals(6, left.getMappingAt(3).value);
    assertEquals(9, left.getMappingAt(4).key);
    assertEquals(8, left.getMappingAt(4).value);
    assertEquals(11, left.getMappingAt(5).key);
    assertEquals(10, left.getMappingAt(5).value);
    assertEquals(13, left.getMappingAt(6).key);
    assertEquals(12, left.getMappingAt(6).value);
    assertEquals(15, left.getMappingAt(7).key);
    assertEquals(14, left.getMappingAt(7).value);
  }
}