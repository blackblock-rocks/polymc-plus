package rocks.blackblock.polymcplus.tools;

import rocks.blackblock.polymcplus.PolyMcPlus;

import java.util.HashMap;
import java.util.Map;

/**
 * A hashmap where values are stored for a certain duration
 * in Minecraft ticks
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class TickCache<K, V> {

    // The actual backing map to use
    private final Map<K, ValueInfo> map;

    // The overal max age any entry can be
    private final int max_age;

    // Create a new TickCache with a regular HashMap
    public TickCache(int max_age) {
        this(new HashMap<>(), max_age);
    }

    // Create a new TickCache with a specific type of map as backing
    protected TickCache(Map<K, ValueInfo> map, int max_age) {
        this.map = map;
        this.max_age = max_age;
    }

    /**
     * Put in a new key & value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void put(K key, V value) {
        ValueInfo info = new ValueInfo(value, PolyMcPlus.getTick());
        this.map.put(key, info);
    }

    /**
     * Get the value of a certain key
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public V get(K key) {

        V result = null;
        ValueInfo info = this.map.get(key);

        if (info != null) {
            result = info.getValueIfAllowed();

            if (result == null) {
                this.map.remove(key);
            }
        }

        return result;
    }

    /**
     * Is there a valid value for the given key?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public boolean contains(K key) {
        ValueInfo info = this.map.get(key);
        return info != null && !info.isExpired();
    }

    /**
     * The internal class to keep track of an entry's value & age
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    protected class ValueInfo {
        private final V value;
        private int tick_birth;
        private final int tick_death;

        public ValueInfo(V value, int tick_birth) {
            this.value = value;
            this.tick_birth = tick_birth;
            this.tick_death = tick_birth + max_age;
        }

        public boolean isExpired() {
            return PolyMcPlus.getTick() > this.tick_death;
        }

        public V getValueIfAllowed() {
            if (this.isExpired()) {
                return null;
            }

            return this.value;
        }
    }
}
