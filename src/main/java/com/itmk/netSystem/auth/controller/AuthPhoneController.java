package com.itmk.netSystem.auth.controller;

import com.itmk.netSystem.auth.service.IdentityAuthService;
import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 小程序端身份认证相关接口
 */
@RestController
@RequestMapping("/wxapi/allApi")
public class AuthPhoneController {

    @Autowired
    private IdentityAuthService identityAuthService;

    @Value("${web.uploadpath}")
    private String webUploadpath;

    /**
     * 4. 上传身份认证照片（小程序端）
     * UPLOAD /wxapi/allApi/uploadPhoto
     * form-data: file
     * 返回：图片访问路径（如 /images/xxx.jpg）
     */
    @PostMapping("/uploadPhoto")
    public ResultVo uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResultUtils.error("上传文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ResultUtils.error("文件名格式不正确");
        }
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedExtensions.contains(fileExtensionName)) {
            return ResultUtils.error("不支持的文件类型");
        }

        String newName = UUID.randomUUID().toString() + fileExtensionName;
        String projectRootPath = System.getProperty("user.dir");
        String fullUploadPath = projectRootPath + File.separator + webUploadpath;
        File fileDir = new File(fullUploadPath);
        if (!fileDir.exists() && !fileDir.mkdirs()) {
            return ResultUtils.error("创建上传目录失败");
        }
        File targetFile = new File(fullUploadPath, newName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            return ResultUtils.error("文件上传失败");
        }
        String url = "/images/" + newName;
        return ResultUtils.success("上传成功", url);
    }

    /**
     * 5. 提交身份认证申请（小程序端）
     * POST /wxapi/allApi/submitAuth
     * body: { userType, cardNo, cardFront, cardBack }
     */
    @PostMapping("/submitAuth")
    public ResultVo submitAuth(@RequestBody Map<String, Object> body) {
        String userType = body.get("userType") != null ? body.get("userType").toString() : null;
        String cardNo = body.get("cardNo") != null ? body.get("cardNo").toString() : null;
        String cardFront = body.get("cardFront") != null ? body.get("cardFront").toString() : null;
        String cardBack = body.get("cardBack") != null ? body.get("cardBack").toString() : null;

        // 可选：允许传入 userId/username
        Integer userId = null;
        String username = null;
        if (body.get("userId") != null) {
            try { userId = Integer.valueOf(body.get("userId").toString()); } catch (Exception ignored) {}
        }
        if (body.get("username") != null) {
            username = body.get("username").toString();
        }

        if (userType == null || cardNo == null || cardFront == null || cardBack == null) {
            return ResultUtils.error("参数错误");
        }

        boolean ok = identityAuthService.submit(userId, username, userType, cardNo, cardFront, cardBack);
        if (!ok) {
            return ResultUtils.error("提交失败");
        }
        return ResultUtils.success("提交成功");
    }
}