package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.Serializable;
import java.sql.*;

public class CacheJdbcClientStore extends CacheStoreAdapter<AffinityKey, Person> implements Serializable {

    @IgniteInstanceResource
    private transient Ignite ignite;

    private transient JdbcTemplate jdbcTemplate;

    public CacheJdbcClientStore() {
        jdbcTemplate = new JdbcTemplate(JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", ""));
    }

    @Override
    public Person load(AffinityKey aLong) throws CacheLoaderException {
        return null;
    }

    @Override
    public void write(Cache.Entry<? extends AffinityKey, ? extends Person> entry) throws CacheWriterException {
        try (Connection conn = connection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "merge into PERSONS (id, balance, type) key (id) VALUES (?, ?, ?)")) {
                Person val = entry.getValue();

                st.setLong(1, (Long) entry.getKey().key());
                st.setLong(2, val.getBalance());
                st.setLong(3, val.getDepartmentType());

                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to write", e);
        }
    }

    @Override
    public void delete(Object o) throws CacheWriterException {

    }

    private Connection connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
        connection.setAutoCommit(true);
        return connection;
    }
}
