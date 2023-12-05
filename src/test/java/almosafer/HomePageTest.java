package almosafer;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class HomePageTest {

    @Test
    public void getCurrencyList() {
        RestAssured.given().baseUri("https://www.almosafer.com")
                .when().get("/api/system/currency/list")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .assertThat().contentType("application/json")
                .assertThat().body("base", is(not(empty())),
                        "base.name", equalTo("Saudi Riyal"),
                        "equivalent", hasSize(11),
                        "failed", is(empty()),
                        "equivalent.name", hasItem("US Dollar"),
                        "equivalent.name", not(hasItem("Jordanian Dinar"))
                );
    }
}
