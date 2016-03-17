package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.Event;
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
@ComponentScan(basePackages = {"ignite"})
@Import(Context.class)
public class Launcher {

    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }

    @Bean
    public Ignite ignite(IgniteConfiguration igniteConfiguration) {
        jdbcTemplate = new JdbcTemplate(JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", ""));

        Ignite ignite = Ignition.start(igniteConfiguration);

        final IgniteCache<Long, Person> cache = ignite.getOrCreateCache("persons__cache");

        ignite.events().localListen((IgnitePredicate<Event>) event -> {
            if (ignite.cluster().nodes().size() >= 3) {
                Collection<Person> persons = jdbcTemplate.query("select * from persons", (rs, rowNum) -> {
                    Person person = new Person();

                    person.setId(rs.getLong(1));
                    person.setBalance(rs.getLong(2));
                    person.setType(rs.getLong(3));

                    return person;
                });

                persons.stream().forEach(e -> cache.put(e.getId(), e));
            }

            return true;
        }, EventType.EVT_NODE_JOINED);

        return ignite;
    }
}
