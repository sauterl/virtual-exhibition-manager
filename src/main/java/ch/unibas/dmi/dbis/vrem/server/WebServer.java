package ch.unibas.dmi.dbis.vrem.server;

import ch.unibas.dmi.dbis.vrem.config.Config;
import ch.unibas.dmi.dbis.vrem.database.codec.VREMCodecProvider;
import ch.unibas.dmi.dbis.vrem.database.dao.VREMReader;
import ch.unibas.dmi.dbis.vrem.database.dao.VREMWriter;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.ListExhibitionsHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.ListExhibitsHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.LoadExhibitionHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.content.RequestContentHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.SaveExhibitionHandler;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import spark.Spark;

import static spark.Spark.*;


@Command(name = "server", description = "Start the web server")
public class WebServer implements Runnable {

    @Option(title = "Configuration", name = {"--config", "-c"}, description = "Path to configuration file")
    private String config;

    @Override
    public void run() {
        try {
            final Gson gson = new Gson();
            final String json = new String(Files.readAllBytes(Paths.get(this.config)), StandardCharsets.UTF_8);
            final Config config = gson.fromJson(json, Config.class);

            final CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(new VREMCodecProvider()));
            final ConnectionString connectionString = config.database.getConnectionString();
            final MongoClientSettings settings = MongoClientSettings.builder().codecRegistry(registry).applyConnectionString(connectionString).applicationName("VREM").build();

            final MongoClient client = MongoClients.create(settings);
            final MongoDatabase db = client.getDatabase(config.database.database);
            final VREMReader reader = new VREMReader(db);
            final VREMWriter writer = new VREMWriter(db);

            /* Set port. */
            port(config.server.getPort());

            final Path docRoot = config.server.getDocumentRoot();
            if (!Files.exists(docRoot)) {
                throw new IOException("DocumentRoot does not exist.");
            }

            /* Register routes. */
            get("/content/get/:path", new RequestContentHandler(docRoot));
            get("/exhibitions/list", new ListExhibitionsHandler(reader));
            get("/exhibits/list", new ListExhibitsHandler(reader));
            get("/exhibitions/load/:id", new LoadExhibitionHandler(reader));
            post("/exhibitions/save", new SaveExhibitionHandler(writer));

            /* Configure the result after processing was completed. */
            after((request, response) -> {
                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Headers", "*");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
