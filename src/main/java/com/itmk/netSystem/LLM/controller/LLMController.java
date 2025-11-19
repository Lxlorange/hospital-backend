package com.itmk.netSystem.LLM.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itmk.netSystem.LLM.entity.CommonQuestionsVO;
import com.itmk.netSystem.LLM.entity.LLMstore;
import com.itmk.netSystem.LLM.service.LLMService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/LLM")
public class LLMController {
    @Autowired
    private LLMService llmService;
    @GetMapping("/getCommonQuestions")
    public ResultVo getAll(){
        QueryWrapper<LLMstore> query = new QueryWrapper<>();
        query.lambda().orderByDesc(LLMstore::getId);
        List<LLMstore> list = llmService.list(query);
        List<CommonQuestionsVO> askList = new ArrayList<>();
        for(LLMstore llmstore : list){
            CommonQuestionsVO commonQuestionsVO = new CommonQuestionsVO();
            commonQuestionsVO.setQuestionId(llmstore.getId());
            commonQuestionsVO.setQuestion(llmstore.getMessage());
            askList.add(commonQuestionsVO);
        }
        return ResultUtils.success("查询成功", askList);
    }

    @RequestMapping(value = "/ask", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultVo ask(@RequestBody(required = false) String message,
                        @RequestParam(value = "questionId", required = false) Long questionId) {
        // 如果未提供自由提问内容，但有常见问题ID，则读取对应问题
        if ((message == null || message.trim().isEmpty()) && questionId != null) {
            try {
                String answer = llmService.getCommonQuestionLoads(questionId);
                return ResultUtils.success("成功", answer);
            } catch (Exception e) {
                return ResultUtils.error("提问失败:" + e.getMessage());
            }
        } else if (message != null && questionId == null) {
            try {
                String answer = llmService.getLoads(message);
                return ResultUtils.success("成功", answer);
            } catch (Exception e) {
                return ResultUtils.error("提问失败:" + e.getMessage());
            }
        } else {
            return ResultUtils.error("请输入问题或选择常见问题");
        }
    }

    // 管理员新增常见问题
    @PostMapping("/admin/addCommonQuestion")
    public ResultVo addCommonQuestion(@RequestBody LLMstore request) {
        if (request == null) {
            return ResultUtils.error("请求体不能为空");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResultUtils.error("常见问题必须填写用户问题内容(message)");
        }
        try {
            boolean ok = llmService.save(request);
            if (!ok) {
                return ResultUtils.error("保存失败");
            }
            return ResultUtils.success("新增成功", request);
        } catch (Exception e) {
            return ResultUtils.error("新增失败:" + e.getMessage());
        }
    }

}
