import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public final class TrendingStocks {
  public static List<String> trendingStocks() throws IOException, InterruptedException {
    List<String> trendingList = new ArrayList<>();
    JSONObject obj = new JSONObject(trendingStocksInJson());
    JSONArray arr = obj.getJSONObject("finance").getJSONArray("result");
    for (int i = 0; i < arr.length(); i++) {
      JSONArray quotes = arr.getJSONObject(i).getJSONArray("quotes");
        for (int j = 0; j < quotes.length(); j++) {
        trendingList.add(quotes.getJSONObject(j).getString("symbol"));
      }
    }
    return trendingList;
  }

  private static String trendingStocksInJson() throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://yahoo-finance-low-latency.p.rapidapi.com/v1/finance/trending/US"))
            .header("x-rapidapi-key", "c58c838e38msh5e0d93eed17da54p10f366jsn41b0c8b57fe2")
            .header("x-rapidapi-host", "yahoo-finance-low-latency.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }
}
