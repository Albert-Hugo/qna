package com.ido.qna.util;

import java.io.IOException;

/**
 * @author ido
 * Date: 2018/5/4
 **/
public class FfmpegUtil {
    /**
     *
     * @param ffmpegPath    转码工具的存放路径
     * @param upFilePath    要截图的视频源文件
     * @param mediaPicPath  添加截取的图片的保存路径
     * @return
     */
    public static boolean screenImage(String ffmpegPath, String upFilePath, String mediaPicPath) throws IOException {

        try {
            Process p = Runtime.getRuntime().exec(ffmpegPath+" -ss 00:00.01 -i "+upFilePath+" -frames:v 1 "+mediaPicPath);
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return true;
    }

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("/Users/junli/software/ffmpeg -ss 00:00.01 -i /Users/junli/Desktop/WeChatSight33.mp4 -frames:v 1 /Users/junli/Desktop/out2.jpg");
    }
}
