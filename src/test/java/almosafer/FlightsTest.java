package almosafer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;

public class FlightsTest {
    private String route;

    @Test(priority = 1)
    public void getFlightDetails (){
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(3);

        List<HashMap<String,Object>> countryList = RestAssured.given().baseUri("https://www.almosafer.com")
                .when().get("/api/system/country/list")
                .then()
                .extract().response().jsonPath().get();

        Random random = new Random(0);
        String fromCountry = countryList.get(random.nextInt(10)).get("ISOCode").toString();
        String toCountry = countryList.get(random.nextInt(10)).get("ISOCode").toString();
        route = fromCountry + "-" + toCountry;

        RestAssured.given().baseUri("https://www.almosafer.com")
                       .queryParam("query", route + "/" + fromDate + "/" + toDate + "/Economy/1Adult")
                       .when().get("/api/v3/flights/flight/search")
                       .then().log().all()
                       .assertThat().statusCode(HttpStatus.SC_OK)
                       .assertThat().body(
                               "request.searchType" ,equalTo("Roundtrip") ,
                               "request.cabin" ,equalTo("Economy"),
                               "request.pax.totalSeats" ,equalTo(1)
                       );
    }

    @Test(priority = 2)
    public void getAvailableRoutes (){
        File body =new File("src/test/resources/routesBody.json");

        RestAssured.given().baseUri("https://www.almosafer.com")
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/cms/page")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .assertThat().contentType(ContentType.HTML)
                .assertThat().body("routes" ,is(not(empty())))
                .extract().response();
    }

}