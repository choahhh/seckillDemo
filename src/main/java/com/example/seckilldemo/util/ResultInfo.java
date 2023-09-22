package com.example.seckilldemo.util;

import lombok.Data;

import java.io.Serializable;

/***
 * @author zhichenghan
 * @since 2020/7/1 11:21 上午
 */
@Data
public class ResultInfo<T> implements Serializable {
	private static final long serialVersionUID = -7155915849493420131L;

	/***
	 * 是否成功
	 */
	private boolean success;

	/***
	 * 业务数据
	 */
	private T data;

	/***
	 * 错误编码
	 */
	private String code;

	/***
	 * 错误消息
	 */
	private String message;



	public void fail(String errorCode, String errorMsg) {
		this.success = false;
		this.code = errorCode;
		this.message = errorMsg;
	}

	public void success(T data) {
		this.success = true;
		this.data = data;
	}

	/***
	 * 创建成功结果
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> createSuccess() {
		ResultInfo<T> result = new ResultInfo<>();
		result.success(null);
		return result;
	}

	/***
	 * 创建成功结果
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> createSuccess(T data) {
		ResultInfo<T> result = new ResultInfo<>();
		result.setMessage("successfully");
		result.success(data);
		return result;
	}

	/***
	 * 创建成功结果
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> createSuccess(String message,T data) {
		ResultInfo<T> result = new ResultInfo<>();
		result.setMessage(message);
		result.success(data);
		return result;
	}

	/***
	 * 创建失败结果
	 * @param code
	 * @param msg
	 * @param <T>
	 * @return
	 */
	public static <T> ResultInfo<T> createFail(String code, String msg) {
		ResultInfo<T> result = new ResultInfo<>();
		result.fail(code, msg);
		return result;
	}

}
