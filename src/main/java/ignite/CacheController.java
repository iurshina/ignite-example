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

@RestController
@RequestMapping(path = "/api", method = RequestMethod.GET)
public class CacheController {

    @Autowired
    private Ignite ignite;

    @RequestMapping("/getAll")
    public List<Person> getAll() {
        final IgniteCache<Long, Person> cache = ignite.getOrCreateCache("persons__cache");

        List<Person> people = new ArrayList<>(cache.size());
        cache.forEach(e -> people.add(e.getValue()));

        ignite.compute().broadcast(new GetDataJob());

        return people;
    }

    @RequestMapping("/firstTransaction")
    public Person firstTransaction() throws InterruptedException {
        final IgniteCache<Long, Person> cache = ignite.getOrCreateCache("persons__cache");

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

        Person person = cache.get(1L);
        person.setBalance(11000L);

        Thread.sleep(15000L);

        cache.put(person.getId(), person);

        transaction.commit();

        return cache.get(1L);
    }

    @RequestMapping("/secondTransaction")
    public Person secondTransaction() throws InterruptedException {
        final IgniteCache<Long, Person> cache = ignite.getOrCreateCache("persons__cache");

        Transaction transaction = ignite.transactions().txStart(TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.SERIALIZABLE);

        Person person = cache.get(1L);
        person.setBalance(199000L);

        cache.put(person.getId(), person);

        transaction.commit();

        return cache.get(1L);
    }
}
