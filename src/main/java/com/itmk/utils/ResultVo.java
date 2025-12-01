package com.itmk.utils;

public class ResultVo<T> {
    private String msg;
    private int code;
    private T data;

    public String getMsg() {
        return this.msg;
    }

    public int getCode() {
        return this.code;
    }

    public T getData() {
        return this.data;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResultVo)) {
            return false;
        } else {
            ResultVo<?> other = (ResultVo)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else {
                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ResultVo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCode();
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getMsg();
        return "ResultVo(msg=" + var10000 + ", code=" + this.getCode() + ", data=" + String.valueOf(this.getData()) + ")";
    }

    public ResultVo(final String msg, final int code, final T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }
}
