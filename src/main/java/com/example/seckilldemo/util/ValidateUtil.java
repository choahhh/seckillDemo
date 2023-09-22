package com.example.seckilldemo.util;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JiangPan
 * @version 1.0.0
 * @ClassName ValidateUtil.java
 * @Description 校验工具类
 * @createTime 2020年07月29日 13:46:00
 */
@Slf4j
public class ValidateUtil {
    private static final Validator validator;
    private static final Map<String,Integer> codeMap = new HashMap<>();

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        codeMap.put("A", 10);
        codeMap.put("B", 12);
        codeMap.put("C", 13);
        codeMap.put("D", 14);
        codeMap.put("E", 15);
        codeMap.put("F", 16);
        codeMap.put("G", 17);
        codeMap.put("H", 18);
        codeMap.put("I", 19);
        codeMap.put("J", 20);
        codeMap.put("K", 21);
        codeMap.put("L", 23);
        codeMap.put("M", 24);
        codeMap.put("N", 25);
        codeMap.put("O", 26);
        codeMap.put("P", 27);
        codeMap.put("Q", 28);
        codeMap.put("R", 29);
        codeMap.put("S", 30);
        codeMap.put("T", 31);
        codeMap.put("U", 32);
        codeMap.put("V", 34);
        codeMap.put("W", 35);
        codeMap.put("X", 36);
        codeMap.put("Y", 37);
        codeMap.put("Z", 38);
    }

    /**
     * 功能详细描述: 正则校验工具类
     *
     * @param check 字符串
     * @param field 正则
     * @methodName: checkRegex
     * @return: boolean
     * @Author: JiangPan
     * @date: 2020/7/29 13:47
     */
    public static boolean checkRegex(String check, String field) {
        boolean flag;
        try {
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(field);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验集装箱号是否有效
     * @param containerNo
     * @return
     */
    public static Result checkContainerNo(String containerNo){
        if(containerNo==null || containerNo.length()<=0){
            return new Result(true,null);
        }
        int n = containerNo.length();
        if(n!=11){
            return new Result(false,"集装箱号必须为11位");
        }
        if(!containerNo.substring(0, 4).matches("[A-Z]{4}")){
            return new Result(false,"箱号前4位必须为大写字母");
        }
        int positon = 1;
        int sum = 0;
        for (int i = 0; i < containerNo.length() - 1; i++) {
            if (codeMap.containsKey(containerNo.substring(i, i + 1))) {
                sum += Double.valueOf(codeMap.get(containerNo.substring(i,
                        i + 1))) * Math.pow(2, positon - 1);
            } else {
                sum += Double.parseDouble(containerNo.substring(i, i + 1))
                        * Math.pow(2, positon - 1);
            }
            positon++;
        }
        int checkDigit = sum % 11 % 10;
        if(!containerNo.substring(10, 11).equals(checkDigit+"")){
            return new Result(false,"箱号第11位校验位错误");
        }
        return new Result(true,null);
    }

    public static class Result{
        private final boolean success;
        private final String msg;

        public Result(boolean success,String msg){
            this.success = success;
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 校验字段属性
     * @param obj
     * @return
     */
    public static String validateModelReturnOne(Object obj) {
        // 用于存储验证后的错误信息
        StringBuffer buffer = new StringBuffer(64);
        // 验证某个对象,，其实也可以只验证其中的某一个属性的
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        Iterator<ConstraintViolation<Object>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()) {
            String message = iterator.next().getMessage();
            return message;
        }
        return buffer.toString();
    }
}
