package com.danny.log.desensitized;

import com.alibaba.fastjson.JSON;
import com.danny.log.desensitized.enums.RoleTypeEnum;
import com.danny.log.desensitized.utils.DesensitizedUtils;

import java.util.ArrayList;
import java.util.List;

public class DesensitizedUtilsTest {
    public static void main(String[] args) {

        DesensitizedBean desensitizedBean = new DesensitizedBean();
        desensitizedBean.setRealName("胡丹尼");
        desensitizedBean.setIdCardNo("158199199013141120");
        desensitizedBean.setMobileNo("13579246810");
        desensitizedBean.setPassword("123456");
        desensitizedBean.setBankCardNo("6227000212090659057");
        desensitizedBean.setEmail("hudanni6688@126.com");
        desensitizedBean.setFixedPhone("010-6720174");
        desensitizedBean.setAddress("北京市通州区次渠南里六区");

        List<String> roles = new ArrayList<>();
        roles.add(RoleTypeEnum.CHINESE_NAME_ROLES.name());
        roles.add(RoleTypeEnum.ID_CARD_ROLES.name());

        DesensitizedUtils<DesensitizedBean> desensitizedBeanUtils = new DesensitizedUtils<>();
        DesensitizedBean desensitizedBeanTemp = desensitizedBeanUtils.getDesensitizedObject(desensitizedBean);

        System.out.println("JSON.toJSONString(desensitizedBean) = " + JSON.toJSONString(desensitizedBean));
        System.out.println("JSON.toJSONString(desensitizedBeanTemp) = " + JSON.toJSONString(desensitizedBeanTemp));
    }

}