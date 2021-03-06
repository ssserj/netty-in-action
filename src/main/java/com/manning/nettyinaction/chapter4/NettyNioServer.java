package com.manning.nettyinaction.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelStateHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Listing 4.4  of <i>Netty in Action</i>
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class NettyNioServer {

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8"));
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
             .channel(NioServerSocketChannel.class)
             .localAddress(new InetSocketAddress(port))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) 
                     throws Exception {
                     ch.pipeline().addLast(new ChannelStateHandlerAdapter() {
                         @Override
                         public void channelActive(ChannelHandlerContext ctx) throws Exception {
                             ctx.write(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                         }

                         @Override
                         public void inboundBufferUpdated(ChannelHandlerContext ctx) throws Exception {
                             ctx.fireInboundBufferUpdated();
                         }
                     });
                 }
             });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            b.shutdown();
        }
    }
}

