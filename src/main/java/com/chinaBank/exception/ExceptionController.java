package com.chinaBank.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j;

@ControllerAdvice
@Log4j
public class ExceptionController {
	@ExceptionHandler(value = { Exception.class })
	@ResponseBody
	public Object error(Exception ex) {
		Map<String, String> map = new HashMap<>();
		map.put("message", "系统异常:"+ex.getMessage());
		map.put("code", "500");
		log.error(ex.getMessage());
		return map;
	}
}