package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.upload.path}")
    private String uploadPath;

    /**
     * 获取账号设置页面路径
     * @return 账号设置页面路径
     */
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传用户头像
     * 1. 校验上传的文件是否为空
     * 2. 校验文件类型是否合法 是否为图片
     *    2.1 获取文件的后缀名
     *    2.2 判断文件的后缀名是否为png jpg
     *    2.3 不是 报错
     *    2.4 是 继续往下
     * 3. 为图片生成一个新的随机名（防止多个用户的文件名相同 产生冲突）
     * 4. 存储图片
     * 5. 更新用户的头像url
     * 6. 跳转回账号设置setting页面
     * @param headerImage
     * @param mv
     * @return
     */
    @PostMapping("/upload")
    public ModelAndView uploadHeader(MultipartFile headerImage, ModelAndView mv) {
        // 1. 校验上传的图片headerImage是否为空
        if (headerImage == null) {
            mv.addObject("error", "您还未上传图片！");
            mv.setViewName("/site/setting");
            return mv;
        }
        // 2. 校验文件类型是否合法
        // 2.1 获取文件的后缀名
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 2.2 判断文件的后缀名是否为png, jpg
        if (!suffix.equals(".jpg") && !suffix.equals(".png")) {
            // 2.3 不是 则报错
            mv.addObject("error", "上传文件类型有误，只允许上传jpg, png类型的文件");
        }
        // 2.4 是则继续

        // 3. 生成新的随机图片名
        String picName = CommunityUtil.generateUUID() + suffix;
        // 4. 存储文件
        // 新建文件
        File file = new File(uploadPath + "/" + picName);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error("上传用户头像失败：", e.getMessage());
            // mv.addObject("error", "上传用户头像失败，服务器出现故障！");
            throw new RuntimeException("上传用户头像失败：" + e.getMessage());
        }

        // 5. 更新用户的headerUrl（web访问路径）
        // http://localhost:80/community/user/header/图片名
        String headerUrl = domain + contextPath + "/user/header/" + picName;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), headerUrl);

        mv.setViewName("redirect:/user/setting");
        return mv;
    }

    /**
     * 读取头像
     * 1. 根据headerUrl获得文件名 （http://localhost:80/community/user/header/图片名）
     * 2. 验证文件名是否为空
     * 3. 拼接文件在服务器中的完整存储路径
     * 4. 获取对应的文件
     * 5. 验证文件是否为空 是则报错 不是则继续往下
     * 6. 设置响应的文件类型
     * 7. 将图片文件写入输出流
     * @param filename
     * @param response
     */
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable String filename, HttpServletResponse response) {
        // 2. 验证文件名是否为空
        if (StringUtils.isBlank(filename)) {
            log.error("info", "读取头像信息失败！");
        }
        // 3. 拼接文件在服务器中的完整存储路径
        filename = uploadPath + "\\" + filename;
        // 4. 获取对应的文件
        File file = new File(filename);
        // 5. 验证文件是否为空 是则报错 不是则继续往下
        if (file == null) {
            log.error("info", "读取头像信息失败！");
        }

        // 6. 设置响应的文件类型
        // 注意 响应头的信息需要在获取输出流之前设置才能够成功发送到客户端
        // 也就是说 contentType的值必须先设置 再调用response.getOutputStream();方法 才能成功设置contentType的值
        // 如果先调用response.getOutputStream(); 再 response.setContentType("image/" + suffix); 则最终contentType的值为null 无法设置成功
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);

        // 7. 将图片文件写入输出流
        try (
                // 获取输出流
                ServletOutputStream os = response.getOutputStream();
                // 获取输入流
                FileInputStream fis = new FileInputStream(filename);
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            // 从输入流中读取信息 每次读1024个字节
            // 当b==-1时 说明已经读完
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取头像失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
