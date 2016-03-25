package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lang.IgnitePredicate;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

@SpringBootApplication
@ComponentScan(basePackages = {"ignite", "cassandra"})
@Import(Context.class)
public class Launcher {

    public static final String CACHE_NAME = "persons__cache";

    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }

    @Bean
    public Ignite ignite(IgniteConfiguration igniteConfiguration) {
        jdbcTemplate = new JdbcTemplate(JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", ""));

        Ignite ignite = Ignition.start(igniteConfiguration);

        final IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);
//        final IgniteCache<Long, Department> departmentCache = ignite.getOrCreateCache("dep_cache");

        ignite.events().localListen((IgnitePredicate<DiscoveryEvent>) event -> {
            if (ignite.cluster().nodes().size() >= 3) {
                Collection<Person> persons = jdbcTemplate.query("select * from persons", (rs, rowNum) -> {
                    Person person = new Person();

                    long depType = rs.getLong(3);
                    long id = rs.getLong(1);

                    person.setId(id);
                    person.setBalance(rs.getLong(2));
                    person.setDepartmentType(depType);

                    return person;
                });

                System.out.println();

                persons.stream().forEach(e -> cache.put(new AffinityKey(e.getId(), e.getDepartmentType()), e));

//                departmentCache.put(1L, new Department(1));
//                departmentCache.put(2L, new Department(2));
//                departmentCache.put(3L, new Department(3));
            }

            return true;
        }, EventType.EVT_NODE_JOINED);

        return ignite;
    }
}
