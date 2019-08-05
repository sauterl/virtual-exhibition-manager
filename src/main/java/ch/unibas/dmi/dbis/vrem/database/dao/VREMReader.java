package ch.unibas.dmi.dbis.vrem.database.dao;

import ch.unibas.dmi.dbis.vrem.database.codec.ExhibitionCodec;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibit;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibition;

import ch.unibas.dmi.dbis.vrem.model.exhibition.ExhibitionSummary;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class VREMReader extends VREMDao {

    /**
     *
     * @param database
     */
    public VREMReader(MongoDatabase database) {
        super(database);
    }

    /**
     *
     * @param id
     * @return
     */
    public Exhibition getExhibition(ObjectId id) {
        return getExhibition(ExhibitionCodec.FIELD_NAME_ID, id);
    }

    public Exhibition getExhibition(String key){
        return getExhibition(ExhibitionCodec.FIELD_NAME_KEY, key);
    }

    private Exhibition getExhibition(String fieldName, Object key){
        final MongoCollection<Exhibition> exhibitions = this.database.getCollection(EXHIBITION_COLLECTION, Exhibition.class);
        return exhibitions.find(Filters.eq(fieldName,key)).first();
    }

    /**
     *
     * @return
     */
    public List<ExhibitionSummary> listExhibitions() {
        final MongoCollection<Document> exhibitions = database.getCollection(EXHIBITION_COLLECTION);
        final List<ExhibitionSummary> list = new ArrayList<>();
        for (Document document : exhibitions.find().projection(Projections.include("_id", "name"))) {
            list.add(new ExhibitionSummary(document.getObjectId("_id"), document.getString("name")));
        }
        return list;
    }
}


