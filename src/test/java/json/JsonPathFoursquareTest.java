package json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Anderson on 2/4/15.
 */
public class JsonPathFoursquareTest {

  @Test
  public void testParseFoursquareApi() throws IOException, URISyntaxException {
    URI uri = new URI( String.format( "https://api.foursquare.com/v2/venues/search" +
                    "?client_id=%s&client_secret=%s&ll=%s&v=20140806&m=foursquare&categoryId=4d4b7105d754a06374d81259",
            "ZB1PWXEJF2ZO3LNDHQEHVU11BHDEX4QTE4ZTZHBQMT3JFZD0",
            "V4SDDITEI54LRYG3C3JTVM3AOEII5GU4HT4DVTHKITFEKYBJ",
            String.format( "%s,%s", -23.588529, -46.680510 ) ) );
    final ParseContext jsonPath = JsonPath.using( Configuration.builder().jsonProvider( new JacksonJsonProvider() )
            .mappingProvider( new JacksonMappingProvider() ).build() );
    final List<Object> list = jsonPath.parse( uri.toURL().openStream() ).read( "$.response.venues" );
    final List<Map<String, Object>> venues = list.stream().collect( mapping( ( o ) -> {
      DocumentContext ctx = jsonPath.parse( o );
      Map<String, Object> venue = new HashMap<>();
      venue.put( "id", ctx.read( "$.id", String.class ) );
      venue.put( "name", ctx.read( "$.name", String.class ) );
      Map<String, String> location = new HashMap<>();
      location.put( "address", ctx.read( "$.location.address", String.class ) );
      location.put( "crossStreet", ctx.read( "$[*].location.crossStreet", String.class ) );
      location.put( "postalCode", ctx.read( "$.location.postalCode", String.class ) );
      location.put( "city", ctx.read( "$.location.city", String.class ) );
      location.put( "state", ctx.read( "$.location.state", String.class ) );
      location.put( "country", ctx.read( "$.location.country", String.class ) );
      venue.put( "location", location );
      return venue;
    }, toList() ) );
    assertThat( venues.isEmpty(), is( false ) );
  }
}
