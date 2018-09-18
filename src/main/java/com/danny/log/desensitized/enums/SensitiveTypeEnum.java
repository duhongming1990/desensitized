package com.danny.log.desensitized.enums;

import com.danny.log.desensitized.type.*;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 15:29
 */
public enum SensitiveTypeEnum {
    /**
     * 中文名
     */
    CHINESE_NAME(new ChineseNameTypeBase()),
    /**
     * 身份证号
     */
    ID_CARD(new IdCardTypeBase()),
    /**
     * 座机号
     */
    FIXED_PHONE(new FixedPhoneTypeBase()),
    /**
     * 手机号
     */
    MOBILE_PHONE(new MobilePhoneTypeBase()),
    /**
     * 地址
     */
    ADDRESS(new AddressTypeBase()),
    /**
     * 电子邮件
     */
    EMAIL(new EmailTypeBase()),
    /**
     * 银行卡
     */
    BANK_CARD(new BankCardTypeBase()),
    /**
     * 密码
     */
    PASSWORD(new PasswordTypeBase());
//
//
//    /////////////////////////////////////车联网平台数据脱敏/////////////////////////////////////////////////////////
//    TYPE_NAME,//如：姓名。3个字以内隐藏第1个字，4-6个字隐藏前2个字，大于6个字隐藏第3-6个字
//
//    TYPE_ENTERPRISE_NAME,//如：企业名称。长度4个字及以下的，首尾各保留1个字；长度5-6个字的，首尾各保留2个字；长度7个字及以上奇数，隐去中间3个字；长度8个字及以上偶数，隐去中间4个字；
//
//     TYPE_NO ,//如：客户编号||联系电话（手机）。保留前3位和最后3位.
//
//    TYPE_NETWORK_ACCOUNT,//如：网站账户||微信 。分段屏蔽，每隔2位用*替换2位
//
//    TYPE_ELEC_ADDR,//如：用电地址 。取结构化地址“省+市+区县+街道/乡镇+居委会/村+道路+小区+门牌号”中“省+市+区县+门牌号”部分，门牌号保留最后5位，中间用6个*代替
//
//    TYPE_COMMON_ADDR,//如：联系人地址、法人地址。长度5个字及以下的，保留第1个字和最后2个字；长度6-9个字的，保留最后5个字；长度为10个字及以上的，隐去最后5个字之前的4个字；
//
//    TYPE_TEL,//如：只针对固话。区号不隐藏，7-8位电话号码保留最后3位
//
//    TYPE_EMAIL,//如：邮箱。 “@”前小于等于4位的，隐藏第1位；大于4位的，保留前3位
//
//    TYPE_QQ,//如：qq 保留前2位和最后1位
//
//    TYPE_IDCARD,//如：居民身份证号、驾驶证号 ; 保留前6位和最后4位
//
//    TYPE_SOLDIER_CARD,//如：军人证号；保留最后3位
//
//    TYPE_PASSPORT_NO,//如：护照号；保留1位字母和最后3位数字
//
//    TYPE_TAIWAI_IDCARD,//如：台胞证号；保留第5-8位
//
//    TYPE_CAR_NO,//如：车牌号；鲁AN8577->鲁A***77  保留地区编码和流水号最后2位
//
//    TYPE_CAR_FRAME,//如：车架号； 保留最后6位。
//
//    TYPE_BANK_NO,//如：银行卡号||存折账号||增值税税号 ； 保留前4位和最后4位
//
//    TYPE_ADDED_VALUE_TAX;//如：增值税账户  ；保留最后4位

    private BaseDesensitizedType baseDesensitizedType;

    SensitiveTypeEnum(BaseDesensitizedType baseDesensitizedType){
        this.baseDesensitizedType = baseDesensitizedType;
    }

    public BaseDesensitizedType getBaseDesensitizedType() {
        return baseDesensitizedType;
    }

    public void setBaseDesensitizedType(BaseDesensitizedType baseDesensitizedType) {
        this.baseDesensitizedType = baseDesensitizedType;
    }
}
