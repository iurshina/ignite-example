package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

import static ignite.Launcher.CACHE_NAME;

public class CacheLoader implements LifecycleBean {

    @IgniteInstanceResource
    private Ignite ignite;

    private JdbcTemplate jdbcTemplate;

    public CacheLoader() {
        jdbcTemplate = new JdbcTemplate(JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", ""));
    }

    @Override
    public void onLifecycleEvent(LifecycleEventType lifecycleEventType) throws IgniteException {
        if (lifecycleEventType == LifecycleEventType.AFTER_NODE_START) {
            final IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

            Collection<Person> persons = jdbcTemplate.query("select * from persons", (rs, rowNum) -> {
                Person person = new Person();

                long id = rs.getLong(1);
                long type = rs.getLong(3);

                person.setId(id);
                person.setBalance(rs.getLong(2));
                person.setDepartmentType(rs.getLong(3));

                return person;
            });

            persons.stream().forEach(e -> cache.put(new AffinityKey(e.getId(), e.getDepartmentType()), e));
        }
    }
}
