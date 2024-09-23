package cn.tradewar.wx.controller;

import cn.tradewar.core.common.GenericResponse;
import cn.tradewar.core.common.ServiceError;
import cn.tradewar.core.consts.LogConst;
import cn.tradewar.dao.model.bo.WxLoginBo;
import cn.tradewar.wx.service.WeChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;


@Log4j2
@RestController
@RequestMapping("/wx/auth")
public class WxAuthController {


	private final WeChatService weChatService;

	public WxAuthController(WeChatService weChatService){
		this.weChatService = weChatService;
	}


	/**
	 * 微信通过手机号码登陆
	 * 当用户不存在时，则注册用户
	 * 当用户的openId同绑定的手机号码不同时，则提示更换手机号码登陆
	 * @param wxLoginBo WxLoginBo
	 * @param request HttpServletRequest
	 * @return GenericResponse
	 */
	@PostMapping("wxLoginPhone")
	public GenericResponse wxLoginPhone(@Valid @RequestBody WxLoginBo wxLoginBo,
										HttpServletRequest request) {
		log.info(LogConst.WX_BEGIN, "wxLoginPhone", wxLoginBo);
		if (StringUtils.isBlank(wxLoginBo.getPhone()) &&
				(StringUtils.isBlank(wxLoginBo.getIv()) || StringUtils.isBlank(wxLoginBo.getEncryptedData()))) {
			return GenericResponse.badArgument();
		}
		return weChatService.wxLoginPhone(wxLoginBo, request);
	}


	/**
	 * 微信登陆
	 * @param code 微信code
	 * @return GenericResponse
	 */
	@PostMapping("wxLogin")
	public GenericResponse login(@RequestParam(required = false) String code)  {
		log.info(LogConst.WX_BEGIN, "login", code);
		// 输入验证
		if (code == null || code.isEmpty()) {
			log.error("微信code不能为空");
			return GenericResponse.response(ServiceError.ARG_ERROR);
		}
		try{
			return weChatService.wxLogin(code);
		} catch (Exception e) {
			log.error(LogConst.WX_END_ERR, "login", code, e.getMessage());
			return GenericResponse.response(ServiceError.UN_KNOW_ERROR);
		}

	}

	/**
	 * 注销登录
	 *
	 * @param userId userId
	 * @return GenericResponse
	 */
	@PostMapping("wxLogout")
	public GenericResponse logout(@RequestParam(required = false) Long userId) {
		log.info(LogConst.WX_BEGIN, "logout", userId);
		if (userId == null || userId <= 0) {
			log.error(LogConst.WX_END_ERR, "logout", null, "userId不能为空");
			return GenericResponse.response(ServiceError.ARG_ERROR);
		}
		return weChatService.wxLogout(userId);
	}


//	@PostMapping("wxBindMobile")
//	public GenericResponse bindMobile(@Valid @RequestBody WxBindMobileBo bo) {
//		log.info(LogConst.WX_BEGIN, "bindMobile", bo);
//		return weChatService.wxBindMobile(bo);
//	}
}

