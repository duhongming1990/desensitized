package com.danny.log.desensitized;

import com.danny.log.desensitized.annotation.Desensitized;
import com.danny.log.desensitized.enums.RoleTypeEnum;
import com.danny.log.desensitized.enums.SensitiveTypeEnum;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 15:37
 */
public class DesensitizedBean {

    @Desensitized(type = SensitiveTypeEnum.CHINESE_NAME,role = RoleTypeEnum.CHINESE_NAME_ROLES)
    private String realName;

    @Desensitized(type = SensitiveTypeEnum.ID_CARD,role = RoleTypeEnum.ID_CARD_ROLES)
    private String idCardNo;

    @Desensitized(type = SensitiveTypeEnum.MOBILE_PHONE,role = RoleTypeEnum.MOBILE_PHONE_ROLES)
    private String mobileNo;

    @Desensitized(type = SensitiveTypeEnum.PASSWORD,role = RoleTypeEnum.PASSWORD_ROLES)
    private String password;

    @Desensitized(type = SensitiveTypeEnum.BANK_CARD,role = RoleTypeEnum.BANK_CARD_ROLES)
    private String bankCardNo;

    @Desensitized(type = SensitiveTypeEnum.EMAIL,role = RoleTypeEnum.EMAIL_ROLES)
    private String email;

    @Desensitized(type = SensitiveTypeEnum.ADDRESS,role = RoleTypeEnum.ADDRESS_ROLES)
    private String address;

    @Desensitized(type = SensitiveTypeEnum.FIXED_PHONE,role = RoleTypeEnum.FIXED_PHONE_ROLES)
    private String fixedPhone;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFixedPhone() {
        return fixedPhone;
    }

    public void setFixedPhone(String fixedPhone) {
        this.fixedPhone = fixedPhone;
    }
}