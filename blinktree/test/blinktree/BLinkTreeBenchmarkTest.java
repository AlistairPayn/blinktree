package blinktree;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Stream;

public class BLinkTreeBenchmarkTest {
  int testSeed = 256;
  int testSize = 10000000;

  @Test
  void benchmarkBLinkTreeRandom() {
    final var random = new Random(testSeed);

    final var batch = Stream.generate(random::nextInt)
            .limit(testSize)
            .toList();

    final var tree = new BLinkTree<Integer, Integer>(1000);
    {
      System.out.println("Testing BLinkTree random insertions: ");
      var start = System.nanoTime();
      batch.forEach((i) -> tree.put(i, i));
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }

    {
      System.out.println("Testing BLinkTree random find: ");
      var start = System.nanoTime();
      batch.forEach(tree::get);
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }
  }

  @Test
  void benchmarkBLinkTreeSequential() {
    final var batch = Stream.iterate(0, i -> i + 1)
            .limit(testSize)
            .toList();

    final var tree = new BLinkTree<Integer, Integer>(1000);
    {
      System.out.println("Testing BLinkTree random insertions: ");
      var start = System.nanoTime();
      batch.forEach((i) -> tree.put(i, i));
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }

    {
      System.out.println("Testing BLinkTree random find: ");
      var start = System.nanoTime();
      batch.forEach(tree::get);
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }
  }

  @Test
  void benchmarkTreeMapRandom() {
    final var random = new Random(testSeed);

    final var batch = Stream.generate(random::nextInt)
            .limit(testSize)
            .toList();

    final var tree = new TreeMap<Integer, Integer>();
    {
      System.out.println("Testing TreeMap random insertions: ");
      var start = System.nanoTime();
      batch.forEach((i) -> tree.put(i, i));
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }

    {
      System.out.println("Testing TreeMap random find: ");
      var start = System.nanoTime();
      batch.forEach(tree::get);
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }
  }

  @Test
  void benchmarkTreeMapSequential() {
    final var batch = Stream.iterate(0, i -> i + 1)
            .limit(testSize)
            .toList();

    final var tree = new TreeMap<Integer, Integer>();
    {
      System.out.println("Testing TreeMap random insertions: ");
      var start = System.nanoTime();
      batch.forEach((i) -> tree.put(i, i));
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }

    {
      System.out.println("Testing TreeMap random find: ");
      var start = System.nanoTime();
      batch.forEach(tree::get);
      var end = System.nanoTime();
      System.out.println("Runtime: " + Duration.of(end - start, ChronoUnit.NANOS).toMillis());
    }
  }
}
