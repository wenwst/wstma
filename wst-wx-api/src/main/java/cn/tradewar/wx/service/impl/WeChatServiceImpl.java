package cn.tradewar.wx.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.consts.LogConst;
import cn.tradewar.core.utils.IpUtil;
import cn.tradewar.dao.auth.utils.JwtTokenUtil;
import cn.tradewar.core.utils.RedisUtil;
import cn.tradewar.dao.model.bo.UserInfoBo;
import cn.tradewar.dao.model.bo.UserTokenBo;
import cn.tradewar.dao.model.bo.WxBindMobileBo;
import cn.tradewar.dao.model.bo.WxLoginBo;
import cn.tradewar.dao.model.entity.WstUserEntity;
import cn.tradewar.dao.services.WstUserService;
import cn.tradewar.wx.service.WeChatService;
import cn.tradewar.dao.auth.utils.UserUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.crypto.PKCS7Encoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import cn.binarywang.wx.miniapp.api.WxMaService;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 微信服务实现
 * @author wenwst@163.com
 * @date 20240919
 */
@Log4j2
@Service
public class WeChatServiceImpl implements WeChatService{

    @Setter
    @Value("${token.expirationMilliSeconds}")
    private Long expirationTime;

    private final RedisUtil redisUtil;
    private final WstUserService wstUserService;
    private final WxMaService wxMaService;

    public WeChatServiceImpl(WstUserService wstUserService,
                             WxMaService wxMaService,
                             RedisUtil redisUtil) {
        this.wstUserService = wstUserService;
        this.wxMaService = wxMaService;
        this.redisUtil = redisUtil;
    }


    /**
     * 小程序登录
     * @param code 小程序code
     * @return GenericResponse
     */
    @Override
    public GenericResponse wxLogin(String code) {

        try {
            WxMaJscode2SessionResult sessionInfo = this.wxMaService.getUserService().getSessionInfo(code);
            if (sessionInfo == null || sessionInfo.getSessionKey() == null || sessionInfo.getOpenid() == null) {
                log.error(LogConst.WX_END_ERR, "wxLogin", code);
                return GenericResponse.response(ServiceError.WX_CODE_ERR);
            }

//            String sessionKey = sessionInfo.getSessionKey();
            String openId = sessionInfo.getOpenid();


            Optional<WstUserEntity> userOptional = wstUserService.getUserByWxOpenId(openId);
            WstUserEntity user = userOptional.orElseGet(() -> createNewUser(openId));

            if (user == null) {
                return GenericResponse.response(ServiceError.UN_KNOW_ERROR);
            }

            Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
            return generateTokenAndRespond(user, expirationDate);
        } catch (WxErrorException e) {
            log.error(LogConst.WX_END_ERR, "wxLogin", code, e);
            return GenericResponse.response(ServiceError.UN_KNOW_ERROR);
        }
    }

    private WstUserEntity createNewUser(String openId) {
        WstUserEntity newUser = new WstUserEntity();
        newUser.setWeixinOpenid(openId);
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        return wstUserService.createUser(newUser).orElse(null);
    }



    private GenericResponse generateTokenAndRespond(WstUserEntity user, Date expirationDate) {
        grantUserPermission(user);
        String token = JwtTokenUtil.generateToken(user, expirationDate);
        cacheUserToken(user, token);
        return GenericResponse.response(ServiceError.NORMAL, token);
    }


    /**
     * 注销登录
     *
     * @param userId userId
     * @return GenericResponse
     */
    @Override
    public GenericResponse wxLogout(Long userId) {
        log.info("【请求开始】注销登录,请求参数，userId:{}", userId);
        if (userId == null) {
            return GenericResponse.unLogin();
        }
        try {
            redisUtil.deleteKey(userId.toString());
            return GenericResponse.response(ServiceError.NORMAL);
        } catch (Exception e) {
            log.error("注销登录出错：userId:{}", userId);
            return GenericResponse.response(ServiceError.UN_KNOW_ERROR);
        }
    }

