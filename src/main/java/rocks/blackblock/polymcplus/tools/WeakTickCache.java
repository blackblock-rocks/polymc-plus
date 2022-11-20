package rocks.blackblock.polymcplus.tools;

import java.util.WeakHashMap;

/**
 * Create a TickCache with a WeakHashMap as a backing
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class WeakTickCache<K, V> extends TickCache<K, V> {
    public WeakTickCache(int max_age) {
        super(new WeakHashMap<>(), max_age);
    }
}
