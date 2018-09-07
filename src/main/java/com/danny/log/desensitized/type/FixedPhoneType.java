package com.danny.log.desensitized.type;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 16:55
 */
public class FixedPhoneType extends DesensitizedType {
    @Override
    public String desensitized() {
        return fixedPhone(desensitizedStr);
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
}