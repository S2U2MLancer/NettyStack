import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import junit.framework.TestCase;
import slancer.nettystack.host.http.HttpSvr;


/**
 * Created by ashley on 17-4-7.
 */
public class HttpSvrTest extends TestCase{
    public void testHttpSvr(){
        ChannelInitializer<SocketChannel> channelInit = new HttpChannelInitializer();
        HttpSvr pserver = new HttpSvr(channelInit, 8088);
        pserver.startSvr();
    }
}
