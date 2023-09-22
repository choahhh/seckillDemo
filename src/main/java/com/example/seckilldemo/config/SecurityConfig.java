package com.example.seckilldemo.config;


import com.example.seckilldemo.entity.vo.FormUserDetails;
import com.example.seckilldemo.service.UserService;
import com.example.seckilldemo.util.AjaxResult;
import com.example.seckilldemo.util.ResultInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
//@Primary
@Slf4j
public class SecurityConfig {


    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserService userService;




    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void doResult(HttpServletRequest request, HttpServletResponse response, Object result, int responseStatus) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(responseStatus);
        PrintWriter out = response.getWriter();
        out.write(objectMapper.writeValueAsString(result));
        out.flush();
        out.close();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] path = {
                "/layer/**",
                "/js/**",
                "/druid/**",
                "/doc.html",
                "/v2/api-docs",
                "/webjars/**",
                "/user/**",
                "/swagger-resources/**",
                "/",
                "/img/**",
                "/bootstrap/**",
                "/jquery-validation/**",
                "/t",
                "/login",
                "/loginweb.html",



        };
        return http.authenticationProvider(authenticationProvider()).authorizeRequests().antMatchers(path).permitAll().anyRequest().authenticated().and().formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
//                .loginPage("/")
                .loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        FormUserDetails userDetails = (FormUserDetails) authentication.getPrincipal();
                        userService.updateLoginTime(userDetails.getId());
//                        LoginLogPO loginLogPO = new LoginLogPO();
                        String remoteIp = getRemoteIp(request);
                        log.info("登入者ip{}=>{}", remoteIp, userDetails.getUsername());
//                        loginLogPO.setLoginIp(remoteIp);
//                        loginLogPO.setNote("登录成功");
//                        loginLogPO.setUserId(userDetails.getUserId());
//                        loginLogPO.setUserName(userDetails.getUserName());
//                        loginLogPO.insert();

                        ResultInfo success = ResultInfo.createSuccess("登录成功");
                        success.setData(authentication);

//                        doResult(request, response, AjaxResult.success("登录成功").setData(authentication));
                        doResult(request, response, success, HttpServletResponse.SC_OK);

                    }
                })
                //登录失败
                .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException, ServletException {
                        log.error("登录失败！失败信息：{}={}", ex, ex.getMessage());
                        AjaxResult error = AjaxResult.error();
                        if (ex instanceof UsernameNotFoundException || ex instanceof BadCredentialsException) {
                            String userName = request.getParameter("username");
                            log.warn("用户名:{} 尝试登录失败", userName);
                            error.setMessage("用户名或密码错误");
//                            Long increment = redisTemplate.opsForValue().increment(incrementKey, 1);
//                            if (increment == 1) {
//                                //设置有效期一分钟
//                                redisTemplate.expire(incrementKey, 25, TimeUnit.MINUTES);
//                            }
                        } else if (ex instanceof DisabledException) {
                            error.setMessage("账户被禁用");
                        } else {
                            error.setMessage("登录失败! " + ex.getMessage());
                        }
                        doResult(request, response, error, HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }).and().exceptionHandling()
                //没有权限，返回json
                .accessDeniedHandler((request, response, ex) -> {
                    doResult(request, response, AjaxResult.error("权限不足").setCode(403), HttpServletResponse.SC_UNAUTHORIZED);
                })
                .and()
                //关闭CSRF保护即可。
                .cors().and().csrf().disable()
                //可以iframe
                .headers().cacheControl().and().frameOptions().disable().and().requestCache().requestCache(new NullRequestCache()).and()
                .exceptionHandling()
                //授权过期以后重写返回ajax
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

                        System.out.println(authException);
                        AjaxResult error = new AjaxResult<>();
                        error.setState("loginOut");
                        doResult(request, response, error, HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }).and().logout()
                .logoutUrl("/loginout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    AjaxResult success = AjaxResult.success("退出成功");
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(objectMapper.writeValueAsString(success));
                    out.flush();
                    out.close();
                }).and().build();
    }


    private String getRemoteIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 密码明文加密方式配置
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //对默认的UserDetailsService进行覆盖
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new PasswordEncoder() {

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                //23年3月6日加入AES加密  需要先解密
                String userPassword = rawPassword.toString();
//                try {
////                    userPassword = EncryptUtil.aesDecryptForFront(userPassword, EncryptUtil.KEY_DES);
//                } catch (Exception e) {
//                    return false;
//                }
                if (userPassword == null) {
                    return false;
                }
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                boolean match = passwordEncoder.matches(userPassword, encodedPassword);
                return match;
            }

            @Override
            public String encode(CharSequence rawPassword) {
                return (String) rawPassword;
            }
        });
        return authenticationProvider;
    }
}