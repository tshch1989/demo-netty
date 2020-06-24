ChannelPipeline,ChannelHandlerContext基本说明请参照源码说明
每个channel创建时默认初始一个pipeline对象,参照代码:\
protected AbstractChannel(Channel parent) {\
    this.parent = parent;\
    id = newId();\
    unsafe = newUnsafe();\
    pipeline = newChannelPipeline();\
}\
每个pipeline初始化时候创建两个Context:\
protected DefaultChannelPipeline(Channel channel) {\
    this.channel = ObjectUtil.checkNotNull(channel, "channel");\
    succeededFuture = new SucceededChannelFuture(channel, null);\
    voidPromise =  new VoidChannelPromise(channel, true);\
    tail = new TailContext(this);\
    head = new HeadContext(this);\
    head.next = tail;\
    tail.prev = head;\
}\
每当添加一个channelHandler,会创建一个context对象:\
private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {\
    return new DefaultChannelHandlerContext(this, childExecutor(group), name, handler);\
}\
当handler添加到pipeline后会触发handleradd回调\
pipeline通过调用触发读取事件
@Override\
public final ChannelPipeline fireChannelRead(Object msg) {\
    AbstractChannelHandlerContext.invokeChannelRead(head, msg);\
    return this;\
}\
然后通过\
private AbstractChannelHandlerContext findContextInbound(int mask) {
    AbstractChannelHandlerContext ctx = this;
    EventExecutor currentExecutor = executor();
    do {
        ctx = ctx.next;
    } while (skipContext(ctx, currentExecutor, mask, MASK_ONLY_INBOUND));
    return ctx;
}
发现下一个需要触发的context,执行回调.
