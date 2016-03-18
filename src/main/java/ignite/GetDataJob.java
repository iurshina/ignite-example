package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static ignite.Launcher.CACHE_NAME;

public class GetDataJob implements IgniteCallable<Collection<Person>> {

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public Collection<Person> call() throws Exception {
        IgniteCache<Long, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        List<String> ids = new ArrayList<>();
        cache.localEntries(CachePeekMode.ALL).forEach(e -> ids.add(String.valueOf(e.getKey())));

        System.out.println("Entities on the node: size: " + cache.localSize() + ", ids: " + String.join(", ", ids));

        return Collections.emptyList();
    }
}
