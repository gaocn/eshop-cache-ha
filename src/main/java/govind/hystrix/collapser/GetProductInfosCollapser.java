package govind.hystrix.collapser;

import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.*;
import govind.dao.entity.ProductInfo;
import govind.utils.HttpClientUtils;

import java.util.Collection;
import java.util.List;

public class GetProductInfosCollapser extends HystrixCollapser<List<ProductInfo>, ProductInfo, Long> {
	private Long productId;

	public GetProductInfosCollapser(Long productId) {
		super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("GetProductInfosCollapser"))
				.andCollapserPropertiesDefaults(HystrixCollapserProperties.defaultSetter()
						.withMaxRequestsInBatch(10)
						.withTimerDelayInMilliseconds(10)));
		this.productId = productId;
	}

	@Override
	public Long getRequestArgument() {
		return productId;
	}

	@Override
	protected HystrixCommand<List<ProductInfo>> createCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {
		return new BatchCommand(requests);
	}

	@Override
	protected void mapResponseToRequests(List<ProductInfo> batchResponse, Collection<CollapsedRequest<ProductInfo, Long>> requests) {
		int coutn = 0;
		for (CollapsedRequest<ProductInfo, Long> request : requests) {
			request.setResponse(batchResponse.get(coutn++));
		}
	}

	@Override
	protected String getCacheKey() {
		return "product_info_" + productId;
	}

	private static final class BatchCommand extends HystrixCommand<List<ProductInfo>> {
		private final Collection<CollapsedRequest<ProductInfo, Long>> requests;

		protected BatchCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfosCollapserBatchCommand")));
			this.requests = requests;
		}

		@Override
		protected List<ProductInfo> run() throws Exception {
			StringBuilder builder = new StringBuilder();
			//将一个批次的请求拼接在一起
			for (CollapsedRequest<ProductInfo, Long> request : requests) {
				builder.append(request.getArgument()).append(",");
			}
			String param = builder.toString().substring(0, builder.length() - 1);
			//将多个ids合并在一个batch内，发送一次请求获得所有结果
			String url = "http:127.0.0.1:8080/getProductInfos?productIds=" + param;
			String response = HttpClientUtils.sendGetRequest(url);

			List<ProductInfo> productInfos = JSONArray.parseArray(response, ProductInfo.class);
			return productInfos;
		}
	}
}
