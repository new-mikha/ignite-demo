package org.example;

import static org.example.Client.readSimpleCache;
import static org.example.Configs.getIgniteConfiguration;
import static org.example.Configs.getPartitionedCacheConfiguration;
import static org.example.Configs.getSimpleCacheConfiguration;

import java.util.Scanner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.DataA;
import org.example.model.DataKey;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class AppWithIgniteDataNode {
  private static final Logger LOG = LogManager.getLogger();

  public static void main(String[] args) {

    if(args.length == 0) {
      Client.main(args);
    }

    int serverIndex = Integer.parseInt(args[0]);

    IgniteConfiguration cfg = getIgniteConfiguration(serverIndex);

    try {
      Ignite ignite = Ignition.getOrStart(cfg);

      LOG.info("------------");
      LOG.info("Started server #{}", serverIndex);

      //writeSimpleCache(ignite);
      //readAfterEnter(ignite);
      //writePartitionedCache(ignite);

      if (serverIndex == 3)
        deployServices(ignite);

    } catch (Throwable err) {
      LOG.error("Err in main", err);
      throw err;
    }
  }

  private static void readAfterEnter(Ignite ignite) {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Press Enter to read...");
      scanner.nextLine();

      readSimpleCache(ignite);
    }
  }

  public static void writeSimpleCache(Ignite ignite) {
    CacheConfiguration<Long, DataA> cacheCfg =
      getSimpleCacheConfiguration(DataA.class);

    IgniteCache<Long, DataA> cache = ignite.getOrCreateCache(cacheCfg);

    for (long i = 0; i < 10_000; i++) {
      cache.put(i, new DataA("val" + i));
    }

    LOG.info("{} is filled in, size = {}", cache.getName(), cache.size());
  }

  public static void writePartitionedCache(Ignite ignite) {
    CacheConfiguration<DataKey, DataA> cacheCfg =
      getPartitionedCacheConfiguration(DataA.class);

    IgniteCache<DataKey, DataA> cache = ignite.getOrCreateCache(cacheCfg);

    String[] tickers = { "MSFT", "GOOG", "AMZN", "ALTQ", "TK_A", "TK_B" };

    for (int i = 0; i < 100; i++) {
      String ticker = tickers[i % tickers.length];

      DataKey key = new DataKey(i, ticker);
      cache.putAsync(key, new DataA("Order #" + i + ", " + ticker));
    }

    LOG.info("{} is filled in, size = {}", cache.getName(), cache.size());
  }

  private static void deployServices(Ignite ignite) {
//    ignite.services()
//      .deployClusterSingleton("SimpleService", new SimpleService(() -> "Helloooooo!"));

    ignite.services()
      .deployNodeSingleton("ListeningService", new ListeningService());
  }
}