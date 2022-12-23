package com.shuozi.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.shuozi.reggie.common.BaseContext;
import com.shuozi.reggie.common.R;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.HandshakeResponse;
import java.io.IOException;

//暂时修改了过滤器  让登录不验证
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Log4j2
public class LoginCheckFilter implements Filter
{
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        ServletRequest request；这个是将子类对象赋给父类引用，他运行时的类型是子类，编译时的类型是父类，
//        但是在运行时，父类类型对象调用的方法如果子类里面有，
//        那就执行子类里面的方法，如果编译时的类型也就是父类没有调用的那个方法，则报错。所以在那里要做一个强制类型转换，否则就会报错。

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

//        获取本次请求的uri
        String requestURI = httpServletRequest.getRequestURI();
        log.info("拦截到了请求:{}",requestURI);
//        花括号是占位符
//        log.info("我拦截了{}",requestURI);

//        这里采用的是加载但是不显示数据  s内包括的界面都进行拦截
           String[] s =new String[]{
          "/backend/**",
            "/front/**",
            "/employee/login",
            "/employee/logout",
            "/user/login",   //移动端登陆
            "/user/sendMsg"  //移动端发送短信
        };
        boolean b = checkUrl(s, requestURI);

        if (b == true) {
            log.info("{}不需要处理",requestURI);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
//    判断登陆状态  如果已登录直接放行
        if (httpServletRequest.getSession().getAttribute("employee")!= null)
        {
            log.info("用户已登录，员工的id为{}",httpServletRequest.getSession().getAttribute("employee"));
//            此处添加ThreadLocal的set进行存储 登录进来的员工得id
            Long l = (Long) httpServletRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentLocal(l);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
//      移动端判断
        if (httpServletRequest.getSession().getAttribute("user")!= null)
        {
            log.info("移动端已登录，员工的id为{}",httpServletRequest.getSession().getAttribute("user"));
//            此处添加ThreadLocal的set进行存储 登录进来的员工得id
            Long userId = (Long) httpServletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentLocal(userId);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }


        log.info("用户未登录");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;
    }


    public boolean checkUrl(String[] s ,String requestUri)
    {
        for (String s1 : s) {
            boolean match = PATH_MATCHER.match(s1,requestUri);
            if (match)
            {
                return true;
            }
        }
        return false;
    }
}
