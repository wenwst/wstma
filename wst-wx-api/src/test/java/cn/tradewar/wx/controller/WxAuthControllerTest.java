package cn.tradewar.wx.controller;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.wx.service.WeChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class WxAuthControllerTest{

    @MockBean
    private WeChatService weChatService; // 模拟服务层

    @Autowired
    private WxAuthController wxAuthController; // 注入控制器

    private MockMvc mockMvc; // 注入 MockMvc 实例

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(wxAuthController).build(); // 手动创建 MockMvc
    }

    @Test
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
}