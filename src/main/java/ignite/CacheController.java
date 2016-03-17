package ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
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
}
