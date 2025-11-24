package com.oj.onlinejudge.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("头像上传成功")
    void uploadAvatar_success() throws Exception {
        String tk = registerStudent().token();
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", squareImageBytes(300));
        mockMvc.perform(multipart("/api/files/avatar").file(file).header("Authorization", tk))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code", is(0)))
            .andExpect(jsonPath("$.data.url", containsString("/files/avatars/")));
    }

    @Test
    @DisplayName("头像上传-非正方形拒绝")
    void uploadAvatar_notSquare() throws Exception {
        String tk = registerStudent().token();
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", rectangleImageBytes());
        mockMvc.perform(multipart("/api/files/avatar").file(file).header("Authorization", tk))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is(400)));
    }

    private byte[] squareImageBytes(int size) throws Exception {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, size, size);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private byte[] rectangleImageBytes() throws Exception {
        BufferedImage image = new BufferedImage(200, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
