package govind.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import govind.cache.LocationCache;
import govind.dao.entity.ProductInfo;
import govind.utils.HttpClientUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 第一级降级策略：通过备用机房的服务IP获取信息。
 * 第二级降级策略：通过stubbed fallback返回信息。
 * 需要注意的是：在做多级降级时，要将降级command的线程池独立出来，因为如果主
 * 流程的command都失败了可能线程池已经被占满了，降级command必须用自己的独立
 * 线程池确保能够执行。
 */
public class DoubleLevelFallbackCommand extends HystrixCommand<ProductInfo> {
	private Integer productId;

	public DoubleLevelFallbackCommand(boolean failure) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("DoubleLevelFallbackGroup")).andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("DoubleLevelFallbackThreadPool")));
	}

	@Override
	protected ProductInfo run() throws Exception {
		throw new Exception("");
	}

	@Override
	protected ProductInfo getFallback() {
		return new FirstLevelFallbackCommand(productId).execute();
	}

	private static class FirstLevelFallbackCommand extends HystrixCommand<ProductInfo> {
		private Integer productId;

		public FirstLevelFallbackCommand(Integer productId) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("FirstLevelFallbackGroup"))
			.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("FirstLevelFallbackThreadPool")));
			this.productId = productId;
		}

		@Override
		protected ProductInfo run() throws Exception {
			/** 第一级降级策略，从备用机放获取信息 */
			String url = "http://127.0.0.1:8080/getProductInfo/" + productId;
			String respose = HttpClientUtils.sendGetRequest(url);
			ProductInfo productInfo = JSONObject.parseObject(respose, ProductInfo.class);
			return productInfo;
		}

		@Override
		protected ProductInfo getFallback() {
			/** 第二级降级策略，采用stubbed fallbakc机制 */
			ProductInfo productInfo = new ProductInfo();
			productInfo.setId(productId);
			productInfo.setCityId(1);
			productInfo.setCityId(Integer.valueOf(LocationCache.getCityName(1)));
			productInfo.setColor("默认颜色");
			productInfo.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			productInfo.setPictures("default.jpg");
			productInfo.setService("默认售后服务");
			productInfo.setSpecification("默认规格");
			return productInfo;
		}
	}
}
