package filter;

import io.netty.handler.codec.http.HttpResponse;

public interface HttpResposeFilter {

  void filter(HttpResponse fullHttpResponse);

}
