package com.danny.log.desensitized.type;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 16:21
 */
public class ChineseNameTypeBase extends BaseDesensitizedType {

    @Override
    public String desensitized() {
        return chineseName(desensitizedStr);
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
}