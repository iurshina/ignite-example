package cassandra;

import org.apache.cassandra.service.CassandraDaemon;

import java.io.IOException;

public class CassandraHelper {

    private static CassandraDaemon cassandraDaemon;

    public static void startEmbeddedCassandra() throws IOException {
        System.setProperty("cassandra.config", "file:///D:/Work/Cassandra embedded conf/cassandra.yaml");

        cassandraDaemon = new CassandraDaemon();
        cassandraDaemon.init(null);
        cassandraDaemon.start();
    }

    public static void stopEmbeddedCassandra() throws IOException {
        cassandraDaemon.stop();
    }

    public static void main(String[] args) throws IOException {
        startEmbeddedCassandra();
    }
}
