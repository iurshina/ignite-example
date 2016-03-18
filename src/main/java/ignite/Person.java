package ignite;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class Person {

    @AffinityKeyMapped
    private long id;
    private long balance;
    private long type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }
}
