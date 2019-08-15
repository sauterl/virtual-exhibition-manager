package ch.unibas.dmi.dbis.vrem.database.dao;

import ch.unibas.dmi.dbis.vrem.model.collection.ArtCollection;
import ch.unibas.dmi.dbis.vrem.model.collection.ExhibitUpload;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibit;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibition;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class VREMWriter extends VREMDao {
    /**
     *
     * @param database
     */
    public VREMWriter(MongoDatabase database) {
        super(database);
    }

    /**
     *
     * @param exhibition
     * @return
     */
    public boolean saveExhibition(Exhibition exhibition) {
        final MongoCollection<Exhibition> collection = this.database.getCollection(EXHIBITION_COLLECTION, Exhibition.class);
        final UpdateResult result = collection.replaceOne(Filters.eq("_id", exhibition.id), exhibition);
        if (result.getMatchedCount() == 0) {
            collection.insertOne(exhibition);
        } else if (result.getModifiedCount() == 0) {
            return false;
        }
        return true;
    }
    public String uploadExhibit(ExhibitUpload exhibitUpload) {
        final MongoCollection<ArtCollection> mongoCollection = this.database.getCollection(CORPUS_COLLECTION, ArtCollection.class);

        // Construct new Exhibit to generate ID
        Exhibit to_add = new Exhibit(
                exhibitUpload.exhibit.name,
                exhibitUpload.exhibit.description,
                exhibitUpload.exhibit.path,
                exhibitUpload.exhibit.type,
                exhibitUpload.exhibit.position,
                exhibitUpload.exhibit.size,
                exhibitUpload.exhibit.audio,
                exhibitUpload.exhibit.light
                );

        // Construct Path with ID
        to_add.path = exhibitUpload.artCollection + "/" + to_add.id + "." + exhibitUpload.fileExtension;


        // Add Exhibit to DB
        UpdateResult result = mongoCollection.updateOne(
                Filters.eq("name", exhibitUpload.artCollection),
                Updates.push("exhibits", to_add)
        );

        return to_add.path;

    }
}
