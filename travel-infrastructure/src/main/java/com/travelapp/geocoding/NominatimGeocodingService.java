package com.travelapp.geocoding;

import com.travelapp.events.ports.GeocodingPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NominatimGeocodingService implements GeocodingPort {

    private final WebClient nominatim;

    public NominatimGeocodingService() {
        this.nominatim = WebClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "TravelApp/1.0")
            .defaultHeader("Accept-Language", "en")
            .build();
    }

    @Override
    @Cacheable(value = "geocoding", key = "#query", unless = "#result == null")
    public double[] geocode(String query) {
        if (query == null || query.isBlank()) return null;
        try {
            var results = nominatim.get()
                .uri(u -> u.path("/search")
                    .queryParam("q", query)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .build())
                .retrieve()
                .bodyToMono(List.class)
                .timeout(Duration.ofSeconds(5))
                .block();

            if (results == null || results.isEmpty()) return null;

            @SuppressWarnings("unchecked")
            var first = (Map<String, Object>) results.get(0);
            double lat = Double.parseDouble((String) first.get("lat"));
            double lon = Double.parseDouble((String) first.get("lon"));
            return new double[]{lat, lon};

        } catch (Exception ex) {
            log.warn("Geocoding failed for query '{}': {}", query, ex.getMessage());
            return null;
        }
    }

    @Override
    public double[] geocodeIata(String iataCode) {
        if (iataCode == null) return null;
        var coords = IATA_COORDS.get(iataCode.toUpperCase());
        if (coords != null) return coords;
        // Fallback: ask Nominatim for the airport name
        return geocode(iataCode + " airport");
    }

    // ── IATA lookup table — top ~200 world airports ───────────────────────────

    private static final Map<String, double[]> IATA_COORDS = Map.ofEntries(
        // Europe
        Map.entry("MAD", new double[]{40.4719, -3.5626}),
        Map.entry("BCN", new double[]{41.2974,  2.0833}),
        Map.entry("LHR", new double[]{51.4775, -0.4614}),
        Map.entry("LGW", new double[]{51.1537, -0.1821}),
        Map.entry("STN", new double[]{51.8850,  0.2350}),
        Map.entry("CDG", new double[]{49.0097,  2.5479}),
        Map.entry("ORY", new double[]{48.7233,  2.3794}),
        Map.entry("AMS", new double[]{52.3086,  4.7639}),
        Map.entry("FRA", new double[]{50.0379,  8.5622}),
        Map.entry("MUC", new double[]{48.3537, 11.7750}),
        Map.entry("TXL", new double[]{52.5597, 13.2877}),
        Map.entry("BER", new double[]{52.3667, 13.5033}),
        Map.entry("ZRH", new double[]{47.4647,  8.5492}),
        Map.entry("GVA", new double[]{46.2381,  6.1089}),
        Map.entry("VIE", new double[]{48.1103, 16.5697}),
        Map.entry("FCO", new double[]{41.8003, 12.2389}),
        Map.entry("MXP", new double[]{45.6306,  8.7281}),
        Map.entry("NAP", new double[]{40.8860, 14.2908}),
        Map.entry("ATH", new double[]{37.9364, 23.9445}),
        Map.entry("LIS", new double[]{38.7742, -9.1342}),
        Map.entry("OPO", new double[]{41.2481, -8.6814}),
        Map.entry("BRU", new double[]{50.9014,  4.4844}),
        Map.entry("CPH", new double[]{55.6180, 12.6561}),
        Map.entry("OSL", new double[]{60.1939, 11.1004}),
        Map.entry("ARN", new double[]{59.6519, 17.9186}),
        Map.entry("HEL", new double[]{60.3183, 24.9497}),
        Map.entry("WAW", new double[]{52.1657, 20.9671}),
        Map.entry("PRG", new double[]{50.1008, 14.2600}),
        Map.entry("BUD", new double[]{47.4369, 19.2556}),
        Map.entry("OTP", new double[]{44.5711, 26.0850}),
        Map.entry("SOF", new double[]{42.6967, 23.4114}),
        Map.entry("DUB", new double[]{53.4213, -6.2700}),
        Map.entry("EDI", new double[]{55.9500, -3.3725}),
        Map.entry("MAN", new double[]{53.3537, -2.2750}),
        Map.entry("VLC", new double[]{39.4893,  0.4816}),
        Map.entry("AGP", new double[]{36.6749, -4.4991}),
        Map.entry("PMI", new double[]{39.5517,  2.7388}),
        Map.entry("IBZ", new double[]{38.8728,  1.3731}),
        Map.entry("LPA", new double[]{27.9319, -15.3866}),
        Map.entry("TFS", new double[]{28.0445, -16.5725}),
        // North America
        Map.entry("JFK", new double[]{40.6413, -73.7781}),
        Map.entry("LGA", new double[]{40.7769, -73.8740}),
        Map.entry("EWR", new double[]{40.6925, -74.1687}),
        Map.entry("LAX", new double[]{33.9425, -118.4081}),
        Map.entry("SFO", new double[]{37.6213, -122.3790}),
        Map.entry("ORD", new double[]{41.9742, -87.9073}),
        Map.entry("ATL", new double[]{33.6407, -84.4277}),
        Map.entry("DFW", new double[]{32.8998, -97.0403}),
        Map.entry("MIA", new double[]{25.7959, -80.2870}),
        Map.entry("BOS", new double[]{42.3656, -71.0096}),
        Map.entry("SEA", new double[]{47.4502, -122.3088}),
        Map.entry("DEN", new double[]{39.8561, -104.6737}),
        Map.entry("LAS", new double[]{36.0840, -115.1537}),
        Map.entry("PHX", new double[]{33.4373, -112.0078}),
        Map.entry("IAD", new double[]{38.9531, -77.4565}),
        Map.entry("DCA", new double[]{38.8521, -77.0377}),
        Map.entry("YYZ", new double[]{43.6772, -79.6306}),
        Map.entry("YVR", new double[]{49.1967, -123.1815}),
        Map.entry("YUL", new double[]{45.4706, -73.7408}),
        Map.entry("MEX", new double[]{19.4363, -99.0721}),
        Map.entry("CUN", new double[]{21.0365, -86.8771}),
        // South America
        Map.entry("GRU", new double[]{-23.4356, -46.4731}),
        Map.entry("EZE", new double[]{-34.8222, -58.5358}),
        Map.entry("SCL", new double[]{-33.3930, -70.7858}),
        Map.entry("BOG", new double[]{ 4.7016, -74.1469}),
        Map.entry("LIM", new double[]{-12.0219, -77.1143}),
        // Asia
        Map.entry("DXB", new double[]{25.2528,  55.3644}),
        Map.entry("AUH", new double[]{24.4330,  54.6511}),
        Map.entry("DOH", new double[]{25.2731,  51.6081}),
        Map.entry("IST", new double[]{41.2753,  28.7519}),
        Map.entry("SAW", new double[]{40.8983,  29.3092}),
        Map.entry("SIN", new double[]{ 1.3644, 103.9915}),
        Map.entry("HKG", new double[]{22.3080, 113.9185}),
        Map.entry("BKK", new double[]{13.6900, 100.7501}),
        Map.entry("DMK", new double[]{13.9126, 100.6067}),
        Map.entry("KUL", new double[]{ 2.7456, 101.7072}),
        Map.entry("NRT", new double[]{35.7647, 140.3864}),
        Map.entry("HND", new double[]{35.5494, 139.7798}),
        Map.entry("ICN", new double[]{37.4602, 126.4407}),
        Map.entry("PEK", new double[]{40.0799, 116.6031}),
        Map.entry("PVG", new double[]{31.1443, 121.8083}),
        Map.entry("CAN", new double[]{23.3924, 113.2988}),
        Map.entry("DEL", new double[]{28.5665,  77.1031}),
        Map.entry("BOM", new double[]{19.0896,  72.8656}),
        Map.entry("BLR", new double[]{13.1979,  77.7063}),
        Map.entry("CMB", new double[]{ 7.1808,  79.8841}),
        Map.entry("MLE", new double[]{ 4.1919,  73.5292}),
        Map.entry("TPE", new double[]{25.0777, 121.2325}),
        Map.entry("MNL", new double[]{14.5086, 121.0197}),
        Map.entry("CGK", new double[]{-6.1256, 106.6559}),
        Map.entry("DPS", new double[]{-8.7482, 115.1672}),
        Map.entry("SGN", new double[]{10.8188, 106.6519}),
        Map.entry("HAN", new double[]{21.2212, 105.8072}),
        Map.entry("RGN", new double[]{16.9073,  96.1331}),
        Map.entry("REP", new double[]{13.4107, 103.8133}),
        Map.entry("HKT", new double[]{ 8.1132, 98.3169}),
        Map.entry("CNX", new double[]{18.7669,  98.9628}),
        Map.entry("TLV", new double[]{32.0114,  34.8867}),
        Map.entry("AMM", new double[]{31.7226,  35.9932}),
        Map.entry("BEY", new double[]{33.8209,  35.4883}),
        // Oceania
        Map.entry("SYD", new double[]{-33.9399, 151.1753}),
        Map.entry("MEL", new double[]{-37.6690, 144.8410}),
        Map.entry("BNE", new double[]{-27.3842, 153.1175}),
        Map.entry("PER", new double[]{-31.9403, 115.9669}),
        Map.entry("AKL", new double[]{-37.0082, 174.7850}),
        // Africa
        Map.entry("CAI", new double[]{30.1219,  31.4056}),
        Map.entry("JNB", new double[]{-26.1367,  28.2411}),
        Map.entry("CPT", new double[]{-33.9715,  18.6021}),
        Map.entry("NBO", new double[]{-1.3192,  36.9275}),
        Map.entry("CMN", new double[]{33.3675,  -7.5897}),
        Map.entry("TUN", new double[]{36.8510,  10.2272}),
        Map.entry("ALG", new double[]{36.6910,   3.2153}),
        Map.entry("ADD", new double[]{ 8.9779,  38.7993}),
        Map.entry("ACC", new double[]{ 5.6052,  -0.1668}),
        Map.entry("LOS", new double[]{ 6.5774,   3.3212})
    );
}
