package com.wztc.demo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.Response;
import com.wztc.demo.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    MinioUtil minioUtil;

    // 文件上传存储路径
    public static final String filePath = System.getProperty("user.dir") + "/upload/";

    @Value("${server.port}")
    private String port;

    @Value("${ip}")
    private String ip;

    /**
     * 上传文件
     * @param file
     * @return 图片链接
     */
    @NoTokenAccess
    @PostMapping("/minio/upload")
    public Response minioupload(MultipartFile file) {
        String strFileflag = String.valueOf(System.currentTimeMillis());
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String path = null;
        try {
            // 文件存储形式：时间戳-文件名
            path = minioUtil.uploadFile(strFileflag + "-" + fileName, file.getInputStream(), contentType);
            System.out.println(fileName + "--上传成功");
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        return Response.success(path);
    }

    /**
     * 删除文件
     * @param fileName
     * @return  None
     */
    @NoTokenAccess
    @DeleteMapping("/minio/delete/{fileName}")
    public Response miniodelete(@PathVariable String fileName) {
        try {
            // 根据文件名删除图片
            minioUtil.deleteFile(fileName);
            System.out.println(fileName + "----Minio图片删除成功");
        } catch (Exception e) {
            System.err.println(fileName + "----Minio图片删除失败");
        }
        return Response.success();
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    @NoTokenAccess
    @PostMapping("/upload")
    public Response upload(MultipartFile file) {
        String strFileflag = String.valueOf(System.currentTimeMillis());
        String fileName = file.getOriginalFilename();
        try {
            FileUtil.mkdir(new File(filePath));
            // 文件存储形式：时间戳-文件名
            FileUtil.writeBytes(file.getBytes(), new File(filePath + strFileflag + "-" + fileName)); // ***/manager/files/1697438073596-avatar.png
            System.out.println(fileName + "--上传成功");
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        //访问链接   http://localhost:5000/files/1697438073596-avatar.png
        return Response.success(strFileflag + "-" + fileName);
    }


    /**
     * 访问文件
     * @param filename
     * @param response
     */
    @NoTokenAccess
    @GetMapping("/{filename}")
    public void downloadFile(@PathVariable String filename, HttpServletResponse response) {
        OutputStream os;
        try {
            if (!StringUtils.isEmpty(filename)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(new File(filePath + filename));
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }


    /**
     * 编辑器上传文件
     * @param file
     * @return
     */
    @PostMapping("/editor/upload")
    public JSONObject editorUpload(@RequestParam MultipartFile file, @RequestParam String type) {
        String strFileflag = String.valueOf(System.currentTimeMillis());
        String fileName = file.getOriginalFilename();
        try {
            FileUtil.mkdir(new File(filePath));
            // 文件存储形式：时间戳-文件名
            FileUtil.writeBytes(file.getBytes(), new File(filePath + strFileflag + "-" + fileName));
            System.out.println(fileName + "--上传成功");

        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }

        String url = "http://" + ip + ":" + port + "/files/" + strFileflag + "-" + fileName;

        if("image".equals(type)){
            return new JSONObject()
                    .putOnce("errno", 0)
                    .putOnce("data", new JSONArray()
                            .put(new JSONObject().putOnce("url", url))
                    );
        }else if ("video".equals(type)){
            return new JSONObject()
                    .putOnce("errno", 0)
                    .putOnce("data", new JSONObject().putOnce("url", url)
                    );
        }

        return new JSONObject().putOnce("errno", 0);
    }


    /**
     * 访问指定文件夹内容文件
     * @param filename
     * @param response
     */
    @NoTokenAccess
    @GetMapping("/{path}/{filename}")
    public void downloadFile(@PathVariable String path, @PathVariable String filename, HttpServletResponse response) {
        OutputStream os;
        try {
            if (!StringUtils.isEmpty(filename)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(new File(filePath + path + "/" + filename));
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

}
