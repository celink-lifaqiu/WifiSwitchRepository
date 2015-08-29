package com.celink.service.impl;

import java.util.Map;

import com.celink.action.UserAction;
import com.celink.action.pojo.User;
import com.celink.service.UserService;
import com.celink.util.JsonUtil;
import com.celink.util.ValidationUtils;

public class UserServiceImpl implements UserService {

	@Override
	public int regist(String jsonData) {
		int code = 0;
		Map<String, Object> map = JsonUtil.parseJSON2Map1(jsonData);
		String email = map.get("email")==null?"":map.get("email").toString();
		String pwd = map.get("pwd")==null?"":map.get("pwd").toString();
		String nickName = map.get("nickName")==null?"":map.get("nickName").toString();
		if(!ValidationUtils.validateEmail(email)){
			code = 5;
		}else if(pwd.length() < 6 || pwd.length() > 16){
			code = 6;
		}else if(UserAction.findUserByEmail(email) != null){
			code = 7;
		}else{
			User user = new User();
			user.setEmail(email);
			user.setPwd(pwd);
			user.setNickName(nickName);
			code = UserAction.addUser(user);
		}
		
		return code;
	}
	
	@Override
	public Object login(String jsonData) {
		Integer code = 0;
		User user = null;
		Map<String, Object> map = JsonUtil.parseJSON2Map1(jsonData);
		String email = map.get("email")==null?"":map.get("email").toString();
		String pwd = map.get("pwd")==null?"":map.get("pwd").toString();
		if(!ValidationUtils.validateEmail(email)){
			code = 5;
		}else if(pwd.length() < 6 || pwd.length() > 16){
			code = 6;
		}else{
			user = UserAction.findUserByEmail(email, pwd);
			if(user == null){
				code = 500;
			}
		}
		if(code == 0){
			return user;
		}
		return code;
	}

}
