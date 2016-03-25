package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static ignite.Launcher.*;

@RestController
@RequestMapping(path = "/api", method = RequestMethod.GET)
public class CacheController {

    @Autowired
    private Ignite ignite;

    @RequestMapping("/getAll")
    public List<Person> getAll() {
        final IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        List<Person> people = new ArrayList<>(cache.size());
        cache.forEach(e -> people.add(e.getValue()));

        ignite.compute().broadcast(new GetDataJob());

        return people;
    }

    @RequestMapping("/firstTransaction")
    public Person firstTransaction() throws InterruptedException {
        final IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

        Person person = cache.get(new AffinityKey(1L, 1L));
        person.setBalance(11000L);

        Thread.sleep(15000L);

        cache.put(new AffinityKey(person.getId(), 1L), person);

        transaction.commit();

        return cache.get(new AffinityKey(1L, 1L));
    }

    @RequestMapping("/secondTransaction")
    public Person secondTransaction() throws InterruptedException {
        final IgniteCache<AffinityKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

//        Person person = cache.get(1L);
//        person.setBalance(199000L);
//
//        cache.put(person.getId(), person);

        ignite.compute().affinityRun(CACHE_NAME, 1L, () -> System.out.println("AffirnityRun"));

        ignite.compute().affinityRun(CACHE_NAME, 1L, () -> {
            Person person = cache.get(new AffinityKey(2L, 2L));
            person.setBalance(12000L);

            cache.put(new AffinityKey(2L, 2L), person);
            System.out.println("Affinity run: data changed");
        });

        transaction.commit();

        return cache.get(new AffinityKey(2L, 2L));
    }
}
