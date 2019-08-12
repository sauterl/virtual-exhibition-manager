package ch.unibas.dmi.dbis.vrem.server;

import static com.beerboy.ss.descriptor.EndpointDescriptor.endpointPath;
import static com.beerboy.ss.descriptor.MethodDescriptor.path;

import ch.unibas.dmi.dbis.vrem.database.dao.VREMReader;
import ch.unibas.dmi.dbis.vrem.database.dao.VREMWriter;
import ch.unibas.dmi.dbis.vrem.model.ListExhibitionsResponse;
import ch.unibas.dmi.dbis.vrem.model.exhibition.Exhibition;
import ch.unibas.dmi.dbis.vrem.server.handlers.content.RequestContentHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.ListExhibitionsHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.LoadExhibitionHandler;
import ch.unibas.dmi.dbis.vrem.server.handlers.exhibition.SaveExhibitionHandler;
import com.beerboy.ss.SparkSwagger;
import com.beerboy.ss.rest.Endpoint;
import java.nio.file.Path;
import org.bson.types.ObjectId;

/**
 * VREM's REST Api, provided as an OpenApi / Swagger compatible specification.
 *
 * This is a code-first approach of providing an external documentation within the code. See <a
 * href="https://github.com/manusant/spark-swagger">the github repo</a> for further information.
 *
 * @author loris.sauter
 * @see com.beerboy.ss.rest.Endpoint
 */
public class VREMEndpoint implements Endpoint {

  public static final String BASE_PATH = "/";

  private final VREMReader reader;
  private final VREMWriter writer;
  private final Path docRoot;

  public VREMEndpoint(VREMReader reader, VREMWriter writer, Path docRoot) {
    this.reader = reader;
    this.writer = writer;
    this.docRoot = docRoot;
  }

  @Override
  public void bind(final SparkSwagger rest) {
    /* Prepare the endpoint */
    rest.endpoint(
        endpointPath(BASE_PATH)
            .withDescription(
                "VREM REST API exposing operations for dynamically creating, managing and reading virtual exhibitions"),
        ((request, response) -> {
        })
    ).get(path("/content/get/:path")
        .withDescription("Get operation for content requests.")
        .withPathParam().withName(":path").withDescription("URL-encoded path of the content").and()
        .withGenericResponse(), new RequestContentHandler(docRoot)
    ).get(path("/exhibitions/list")
        .withDescription("Lists all available exhibitions")
        .withResponseType(ListExhibitionsResponse.class)
        .withGenericResponse(), new ListExhibitionsHandler(reader)
    ).get(path("/exhibitions/load/:id")
        .withDescription("Loads the specified exhibition")
        .withPathParam().withName(":id").withObject(ObjectId.class).and()
        .withGenericResponse(), new LoadExhibitionHandler(reader)
    ).post(path("/exhibitions/save")
        .withDescription("Saves a given exhibition")
        .withRequestType(Exhibition.class)
        .withGenericResponse(), new SaveExhibitionHandler(writer)
    ).after(((request, response) -> {
      /* Configure the result after processing was completed. */
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Headers", "*");
    }));
  }
}
