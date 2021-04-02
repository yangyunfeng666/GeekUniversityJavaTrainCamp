package filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class MyHttpResponseFilter implements HttpResposeFilter {

  @Override
  public void filter(FullHttpResponse fullHttpResponse) {
    //在返回数据的请求头里面添加number属性值2
    fullHttpResponse.headers().set("number", "2");
  }
}
