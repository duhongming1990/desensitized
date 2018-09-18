package com.danny.log.desensitized.type;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 16:56
 */
public class PasswordTypeBase extends BaseDesensitizedType {
    @Override
    public String desensitized() {
        return password(desensitizedStr);
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