package cn.tradewar.wx.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.utils.RedisUtil;
import cn.tradewar.dao.model.entity.WstUserEntity;
import cn.tradewar.dao.services.WstUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeChatServiceImplTest{

    @InjectMocks
    private WeChatServiceImpl weChatService;

    @Mock
    private WstUserService wstUserService;

    @Mock
    private WxMaService wxMaService; // 模拟外部服务

    @Mock
    private RedisUtil redisUtil; // Mocking RedisUtil

    @Mock
    private WxMaUserService wxMaUserService; // 模拟外部用户服务

    private static final String VALID_CODE = "validCode";
    private static final String MOCK_OPENID = "mockOpenId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(wxMaService.getUserService()).thenReturn(wxMaUserService); // 设置外部服务的返回
        weChatService.setExpirationTime(3600000L);
    }


    @Test
    void testWxLogin_Success() throws WxErrorException {
        WxMaJscode2SessionResult sessionResult = new WxMaJscode2SessionResult();
        sessionResult.setSessionKey("mockSessionKey");
        sessionResult.setOpenid(MOCK_OPENID);

        // Mocking the external service call to return session info
        when(wxMaUserService.getSessionInfo(VALID_CODE)).thenReturn(sessionResult);

        // Mock behavior when the user is not found
        when(wstUserService.getUserByWxOpenId(MOCK_OPENID)).thenReturn(Optional.empty());

        // Create a new user mock
        WstUserEntity newUser = new WstUserEntity();
        newUser.setId(1L); // Set a valid ID
        newUser.setWeixinOpenid(MOCK_OPENID); // Set openId
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        // Mock the user creation service call
        when(wstUserService.createUser(any(WstUserEntity.class))).thenReturn(Optional.of(newUser));

        // Call the wxLogin method
        GenericResponse response = weChatService.wxLogin(VALID_CODE);

        // Validate the response
        assertEquals(ServiceError.NORMAL.getCode(), response.getStatusCode());
        assertEquals("操作成功", response.getMsg());
        assertTrue(response.isSuccess());
        assertNotNull(response.getContent());
        verify(redisUtil).hset(anyString(), any());
        // Additional assertions if necessary
        // Print details for clarity
        System.out.println("Test completed. Response: " + response);
    }


    @Test
    void testWxLogin_InvalidCode() throws WxErrorException {
        // 模拟外部服务调用返回 null
        when(wxMaUserService.getSessionInfo(VALID_CODE)).thenReturn(null);

        // 调用方法
        GenericResponse response = weChatService.wxLogin(VALID_CODE);

        // 验证结果
        assertEquals(ServiceError.WX_CODE_ERR.getCode(), response.getStatusCode());
        assertEquals("获取微信 openId 出错", response.getMsg());
    }

    @Test
    void testWxLogin_UserNotFound() throws WxErrorException {
        WxMaJscode2SessionResult sessionResult = new WxMaJscode2SessionResult();
        sessionResult.setSessionKey("mockSessionKey");
        sessionResult.setOpenid(MOCK_OPENID);

        // 模拟外部服务调用
        when(wxMaUserService.getSessionInfo(VALID_CODE)).thenReturn(sessionResult);
        when(wstUserService.getUserByWxOpenId(MOCK_OPENID)).thenReturn(Optional.empty());

        // 调用方法
        GenericResponse response = weChatService.wxLogin(VALID_CODE);

        // 验证结果
        assertEquals(ServiceError.UN_KNOW_ERROR.getCode(), response.getStatusCode());
        assertEquals("未知错误", response.getMsg());
    }

    @Test
    void testWxLogin_WxErrorException() throws WxErrorException {
        // 模拟抛出异常
        when(wxMaUserService.getSessionInfo(VALID_CODE)).thenThrow(new WxErrorException("error"));

        // 调用方法
        GenericResponse response = weChatService.wxLogin(VALID_CODE);

        // 验证结果
        assertEquals(ServiceError.UN_KNOW_ERROR.getCode(), response.getStatusCode());
        assertEquals("未知错误", response.getMsg());
    }
}