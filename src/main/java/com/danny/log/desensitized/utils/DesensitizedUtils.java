package com.danny.log.desensitized.utils;

import com.danny.log.desensitized.annotation.Desensitized;
import com.danny.log.desensitized.enums.RoleTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 15:10
 */
public class DesensitizedUtils<T> {
    private List<String> roles;
    public T getDesensitizedObject(T javaBean) {
        return this.getDesensitizedObject(javaBean,new ArrayList<>());
    }

    public T getDesensitizedObject(T javaBean,List<String> roles) {
        this.roles = roles;
        try{
            /* 克隆出一个实体进行字段修改，避免修改原实体 */
            //Object clone =ObjectUtils.deepCloneObject(javaBean);
            //Object clone =ObjectUtils.deepCloneByFastJson(javaBean);
            T clone = (T)ObjectUtils.deepClone(javaBean);

            /* 定义一个计数器，用于避免重复循环自定义对象类型的字段 */
            Set<Integer> referenceCounter = new HashSet<Integer>();

            /* 对克隆实体进行脱敏操作 */
            this.replace(ObjectUtils.getAllFields(clone), clone, referenceCounter);

            /* 清空计数器 */
            referenceCounter.clear();
            return clone;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对需要脱敏的字段进行转化
     *
     * @param fields
     * @param javaBean
     * @param referenceCounter
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void replace(Field[] fields, Object javaBean, Set<Integer> referenceCounter) throws IllegalArgumentException, IllegalAccessException {
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
                                if (isNotGeneralType(arrayObject.getClass(), arrayObject, referenceCounter)) {
                                    replace(ObjectUtils.getAllFields(arrayObject), arrayObject, referenceCounter);
                                }
                            }
                        } else if (value instanceof Collection<?>) {//对集合类型的字段进行递归过滤
                            Collection<?> c = (Collection<?>) value;
                            Iterator<?> it = c.iterator();
                            while (it.hasNext()) {// TODO: 待优化
                                Object collectionObj = it.next();
                                if (isNotGeneralType(collectionObj.getClass(), collectionObj, referenceCounter)) {
                                    replace(ObjectUtils.getAllFields(collectionObj), collectionObj, referenceCounter);
                                }
                            }
                        } else if (value instanceof Map<?, ?>) {//对Map类型的字段进行递归过滤
                            Map<?, ?> m = (Map<?, ?>) value;
                            Set<?> set = m.entrySet();
                            for (Object o : set) {
                                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                                Object mapVal = entry.getValue();
                                if (isNotGeneralType(mapVal.getClass(), mapVal, referenceCounter)) {
                                    replace(ObjectUtils.getAllFields(mapVal), mapVal, referenceCounter);
                                }
                            }
                        } else if (value instanceof Enum<?>) {
                            continue;
                        }

                        /*除基础类型、jdk类型的字段之外，对其他类型的字段进行递归过滤*/
                        else {
                            if (!type.isPrimitive()
                                    && type.getPackage() != null
                                    && !StringUtils.startsWith(type.getPackage().getName(), "javax.")
                                    && !StringUtils.startsWith(type.getPackage().getName(), "java.")
                                    && !StringUtils.startsWith(field.getType().getName(), "javax.")
                                    && !StringUtils.startsWith(field.getName(), "java.")
                                    && referenceCounter.add(value.hashCode())) {
                                replace(ObjectUtils.getAllFields(value), value, referenceCounter);
                            }
                        }
                    }

