package cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "/cassandra", method = RequestMethod.GET)
public class CassandraController {

    @Autowired
    private Ignite ignite;

    @RequestMapping("/stopCassandra")
    public String stopCassandra() throws IOException {
        CassandraHelper.stopEmbeddedCassandra();
        return "stopped";
    }

    @RequestMapping("/startCassandra")
    public String startCassandra() throws IOException {
        ignite.compute().run(() -> {
            try {
                CassandraHelper.startEmbeddedCassandra();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return "started";
    }

    @RequestMapping("/keySpace")
    public String keySpace() throws IOException {
        ignite.compute().run(() -> {
            Cluster cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .build();

            String query = "CREATE KEYSPACE tp WITH replication "
                    + "= {'class':'SimpleStrategy', 'replication_factor':1};";

            Session session = cluster.connect();
            session.execute(query);

            session.execute("USE tp");

            cluster.close();
        });

        return "Keyspace created";
    }
}
