package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.Serializable;
import java.sql.*;

public class CacheJdbcClientStore extends CacheStoreAdapter<Long, Person> implements Serializable {

    @IgniteInstanceResource
    private transient Ignite ignite;

    private transient JdbcTemplate jdbcTemplate;

    public CacheJdbcClientStore() {
        jdbcTemplate = new JdbcTemplate(JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", ""));
    }

    @Override
    public Person load(Long aLong) throws CacheLoaderException {
        return null;
    }

    @Override
    public void write(Cache.Entry<? extends Long, ? extends Person> entry) throws CacheWriterException {
        try (Connection conn = connection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "merge into PERSONS (id, balance, type) key (id) VALUES (?, ?, ?)")) {
                Person val = entry.getValue();

                st.setLong(1, entry.getKey());
                st.setLong(2, val.getBalance());
                st.setLong(3, val.getType());

                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to write", e);
        }
    }

    @Override
    public void delete(Object o) throws CacheWriterException {

    }

//    @Override
//    public void loadCache(IgniteBiInClosure<Long, Person> clo, Object... args) {
//        if (args == null || args.length == 0 || args[0] == null)
//            throw new CacheLoaderException("Expected entry count parameter is not provided.");
//
//        final int entryCnt = (Integer) args[0];
//
//        try (Connection conn = connection()) {
//            try (PreparedStatement st = conn.prepareStatement("select * from persons")) {
//                try (ResultSet rs = st.executeQuery()) {
//                    int cnt = 0;
//
//                    while (cnt < entryCnt && rs.next()) {
//                        Person person = new Person();
//
//                        person.setId(rs.getLong(1));
//                        person.setBalance(rs.getLong(2));
//                        person.setType(rs.getLong(3));
//
//                        cnt++;
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new CacheLoaderException("Failed to load values from cache store.", e);
//        }
//    }

    private Connection connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
        connection.setAutoCommit(true);
        return connection;
    }
}
