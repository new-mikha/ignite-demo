package org.example;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.log4j2.Log4J2Logger;
import org.apache.ignite.spi.collision.fifoqueue.FifoQueueCollisionSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.example.model.DataKey;

public class Configs {

  public static <T> CacheConfiguration<Long, T> getSimpleCacheConfiguration(
    Class<T> clazz)
  {
    CacheConfiguration<Long, T> cacheCfg = new CacheConfiguration<>();

    cacheCfg.setName("simple-" + clazz.getSimpleName());
    cacheCfg.setCacheMode(CacheMode.PARTITIONED);
    cacheCfg.setBackups(1);
    cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
    cacheCfg.setStatisticsEnabled(true);
    cacheCfg.setTypes(Long.class, clazz);

    return cacheCfg;
  }

  public static <T> CacheConfiguration<DataKey, T> getPartitionedCacheConfiguration(
    Class<T> clazz)
  {
    CacheConfiguration<DataKey, T> cacheCfg = new CacheConfiguration<>();

    cacheCfg.setName(clazz.getSimpleName());
    cacheCfg.setCacheMode(CacheMode.PARTITIONED);
    cacheCfg.setBackups(1);
    cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
    cacheCfg.setStatisticsEnabled(true);
    cacheCfg.setTypes(DataKey.class, clazz);

    return cacheCfg;
  }

  public static IgniteConfiguration getIgniteConfiguration(int serverIndex) {
    return getIgniteConfiguration(serverIndex, null);
  }

  public static IgniteConfiguration getIgniteConfiguration(int serverIndex,
    String groupName)
  {
    int communicationPort = getCommunicationPort(serverIndex);
    int discoveryPort = getDiscoveryPort(serverIndex);
    int thinPort = getThinPort(serverIndex);

    //    System.out.printf(
    //      "Server index: %d\n\tcommunicationPort: %d\n\tdiscoveryPort: %d\n\tthinPort: %d%n",
    //      serverIndex, communicationPort, discoveryPort, thinPort);

    IgniteConfiguration cfg = new IgniteConfiguration();

    if (groupName != null)
      cfg.setUserAttributes(
        Collections.singletonMap("me.groupName", groupName));

    cfg.setGridLogger(new Log4J2Logger());

    File workDir = Path.of("build", "workdir",
      "inst-" + serverIndex + "-" + System.currentTimeMillis()).toFile();

    cfg.setWorkDirectory(workDir.getAbsolutePath());

    TcpDiscoverySpi discoverySpi = getTcpDiscoverySpi(discoveryPort);

    cfg.setDiscoverySpi(discoverySpi);

    TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
    tcpCommunicationSpi.setLocalPort(communicationPort);
    tcpCommunicationSpi.setTcpNoDelay(true);

    cfg.setCommunicationSpi(tcpCommunicationSpi);

    cfg.setClientConnectorConfiguration(
      new ClientConnectorConfiguration().setJdbcEnabled(true)
        .setOdbcEnabled(true).setThinClientEnabled(true).setPort(thinPort));

    cfg.setCollisionSpi(new FifoQueueCollisionSpi().setParallelJobsNumber(32));

    long offHeapGBs = 2;
    long offHeapBytes = offHeapGBs * 1024 * 1024 * 1024;
    var dataStorageConfiguration = new DataStorageConfiguration();
    var dataRegionConfiguration = new DataRegionConfiguration();
    dataRegionConfiguration.setMaxSize(offHeapBytes)
      .setInitialSize(offHeapBytes);
    dataStorageConfiguration.setDefaultDataRegionConfiguration(
      dataRegionConfiguration);
    cfg.setDataStorageConfiguration(dataStorageConfiguration);

    return cfg;
  }

  private static TcpDiscoverySpi getTcpDiscoverySpi(int discoveryPort) {
    Set<InetSocketAddress> addresses = new HashSet<>();
    for (int i = 0; i < 4; i++)
      addresses.add(new InetSocketAddress("localhost", getDiscoveryPort(i)));

    TcpDiscoveryVmIpFinder finder = new TcpDiscoveryVmIpFinder();
    finder.registerAddresses(addresses);

    TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
    discoverySpi.setLocalPort(discoveryPort);
    discoverySpi.failureDetectionTimeoutEnabled(true);
    discoverySpi.setIpFinder(finder);

    discoverySpi.setJoinTimeout(10_000);

    return discoverySpi;
  }

  private static int getDiscoveryPort(int serverIndex) {
    return 20658 + serverIndex * 10;
  }

  private static int getCommunicationPort(int serverIndex) {
    return 19652 + serverIndex * 10;
  }

  private static int getThinPort(int serverIndex) {
    return 21343 + serverIndex * 10;
  }

}