    /**
     * wxBindMobile 微信绑定手机号码。
     *
     * @param wxBindMobileBo WxBindMobileBo
     */
    @Override
    public GenericResponse wxBindMobile(WxBindMobileBo wxBindMobileBo) {
        Long userId = UserUtils.getUserId();
        wxBindMobileBo.setUserId(userId);
        return wstUserService.updateUserMobile(wxBindMobileBo);

    }


    /**
     * 微信通过手机号码登陆
     * 当用户不存在时，则注册用户
     * @param wxLoginBo WxLoginBo
     * @param request HttpServletRequest
     * @return GenericResponse
     */
    @Override
    public GenericResponse wxLoginPhone(WxLoginBo wxLoginBo,
                               HttpServletRequest request) {
        log.info(LogConst.WX_BEGIN, "wxLoginPhone", JSONObject.toJSONString(wxLoginBo));

        final String code = wxLoginBo.getCode();

        String sessionKey;
        String openId;
        try {
            WxMaJscode2SessionResult sessionInfo =
                    this.wxMaService.getUserService().getSessionInfo(code);
            sessionKey = sessionInfo.getSessionKey();
            openId = sessionInfo.getOpenid();
            if (sessionKey == null || openId == null) {
                log.error(LogConst.WX_END_ERR,"wxLogin", code);
                return GenericResponse.response(ServiceError.WX_CODE_ERR);
            }
        } catch (WxErrorException e) {
            return GenericResponse.response(ServiceError.WX_CODE_ERR);
        }

        String encryptedData =  wxLoginBo.getEncryptedData();
        String vi =  wxLoginBo.getIv();

        // 用户没有传电话号码，则需要通过加密数据获取电话号码
        String mobile = getMobileNumber(wxLoginBo, sessionKey, encryptedData, vi);
        if(StringUtils.isBlank(mobile)) {
            if(StringUtils.isBlank(wxLoginBo.getEncryptedData())
                    || StringUtils.isBlank(wxLoginBo.getIv())) {
                return GenericResponse.badArgument();
            }
        }
        if(null == mobile) {
            return GenericResponse.response(ServiceError.WX_MOBILE_ERROR);
        }

        // 通过 openId 获取用户信息
        // 获取用户或创建用户
        WstUserEntity user = getUserOrCreateNew(openId, mobile, request);
        if (user == null) {
            return GenericResponse.response(ServiceError.UN_KNOW_ERROR);
        }


        // 生成token，并缓存到redis中
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        String token = JwtTokenUtil.generateToken(user, expirationDate);
        grantUserPermission(user);
        cacheUserToken(user, token);

        // token
        UserTokenBo userToken = new UserTokenBo();
        userToken.setUserId(user.getId());
        userToken.setToken(token);
        userToken.setSessionKey(sessionKey);

        Map<Object, Object> result = new HashMap<>();
        result.put("token", userToken.getToken());
        result.put("tokenExpire", expirationDate);
        UserInfoBo userInfo = new UserInfoBo();
        userInfo.setUserId(user.getId());
        userInfo.setOpenId(openId);
        userInfo.setPhone(user.getMobile());
        userInfo.setRegisterDate(user.getCreateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        userInfo.setStatus(user.getStatus());
        userInfo.setPhone(user.getMobile());
        userInfo.setAvatarUrl(user.getAvatar());
        userInfo.setNickName(user.getNickname());
        result.put("userInfo", userInfo);
        log.info(LogConst.WX_END_ERR, "wxLoginPhone", JSONObject.toJSONString(result));
        return GenericResponse.response(ServiceError.NORMAL, result );

    }

    private WstUserEntity getUserOrCreateNew(String openId, String mobile, HttpServletRequest request) {
        Optional<WstUserEntity> userOptional = wstUserService.getUserByWxOpenId(openId);

        if (userOptional.isPresent()) {
            WstUserEntity user = userOptional.get();
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.client(request));
            return updateUserMobileIfNeeded(user, mobile);
        } else {
            return createNewUser(openId, mobile, request);
        }
    }

