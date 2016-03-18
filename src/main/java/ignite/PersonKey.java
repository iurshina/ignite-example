package ignite;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class PersonKey {

    private long id;
    @AffinityKeyMapped
    private long type;

    public PersonKey(long id, long type) {
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonKey personKey = (PersonKey) o;

        if (id != personKey.id) return false;
        return type == personKey.type;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (type ^ (type >>> 32));
        return result;
    }
}
