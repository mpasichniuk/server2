package com.example.server2;

import com.example.server2.dto.BasicRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

    public class Network {

        private static final Network INSTANCE = new Network();

        private static final String HOST = "localhost";
        private static final int PORT = 4500;
        public static final int MB_20 = 20 * 1_000_000;
        private Channel channel;

        private Network() {
            Thread t = new Thread(() -> {
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            //При подключении к серверу на стороне клиента открывается канал SocketChannel
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    channel = socketChannel;
                                    socketChannel.pipeline().addLast(
                                            new ObjectDecoder(MB_20, ClassResolvers.cacheDisabled(null)),
                                            new ObjectEncoder()
                                    );
                                }
                            });
                    ChannelFuture future = b.connect(HOST, PORT).sync();
                    this.channel = future.channel();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                }
            });
            t.setDaemon(true);
            t.start();
        }

        public void close() {
            channel.close();
        }

        public void sendRequest(BasicRequest basicRequest) throws InterruptedException {
            channel.writeAndFlush(basicRequest).sync();
        }

        public static Network getInstance() {
            return INSTANCE;
        }

    }
