package cn.bugstack.chatgpt.data.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 敏感词配置
 * @create 2023-09-16 17:38
 */
@Slf4j
@Configuration
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .wordReplace((stringBuilder, chars, wordResult, iWordContext) -> {
                    String sensitiveWord = InnerWordCharUtils.getString(chars, wordResult);
                    log.info("检测到敏感词: {}", sensitiveWord);
                    /* 替换操作，你可以指定的替换为*或者其他
                    else {
                        int wordLength = wordResult.endIndex() - wordResult.startIndex();
                        for (int i = 0; i < wordLength; i++) {
                            stringBuilder.append("");
                        }
                    }*/
                })
                .ignoreCase(true)//忽略到大小写
                .ignoreWidth(true)//全角半角
                .ignoreNumStyle(true)//
                .ignoreChineseStyle(true)//中文简繁体
                .ignoreEnglishStyle(true)//英文大小写
                .ignoreRepeat(false)
                .enableNumCheck(true)//数字检查
                .enableEmailCheck(true)//邮箱地址检测
                .enableUrlCheck(true)//url
                .enableWordCheck(true)//启用敏感词检查
                .numCheckLen(1024)//数字长度检查
                .init();
    }

}