                    //脱敏操作
                    setNewValueForField(javaBean, field, value);

                }
            }
        }
    }

    /**
     * 排除基础类型、jdk类型、枚举类型的字段
     *
     * @param clazz
     * @param value
     * @param referenceCounter
     * @return
     */
    private boolean isNotGeneralType(Class<?> clazz, Object value, Set<Integer> referenceCounter) {
        return !clazz.isPrimitive()
                && clazz.getPackage() != null
                && !clazz.isEnum()
                && !StringUtils.startsWith(clazz.getPackage().getName(), "javax.")
                && !StringUtils.startsWith(clazz.getPackage().getName(), "java.")
                && !StringUtils.startsWith(clazz.getName(), "javax.")
                && !StringUtils.startsWith(clazz.getName(), "java.")
                && referenceCounter.add(value.hashCode());
    }

    /**
     * 脱敏操作（按照规则转化需要脱敏的字段并设置新值）
     * 目前只支持String类型的字段，如需要其他类型如BigDecimal、Date等类型，可以添加
     *
     * @param javaBean
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    public void setNewValueForField(Object javaBean, Field field, Object value) throws IllegalAccessException {
        //处理自身的属性
        Desensitized annotation = field.getAnnotation(Desensitized.class);
        if (field.getType().equals(String.class) && null != annotation
            //&& executeIsEffictiveMethod(javaBean, annotation)
                ) {
            String valueStr = (String) value;
            if (StringUtils.isNotBlank(valueStr)) {

                boolean isDesensitized = false;
                //角色判断以及动态分配脱敏规则
                if (CollectionUtils.isNotEmpty(this.roles)){
                    for(RoleTypeEnum roleTypeEnum : annotation.role()){
                        if(this.roles.contains(roleTypeEnum.toString())){
                            isDesensitized = true;
                            break;
                        }
                    }
                }else{
                    isDesensitized = true;
                }
                if(isDesensitized){
                    field.set(javaBean, annotation.type().getDesensitizedType().setDesensitizedStr(valueStr).desensitized());
                }
            }
        }
    }


    /////////////////////////////////////车联网平台数据脱敏/////////////////////////////////////////////////////////

    /**
     *
     * @param data--1
     * @return 姓名
     */
    private static  String getName(String data){
        int length = data.length();
        if(length>0&&length<=3){//3个字以内隐藏第1个字
            data = data.replace(data.charAt(0), '*');
        }else if(length>=4&&length<=6){//4-6个字隐藏前2个字
            data = data.replace(data.charAt(0), '*').replace(data.charAt(1), '*');
        }else {//大于6个字隐藏第3-6个字
            data = data.substring(0, 2)+getReplaceCharStr(4)+data.substring(5);
        }
        return data;
    }
    /**
     *
     * @param data--2
     * @return 返回企业名称 ；
     */
    private static String getEnterpriseName(String data){

        int length = data.length();
        if(length<=2){
            return getDefault(data);//规则之外走默认
        }else if(length>2&&length<=4){//长度4个字及以下的，首尾各保留1个字
            data = data.substring(0, 1)+getReplaceCharStr(length-2)+data.substring(length-1);
        }else if(length>=5&&length<=6){//长度5-6个字的，首尾各保留2个字；
            data = data.substring(0, 2)+getReplaceCharStr(length-4)+data.substring(length-2);
        }else if(length>=7&&length%2!=0) {//长度7个字及以上奇数，隐去中间3个字；
            int middelIndex=(length+1)/2-1;
            data = data.replace(data.charAt(middelIndex-1), '*').replace(data.charAt(middelIndex), '*').replace(data.charAt(middelIndex+1), '*');
        }else if(length>=8&&length%2==0){//长度8个字及以上偶数，隐去中间4个字；
            int startIndex=length/2-2;
            int endIndex = length/2+2;
            data = data.substring(0,startIndex)+getReplaceCharStr(length-4)+data.substring(endIndex);
        }
        return data;
    }
    /**
     *
     * @param data--3
     * @return 保留前3位和最后3位
     */
    private static String getBothSides3(String data){
        int length = data.length();
        if(length>6){
            data = data.substring(0,3)+getReplaceCharStr(length-6)+data.substring(length-3);
            return data;
        }

        return getDefault(data);
    }

    /**
     *
     * @param data--4
     * @return  网站账户||微信 。分段屏蔽，每隔2位用*替换2位
     */
    private static String getNetworkAccount(String data){
        String reg2 = "";
        String res = "";
        int group = (data.length()+1)/2;
        for(int i=1;i<=(data.length()+1)/2;i++){

            if(i%2==1){
                res += "$" + i;
            }else{
                if(i<group){
                    res +="**";
                }else{
                    if(data.length()%2==0){
                        res +="**";
                    }else{
                        res +="*";
                    }
                }
            }
            if(i<group){
                reg2 +="(\\d{2})";
            }else{
                if(data.length()%2==0){
                    reg2 +="(\\d{2})";
                }else{
                    reg2 +="(\\d{1})";
                }
            }
        }

        data = data.replaceAll(reg2, res);
        return data;
    }

    /**
     *
     * @param data--5
     * @return TODO:针对具体地址来设定规则，这里走默认
     */
    private static String getElecAddr(String data){

        return getDefault(data);
    }

    /**
     *
     * @param data--6
     * @return 联系人地址、法人地址
     */
    private static String getCommonAddr(String data){
        int length = data.length();
        if(length<=3){
            return getDefault(data);//规则之外走默认
        }else if(length>3&&length<=5){//长度5个字及以下的，保留第1个字和最后2个字
            data = data.substring(0, 1)+getReplaceCharStr(length-3)+data.substring(length-2);
        }else if (length>=6&&length<=9){//长度6-9个字的，保留最后5个字；
            data = getReplaceCharStr(length-5)+data.substring(length-5);
        }else {//长度为10个字及以上的，隐去最后5个字之前的4个字；
            data = data.substring(0, length-9)+"****"+data.substring(length-5);
        }
        return data;
    }

    /**
     *
     * @param data--7
     * @return 固话
     */
    private static String getTel(String data){
        int length = data.length();
        if(length>3){//区号不隐藏，7-8位电话号码保留最后3位
            data = getReplaceCharStr(length-3)+data.substring(length-3);
            return data;
        }else {
            return getDefault(data);//规则之外走默认
        }
    }

    /**
     *
     * @param data--8
     * @return 邮箱
     */
    private static String getEmail(String data){
        int length = data.length();
        //“@”前小于等于4位的，隐藏第1位；大于4位的，保留前3位
        if(length>0){
            int index = data.indexOf('@');
            if(index>0){
                String str= data.substring(0,index);
                if(str.length()<=4){
                    data = "*"+data.substring(1);
                }else{
                    data = "***"+data.substring(3);
                }
            }

        }
        return data;
    }
    /**
     *
     * @param data--9
     * @return QQ
     */
    private static String getQQ(String data){
        int length = data.length();
        //保留前2位和最后1位
        if(length<4){
            return getDefault(data);//规则之外走默认
        }else{
            data = data.substring(0, 2)+getReplaceCharStr(length-3)+data.substring(length-1);
            return data;
        }

    }

    /**
     *
     * @param data--10
     * @return 居民身份证号、驾驶证号 ;
     */
    private static String getIdCard(String data){
        int length = data.length();
        if(length<11){
            return getDefault(data);//规则之外走默认
        }else{//保留前6位和最后4位
            data = data.substring(0,6)+getReplaceCharStr(length-10)+data.substring(length-4);
            return data;
        }
    }

    /**
     *
     * @param data--11
     * @return 护照号；保留1位字母和最后3位数字
     */
    private static String getSoldierCard(String data){
        int length = data.length();
        if(length<4){
            return getDefault(data);//规则之外走默认
        }else{//保留最后3位
            data = getReplaceCharStr(length-3)+data.substring(length-3);
            return data;
        }
    }

    /**
     *
     * @param data--12
     * @return 护照号；
     */
    private static String getPassportNo(String data){
        int length = data.length();
        if(length<5){
            return getDefault(data);//规则之外走默认
        }else{//保留1位字母和最后3位数字
            data = data.substring(0,1)+getReplaceCharStr(length-4)+data.substring(length-3);
            return data;
        }
    }
    /**
     *
     * @param data--13
     * @return 台胞证号；
     */
    private static String getTaiWaiIdCard(String data){
        int length = data.length();
        if(length<8){
            return getDefault(data);//规则之外走默认
        }else{//保留第5-8位
            data = getReplaceCharStr(4)+data.substring(4,8)+getReplaceCharStr(length-8);
            return data;
        }
    }
    /**
     *
     * @param data--14
     * @return 车牌号；鲁AN8577->鲁A***77
     */
    private static String getCarNo(String data){
        int length = data.length();
        if(length<5){
            return getDefault(data);//规则之外走默认
        }else{//保留地区编码和流水号最后2位
            data =data.substring(0,2)+ getReplaceCharStr(length-4)+data.substring(length-2);
            return data;
        }
    }
    /**
     *
     * @param data--15
     * @return  车架号；
     */
    private static String getCarFrame(String data){
        int length = data.length();
        if(length<7){
            return getDefault(data);//规则之外走默认
        }else{//保留最后6位。
            data =getReplaceCharStr(length-6)+data.substring(length-6);
            return data;
        }
    }

    /**
     *
     * @param data--16
     * @return  银行卡号||存折账号||增值税税号 ；
     */
    private static String getBankNo(String data){
        int length = data.length();
        if(length<15){
            return getDefault(data);//规则之外走默认
        }else{//保留前4位和最后4位。
            data =data.substring(0,4)+getReplaceCharStr(length-8)+data.substring(length-4);
            return data;
        }
    }
    /**
     *
     * @param data--17
     * @return  增值税账户  ；
     */
    private static String getAddedValueTax(String data){
        int length = data.length();
        if(length<8){
            return getDefault(data);//规则之外走默认
        }else{//保留最后4位
            data =getReplaceCharStr(length-4)+data.substring(length-4);
            return data;
        }
    }

    /**
     *
     * @param length 生成固定长度的*
     * @return
     */
    private static String getReplaceCharStr(int length){
        String str = "";
        for(int i= 1;i<=length;i++){
            str+="*";
        }
        return  str;
    }

    /**
     *
     * @param data
     * @return 都不满足情况下，获取默认加密方式
     */
    private static String getDefault(String data){
        return getName(data);
    }
}