package govind.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.servlet.*;
import java.io.IOException;

/**
 *Hystrix请求上下文
 */
public class HystrixRequestContextFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
		try {
			chain.doFilter(request,response);
		} catch (Exception e) {

		} finally {
			ctx.shutdown();
		}
	}
}
