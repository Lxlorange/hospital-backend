package com.itmk.netSystem.imageTransfer;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/upload")
public class ImageTransferController {

    @Value("${web.uploadpath}")
    private String webUploadpath; // 这将获取到 "uploads/"

    /**
     * 批量图片上传
     * @param files 接收多个文件
     * @return 包含所有图片访问路径的列表
     */
    @PostMapping("/uploadMultipleImages")
    public ResultVo uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResultUtils.error("上传文件列表不能为空");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            // 复用单文件上传的逻辑
            ResultVo singleUploadResult = uploadImage(file);
            if (singleUploadResult.getCode() == 200) {
                urls.add((String) singleUploadResult.getData());
            } else {
                // 如果任何一个文件上传失败，可以整体返回失败，或记录部分成功
                return ResultUtils.error("文件上传失败: " + file.getOriginalFilename());
            }
        }
        return ResultUtils.success("所有文件上传成功", urls);
    }

    /**
     * 获取已上传文件的列表
     * @return 文件名列表
     */
    @GetMapping("/listFiles")
    public ResultVo listFiles() {
        String fullUploadPath = System.getProperty("user.dir") + File.separator + webUploadpath;
        File fileDir = new File(fullUploadPath);

        if (!fileDir.exists() || !fileDir.isDirectory()) {
            return ResultUtils.success("目录为空或不存在", new ArrayList<>());
        }

        File[] files = fileDir.listFiles();
        if (files == null) {
            return ResultUtils.error("无法读取目录内容");
        }

        List<String> fileNames = Arrays.stream(files)
                .map(File::getName)
                .collect(Collectors.toList());

        return ResultUtils.success("查询成功", fileNames);
    }

    /**
     * 删除指定的文件
     * @param filename 要删除的文件名
     * @return 操作结果
     */
    @DeleteMapping("/deleteImage")
    public ResultVo deleteImage(@RequestParam("filename") String filename) {
        // 安全校验，防止路径遍历攻击
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResultUtils.error("非法的文件名");
        }

        String fullUploadPath = System.getProperty("user.dir") + File.separator + webUploadpath;
        File fileToDelete = new File(fullUploadPath, filename);

        if (!fileToDelete.exists()) {
            return ResultUtils.error("文件不存在");
        }

        if (fileToDelete.delete()) {
            return ResultUtils.success("文件删除成功");
        } else {
            return ResultUtils.error("文件删除失败");
        }
    }

    /**
     * 带类型校验的图片上传
     * @param file 上传的文件
     * @return 操作结果
     */
    @PostMapping("/uploadImageWithValidation")
    public ResultVo uploadImageWithValidation(@RequestParam("file") MultipartFile file) {
        // 1. 基础校验
        if (file.isEmpty()) {
            return ResultUtils.error("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ResultUtils.error("文件名格式不正确");
        }

        // 2. 文件类型白名单校验
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedExtensions.contains(fileExtensionName)) {
            return ResultUtils.error("不支持的文件类型，仅允许上传 " + allowedExtensions);
        }

        // 3. 文件大小校验 (例如：不超过5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            return ResultUtils.error("文件大小不能超过5MB");
        }

        // 4. 调用原始上传方法
        return uploadImage(file);
    }

    /**
     * 文件下载
     * @param filename 文件名
     * @param request HTTP请求对象，用于获取MIME类型
     * @return 文件资源
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        String fullUploadPath = System.getProperty("user.dir") + File.separator + webUploadpath;
        Path filePath = Paths.get(fullUploadPath).resolve(filename).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file) {
        String url = "";

        // 获取上传图片的原始文件名
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ResultUtils.error("文件名格式不正确");
        }
        // 提取图片的扩展名 (如 .png)
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        // 生成新的文件名，防止重复
        String newName = UUID.randomUUID().toString() + fileExtensionName;

        // 1. 获取项目的根目录。System.getProperty("user.dir") 会返回项目启动的位置。
        String projectRootPath = System.getProperty("user.dir");

        // 2. 将项目根目录和配置文件中的相对路径拼接起来
        String fullUploadPath = projectRootPath + File.separator + webUploadpath;

        // 3. 使用拼接后的完整路径来创建目录
        File fileDir = new File(fullUploadPath);
        if (!fileDir.exists()) {
            // mkdirs() 可以创建多层目录，更安全
            if (!fileDir.mkdirs()) {
                // 如果创建失败，直接返回错误
                return ResultUtils.error("创建上传目录失败");
            }
        }

        // 4. 构造目标文件，使用完整路径
        File targetFile = new File(fullUploadPath, newName);

        try {
            // 执行文件上传
            file.transferTo(targetFile);
            // 构造图片相对路径，给前端使用
            url = "/" + newName;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtils.error("文件上传失败"); // 上传失败返回更明确的错误信息
        }

        // 成功返回带前缀的完整路径
        return ResultUtils.success("上传成功", "/images" + url);
    }
}