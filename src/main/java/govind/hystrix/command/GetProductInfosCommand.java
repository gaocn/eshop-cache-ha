package govind.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import govind.dao.entity.ProductInfo;
import govind.utils.HttpClientUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.observables.SyncOnSubscribe;

/**
 * 批量查询多个商品数据的command
 */
public class GetProductInfosCommand extends HystrixObservableCommand<ProductInfo> {
	private String[] productIds;

	public GetProductInfosCommand(String[] productIds) {
		//将所有获取商品信息的请求绑定到同一个线程池中
		super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
		this.productIds = productIds;
	}

	@Override
	protected Observable<ProductInfo> construct() {
		return Observable.unsafeCreate(subscriber -> {
			if (!subscriber.isUnsubscribed()) {
				for (int i = 0; i < productIds.length; i++) {
					ProductInfo productInfo = getProductInfo(Long.valueOf(productIds[i]));
					subscriber.onNext(productInfo);
				}
				subscriber.onCompleted();
			}
		});
	}
	private ProductInfo getProductInfo(Long productId) {
		String url = "http://127.0.0.1:8080/getProductInfo/" + productId;
		String respose = HttpClientUtils.sendGetRequest(url);
		return JSONObject.parseObject(respose, ProductInfo.class);
	}
}
