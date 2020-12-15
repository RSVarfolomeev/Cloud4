package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EchoHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOG = LoggerFactory.getLogger(EchoHandler.class);
    private static final ConcurrentLinkedQueue<ChannelHandlerContext> clients
            = new ConcurrentLinkedQueue<>();

    private static int counter = 0;
    private String userName;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        counter++;
        clients.add(ctx);
        userName = "user" + counter;
        LOG.info("Client with nick: {}, with ip: {} connected", userName, ctx.channel().localAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        counter--;
        clients.remove(ctx);
        LOG.info("Client with nick: {}, with ip: {} leave", userName, ctx.channel().localAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String s) throws Exception {
        for (ChannelHandlerContext client : clients) {
            client.writeAndFlush("[" + userName + "]: " + s);
        }
    }
}
