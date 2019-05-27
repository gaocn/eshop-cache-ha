package govind.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import govind.cache.LocationCache;
import govind.dao.entity.ProductInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StubbedFallbackCommand extends HystrixCommand<ProductInfo> {
	private Integer productId;
	public StubbedFallbackCommand(boolean failure) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("StubbedFallbackGroup")));
	}
	@Override
	protected ProductInfo run() throws Exception {
		throw new Exception("");
	}

	@Override
	protected ProductInfo getFallback() {
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
