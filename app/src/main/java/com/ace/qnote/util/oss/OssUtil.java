package com.ace.qnote.util.oss;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ace.qnote.util.LocalCredentialProvider;
import com.ace.qnote.util.MD5Util;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;

import java.util.ArrayList;
import java.util.UUID;

public class OssUtil {

    private static final int MSG_PROGRESS = 1000;
    private static final int MSG_SUCCESS = 1001;
    private static final int MSG_ERROR = 1002;

    private static class UploadHandler extends Handler{
        OssListener ossListener;

        public UploadHandler(OssListener ossListener) {
            this.ossListener = ossListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PROGRESS:
                    ProgressInfo progressInfo = (ProgressInfo) msg.obj;
                    ossListener.onProgress(progressInfo.getProgress(),progressInfo.getMax());
                case MSG_SUCCESS:
                    ossListener.onSuccess((ArrayList<String>) msg.obj);
                    break;
                case MSG_ERROR:
                    ossListener.onFail();
                    break;
            }
        }
    }

    private static UploadHandler uploadHandler;

    public static CosXmlService getService(Context context) {
        String appid = "1253746866";
        String region = "ap-guangzhou";

        String secretId = "AKID1rTpwoXowoUzIQvijYAannaBZkrmb9Xj";
        String secretKey = "83lGZfYFj04way6yax4vWsyoPkpeEdqp";
        long keyDuration = 600; //SecretKey 的有效时间，单位秒

        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();

        //创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretId, secretKey, keyDuration);

        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        Context appContext = context.getApplicationContext();

        return new CosXmlService(appContext, serviceConfig, localCredentialProvider);
    }

    public static void upload(CosXmlService cosXmlService,ArrayList<String> filePaths, OssListener ossListener) {
        uploadHandler = new UploadHandler(ossListener);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> urlList = new ArrayList<>();
                for (String filePath : filePaths) {
                    try {
                        String bucket = "qnote-1253746866"; // cos v5 的 bucket格式为：xxx-appid, 如 test-1253960454
                        long signDuration = 600; //签名的有效期，单位为秒
                        String cosPath = MD5Util.crypt(UUID.randomUUID().toString())+".jpg"; //格式如 cosPath = "test.txt";
                        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, filePath);
                        putObjectRequest.setSign(signDuration,null,null); //若不调用，则默认使用sdk中sign duration（60s）
                        PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
                        urlList.add(putObjectResult.accessUrl);
                    } catch (CosXmlClientException e) {
                        uploadHandler.sendEmptyMessage(MSG_ERROR);
                        return;
                    } catch (CosXmlServiceException e) {
                        uploadHandler.sendEmptyMessage(MSG_ERROR);
                        return;
                    }
                }
                Message message = new Message();
                message.what = MSG_SUCCESS;
                message.obj = urlList;
                uploadHandler.sendMessage(message);
            }
        });
        thread.start();
//        for (String filePath : filePaths) {
//            putObjectRequest.setSign(signDuration, null, null); //若不调用，则默认使用sdk中sign duration（60s）
//            /*设置进度显示
//              实现 CosXmlProgressListener.onProgress(long progress, long max)方法，
//              progress 已上传的大小， max 表示文件的总大小
//            */
//            putObjectRequest.setProgressListener(new CosXmlProgressListener() {
//                @Override
//                public void onProgress(long progress, long max) {
//                    Message message = new Message();
//                    message.what=MSG_PROGRESS;
//                    message.obj = new ProgressInfo(progress,max);
//                }
//            });
//            cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
//                @Override
//                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                    Message message = new Message();
//                    message.what=MSG_SUCCESS;
//                    message.obj = result;
//                    uploadHandler.sendMessage(message);
//                }
//
//                @Override
//                public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
//                }
//            });
//        }


    }
}
