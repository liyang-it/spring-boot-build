package com.mh.jishi.util;


import com.github.binarywang.utils.qrcode.MatrixToImageWriter;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @Auther: Lizr
 * @Date: 2020-06-23 11:22
 * @Description: 二维码生产工具类
 */
public class QRUtil {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private QRUtil() {
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        Base64.Encoder encoder = Base64.getEncoder();
        String base64QrCode = encoder.encodeToString(outputStream.toByteArray());
//        BufferedImage image = toBufferedImage(matrix);
//        System.out.println("image:"+image);

//        if (!ImageIO.write(image, format, stream)) {
//            throw new IOException("Could not write an image of format " + format);
//        }
    }

    public static String writeToBase64(String urlCode) {
        try {
            int width = 300;
            int height = 300;
            //二维码的图片格式
            String format = "png";
            Hashtable hints = new Hashtable();
            //内容所使用编码
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix;
            bitMatrix = new MultiFormatWriter().encode(urlCode, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            Base64.Encoder encoder = Base64.getEncoder();
            String base64QrCode = encoder.encodeToString(outputStream.toByteArray());
            return base64QrCode;
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    /*二维码生成*/
    public static Map<String,Object> zxingCodeCreate(String urlCode){
        int width = 300;
        int height = 300;
        Map<EncodeHintType, String> his = new HashMap<EncodeHintType, String>();
        //设置编码字符集
        his.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            //1、生成二维码
            BitMatrix encode = new MultiFormatWriter().encode(urlCode, BarcodeFormat.QR_CODE, width, height, his);
            //2、获取二维码宽高
            int codeWidth = encode.getWidth();
            int codeHeight = encode.getHeight();
            //3、将二维码放入缓冲流
            BufferedImage image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < codeWidth; i++) {
                for (int j = 0; j < codeHeight; j++) {
                    //4、循环将二维码内容定入图片
                    image.setRGB(i, j, encode.get(i, j) ? BLACK : WHITE);
                }
            }
            // 创建一个输出流
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //将图片写出到指定位置（复制图片）
            ImageIO.write(image, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            // 定义一个 Map 集合 存放返回值
            Map<String,Object> retMap = upload(is);
            return retMap;
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("二维码生成失败");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("生成二维码图片失败");
        }
        return null;
    }

    /*七牛云上传*/
    public static Map<String, Object> upload (InputStream stream){
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        // 上传凭证
        String accessKey ="RvqGVYV0NavdXQKUn1psfDYfiylylVlfbTjSRbXj";//七牛存储AccessKey
        String secretKey ="4UqKwVSFxDNLTxTCQ1Z_l2vXTcLLN5Nd-snGjbnV";//七牛存储secretKey
        String bucket = "xinsicheng";//七牛存储bucket
        String resourceHost="http://cunchu.huimasheng.com/";//(七牛)资源访问域名
        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID().toString().replaceAll("\\-", "");
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(stream, key, upToken, null, null);
                // 解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                /*retMap.put("url", "上传的连接" + putRet.key)*/;
                retMap.put("hash", putRet.hash);
                retMap.put("filePath",resourceHost+putRet.key);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    ex2.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retMap;
    }

}