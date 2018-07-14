package com.ace.qnote.util.oss;

import com.tencent.cos.xml.exception.CosXmlClientException;

import java.util.ArrayList;

public interface OssListener {
    void onProgress(long progress, long max);

    void onSuccess(ArrayList<String> url);

    void onFail();
}
