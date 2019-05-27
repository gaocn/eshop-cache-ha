package govind.cache;

import java.util.HashMap;
import java.util.Map;

public class LocationCache {
	private static Map<Integer, String> cityMap = new HashMap<>();
	static {
		cityMap.put(1, "北京");
	}

	public static String getCityName(Integer cityId) {
		return cityMap.get(cityId);
	}

}
