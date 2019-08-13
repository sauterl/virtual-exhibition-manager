package ch.unibas.dmi.dbis.vrem.model.collection;


import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibit;
import org.bson.types.ObjectId;
import java.util.HashMap;
import java.util.Map;

public class ArtCollection {
    public final String id;
    public String name;
    public final Map<String,String> metadata = new HashMap<>();
    public Exhibit[] exhibits;

    public ArtCollection(ObjectId id, String name, Exhibit[] exhibits) {
        this.id = id.toHexString();
        this.name = name;
        this.exhibits = exhibits;
    }

    public ArtCollection(String name, Exhibit[] exhibits) {
        this(new ObjectId(), name, exhibits);
    }

}
