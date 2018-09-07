package com.danny.log.desensitized.type;


import org.apache.commons.lang3.StringUtils;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 16:19
 */
public abstract class DesensitizedType {

    public String desensitizedStr;

    public String desensitized(){
        return StringUtils.EMPTY;
    }

    public DesensitizedType setDesensitizedStr(String desensitizedStr) {
        this.desensitizedStr = desensitizedStr;
        return this;
    }
}