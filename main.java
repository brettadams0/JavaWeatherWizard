import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AdvancedWeatherApp {

    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_API_URL = "http://api.openweathermap.org/data/2.5/forecast";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = scanner.nextLine();
        System.out.print("Do you want the current weather or a 5-day forecast? (current/5-day): ");
        String choice = scanner.nextLine();

        if ("current".equalsIgnoreCase(choice)) {
            getWeather(city, WEATHER_API_URL);
        } else if ("5-day".equalsIgnoreCase(choice)) {
            getWeather(city, FORECAST_API_URL);
        } else {
            System.out.println("Invalid choice. Please enter 'current' or '5-day'.");
        }
    }

    private static void getWeather(String city, String apiUrl) {
        String urlString = apiUrl + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            if (apiUrl.contains("forecast")) {
                System.out.println(parseForecastData(result.toString()));
            } else {
                System.out.println(parseWeatherData(result.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseWeatherData(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        double temp = jsonObject.getJSONObject("main").getDouble("temp");
        String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        return "Current temperature in " + jsonObject.getString("name") + " is: " + temp + "°C and the weather condition is: " + weatherDescription;
    }

    private static String parseForecastData(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);
        StringBuilder forecastBuilder = new StringBuilder();
        forecastBuilder.append("5-Day Forecast for ").append(jsonObject.getJSONObject("city").getString("name")).append(":\n");
        for (int i = 0; i < jsonObject.getJSONArray("list").length(); i += 8) { // Each 8th index represents a new day
            JSONObject dayForecast = jsonObject.getJSONArray("list").getJSONObject(i);
            double temp = dayForecast.getJSONObject("main").getDouble("temp");
            String weatherDescription = dayForecast.getJSONArray("weather").getJSONObject(0).getString("description");
            forecastBuilder.append("Day ").append((i / 8) + 1).append(": ").append(temp).append("°C, ").append(weatherDescription).append("\n");
        }
        return forecastBuilder.toString();
    }
}
