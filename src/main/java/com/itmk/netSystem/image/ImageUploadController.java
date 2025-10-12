package com.itmk.netSystem.image;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

 
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    //获取图片上传路径
    @Value("${web.uploadpath}")
    private String webUploadpath;

    //图片上传方法
    @RequestMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file){
        //上传成功后的图片路径，返回给前端
        String Url = "";
        //获取上传图片的名称
        String fileName = file.getOriginalFilename();
        // aa.png  获取图片的扩展名 png
        String fileExtenionName = fileName.substring(fileName.indexOf("."));
        //生成新的文件名
        String newName = UUID.randomUUID().toString() + fileExtenionName;
        String path = webUploadpath;
        //创建文件夹
        File fileDir = new File(path);
        if(!fileDir.exists()){
            //创建
            fileDir.mkdirs();
            //设置权限
            fileDir.setWritable(true);
        }
        File targetFile = new File(path,newName);
        try{
            //上传
            file.transferTo(targetFile);
            //生成图片路径
            Url = "/"  + targetFile.getName();
        }catch (Exception e){
            return  null;
        }
        return ResultUtils.success("上传成功","/images" + Url);
    }
}