    private WstUserEntity updateUserMobileIfNeeded(WstUserEntity user, String mobile) {
        if (user.getMobile() == null) {
            user.setMobile(mobile);
            user.setUpdateTime(LocalDateTime.now());
            Optional<WstUserEntity> updatedUserOptional = wstUserService.updateUser(user);

            // 更新失败
            return updatedUserOptional.orElse(null);
        }

        // 如果手机号不同，返回错误响应
        if (!user.getMobile().equals(mobile)) {
            // 手机号不一致，返回 null
            return null;
        }
        // 用户已存在且手机号一致
        return user;
    }

    private WstUserEntity createNewUser(String openId, String mobile, HttpServletRequest request) {
        WstUserEntity newUser = new WstUserEntity();
        newUser.setWeixinOpenid(openId);
        newUser.setUsername(openId);
        newUser.setPassword(openId);
        newUser.setAvatar("defaultAvatar.png");
        newUser.setNickname("立度用户");
        newUser.setGender((byte) 1);
        newUser.setMobile(mobile);
        newUser.setUserLevel((byte) 0);
        newUser.setStatus((byte) 0);
        newUser.setLastLoginTime(LocalDateTime.now());
        newUser.setLastLoginIp(IpUtil.client(request));
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        Optional<WstUserEntity> createdUserOptional = wstUserService.createUser(newUser);
        return createdUserOptional.orElse(null); // 创建失败返回 null
    }


    // 提取获取手机号的逻辑到单独方法
    private String getMobileNumber(WxLoginBo wxLoginBo, String sessionKey, String encryptedData, String vi) {
        String mobile = wxLoginBo.getPhone();
        if (StringUtils.isBlank(mobile)) {
            mobile = getUserMobileFromEncryptedData(sessionKey, encryptedData, vi);
        }
        return mobile;
    }


    /**
     * 缓存登陆权限到Redis中
     * @param user WstUserEntity
     * @param token Token
     */
    private void cacheUserToken(WstUserEntity user, String token) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",user.getId().toString());
        hashMap.put("authorities",user.getAuthorities());
        redisUtil.hset(token,hashMap);
    }


    /**
     * 小程序用户授权
     * @param user WstUserEntity
     */
    private void grantUserPermission(WstUserEntity user) {
        Set<SimpleGrantedAuthority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(new SimpleGrantedAuthority("ROLE_USER"));
        user.setAuthorities(authoritiesSet);
    }


    /**
     * 获取用户登陆的电话号码
     * @return mobile/null
     */
    private String getUserMobileFromEncryptedData(String sessionKey, String encryptedData, String iv){
        try {
            String mobile = decrypt(sessionKey, encryptedData, iv);
            log.info(LogConst.WX_BEGIN,"getUserMobileFromEncryptedData", mobile);
            ObjectMapper objMap = new ObjectMapper();
            JsonNode root = objMap.readTree(mobile);
            JsonNode mobileNumber = root.get("phoneNumber");
            return mobileNumber.toString().replaceAll("\"", "");
        } catch (Exception e) {
            log.error("【请求结束】微信登录->通过请求encryptedData获取用户电话->getUserMobileFromEncryptedData->异常: ", e);
            return null;
        }
    }


    public static String decrypt(String sessionKey, String encryptedData, String ivStr) {
        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(org.apache.commons.codec.binary.Base64.decodeBase64(ivStr)));
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, new SecretKeySpec(org.apache.commons.codec.binary.Base64.decodeBase64(sessionKey), "AES"), params);
            return new String(PKCS7Encoder.decode(cipher.doFinal(Base64.decodeBase64(encryptedData))), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败！", e);
        }
    }

}
