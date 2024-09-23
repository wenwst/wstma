package cn.tradewar.wx.controller;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.common.TmsAnnotation;
import cn.tradewar.dao.model.bo.UserInfoBo;
import cn.tradewar.dao.model.bo.WxLoginBo;
import cn.tradewar.wx.service.WeChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TmsAnnotation("S100")
@SpringBootTest
class WxAuthControllerTest{

    @MockBean
    private WeChatService weChatService; // 模拟服务层

    @Autowired
    private WxAuthController wxAuthController; // 注入控制器

    private MockMvc mockMvc; // 注入 MockMvc 实例

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(wxAuthController).build(); // 手动创建 MockMvc
    }


    @Test
    @TmsAnnotation("C101")
    void testWxLogin_Success() throws Exception {
        String mockToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODM3MDU4MDQ5MDIzODU2NjQyIiwiZXhwIjoxNzI2ODMwOTQ0LCJpYXQiOjE3MjY4MjM3NDQsImlzcyI6IkpBTUVTIn0.CihFYMnwj_na4k3qLOz9DbA6zKaC2fpTSBhZSHyXmQ0guN18fTgzdk8UZF_1CZ6lQ0nl_m-pNhV4YzNaJlMtLg";
        GenericResponse mockResult = GenericResponse.response(ServiceError.NORMAL, mockToken);

        // 模拟服务层行为
        when(weChatService.wxLogin("validCode")).thenReturn(GenericResponse.response(ServiceError.NORMAL, mockResult));

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLogin")
                        .param("code", "validCode") // 传递参数 code
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // 表单提交
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(true)) // 验证 JSON 字段
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty())
                .andExpect(jsonPath("$.statusCode").value(1));
    }


    @Test
    @TmsAnnotation("C102")
    void testWxLogin_EmptyCode() throws Exception {
        // 发起POST请求，传递空的 code
        mockMvc.perform(post("/wx/auth/wxLogin")
                        .param("code", "") // 传递空的 code
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(false)) // 验证失败
                .andExpect(jsonPath("$.msg").value("参数错误")) // 验证错误消息
                .andExpect(jsonPath("$.statusCode").value(ServiceError.ARG_ERROR.getCode())); // 验证状态码
    }

    @Test
    @TmsAnnotation("C103")
    void testWxLogin_Exception() throws Exception {
        // 模拟服务层行为，抛出异常
        when(weChatService.wxLogin("validCode")).thenThrow(new RuntimeException("Service error"));

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLogin")
                        .param("code", "validCode") // 传递参数 code
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(false)) // 验证失败
                .andExpect(jsonPath("$.msg").value("未知错误")) // 验证错误消息
                .andExpect(jsonPath("$.statusCode").value(ServiceError.UN_KNOW_ERROR.getCode())); // 验证状态码
    }


    @Test
    @TmsAnnotation("C104")
    void testWxLoginPhone_Phone_Success() throws Exception {
        WxLoginBo wxLoginBo = WxLoginBo.builder()
                .code("JzdWIiOiIxODM3MDU4MDQ")
                .phone("189999999999")
                .build();


        Map<Object, Object> result = new HashMap<>();
        String mockToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODM3MDU4MDQ5MDIzODU2NjQyIiwiZXhwIjoxNzI2ODMwOTQ0LCJpYXQiOjE3MjY4MjM3NDQsImlzcyI6IkpBTUVTIn0.CihFYMnwj_na4k3qLOz9DbA6zKaC2fpTSBhZSHyXmQ0guN18fTgzdk8UZF_1CZ6lQ0nl_m-pNhV4YzNaJlMtLg";
        result.put("token", mockToken);
        result.put("tokenExpire", new Date());
        UserInfoBo userInfo = new UserInfoBo();
        userInfo.setUserId(1L);
        userInfo.setOpenId("openId");
        userInfo.setPhone("189999999999");
        userInfo.setRegisterDate("2024-09-22");
        userInfo.setStatus((byte)1);
        userInfo.setAvatarUrl("user.getAvatar()");
        userInfo.setNickName("user.getNickname()");
        result.put("userInfo", userInfo);
        GenericResponse mockResult = GenericResponse.response(ServiceError.NORMAL, result);

        // 模拟服务层行为
        when(weChatService.wxLoginPhone(eq(wxLoginBo), any(HttpServletRequest.class)))
                .thenReturn(mockResult);

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLoginPhone")
                        .content(objectMapper.writeValueAsString(wxLoginBo))
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andDo(print())
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(true)) // 验证 JSON 字段
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty())
                .andExpect(jsonPath("$.statusCode").value(1));
    }

    @Test
    @TmsAnnotation("C105")
    void testWxLoginPhone_ArgPhone() throws Exception {
        WxLoginBo wxLoginBo = WxLoginBo.builder()
                .code("JzdWIiOiIxODM3MDU4MDQ")
                .phone("189999999999")
                .iv("JzdWIiOiIxODM3MDU4MDQ")
                .encryptedData("JzdWIiOiIxODM3MDU4MDQ")
                .build();


        Map<Object, Object> result = new HashMap<>();
        String mockToken = "eyJhbGciOiJIUzUxMiJ9Q0nl_m-pNhV4YzNaJlMtLg";
        result.put("token", mockToken);
        result.put("tokenExpire", new Date());
        UserInfoBo userInfo = new UserInfoBo();
        userInfo.setUserId(1L);
        userInfo.setOpenId("openId");
        userInfo.setPhone("189999999999");
        userInfo.setRegisterDate("2024-09-22");
        userInfo.setStatus((byte)1);
        userInfo.setAvatarUrl("user.getAvatar()");
        userInfo.setNickName("user.getNickname()");
        result.put("userInfo", userInfo);
        GenericResponse mockResult = GenericResponse.response(ServiceError.NORMAL, result);

        // 模拟服务层行为
        when(weChatService.wxLoginPhone(eq(wxLoginBo), any(HttpServletRequest.class)))
                .thenReturn(mockResult);

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLoginPhone")
                        .content(objectMapper.writeValueAsString(wxLoginBo))
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andDo(print())
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(true)) // 验证 JSON 字段
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty())
                .andExpect(jsonPath("$.statusCode").value(1));
    }

    @Test
    @TmsAnnotation("C106")
    void testWxLoginPhone_ArgIv() throws Exception {
        WxLoginBo wxLoginBo = WxLoginBo.builder()
                .code("JzdWIiOiIxODM3MDU4MDQ")
                .iv("JzdWIiOiIxODM3MDU4MDQ")
                .encryptedData("JzdWIiOiIxODM3MDU4MDQ")
                .build();

        Map<Object, Object> result = new HashMap<>();
        String mockToken = "eyJhbGciOiJIUzUxMiJ9Q0nl_m-pNhV4YzNaJlMtLg";
        result.put("token", mockToken);
        result.put("tokenExpire", new Date());
        UserInfoBo userInfo = new UserInfoBo();
        userInfo.setUserId(1L);
        userInfo.setOpenId("openId");
        userInfo.setPhone("189999999999");
        userInfo.setRegisterDate("2024-09-22");
        userInfo.setStatus((byte)1);
        userInfo.setAvatarUrl("user.getAvatar()");
        userInfo.setNickName("user.getNickname()");
        result.put("userInfo", userInfo);
        GenericResponse mockResult = GenericResponse.response(ServiceError.NORMAL, result);

        // 模拟服务层行为
        when(weChatService.wxLoginPhone(eq(wxLoginBo), any(HttpServletRequest.class)))
                .thenReturn(mockResult);

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLoginPhone")
                        .content(objectMapper.writeValueAsString(wxLoginBo))
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andDo(print())
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(true)) // 验证 JSON 字段
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.msg").isNotEmpty())
                .andExpect(jsonPath("$.statusCode").value(1));
    }

    @Test
    @TmsAnnotation("C106")
    void testWxLoginPhone_OnlyIv_BadArg() throws Exception {
        String mockToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxODM3MDU4MDQ5MDIzODU2NjQyIiwiZXhwIjoxNzI2ODMwOTQ0LCJpYXQiOjE3MjY4MjM3NDQsImlzcyI6IkpBTUVTIn0.CihFYMnwj_na4k3qLOz9DbA6zKaC2fpTSBhZSHyXmQ0guN18fTgzdk8UZF_1CZ6lQ0nl_m-pNhV4YzNaJlMtLg";
        GenericResponse mockResult = GenericResponse.response(ServiceError.NORMAL, mockToken);
        MockHttpServletRequest request = new MockHttpServletRequest();

        WxLoginBo wxLoginBo = WxLoginBo.builder()
                .code("JzdWIiOiIxODM3MDU4MDQ")
                .encryptedData("iIxODM3MDU4MDQ5")
                .build();

        // 模拟服务层行为
        when(weChatService.wxLoginPhone(wxLoginBo, request))
                .thenReturn(GenericResponse.response(ServiceError.NORMAL, mockResult));

        // 发起POST请求并验证返回结果
        mockMvc.perform(post("/wx/auth/wxLoginPhone")
                        .content(objectMapper.writeValueAsString(wxLoginBo))
                        .contentType(MediaType.APPLICATION_JSON)) // 表单提交
                .andExpect(status().isOk()) // 验证状态码 200
                .andExpect(jsonPath("$.success").value(false)) // 验证 JSON 字段
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.msg").value(ServiceError.ARG_ERROR.getMsg()))
                .andExpect(jsonPath("$.statusCode").value(ServiceError.ARG_ERROR.getCode()));
    }

    @Test
    @TmsAnnotation("C107")
    void testWxLoginPhone_BadCode() throws Exception {
        // Step 1: Create a `WxLoginBo` object with an empty `code` field and valid `encryptedData` and `iv` fields.
        WxLoginBo wxLoginBo = WxLoginBo.builder()
                .code("")
                .encryptedData("iIxODM3MDU4MDQ5")
                .iv("iIxODM3MDU4MDQ5")
                .build();
        // Step 2: Send a POST request to `/wx/auth/wxLoginPhone` with the `WxLoginBo` object.
        mockMvc.perform(post("/wx/auth/wxLoginPhone")
                        .content(objectMapper.writeValueAsString(wxLoginBo))
                        .contentType(MediaType.APPLICATION_JSON))
                // Step 3: Assert that the response status is a client error (4xx) indicating the missing `code`.
                .andExpect(status().is4xxClientError());
    }


    @Test
    void testWxLogout_Success() throws Exception {
        Long userId = 1L;
        GenericResponse mockResponse = GenericResponse.response(ServiceError.NORMAL);

        // Mock the service response
        when(weChatService.wxLogout(userId)).thenReturn(mockResponse);

        // Perform the logout request
        mockMvc.perform(post("/wx/auth/wxLogout")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()) // Assert status 200
                .andExpect(jsonPath("$.success").value(true)); // Assert success response
    }

    @Test
    void testWxLogout_NullUserId() throws Exception {
        // Perform the logout request without userId
        mockMvc.perform(post("/wx/auth/wxLogout")
                        .param("userId", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk()) // Assert client error
                .andExpect(jsonPath("$.success").value(false)) // Assert failure response
                .andExpect(jsonPath("$.msg").value("参数错误")); // Assert error message
    }

    @Test
    void testWxLogout_BadUserId() throws Exception {
        // Perform the logout request without userId
        mockMvc.perform(post("/wx/auth/wxLogout")
                        .param("userId", "-1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk()) // Assert client error
                .andExpect(jsonPath("$.success").value(false)) // Assert failure response
                .andExpect(jsonPath("$.msg").value("参数错误")); // Assert error message
    }
}