package com.susu.utils;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Description: Image generation and recognition</p>
 * <p>图片剪切、压缩、转换工具</p>
 * @author sujay
 * @version 16:56 2024/10/29
 * @since JDK1.8 <br/>
 */
public class ImgUtils {

    public static final String IMAGE_TYPE_GIF = "gif";

    public static final String IMAGE_TYPE_JPG = "jpg";

    public static final String IMAGE_TYPE_JPEG = "jpeg";

    public static final String IMAGE_TYPE_BMP = "bmp";

    public static final String IMAGE_TYPE_PNG = "png";

    public static final String IMAGE_TYPE_PSD = "psd";

    public static final List<String> IMAGE_TYPES = Arrays.asList(IMAGE_TYPE_BMP, IMAGE_TYPE_JPG, IMAGE_TYPE_JPEG, IMAGE_TYPE_GIF, IMAGE_TYPE_PNG, IMAGE_TYPE_PSD);

    /**
     * <p>Description: Get file extension</p>
     * <p>获取文件名后缀</p>
     *
     * @param filename 文件名
     */
    public static String getExtension(String filename) {
        String[] split = filename.split("\\.");
        if (split.length > 1) {
            return split[split.length -1];
        }
        return null;
    }

    /**
     * <p>Description: Reduce image size</p>
     * <p>缩小图片尺寸</p>
     *
     * @param source    原文件
     * @param target    输出到目标文件
     * @param scale     缩小到原来大小的多少倍
     */
    public static void scale(File source, File target, float scale) throws IOException {
        convert(source, target, scale, 1);
    }

    /**
     * <p>Description: Compressed image quality</p>
     * <p>压缩图片质量</p>
     *
     * @param source    原文件
     * @param target    输出到目标文件
     * @param quality   压缩到原来大小的多少倍
     */
    public static void compress(File source, File target, float quality) throws IOException {
        convert(source, target, 1, quality);
    }

    /**
     * <p>Description: image conversion </p>
     * <p>图片转换</p>
     *
     * @param source    原文件
     * @param target    输出到目标文件
     * @param scale     缩放倍率
     * @param quality   压缩倍率
     */
    public static void convert(File source, File target, float scale, float quality) throws IOException {

        scale = scale < 0.0F ? 0.0F : Math.min(scale, 1.0F);
        quality = quality < 0.0F ? 0.0F : Math.min(quality, 1.0F);
        BufferedImage img = ImageIO.read(source);

        String targetSuffix = getExtension(target.getName());
        if (null == targetSuffix || !IMAGE_TYPES.contains(targetSuffix)) {
            throw new RuntimeException("The file is not recognized as a image, file name: " + target.getName());
        }

        int width = (int) (img.getWidth() * scale);
        int height = (int) (img.getHeight() * scale);

        String sourceSuffix = getExtension(source.getName());
        if (null == sourceSuffix || !IMAGE_TYPES.contains(sourceSuffix)) {
            throw new RuntimeException("The file is not recognized as a image, file name: " + source.getName());
        }

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaledImage.createGraphics();
        if (IMAGE_TYPE_PNG.equals(sourceSuffix) && !IMAGE_TYPE_PNG.equals(targetSuffix)) {
            graphics.drawImage(img, 0, 0, width, height, Color.white,null);
        } else {
            graphics.drawImage(img, 0, 0, width, height,null);
        }

        ImageWriter writer = ImageIO.getImageWritersByFormatName(targetSuffix).next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(target);
        writer.setOutput(ios);

        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(quality);

        writer.write(null, new IIOImage(scaledImage, null, null), writeParam);
        ios.close();
        writer.dispose();
    }

    public static void main(String[] args) throws IOException {
        convert(new File("G:\\A.png"), new File("G:\\B.jpg"), 0.5f, 0.5f);
    }
}