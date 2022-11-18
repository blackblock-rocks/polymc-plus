package rocks.blackblock.polymcplus.compatibility;

import net.fabricmc.loader.api.FabricLoader;

public class PolyCompatibility {

    /**
     * See if a certain mod is loaded
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     *
     * @param    mod_id   The identifier of the mod to check for
     */
    public static boolean isModLoaded(String mod_id) {
        return FabricLoader.getInstance().isModLoaded(mod_id);
    }

    public static boolean hasPolyvalent() {
        return isModLoaded("polyvalent");
    }

}
