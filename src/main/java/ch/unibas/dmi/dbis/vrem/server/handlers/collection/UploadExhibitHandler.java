package ch.unibas.dmi.dbis.vrem.server.handlers.collection;

import ch.unibas.dmi.dbis.vrem.database.dao.VREMWriter;
import ch.unibas.dmi.dbis.vrem.model.collection.ExhibitUpload;

import ch.unibas.dmi.dbis.vrem.server.handlers.basic.ParsingActionHandler;

import java.util.Map;

public class UploadExhibitHandler extends ParsingActionHandler<ExhibitUpload> {

    private final VREMWriter writer;

    public UploadExhibitHandler(VREMWriter writer) {
        this.writer = writer;
    }

    @Override
    public ExhibitUpload doPost(ExhibitUpload context, Map<String, String> parameters) {
        this.writer.uploadExhibit(context);
        return context;
    }

    @Override
    public Class<ExhibitUpload> inClass() {
        return ExhibitUpload.class;
    }
}
