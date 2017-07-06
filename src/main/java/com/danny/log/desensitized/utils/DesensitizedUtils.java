package com.danny.log.desensitized.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.danny.log.desensitized.annotation.Desensitized;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author huyuyang@lxfintech.com
 * @Title: DesensitizedUtils
 * @Copyright: Copyright (c) 2016
 * @Description:
 * @Company: lxjr.com
 * @Created on 2017-06-07 15:04:33
 */
public class DesensitizedUtils {

    /**
     * 获取脱敏json串
     *
     * @param javaBean
     * @return
     */
    public static String getJson(Object javaBean) {
        String json = null;
        if (null != javaBean) {
            Class<? extends Object> raw = javaBean.getClass();
            try {
                if (raw.isInterface())
                    return json;
                /* 克隆出一个实体进行字段修改，避免修改原实体 */
                Object clone = ObjectCopyUtil.copy(javaBean);
                Set<Integer> referenceCounter = new HashSet<Integer>();
                DesensitizedUtils.replace(DesensitizedUtils.findAllField(raw), clone, referenceCounter);
                json = JSON.toJSONString(clone, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty);
                referenceCounter.clear();
                referenceCounter = null;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /**
     * 获取类的所有字段
     *
     * @param clazz
     * @return
     */
    private static Field[] findAllField(Class<?> clazz) {
        Field[] fileds = clazz.getDeclaredFields();
        while (null != clazz.getSuperclass() && !Object.class.equals(clazz.getSuperclass())) {
            fileds = (Field[]) ArrayUtils.addAll(fileds, clazz.getSuperclass().getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fileds;
    }

    private static void replace(Field[] fields, Object javaBean, Set<Integer> referenceCounter) throws IllegalArgumentException, IllegalAccessException {
        if (null != fields && fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                if (null != field && null != javaBean) {
                    Object value = field.get(javaBean);
                    if (null != value) {
                        Class<?> type = value.getClass();
                        //处理子属性，包括集合中的
                        if (type.isArray()) {//对数组类型的字段进行递归过滤
                            int len = Array.getLength(value);
                            for (int i = 0; i < len; i++) {
                                Object arrayObject = Array.get(value, i);
                                DesensitizedUtils.replace(DesensitizedUtils.findAllField(arrayObject.getClass()), arrayObject, referenceCounter);
                            }
                        } else if (value instanceof Collection<?>) {//对集合类型的字段进行递归过滤
                            Collection<?> c = (Collection<?>) value;
                            Iterator<?> it = c.iterator();
                            while (it.hasNext()) {
                                Object collectionObj = it.next();
                                DesensitizedUtils.replace(DesensitizedUtils.findAllField(collectionObj.getClass()), collectionObj, referenceCounter);
                            }
                        } else if (value instanceof Map<?, ?>) {//对Map类型的字段进行递归过滤
                            Map<?, ?> m = (Map<?, ?>) value;
                            Set<?> set = m.entrySet();
                            for (Object o : set) {
                                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                                Object mapVal = entry.getValue();
                                DesensitizedUtils.replace(DesensitizedUtils.findAllField(mapVal.getClass()), mapVal, referenceCounter);
                            }
                        } else if (value instanceof Enum<?>) {
                            continue;
                        }
                        /*除基础类型、jdk类型的字段之外，对其他类型的字段进行递归过滤*/
                        else if (!type.isPrimitive()
                                && !StringUtils.startsWith(type.getPackage().getName(), "javax.")
                                && !StringUtils.startsWith(type.getPackage().getName(), "java.")
                                && !StringUtils.startsWith(field.getType().getName(), "javax.")
                                && !StringUtils.startsWith(field.getName(), "java.")
                                && referenceCounter.add(value.hashCode())) {
                            DesensitizedUtils.replace(DesensitizedUtils.findAllField(type), value, referenceCounter);
                        }
                    }
                    //处理自身的属性
                    Desensitized annotation = field.getAnnotation(Desensitized.class);
                    if (field.getType().equals(String.class) && null != annotation && executeIsEffictiveMethod(javaBean, annotation)) {
                        String valueStr = (String) value;
                        if (StringUtils.isNotBlank(valueStr)) {
                            switch (annotation.type()) {
                                case CHINESE_NAME: {
                                    field.set(javaBean, DesensitizedUtils.chineseName(valueStr));
                                    break;
                                }
                                case ID_CARD: {
                                    field.set(javaBean, DesensitizedUtils.idCardNum(valueStr));
                                    break;
                                }
                                case FIXED_PHONE: {
                                    field.set(javaBean, DesensitizedUtils.fixedPhone(valueStr));
                                    break;
                                }
                                case MOBILE_PHONE: {
                                    field.set(javaBean, DesensitizedUtils.mobilePhone(valueStr));
                                    break;
                                }
                                case ADDRESS: {
                                    field.set(javaBean, DesensitizedUtils.address(valueStr, 8));
                                    break;
                                }
                                case EMAIL: {
                                    field.set(javaBean, DesensitizedUtils.email(valueStr));
                                    break;
                                }
                                case BANK_CARD: {
                                    field.set(javaBean, DesensitizedUtils.bankCard(valueStr));
                                    break;
                                }
                                case PASSWORD: {
                                    field.set(javaBean, DesensitizedUtils.password(valueStr));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 执行某个对象中指定的方法
     *
     * @param javaBean     对象
     * @param desensitized
     * @return
     */
    private static boolean executeIsEffictiveMethod(Object javaBean, Desensitized desensitized) {
        boolean isAnnotationEffictive = true;//注解默认生效
        if (desensitized != null) {
            String isEffictiveMethod = desensitized.isEffictiveMethod();
            if (isNotEmpty(isEffictiveMethod)) {
                try {
                    Method method = javaBean.getClass().getMethod(isEffictiveMethod);
                    method.setAccessible(true);
                    isAnnotationEffictive = (Boolean) method.invoke(javaBean);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return isAnnotationEffictive;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    public static boolean isEmpty(String str) {
        return !isNotEmpty(str);
    }

    /**
     * 【中文姓名】只显示第一个汉字，其他隐藏为2个星号，比如：李**
     *
     * @param fullName
     * @return
     */
    public static String chineseName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * 【身份证号】显示最后四位，其他隐藏。共计18位或者15位，比如：*************1234
     *
     * @param id
     * @return
     */
    public static String idCardNum(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        String num = StringUtils.right(id, 4);
        return StringUtils.leftPad(num, StringUtils.length(id), "*");
    }

    /**
     * 【固定电话 后四位，其他隐藏，比如1234
     *
     * @param num
     * @return
     */
    public static String fixedPhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num
     * @return
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address
     * @param sensitiveSize 敏感信息长度
     * @return
     */
    public static String address(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * 【电子邮箱 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com>
     *
     * @param email
     * @return
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1)
            return email;
        else
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     *
     * @param password
     * @return
     */
    public static String password(String password) {
        if (StringUtils.isBlank(password)) {
            return "";
        }
        String pwd = StringUtils.left(password, 0);
        return StringUtils.rightPad(pwd, StringUtils.length(password), "*");
    }

}