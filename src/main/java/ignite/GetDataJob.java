package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GetDataJob implements IgniteCallable<Collection<PersonWithObjectKey>> {

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public Collection<PersonWithObjectKey> call() throws Exception {
        IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(Launcher.CACHE_NAME);
//        IgniteCache<Long, Department> depCache = ignite.getOrCreateCache("dep_cache");

        List<String> ids = new ArrayList<>();
        List<String> types = new ArrayList<>();
        cache.localEntries(CachePeekMode.ALL).forEach(e -> {
            ids.add(String.valueOf(e.getKey()));
            types.add(String.valueOf(e.getValue().getDepartmentType()));
        });

        List<String> deps = new ArrayList<>();
//        depCache.localEntries(CachePeekMode.ALL).forEach(longDepartmentEntry -> {
//            deps.add(String.valueOf(longDepartmentEntry.getValue().getDepartmentType()));
//        });

        System.out.println("Entities on the node: size: " + cache.localSize() + ", ids: " + String.join(", ", ids) +
        ", types: " +  String.join(", ", types) + ", deps:" + String.join(", ", deps));

        return Collections.emptyList();
    }
}
