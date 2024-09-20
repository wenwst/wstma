package cn.tradewar.wx.service;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.dao.model.bo.WxBindMobileBo;
import cn.tradewar.dao.model.bo.WxLoginBo;
import jakarta.servlet.http.HttpServletRequest;


public interface WeChatService{

    /**
     * 小程序登录
     * @param code 小程序code
     * @return GenericResponse
     */
    GenericResponse wxLogin(String code);

    /**
     * 微信通过手机号码登陆
     * 当用户不存在时，则注册用户
     * 当用户的openId同绑定的手机号码不同时，则提示更换手机号码登陆
     * @param wxLoginBo WxLoginBo
     * @param request HttpServletRequest
     * @return GenericResponse
     */
    GenericResponse wxLoginPhone(WxLoginBo wxLoginBo,
                               HttpServletRequest request);

    /**
     * 注销登录
     *
     * @param userId userId
     * @return GenericResponse
     */
    GenericResponse wxLogout(Long userId);


    /**
     * wxBindMobile 微信绑定手机号码。
     * @param wxBindMobileBo WxBindMobileBo
     */
    GenericResponse wxBindMobile(WxBindMobileBo wxBindMobileBo);

}
