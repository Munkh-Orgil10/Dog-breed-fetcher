package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher underlying;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.underlying = Objects.requireNonNull(fetcher);
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        String key = breed == null ? "" : breed.toLowerCase(Locale.ROOT);

        // return cached value if present
        List<String> cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        // record attempt to call underlying (even if it throws)
        callsMade++;
        try {
            List<String> result = underlying.getSubBreeds(breed);
            result = List.copyOf(result);
            cache.put(key, result);
            return result;
        } catch (BreedNotFoundException e) {
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}