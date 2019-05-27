package govind.controller;

import govind.hystrix.command.IsDegrade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {
	@GetMapping("/getProductInfo/{producdId:\\d+}")
	@ResponseBody
	public String getProductInfo(@PathVariable("producdId") Long productId) {
		String productInfoJson = "{\"id\":" + productId + ", \"name\":\"iphone8手机\", \"price\":6999,\"pictures\":\"a.jpg,b.jpg\",\"specification\":\"iphone8规格\",\"service\":\"售后服务\", \"color\":\"black\", \"size\":\"5.5\",\"shopId\":1,\"modifiedTime\":\"2019-05-17 21:30:00\",\"cityId\":1,\"brandId\":1}";
		return productInfoJson;
	}

	@GetMapping("/degrade/{degrade}")
	public String setDegrade(@PathVariable("degrade") boolean degrade) {
		IsDegrade.setDegrede(degrade);
		return "success";
	}

}
