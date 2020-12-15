package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyEchoServer {

    private static final Logger LOG = LoggerFactory.getLogger(NettyEchoServer.class);

    //                       String <---------bytes
    // [_]                 <--___--________---____-----    <- 01010010  [_]
    //     00100101001 ->    String -> bytes ------------->

    public NettyEchoServer() throws InterruptedException {


        ByteBuf buf;

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new EchoHandler()
                            );
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8189).sync();
            LOG.info("Server started");
            channelFuture.channel().closeFuture().sync(); // block
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyEchoServer();
    }
}
