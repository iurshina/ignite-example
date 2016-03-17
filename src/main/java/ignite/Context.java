package ignite;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.FactoryBuilder;
import java.util.Arrays;

@Configuration
public class Context {

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration configuration = new IgniteConfiguration();

        TcpDiscoveryMulticastIpFinder finder = new TcpDiscoveryMulticastIpFinder();
        finder.setAddresses(Arrays.asList("127.0.0.1:47500", "127.0.0.1:47501", "127.0.0.1:47502", "127.0.0.1:47503"));

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(finder);

        configuration.setDiscoverySpi(spi);
//        configuration.setLifecycleBeans(new CacheLoader());
        configuration.setPeerClassLoadingEnabled(true);

        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("persons__cache");
        cacheConfiguration.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

//        cacheConfiguration.setCacheStoreFactory(
//                new FactoryBuilder.SingletonFactory<>(new CacheJdbcClientStore()));

        configuration.setCacheConfiguration(cacheConfiguration);

        return configuration;
    }
}
