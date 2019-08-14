package ch.unibas.dmi.dbis.vrem.database.dao;

import ch.unibas.dmi.dbis.vrem.model.collection.ArtCollection;
import ch.unibas.dmi.dbis.vrem.model.collection.ExhibitUpload;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibition;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

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
    public boolean uploadExhibit(ExhibitUpload exhibitUpload) {
        final MongoCollection<ArtCollection> mongoCollection = this.database.getCollection(CORPUS_COLLECTION, ArtCollection.class);
        UpdateResult result = mongoCollection.updateOne(Filters.eq("name", exhibitUpload.artCollection), Updates.addToSet("exhibits", exhibitUpload.exhibit));
        if (result.getModifiedCount() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
