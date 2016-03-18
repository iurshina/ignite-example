package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
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
        final IgniteCache<PersonKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        List<Person> people = new ArrayList<>(cache.size());
        cache.forEach(e -> people.add(e.getValue()));

        ignite.compute().broadcast(new GetDataJob());

        return people;
    }

    @RequestMapping("/firstTransaction")
    public Person firstTransaction() throws InterruptedException {
        final IgniteCache<PersonKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

        Person person = cache.get(new PersonKey(1L, 1));
        person.setBalance(11000L);

        Thread.sleep(15000L);

        cache.put(person.getId(), person);

        transaction.commit();

        return cache.get(new PersonKey(1L, 1));
    }

    @RequestMapping("/secondTransaction")
    public Person secondTransaction() throws InterruptedException {
        final IgniteCache<PersonKey, Person> cache = ignite.getOrCreateCache(CACHE_NAME);

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

//        Person person = cache.get(1L);
//        person.setBalance(199000L);
//
//        cache.put(person.getId(), person);

        ignite.compute().affinityRun(CACHE_NAME, 1L, () -> System.out.println("AffirnityRun"));

        ignite.compute().affinityRun(CACHE_NAME, 1L, () -> {
            Person person = cache.get(new PersonKey(2L, 2));
            person.setBalance(12000L);

            cache.put(person.getId(), person);
            System.out.println("Affinity run: data changed");
        });

        transaction.commit();

        return cache.get(new PersonKey(2L, 2));
    }
}
