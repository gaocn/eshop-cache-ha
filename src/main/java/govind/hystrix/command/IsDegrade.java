package govind.hystrix.command;

/**
 * 是否手动降级的标识位
 */
public class IsDegrade {
	private static boolean isDegrede = false;

	public static boolean isDegrede() {
		return isDegrede;
	}

	public static void setDegrede(boolean isDegrede) {
		IsDegrade.isDegrede = isDegrede;
	}
}
