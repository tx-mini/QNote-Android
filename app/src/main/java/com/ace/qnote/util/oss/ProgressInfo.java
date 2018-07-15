package com.ace.qnote.util.oss;

public class ProgressInfo {
    private long progress;
    private long max;

    public ProgressInfo(long progress, long max) {
        this.progress = progress;
        this.max = max;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }
}
