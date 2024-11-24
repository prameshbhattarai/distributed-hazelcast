package org.distributed.cache;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.distributed.config.Setting;

public class CacheConfig {
    private static final Logger logger = LogManager.getLogger(CacheConfig.class);
    private static HazelcastInstance hazelcastInstance;

    private CacheConfig() {
    }

    public static HazelcastInstance getHazelcastInstance() {
        if (hazelcastInstance == null) {
            logger.info("Initialize the hazelcast...");

            // node A config
            var configA = new Config();
            configA.setClusterName(Setting.CACHE_GROUP);
            var networkConfigA = configA.getNetworkConfig();
            networkConfigA.getJoin()
                    .getTcpIpConfig().setEnabled(true)
                    .addMember("127.0.0.1");
            networkConfigA.setPort(5701);
            hazelcastInstance = Hazelcast.newHazelcastInstance(configA);

            // node B config
            var configB = new Config();
            configB.setClusterName(Setting.CACHE_GROUP);
            var networkConfigB = configB.getNetworkConfig();
            networkConfigB.getJoin()
                    .getTcpIpConfig().setEnabled(true)
                    .addMember("127.0.0.1");
            networkConfigB.setPort(5701);
            hazelcastInstance = Hazelcast.newHazelcastInstance(configB);

            // node C config
            var configC = new Config();
            configC.setClusterName(Setting.CACHE_GROUP);
            var networkConfigC = configC.getNetworkConfig();
            networkConfigC.getJoin()
                    .getTcpIpConfig().setEnabled(true)
                    .addMember("127.0.0.1");
            networkConfigC.setPort(5701);
            hazelcastInstance = Hazelcast.newHazelcastInstance(configC);
        }
        return hazelcastInstance;
    }

    public static void printCacheLocation(String key) {
        var partitionService = getHazelcastInstance().getPartitionService();

        var partition = partitionService.getPartition(key);
        var owner = partition.getOwner();
        logger.info("""
                Owner ID %s
                Owner Address %s
                Partition ID %s
                Key %s
                """.formatted(owner, owner.getAddress().getHost(), partition.getPartitionId(), key));
    }
}
