package com.gmail.at.faint545.connectors;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.factories.SabPostFactory;
import com.gmail.at.faint545.services.SabTask;
import com.gmail.at.faint545.services.SabTask.SabTaskListener;
import com.gmail.at.faint545.status.ResultStatus;

/**
 * This RequestHandler will actually the work for each
 * request. The {@link RequestRouter} determines which
 * method of this class to call.
 * @author alex
 *
 */
public class RequestHandler implements SabTaskListener {
  private static final String LOGTAG = "RequestHandler";

  private Remote mRemote;
  private RequestListener mCallback;
  
  public interface RequestListener {
    public void onQueueReceived(JSONArray queue);
    public void onHistoryReceived(JSONArray queue);
    public void onStatusReceived(boolean status);
    public void onErrorReceived(String message);
  }

  public RequestHandler(Remote remote, Object callback) {
    mRemote = remote;
    if(callback instanceof RequestListener) {
      mCallback = (RequestListener) callback;
    }
    else {
      throw new IllegalStateException(callback.getClass().getName() + " must implement RequestListener!");
    }
  }

  /**
   * A request can be one of the following:
   * DELETE, PAUSE, RESUME, RETRY, SET_SPEED_LIMIT.
   * @author alex
   *
   */
  public enum Request {
    DELETE, PAUSE, RESUME, RETRY, SET_SPEED_LIMIT, DOWNLOAD;

    public static int toInt(Request request) {
      switch(request) {
      case DELETE:
        return 0;
      case PAUSE:
        return 1;
      case RESUME:
        return 2;
      case RETRY:
        return 3;
      case SET_SPEED_LIMIT:
        return 4;
      case DOWNLOAD:
        return 5;
      default:
        return -1;
      }
    }

    public static Request fromInt(int i) {
      switch(i) {
      case 0:
        return DELETE;
      case 1:
        return PAUSE;
      case 2:
        return RESUME;
      case 3:
        return RETRY;
      case 4:
        return SET_SPEED_LIMIT;
      case 5:
        return DOWNLOAD;
      default:
        return null;
      }
    }
  }

  /**
   * Executes a download command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void download(Bundle data) {
    int offset = data == null ? 0 : data.getInt("offset");
    HttpPost queuePost = SabPostFactory.getQueueInstance(mRemote, offset);
    HttpPost historyPost = SabPostFactory.getHistoryInstance(mRemote, offset);
    new SabTask(this).execute(queuePost);
    new SabTask(this).execute(historyPost);
  }
  
  /**
   * Executes a delete command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void delete(Bundle data) {
    String value = data.getString("value");
    String mode = data.getString("mode");
    HttpPost post = SabPostFactory.getDeleteInstance(mRemote, mode, value);
    new SabTask(this).execute(post);
  }

  /**
   * Executes a pause command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void pause(Bundle data) {
    String value = data.getString("value");
    HttpPost post = SabPostFactory.getPauseInstance(mRemote, value);
    new SabTask(this).execute(post);
  }

  /**
   * Executes a resume command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void resume(Bundle data) {
    String value = data.getString("value");
    HttpPost post = SabPostFactory.getResumeInstance(mRemote, value);
    new SabTask(this).execute(post);
  }

  /**
   * Executes a retry command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void retry(Bundle data) {
    String value = data.getString("value");
    HttpPost post = SabPostFactory.getRetryInstance(mRemote, value);
    new SabTask(this).execute(post);
  } 

  /**
   * Executes a speed limit command to the SABNzbd application.
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   */
  public void speedLimit(Bundle data) {
    String value = data.getString("value");
    HttpPost post = SabPostFactory.getSpeedLimitInstance(mRemote, value);
    new SabTask(this).execute(post);
  }

  @Override
  public void onTaskCompleted(ResultStatus<String> status) {
  	switch(status.code) {
  	case HttpStatus.SC_OK:
  		processResult(status.data);
  		break;
  	case HttpStatus.SC_SERVICE_UNAVAILABLE:
  		mCallback.onErrorReceived(status.data);
  		break;
  	}
  }

	private void processResult(String results) {
    try {
      JSONObject resultObject = new JSONObject(results);
      if(resultObject.has("queue")) {
        mCallback.onQueueReceived(resultObject.getJSONObject("queue").getJSONArray("slots"));
      }
      else if(resultObject.has("history")) {
        mCallback.onHistoryReceived(resultObject.getJSONObject("history").getJSONArray("slots"));
      }
      else if(resultObject.has("status")) {
        mCallback.onStatusReceived(resultObject.getBoolean("status"));
      }
    } 
    catch (JSONException e) {
      e.printStackTrace();
    }
  } 
}
