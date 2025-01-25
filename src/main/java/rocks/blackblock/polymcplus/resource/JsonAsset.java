package rocks.blackblock.polymcplus.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.theepicblock.polymc.api.resource.PolyMcAsset;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * A PolyMC asset class that can contain any Json
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.7.0
 */
public class JsonAsset implements PolyMcAsset {

    private final JsonObject root;

    public JsonAsset (JsonObject root) {
        this.root = root;
    }

    @Override
    public void writeToStream(OutputStream outputStream, Gson gson) throws IOException {
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            gson.toJson(root, writer);
            writer.flush();
        }
    }
}
