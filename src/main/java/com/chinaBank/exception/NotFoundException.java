package com.chinaBank.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NotFoundException implements ErrorController {
	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping(value = { "/error" })
	@ResponseBody
	public Object error(HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", "没有找到请求的页面!");
		body.put("code", "404");
		return body;
	}
}