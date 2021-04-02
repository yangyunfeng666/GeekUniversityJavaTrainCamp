package filter;

import io.netty.handler.codec.http.HttpResponse;

public class MyHttpResponseFilter implements HttpResposeFilter {

  @Override
  public void filter(HttpResponse fullHttpResponse) {
    //在返回数据的请求头里面添加number属性值2
    fullHttpResponse.headers().set("number", "2");
  }
}
