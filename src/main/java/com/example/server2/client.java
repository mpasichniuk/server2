package com.example.server2;

import com.example.server2.dto.AuthRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

    final class NettyClient extends ChannelInboundHandlerAdapter {
        public static final int MB_20 = 20 * 100000;

        @Override
        public void channelActive(ChannelHandlerContext c) throws Exception {
            super.channelActive(c);
        }

        @Override
        public void channelRead(ChannelHandlerContext c, Object msg) throws Exception {
            super.channelRead(c, msg);

        }

        static final String HOST = "127.0.0.1";
        static final int PORT = 3500;


        public static void main(String[] args) throws InterruptedException {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup);
                bootstrap.channel(NioSocketChannel.class);// Use NIO to accept new connections.
                bootstrap.remoteAddress("localhost", 3500);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ObjectDecoder(MB_20, ClassResolvers.cacheDisabled(null)));
                        p.addLast(new BasicHandler());

                        p.addLast(new StringDecoder());
                        p.addLast(new StringEncoder());

                        // This is our custom client handler which will have logic for chat.
                        p.addLast(new NettyClient());

                    }
                });

                // Start the client.
                ChannelFuture f = bootstrap.connect().sync();

                Channel channel = f.channel();
                channel.flush();

                AuthRequest authRequest = new AuthRequest("123", "acsb");
                channel.writeAndFlush(authRequest);
                f.channel().closeFuture().sync();


            }
        }




