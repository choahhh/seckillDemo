//package com.example.seckilldemo.config;
//
//
//import com.example.seckilldemo.entity.vo.FormUserDetails;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//@Slf4j
//public class FormUserDetailsArgumentResolver implements HandlerMethodArgumentResolver {
//
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.getParameterType().equals(FormUserDetails.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter,
//                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
//                                  WebDataBinderFactory binderFactory) throws Exception {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            Object o = authentication.getPrincipal();
//            if(o instanceof  FormUserDetails)
//            return (FormUserDetails)o;
//        }
//        return null;
//    }
//
//}
