package govind.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import govind.cache.LocationCache;
import govind.dao.entity.ProductInfo;
import govind.utils.HttpClientUtils;

/**
 * 获取商品信息
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
	private static final HystrixCommandKey GETTER_KEY = HystrixCommandKey.Factory.asKey("GetProductInfoCommand");
	private Long productId;

	public GetProductInfoCommand(Long productId) {
		//每个Group就对应一个线程池
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"))
				.andCommandKey(GETTER_KEY));
		this.productId = productId;
	}
	@Override
	protected ProductInfo run() throws Exception {
		//拿到商品id，调用商品，调用商品服务端的接口获取商品id对应的最新消息
		String url = "http://127.0.0.1:8080/getProductInfo/" + productId;
		String respose = HttpClientUtils.sendGetRequest(url);
		ProductInfo productInfo = JSONObject.parseObject(respose, ProductInfo.class);
		return productInfo;
	}

//	@Override
////	protected String getCacheKey() {
////		return "product_info_" + productId;
////	}
////	/**
////	 * 请求当前指定缓存
////	 */
////	public void flushCache(Integer id) {
////		HystrixRequestCache.getInstance(GETTER_KEY, HystrixConcurrencyStrategyDefault.getInstance()).clear(getCacheKey());
////	}


	@Override
	protected ProductInfo getFallback() {
		ProductInfo productInfo = new ProductInfo();
		productInfo.setId(-1);
		return productInfo;
	}
}
