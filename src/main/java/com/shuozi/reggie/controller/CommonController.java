package com.shuozi.reggie.controller;


import com.shuozi.reggie.common.CustomExceptionDemo;
import com.shuozi.reggie.common.R;
import com.sun.xml.internal.ws.model.wsdl.WSDLOutputImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 包含文件的上传
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController
{

    @Value("${reggie.path}")
    String rootPath;

    /**文件的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> update(MultipartFile file)
    {
        log.info(file.toString());
//       获取原始的文件名称
        String originalFilename = file.getOriginalFilename();
        String suffix=  originalFilename.substring(originalFilename.lastIndexOf("."));
       String fileName =  UUID.randomUUID().toString()+ suffix;
        File dir = new File(rootPath);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(rootPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 下载文件 对文件进行展示
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)
    {
        try {
//           输入流  通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(new File(rootPath+name));
//      输出流通过输出流把文件写回浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
//        写回浏览器的过程
            int length = 0;
            byte[] bytes =new byte[1024];
            while ((length =inputStream.read(bytes))!=-1)
            {
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
//            关闭输入和输出流
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
//            此处把打印的错误信息转换成了 输出的日志信息
            log.info(e.getMessage());
//            e.printStackTrace();
        }
    }
}
