package filter;

import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResposeFilter {

  void filter(FullHttpResponse fullHttpResponse);

}
