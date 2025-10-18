package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed)
            throws BreedFetcher.BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        Request req = new Request.Builder().url(url).get().build();

        try (Response res = client.newCall(req).execute()) {
            if (!res.isSuccessful() || res.body() == null) {
                throw new BreedNotFoundException(breed);
            }

            String body = res.body().string();
            JSONObject json = new JSONObject(body);

            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                throw new BreedNotFoundException(breed);
            }

            JSONArray message = json.optJSONArray("message");
            List<String> out = new ArrayList<>();
            if (message != null) {
                for (int i = 0; i < message.length(); i++) {
                    out.add(message.getString(i));
                }
            }
            return out;
        } catch (IOException | RuntimeException e) {
            // Wrap all issues as BreedNotFoundException per interface contract
            throw new BreedNotFoundException(breed);
        }
    }
}