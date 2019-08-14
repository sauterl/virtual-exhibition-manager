package ch.unibas.dmi.dbis.vrem.model.collection;

import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibit;

import java.util.List;
import java.io.File;

public class ExhibitUpload {
    public String artCollection;
    public Exhibit exhibit;
    public List<File> files;

    public ExhibitUpload(String artCollection, Exhibit exhibit, List<File> files) {
        this.artCollection = artCollection;
        this.exhibit = exhibit;
        this.files = files;
    }
}
