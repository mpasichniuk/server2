package com.example.server2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

public class NettyS {
        static final int PORT = 8007;

        public static void main(String[] args) throws Exception {

            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup) // Set boss & worker groups
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();

                                p.addLast(new StringDecoder());
                                p.addLast(new StringEncoder());

                                p.addLast(new ServerHandler());
                            }
                        });

                // Start the server.
                ChannelFuture f = b.bind(PORT).sync();
                System.out.println("Netty Server started.");

                // Wait until the server socket is closed.
                f.channel().closeFuture().sync();
            } finally {
                // Shut down all event loops to terminate all threads.
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

    @ChannelHandler.Sharable
    class ServerHandler extends SimpleChannelInboundHandler<String> {

        static final List<Channel> channels = new ArrayList<Channel>();

        @Override
        public void channelActive(final ChannelHandlerContext ctx) {
            System.out.println("Client joined - " + ctx);
            channels.add(ctx.channel());
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("Message received: " + msg);
            for (Channel c : channels) {
                c.writeAndFlush("Hello " + msg + '\n');
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println("Closing connection for client - " + ctx);
            ctx.close();
        }
    }




