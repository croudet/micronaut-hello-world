package hello.world.netty.handler.codec.http;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author croudet
 */
@Sharable
@Singleton
@Named("accessLogger")
@Requires(property = "application.server.access-logger.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
public class HttpServerAccessLogHandler extends ChannelDuplexHandler {
    private final Logger accessLogger;

    /** The HTTP {@code X-Forwarded-For} header field name (superseded by {@code Forwarded}). */
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    /**
     * The HTTP <a href="https://tools.ietf.org/html/rfc7239">{@code Forwarded}</a> header field name.
     */
    public static final String FORWARDED = "Forwarded";

    private static final AttributeKey<AccessLog> LOG_HANDLER_CONTEXT = AttributeKey.valueOf("logHandlerContext");
    private static final String MISSING = "-";

    public static final String HTTP_ACCESS_LOGGER = "HTTP_ACCESS_LOGGER";

    // https://github.com/reactor/reactor-netty/blob/master/src/main/java/reactor/netty/http/server/AccessLogHandler.java

    private static class AccessLog {
        static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z",
                Locale.US);
        static final String COMMON_LOG_FORMAT = "{} - [{}] \"{} {} {}\" {} {} {} {}ms";

        String inetAddress;
        String method;
        String uri;
        String protocol;
        int port;
        boolean chunked;
        int status;
        long startTime;
        long contentLength;
        String zonedDateTime;

        AccessLog() {
            this.zonedDateTime = ZonedDateTime.now().format(DATE_TIME_FORMATTER);
        }

        void reset() {
            this.zonedDateTime = ZonedDateTime.now().format(DATE_TIME_FORMATTER);
            inetAddress = null;
            method = null;
            uri = null;
            protocol = null;
            port = -1;
            status = -1;
            startTime = 0L;
            contentLength = 0L;
            chunked = false;
        }

        void increaseContentLength(long contentLength) {
            this.contentLength += contentLength;
        }

        @SuppressWarnings("boxing")
        void logAccess(Logger accessLogger) {
            final long timeElapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            accessLogger.info(COMMON_LOG_FORMAT, inetAddress, zonedDateTime, method, uri, protocol, status,
                    contentLength > -1L ? contentLength : MISSING, port, timeElapsed);
        }
    }

    public HttpServerAccessLogHandler(@Value("${application.server.access-logger.name:HTTP_ACCESS_LOGGER}") String loggerName) {
        this(LoggerFactory.getLogger(loggerName == null || loggerName.isEmpty() ? HTTP_ACCESS_LOGGER : loggerName));
    }

    public HttpServerAccessLogHandler() {
        this(LoggerFactory.getLogger(HTTP_ACCESS_LOGGER));
    }

    public HttpServerAccessLogHandler(Logger accessLogger) {
        super();
        this.accessLogger = accessLogger;
    }

    static String processXForwardedFor(String xforwardedFor) {
        // can contain multiple IPs for proxy chains. the first ip is our
        // client.
        final int firstComma = xforwardedFor.indexOf(',');
        if (firstComma >= 0) {
            return xforwardedFor.substring(0, firstComma);
        } else {
            return xforwardedFor;
        }
    }

    static String processForwarded(String forwarded) {
        final int firstComma = forwarded.indexOf(',');
        final String firstForward = (firstComma >= 0 ? forwarded.substring(0, firstComma) : forwarded)
                .toLowerCase(Locale.US);
        int startIndex = firstForward.indexOf("for");
        if (startIndex == -1) {
            return null;
        }
        final int semiColonIndex = firstForward.indexOf(';');
        final int endIndex = semiColonIndex >= 0 ? semiColonIndex : firstForward.length();
        // skip 'for='
        startIndex += 4;
        // consume space and '='
        while (startIndex < endIndex) {
            char c = firstForward.charAt(startIndex);
            if (Character.isWhitespace(c) || c == '=') {
                ++startIndex;
            } else {
                return firstForward.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    static String inetAddress(SocketChannel channel, HttpRequest request) {
        // maybe this request was proxied or load balanced.
        // try and get the real originating IP
        final String xforwardedFor = request.headers().get(X_FORWARDED_FOR, null);
        if (xforwardedFor == null) {
            final String forwarded = request.headers().get(FORWARDED, null);
            if (forwarded != null) {
                String inet = processForwarded(forwarded);
                if (inet != null) {
                    return inet;
                }
            }
        } else {
            return processXForwardedFor(xforwardedFor);
        }
        return channel.remoteAddress().getHostString();
    }

    private static AccessLog accessLog(SocketChannel channel) {
        final Attribute<AccessLog> attr = channel.attr(LOG_HANDLER_CONTEXT);
        AccessLog accessLog = attr.get();
        if (accessLog == null) {
            accessLog = new AccessLog();
            attr.set(accessLog);
        } else {
            accessLog.reset();
        }
        return accessLog;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (accessLogger.isInfoEnabled() && msg instanceof HttpRequest) {
            final SocketChannel channel = (SocketChannel) ctx.channel();
            AccessLog accessLog = accessLog(channel);
            final HttpRequest request = (HttpRequest) msg;
            accessLog.startTime = System.nanoTime();
            accessLog.inetAddress = inetAddress(channel, request);
            accessLog.port = channel.localAddress().getPort();
            accessLog.method = request.method().name();
            accessLog.uri = request.uri();
            accessLog.protocol = request.protocolVersion().text();
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // modify message on way out to add headers if needed
        if (accessLogger.isInfoEnabled()) {
            processWriteEvent(ctx, msg, promise);
        } else {
            super.write(ctx, msg, promise);
        }
    }

    private void logAtLast(ChannelHandlerContext ctx, Object msg, ChannelPromise promise, AccessLog accessLog) {
        ctx.write(msg, promise.unvoid()).addListener(future -> {
            if (future.isSuccess()) {
                accessLog.logAccess(accessLogger);
            }
        });
    }

    private static boolean processHttpResponse(HttpResponse response, AccessLog accessLog, ChannelHandlerContext ctx, ChannelPromise promise) {
        final HttpResponseStatus status = response.status();
        if (status.equals(HttpResponseStatus.CONTINUE)) {
            ctx.write(response, promise);
            return true;
        }
        final boolean chunked = HttpUtil.isTransferEncodingChunked(response);
        accessLog.chunked = chunked;
        accessLog.status = status.code();
        if (!chunked) {
            accessLog.contentLength = HttpUtil.getContentLength(response, -1L);
        }
        return false;
    }

    private void processWriteEvent(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception  {
        final AccessLog accessLog = ctx.channel().attr(LOG_HANDLER_CONTEXT).get();
        if (accessLog != null && accessLog.method != null) {
            if (msg instanceof HttpResponse && processHttpResponse((HttpResponse) msg, accessLog, ctx, promise)) {
                return;
            }
            if (msg instanceof LastHttpContent) {
                if (accessLog.chunked) {
                    accessLog.increaseContentLength(((LastHttpContent) msg).content().readableBytes());
                }
                logAtLast(ctx, msg, promise, accessLog);
                return;
            } else if (accessLog.chunked) {
                if (msg instanceof ByteBufHolder) {
                    accessLog.increaseContentLength(((ByteBufHolder) msg).content().readableBytes());
                } else if (msg instanceof ByteBuf) {
                    accessLog.increaseContentLength(((ByteBuf) msg).readableBytes());
                }
            }
        }
        super.write(ctx, msg, promise);
    }
}
