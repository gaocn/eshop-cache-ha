package govind.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GetBrandNameCommand extends HystrixCommand<String> {
	private Long brandId;

	public GetBrandNameCommand(Long brandId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BrandGrp"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("HystrixCommandCmd"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutEnabled(true)
						.withExecutionTimeoutInMilliseconds(200)
						.withCircuitBreakerRequestVolumeThreshold(20)
						.withCircuitBreakerErrorThresholdPercentage(40)
						.withCircuitBreakerSleepWindowInMilliseconds(3000)));
		this.brandId = brandId;
	}

	@Override
	protected String run() throws Exception {
		throw new Exception();
	}

	@Override
	protected String getFallback() {
		return "品牌名：乔丹";
	}
}
