package cn.bugstack.chatgpt.data.domain.auth.service;

import cn.bugstack.chatgpt.data.domain.auth.model.entity.AuthStateEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 鉴权验证服务接口
 * @create 2023-08-05 18:22
 */
public interface IAuthService {

    /**
     * 登录验证 看你的验证码是否正确的
     * @param code 验证码
     * @return Token
     */
    AuthStateEntity doLogin(String code);

    /**
     * 校验token
     * @param token
     * @return
     */
    boolean checkToken(String token);

}
