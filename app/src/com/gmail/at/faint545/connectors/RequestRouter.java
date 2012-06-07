package com.gmail.at.faint545.connectors;

import android.os.Bundle;
import android.util.Log;

import com.gmail.at.faint545.Remote;
import com.gmail.at.faint545.connectors.RequestHandler.Request;

/**
 * A RequestRouter takes in any request sent by the {@link RequestReceiver}
 * and passes it along to the appropriate {@link RequestHandler} method.
 * @author alex
 *
 */
public class RequestRouter {
  private static final String LOGTAG = "RequestRouter";
  
  private RequestHandler mRequestHandler;
  
  public RequestRouter(Remote remote, Object callback) {
    mRequestHandler = new RequestHandler(remote,callback);
  }

  /**
   * This is the entry point for any requests. All requests passed
   * through here will be routed to {@link RequestHandler} accordingly.
   * @param request - A {@link Request}
   * @param data - An optional {@link Bundle} with any additional info needed to complete this request.
   * @return True if request was routed successfully, false otherwise.
   */
  public boolean processRequest(Request request,Bundle data) {    
    switch(request) {
    case DOWNLOAD:
      mRequestHandler.download(data);
      return true;
    case DELETE:
      mRequestHandler.delete(data);
      return true;
    case PAUSE:
      mRequestHandler.pause(data);
      return true;
    case RESUME:
      mRequestHandler.resume(data);
      return true;
    case RETRY:
      mRequestHandler.retry(data);
      return true;
    case SET_SPEED_LIMIT:
      mRequestHandler.speedLimit(data);
      return true;
    default: 
      return false;
    }
  }
}
