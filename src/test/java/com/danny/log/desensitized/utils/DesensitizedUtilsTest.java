package com.danny.log.desensitized.utils;

import com.alibaba.fastjson.JSON;
import com.danny.log.desensitized.entity.DesensitizedBean;
import com.danny.log.desensitized.entity.*;
import com.danny.log.desensitized.enums.RoleTypeEnum;
import org.junit.Test;

import java.util.*;

/**
 * @author huyuyang@lxfintech.com
 * @Title: DesensitizedUtilsTest
 * @Copyright: Copyright (c) 2016
 * @Description:
 * @Company: lxjr.com
 * @Created on 2017-07-04 22:40:57
 * 待完善日志脱敏问题 bg项目查询菜单时报错 由org.springframework.validation.support.BindingAwareModelMap引起
 */
public class DesensitizedUtilsTest {

    /**
     * 实体脱敏序列化测试
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    public void testUserInfo() {

        List<String> stringList = new ArrayList<String>();
        stringList.add("danny");
        stringList.add("hoo");
        stringList.add("song");
        Map<String,UserTypeEnum> map=new HashMap<String, UserTypeEnum>();
        map.put("dannymap",UserTypeEnum.ADMINISTRATOR);

        Map<String,UserTypeEnum> map1=new HashMap<String, UserTypeEnum>();
        map.put("dannymap",UserTypeEnum.ADMINISTRATOR);
        Map<String,UserTypeEnum> map2=new HashMap<String, UserTypeEnum>();
        map.put("dannymap",UserTypeEnum.ADMINISTRATOR);
        List<Map> mapList= new ArrayList<>();
        mapList.add(map1);
        mapList.add(map2);

        /*单个实体*/
        BaseUserInfo baseUserInfo = new BaseUserInfo()
                .setRealName("胡丹尼")
                .setIdCardNo("158199199013141120")
                .setMobileNo("13579246810")
                .setAccount("dannyhoo123456")
                .setPassword("123456")
                .setBankCardNo("6227000212090659057")
                .setEmail("hudanni6688@126.com")
                .setUserType(UserTypeEnum.ADMINISTRATOR)
                .setUserService(new UserServiceImpl())
                .setStrList(stringList)
                .setMap(map)
                .setiLimitKey(LimitFrequencyKeyEnum.SMSCODE_MOBILE_DAY_LIMIT)
                .setMapList(mapList);

        /*父类属性*/
        baseUserInfo.setId(101202L)
                .setCreateTime(new Date())
                .setUpdateTime(new Date());

        /*嵌套实体*/
        UserPackage userPackage = new UserPackage()
                .setFlag(true)
                .setBaseUserInfo(baseUserInfo)
                .setUserPackageName("UserPackageName_Danny");


        System.out.println("脱敏前：" + JSON.toJSONString(baseUserInfo));
        System.out.println("脱敏后：" + JSON.toJSONString(new DesensitizedUtils().getDesensitizedObject(baseUserInfo)));
        System.out.println("嵌套实体脱敏后：" + JSON.toJSONString(new DesensitizedUtils().getDesensitizedObject(userPackage)));

    }

    /**
     * 实体脱敏权限测试
     */
    @Test
    public void testRoles(){
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
        DesensitizedBean desensitizedBeanTemp = desensitizedBeanUtils.getDesensitizedObject(desensitizedBean,roles);

        System.out.println("JSON.toJSONString(desensitizedBean) = " + JSON.toJSONString(desensitizedBean));
        System.out.println("JSON.toJSONString(desensitizedBeanTemp) = " + JSON.toJSONString(desensitizedBeanTemp));

    }

}
