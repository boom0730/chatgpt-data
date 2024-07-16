package cn.bugstack.chatgpt.data.domain.openai.service;

import cn.bugstack.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.bugstack.chatgpt.data.types.common.Constants;
import cn.bugstack.chatgpt.data.types.exception.ChatGPTException;
import cn.bugstack.chatgpt.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2023-07-22 21:12
 */
@Slf4j
public abstract class AbstractChatService implements IChatService {

    @Resource
    protected OpenAiSession openAiSession;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        // 1. 校验权限
//        if (!"boom730".equals(chatProcess.getToken())) {
//            throw new ChatGPTException(Constants.ResponseCode.TOKEN_ERROR.getCode(), Constants.ResponseCode.TOKEN_ERROR.getInfo());
//        }
        // 2. 请求应答
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        //下面者两个emitter的代码对于逻辑无用 只是日志打印
        // emitter.onCompletion :
        // 1 资源清理：在流式响应完成后，释放任何占用的资源。
        // 2 日志记录：记录响应完成的日志信息。
//        传递一个实现了 Runnable 接口的实例（通常是一个 lambda 表达式或方法引用）
        //相当于接收到一个流式应答就log
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });
        //如果错误就打印错误信息
        emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

        // 3. 应答处理 调用具体实现类的doMessageResponse去真实应答处理
        try {
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 4. 返回结果
        return emitter;
    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;

}
