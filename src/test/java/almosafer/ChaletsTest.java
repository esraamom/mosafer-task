package almosafer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class ChaletsTest {

    @Test
    public void withoutTokenAccommodationCampaign() {
        RestAssured.given().baseUri("https://www.almosafer.com")
                .when().get("/api/accommodation/campaign")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void withTokenAccommodationCampaign() {
        RestAssured.given().baseUri("https://www.almosafer.com")
                .header("x-authorization","skdjfh73273$7268u2j89s")
                .when().get("/api/accommodation/campaign")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .assertThat().body("campaigns", is(empty()));
    }

    @Test
    public void getAccommodationSearch() {
        String baseCurrency = RestAssured.given().baseUri("https://www.almosafer.com")
                .when().get("/api/system/currency/list")
                .then().extract().response().jsonPath().get("base.code");

        File body = new File("src/test/resources/accommodationSearch.json");
        RestAssured.given().baseUri("https://www.almosafer.com")
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/accommodation/property/search")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .assertThat().body("currencyCode", Matchers.equalTo(baseCurrency));
    }
}
