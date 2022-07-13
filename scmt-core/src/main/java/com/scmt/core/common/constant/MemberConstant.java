package com.scmt.core.common.constant;

/**
 * 常量
 * @author Exrickx
 */
public interface MemberConstant {

    /**
     * 会员默认头像
     */
    String MEMBER_DEFAULT_AVATAR = "https://ooo.0o0.ooo/2020/04/18/NmF3IP4TOoVbLf5.png";

    /**
     * 默认角色权限
     */
    String MEMBER_PERMISSION = "MEMBER";

    /**
     * 会员正常状态
     */
    Integer MEMBER_STATUS_NORMAL = 0;

    /**
     * 会员禁用状态
     */
    Integer MEMBER_STATUS_LOCK = -1;

    /**
     * 普通会员
     */
    Integer MEMBER_TYPE_NORMAL = 0;

    /**
     * VIP
     */
    Integer MEMBER_TYPE_VIP = 1;

    /**
     * 普通会员
     */
    Integer MEMBER_VIP_NONE = 0;

    /**
     * VIP
     */
    Integer MEMBER_VIP_VALID = 1;

    /**
     * 未知
     */
    Integer MEMBER_VIP_INVALID = 2;

    /**
     * PC/H5
     */
    Integer MEMBER_PLATFORM_PC = 0;

    /**
     * 安卓
     */
    Integer MEMBER_PLATFORM_ANDROID = 1;

    /**
     * IOS
     */
    Integer MEMBER_PLATFORM_IOS = 2;

    /**
     * 微信小程序
     */
    Integer MEMBER_PLATFORM_WECHAT = 3;

    /**
     * 支付宝小程序
     */
    Integer MEMBER_PLATFORM_ALIPAY = 4;

    /**
     * QQ小程序
     */
    Integer MEMBER_PLATFORM_QQ = 5;

    /**
     * 字节小程序
     */
    Integer MEMBER_PLATFORM_BYTE = 6;

    /**
     * 百度小程序
     */
    Integer MEMBER_PLATFORM_BAIDU = 7;
}
