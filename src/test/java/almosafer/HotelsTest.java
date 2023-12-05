package almosafer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class HotelsTest {

    private String sId;

    @Test
    public void searchHotelsWithoutToken (){
        File body =new File("src/test/resources/asyncBody.json");
        RestAssured.given().baseUri("https://www.almosafer.com")
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("api/enigma/search/async")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test(priority = 1)
    public void searchHotelsWithToken(){
        File body =new File("src/test/resources/asyncBody.json");

        Response response= RestAssured.given().baseUri("https://www.almosafer.com")
                .contentType(ContentType.JSON)
                .header("token","skdjfh73273$7268u2j89s")
                .body(body)
                .when().post("api/enigma/search/async")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .assertThat().body("sId", notNullValue())
                .extract().response();

        sId = response.body().jsonPath().get("sId");
    }


   @Test(priority = 2)
    public void checkPollResult (){
       String baseCurrency = RestAssured.given().baseUri("https://www.almosafer.com")
               .when().get("/api/system/currency/list")
               .then().extract().response().jsonPath().get("base.code");

       RestAssured.given().baseUri("https://www.almosafer.com")
               .header("token", "skdjfh73273$7268u2j89s")
               .when().get("/api/enigma/search/poll/" + sId)
               .then().log().all()
               .assertThat().statusCode(HttpStatus.SC_OK)
               .assertThat().body("searchStatus", Matchers.oneOf("IN_PROGRESS", "COMPLETED_SUCCESSFULLY"),
                       "totalResults", Matchers.not(0),
                       "sId", equalTo( sId),
                       "currency", equalTo(baseCurrency));
       sId = null;
    }
}
