package cn.com.liucm.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 登陆检查Filter
 * 
 * @author zhousj
 * @created 2017-03-19
 */
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getPathInfo();
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
