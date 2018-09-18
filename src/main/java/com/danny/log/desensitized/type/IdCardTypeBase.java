package com.danny.log.desensitized.type;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 16:33
 */
public class IdCardTypeBase extends BaseDesensitizedType {

    @Override
    public String desensitized() {
        return idCard(desensitizedStr);
    }

    /**
     * 【身份证号】显示最后四位，其他隐藏。共计18位或者15位，比如：*************1234
     *
     * @param id
     * @return
     */
    public static String idCard(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        String num = StringUtils.right(id, 4);
        return StringUtils.leftPad(num, StringUtils.length(id), "*");
    }

}