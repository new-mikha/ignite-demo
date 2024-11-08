package org.example;

import static org.example.AppWithIgniteDataNode.writePartitionedCache;
import static org.example.AppWithIgniteDataNode.writeSimpleCache;
import static org.example.Configs.getIgniteConfiguration;
import static org.example.Configs.getSimpleCacheConfiguration;

import javax.cache.Cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.DataA;

public class Client {
  private static final Logger LOG = LogManager.getLogger();

  public static void main(String[] args) {
    IgniteConfiguration cfg = getIgniteConfiguration(10);
    cfg.setClientMode(true);

    try {
      Ignite ignite = Ignition.getOrStart(cfg);

      LOG.info("------------");
      LOG.info("Started the client");

      // writeSimpleCache(ignite);
      // readSimpleCache(ignite);
      writePartitionedCache(ignite);

    } catch (Throwable err) {
      LOG.error("Err in main", err);
      throw err;
    }
  }

  public static void readSimpleCache(Ignite ignite) {
    CacheConfiguration<Long, DataA> cacheCfg =
      getSimpleCacheConfiguration(DataA.class);

    IgniteCache<Long, DataA> cache = ignite.getOrCreateCache(cacheCfg);

    int iCount = 0;

    for (Cache.Entry<Long, DataA> entry : cache) {
      Long key = entry.getKey();
      DataA value = entry.getValue();

      LOG.info("{}\t{}", key, value.getData());

      if (++iCount > 10)
        break;
    }

    DataA dataA = cache.get(1234L);
    if (dataA != null)
      LOG.info("Value by 'get':\t{}", dataA.getData());

    LOG.info("Done reading, cache size = {}", cache.size());

  }

}
