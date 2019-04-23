package com.xyj.api.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.xyj.api.model.Meta;
import com.xyj.api.model.UserData;
import com.xyj.api.utils.RedisUtil;
import com.xyj.api.model.ApiRes;
import com.xyj.api.enums.RedisPrefixKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TokenAuthFilter implements Filter {
    // 不需要登录的 API
    private List<String> anno = Arrays.asList("/account/login", "/account/register", "/common/*");

    public void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) var1;
        HttpServletResponse response = (HttpServletResponse) var2;

        String methods = request.getMethod();
        String token = request.getHeader("Basic");

        ServletContext context = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        assert ctx != null;
        RedisUtil redisUtil = ctx.getBean(RedisUtil.class);

        if ("OPTIONS".equals(methods)) {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        final String url = request.getRequestURI();
        boolean isAuthSuccess = false;
        if (anno.stream().anyMatch(i -> tokenIgnore(url, i))) {
            isAuthSuccess = true;
        } else if (Strings.isNullOrEmpty(token) || !redisUtil.exists(token, RedisPrefixKeyEnum.ELogin.toString())) {
            response.setStatus(HttpServletResponse.SC_OK);
            OutputStream outputStream = response.getOutputStream();
            ApiRes res = new ApiRes();
            Meta meta = new Meta();
            meta.setMsg("unAuthorization")
                    .setState(HttpServletResponse.SC_UNAUTHORIZED);
            res.setMeta(meta);

            outputStream.write(JSONObject.toJSONBytes(res));
            outputStream.flush();
            outputStream.close();
        }

        if (!Strings.isNullOrEmpty(token) && redisUtil.exists(token, RedisPrefixKeyEnum.ELogin.toString())) {
            String idAndAccount = redisUtil.get(token, RedisPrefixKeyEnum.ELogin.toString());
            String[] arr = idAndAccount.split(":");

            if (arr.length == 2) {
                UserData userData = new UserData();
                userData.setId(Integer.valueOf(arr[0]));
                userData.setAccount(arr[1]);
                request.setAttribute("userData", userData);
                isAuthSuccess = true;
            }
        }

        if (isAuthSuccess) {
            var3.doFilter(var1, var2);
        }
    }

    /**
     * tokenIgnore
     */
    private boolean tokenIgnore(String url, String str) {
        if (url.equals("/swagger-ui.html")) {
            return true;
        } else if (str.endsWith("*")) {
            str = str.substring(0, str.length() - 1);
            return url.toLowerCase().startsWith(str.toLowerCase());
        } else {
            return url.toLowerCase().equals(str.toLowerCase());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("\n" +
                "   █████▒█    ██  ▄████▄   ██ ▄█▀       ██████╗ ██╗   ██╗ ██████╗\n" +
                " ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝\n" +
                " ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗\n" +
                " ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║\n" +
                " ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝\n" +
                "  ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒       ╚═════╝  ╚═════╝  ╚═════╝\n" +
                "  ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░\n" +
                "  ░ ░    ░░░ ░ ░ ░        ░ ░░ ░            我变秃了，但没有变强\n" +
                "           ░     ░ ░      ░  ░");
    }
}
