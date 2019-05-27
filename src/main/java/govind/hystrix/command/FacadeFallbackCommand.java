package govind.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import govind.dao.entity.ProductInfo;
import govind.utils.HttpClientUtils;

/**
 * 商品服务接口的手动降级方案
 * 1、主流程正常走GetProductInfoCommand。
 * 2、手动降级方案是从某个数据源简单获取一些数据封装后返回，。
 */
public class FacadeFallbackCommand extends HystrixCommand<ProductInfo> {
	private Long productId;

	public FacadeFallbackCommand(Long productId) {
		//每个Group就对应一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("FacadeFallbackGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("FacadeFallbackCommand"))
		.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
		.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
		.withExecutionIsolationSemaphoreMaxConcurrentRequests(15)));
		this.productId = productId;
	}
	@Override
	protected ProductInfo run() throws Exception {
		if (!IsDegrade.isDegrede()) {
			return new GetProductInfoCommand(productId).execute();
		} else {
			return new GetProductInfoFromMySQLCommand(productId).execute();
		}
	}

	@Override
	protected ProductInfo getFallback() {
		return new ProductInfo();
	}
}
