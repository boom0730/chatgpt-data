package cn.bugstack.chatgpt.data.trigger.http;

import cn.bugstack.chatgpt.data.domain.auth.service.IAuthService;
import cn.bugstack.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.bugstack.chatgpt.data.domain.openai.model.entity.MessageEntity;
import cn.bugstack.chatgpt.data.domain.openai.service.IChatService;
import cn.bugstack.chatgpt.data.trigger.http.dto.ChatGPTRequestDTO;
import cn.bugstack.chatgpt.data.types.common.Constants;
import cn.bugstack.chatgpt.data.types.exception.ChatGPTException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2023-07-16 09:19
 */
@Slf4j
@RestController()//@Controller 和 @ResponseBody 的结合，表示该类中的所有方法默认都以 JSON 或 XML 的形式返回响应体，而不是视图页面
@CrossOrigin("${app.config.cross-origin}")//跨域的调用！！！ 配置在application中 * 即全部放开也就是不作拦截
@RequestMapping("/api/${app.config.api-version}/chatgpt/")//这个是在app层写的yml文件里面 后续升级方便统一管理 /api/v1
public class ChatGPTAIServiceController {

    @Resource
    private IChatService chatService;

    @Resource
    private IAuthService authService;
    /**
     * 从微信中接收过来？
     * 流式问题，ChatGPT 请求接口
     * 小案例 比起old那个controller 这里使用了设计模式 在domain层写了那些service接口 以及实现类等
     */
    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter completionsStream(@RequestBody ChatGPTRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        //RequestBody RequestHeader均为请求体请求体中的数据
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");//!!!!这里要配置流式输出
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 构建异步响应对象【对 Token 过期拦截】
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            boolean success = authService.checkToken(token);

            if (!success) {
                try {
                    emitter.send(Constants.ResponseCode.TOKEN_ERROR.getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                return emitter;
            }

            // 3. 获取 OpenID
            String openid = authService.openid(token);
            log.info("流式问答请求处理，openid:{} 请求模型:{}", openid, request.getModel());

            // 4. 构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .openid(openid)//请求头中
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(messageEntity -> MessageEntity.builder()
                                    .role(messageEntity.getRole())
                                    .content(messageEntity.getContent())
                                    .name(messageEntity.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            // 5. 请求结果&返回
            return chatService.completions(emitter, chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGPTException(e.getMessage());
        }
    }

}
