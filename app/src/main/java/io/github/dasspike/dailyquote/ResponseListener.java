package io.github.dasspike.dailyquote;

interface ResponseListener {
    <T> void onSuccess(T object, boolean forceRefresh, boolean isImmediate);

    void onError(Exception e);
}
