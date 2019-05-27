package govind.dao.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 商品信息
 */
@Getter
@Setter
@ToString
public class ProductInfo{
	private int id;
	private String name;
	private String price;
	private String pictures;
	private String specification;
	private String service;
	private String color;
	private String size;
	private int shopId;
	private int cityId;
	private String cityName;
	private int brandId;
	private String brandName;
	private String modifiedTime;
}
