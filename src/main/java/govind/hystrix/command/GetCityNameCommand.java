package govind.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import govind.cache.LocationCache;

public class GetCityNameCommand extends HystrixCommand<String> {
	private Integer cityId;

	public GetCityNameCommand(Integer cityId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter()
						.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
		this.cityId = cityId;
	}

	@Override
	protected String run() throws Exception {
		return LocationCache.getCityName(cityId);
	}
}
