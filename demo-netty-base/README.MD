NioEventLoopGroup:事件处理组,负责管理child线程
NioEventLoop:单线程事件处理器,可NioEventLoopGroup管理,
默认通过ThreadPerTaskExecutor初始化线程,延迟初始化
NioEventLoop是真实的工作线程,它的run方法诠释了netty事件工作流程,
内部默认采用LinkedBlockingQueue为存储队列
=====================================================
初始化流程:
ServerBootstrap.bind()是一切的开始
ServerBootstrap.initAndRegister()
1.创建NioServerSocketChannel,此处是通过配置.channel(NioServerSocketChannel.class)提供
2.初始化NioServerSocketChannel,ServerBootstrap.init()方法
 2.1配置NioServerSocketChannel的可选项配置
 2.2配置NioServerSocketChannel属性
 2.3及其重要,添加NioServerSocketChannel的ChannelInitializer
 pipeline.addLast(new ChannelInitializer<Channel>() {
     @Override
     public void initChannel(final Channel ch) {//当NioServerSocketChannel执行register事件后会触发执行handleradd事件,在ChannelInitializer的handleradd中会执行initChannel
         final ChannelPipeline pipeline = ch.pipeline();
         ChannelHandler handler = config.handler();
         if (handler != null) {
             pipeline.addLast(handler);
         }

         ch.eventLoop().execute(new Runnable() {
             @Override
             public void run() {
                 pipeline.addLast(new ServerBootstrapAcceptor(//这是一个ChannelInboundHandlerAdapter子类
                         ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
             }
         });
     }
 });
 2.4NioServerSocketChannel注册到选择器上
  2.4.1AbstractUnsafe.register()会创建一个task来执行真实的注册
  eventLoop.execute(new Runnable() {//此处的eventLoop是 b.group(bossGroup, workerGroup)的bossGroup组,这也是整个netty启动执行的首个任务,此时会触发NioEventLoop创建实际工作线程
      @Override
      public void run() {
          register0(promise);
      }
  });
  private void register0(ChannelPromise promise) {
      try {
          // check if the channel is still open as it could be closed in the mean time when the register
          // call was outside of the eventLoop
          if (!promise.setUncancellable() || !ensureOpen(promise)) {
              return;
          }
          boolean firstRegistration = neverRegistered;
          doRegister();//这里实际进行注册selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this);,可以看到注册时候没有任何感兴趣事件
          neverRegistered = false;
          registered = true;

          // Ensure we call handlerAdded(...) before we actually notify the promise. This is needed as the
          // user may already fire events through the pipeline in the ChannelFutureListener.
          pipeline.invokeHandlerAddedIfNeeded();//此处触发NioServerSocketChannel绑定的pipeline中的handler的handlerAdded方法

          safeSetSuccess(promise);
          pipeline.fireChannelRegistered();//此处触发NioServerSocketChannel已经注册的事件
          // Only fire a channelActive if the channel has never been registered. This prevents firing
          // multiple channel actives if the channel is deregistered and re-registered.
          if (isActive()) {//NioServerSocketChannel不会执行此处代码,但是NioSocketChannel会执行此处代码
              if (firstRegistration) {
                  pipeline.fireChannelActive();//触发激活事件
              } else if (config().isAutoRead()) {
                  // This channel was registered before and autoRead() is set. This means we need to begin read
                  // again so that we process inbound data.
                  //
                  // See https://github.com/netty/netty/issues/4805
                  beginRead();//绑定感兴趣的读事件到选择器上
              }
          }
      } catch (Throwable t) {
          // Close the channel directly to avoid FD leak.
          closeForcibly();
          closeFuture.setClosed();
          safeSetFailure(promise, t);
      }
  }
ServerBootstrap.doBind0()执行端口绑定
1.AbstractChannel.bind()//javaChannel().bind(localAddress, config.getBacklog());最终调用绑定地址
if (!wasActive && isActive()) {
    invokeLater(new Runnable() {
        @Override
        public void run() {
            pipeline.fireChannelActive();//绑定后触发NioServerSocketChannel的active流程
        }
    });
}
2.AbstractNioChannel.doBeginRead()//准备从channel中读取
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    final int interestOps = selectionKey.interestOps();
    if ((interestOps & readInterestOp) == 0) {//这一步,实际注册感兴趣事件到选择器上,NioServerSocketChannel是accept,按实现不同注册事件不同
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}
到这里NioServerSocketChannel已经绑定到指定地址上,并且绑定了accept事件到选择器上,等待请求连接了
现在让我们打开浏览器,访问localhost:8080,
进入外部连接接入流程:
NioEventLoop.run()一切从这里开始,
1.1strategy = select(curDeadlineNanos);//此处等待选择器返回
1.2NioEventLoop.processSelectedKeysOptimized()处理选择的key
if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
    unsafe.read();//我们当前是OP_ACCEPT事件,触发读,unsafe可以当做是NioServerSocketChannel
}
1.3AbstractNioMessageChannel.read()方法被调用
 1.3.1创建接入的NioSocketChannel
 1.3.2触发NioServerSocketChannel上的pipeline的read事件,当前仅有一个handler:ServerBootstrapAcceptor(参照前文:2.3)
 在ServerBootstrapAcceptor执行的事情和初始NioServerSocketChannel类似,添加配置选项,添加属性,添加用户定义handler,
 在这里配置的:
 .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
     @Override
     public void initChannel(SocketChannel ch) throws Exception {
         ch.pipeline().addLast(new DiscardServerHandler());
         ch.pipeline().addLast(new DiscardServerHandler());
     }
 })
 最后,进行注册: childGroup.register(child).addListener(new ChannelFutureListener() {
                     @Override
                     public void operationComplete(ChannelFuture future) throws Exception {
                         if (!future.isSuccess()) {
                             forceClose(child, future.cause());
                         }
                     }
                 });
 注册后,会在active后,把感兴趣事件绑定到选择器上,然后监听到读事件,触发channel的读取事件,然后各个handler执行一遍.
 AbstractNioByteChannel.read()//字节流默认读取
