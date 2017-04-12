
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

/**
 * Created by ashley on 17-4-7.
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    public class HttpHandler extends ChannelInboundHandlerAdapter {
        protected void sendJsonResponse(ChannelHandlerContext ctx, JSONObject json) {
            sendJsonResponse(ctx, HttpResponseStatus.OK, json);
        }

        protected void sendJsonResponse(ChannelHandlerContext ctx, HttpResponseStatus status, JSONObject json) {
            if (ctx.channel().isActive()) {
                ByteBuf buffer = Unpooled.buffer();
                byte[] content = json.toString().getBytes();
                buffer.writeBytes(content);
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
                response.headers().set(HttpHeaderNames.ACCEPT, "application/json;charset=UTF-8");
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            sendJsonResponse(ctx, new JSONObject("{'a':1}"));
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("serverocodec", new HttpServerCodec());
        p.addLast("aggegator", new HttpObjectAggregator(1048576));// 1024*1024
        p.addLast(new HttpHandler());
    }
}
