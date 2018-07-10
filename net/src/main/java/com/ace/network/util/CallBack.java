package com.ace.network.util;


public interface CallBack<T> {
    void onSuccess(T data);

    void onError(Throwable throwable);

    void onFailure(String message);

}
