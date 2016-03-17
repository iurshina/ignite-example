package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.Collection;
import java.util.Collections;

public class GetDataJob implements IgniteCallable<Collection<Person>> {

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public Collection<Person> call() throws Exception {
        IgniteCache<Long, Person> cache = ignite.getOrCreateCache("persons__cache");
        System.out.println("Entities on the node:" + cache.localSize());
        return Collections.emptyList();
    }
}
