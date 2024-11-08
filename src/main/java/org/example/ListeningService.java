package org.example;

import static org.example.Configs.getPartitionedCacheConfiguration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ContinuousQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.DataA;
import org.example.model.DataKey;

public class ListeningService implements Service {

  private static final Logger LOG = LogManager.getLogger();

  @IgniteInstanceResource
  public transient Ignite ignite;

  @Override
  public void init() {
    LOG.info("Starting up " + getClass().getSimpleName());
  }

  @Override
  public void execute() {
    CacheConfiguration<DataKey, DataA> cacheCfg =
      getPartitionedCacheConfiguration(DataA.class);

    IgniteCache<DataKey, DataA> cache = ignite.getOrCreateCache(cacheCfg);

    ContinuousQuery<DataKey, DataA> contQuery = new ContinuousQuery<>();
    contQuery.setLocal(true);
    contQuery.setLocalListener(events -> events.forEach(
      evt -> LOG.info("{}\t{}\t{}", evt.getEventType(), evt.getKey().id,
        evt.getValue().getData())));

    cache.query(contQuery);
  }
}
