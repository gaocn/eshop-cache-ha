package govind.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import govind.dao.entity.ProductInfo;

public class GetProductInfoFromMySQLCommand extends HystrixCommand<ProductInfo> {
	Long productId;

	public GetProductInfoFromMySQLCommand(Long productId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetProductInfoFromMySQL"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoFromMySQLTP")));
		this.productId = productId;
	}

	@Override
	protected ProductInfo run() throws Exception {
		//模拟从数据库直接查询部分数据
		String productInfoJSON = "{\"id\":" + productId + ", \"name\":\"iphone8手机\", \"price\":6999,\"pictures\":\"a.jpg,b.jpg\"";
		return JSONObject.parseObject(productInfoJSON, ProductInfo.class);
	}
}
