package govind.controller;

import com.netflix.hystrix.HystrixCommand;
import govind.dao.entity.ProductInfo;
import govind.hystrix.collapser.GetProductInfosCollapser;
import govind.hystrix.command.GetBrandNameCommand;
import govind.hystrix.command.GetCityNameCommand;
import govind.hystrix.command.GetProductInfoCommand;
import govind.hystrix.command.GetProductInfosCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 */
@Controller
public class CacheController {
	@GetMapping("/change/product/{productId:\\d+}")
	@ResponseBody
	public Object changeProduct(@PathVariable("productId") Long productId) {
		HystrixCommand<ProductInfo> getProductInfoCmd = new GetProductInfoCommand(productId);
		ProductInfo productInfo = getProductInfoCmd.execute();

		// 若下面的操作比较耗时，对对下面代码进行资源隔离和限流
		//String cityName = LocationCache.getCityName(productInfo.getCityId());
		GetCityNameCommand getCityNameCommand = new GetCityNameCommand(productInfo.getCityId());
		String cityName = getCityNameCommand.execute();
		productInfo.setCityName(cityName);

		GetBrandNameCommand brandNameCommand = new GetBrandNameCommand((long) productInfo.getBrandId());
		String brandName = brandNameCommand.execute();
		productInfo.setBrandName(brandName);

		return productInfo;
	}

	/**
	 * 批量查询多条商品数据的请求
	 */
	@GetMapping("/getProducts")
	@ResponseBody
	public Object getProductInfos(@RequestBody String productIds) {
		List<ProductInfo> productInfos = new ArrayList<>();
//		GetProductInfosCommand command = new GetProductInfosCommand(productIds.split(","));
//		command.observe().subscribe(new Observer<ProductInfo>() {
//			@Override
//			public void onCompleted() {
//				System.err.println("请求结束");
//			}
//			@Override
//			public void onError(Throwable e) {
//				System.err.println("请求出错：" + e.getMessage());
//			}
//			@Override
//			public void onNext(ProductInfo productInfo) {
//				System.err.println(productInfo);
//				productInfos.add(productInfo);
//			}
//		});
//		/**
////		 * 同一个请求内，调用多次command，这些command在同一个请求上下文中，
////		 * 因此可以采用请求缓存进行优化。
////		 */
////		for(String productId : productIds.split(",")) {
////			GetProductInfoCommand productInfoCommand = new GetProductInfoCommand(Long.valueOf(productId));
////			ProductInfo productInfo = productInfoCommand.execute();
////			System.err.println(productInfo);
////			System.err.println(productInfoCommand.isResponseFromCache());
////			productInfos.add(productInfo);
////		}
		try {
			List<Future<ProductInfo>> futures = new ArrayList<>();
			for (String productId : productIds.split(",")) {
				//每个实例代表一个请求，内部多个请求会合并为一个batch
				GetProductInfosCollapser collapser = new GetProductInfosCollapser(Long.valueOf(productId));
				futures.add(collapser.queue());
			}
			for (Future<ProductInfo> future : futures) {
				System.err.println(future.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return productInfos;
	}
}
