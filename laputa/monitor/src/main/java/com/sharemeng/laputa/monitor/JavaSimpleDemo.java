package com.sharemeng.laputa.monitor;


import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by Tony on 2017/8/30.
 */
public class JavaSimpleDemo {
    static CollectorRegistry registry = new CollectorRegistry();

    static class ExampleServlet extends HttpServlet {
        static final Counter requests = Counter.build()
                .name("hello_worlds_total").labelNames("ip","status")
                .help("Number of hello worlds served.").register();


        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
                throws ServletException, IOException {
            resp.getWriter().println("Hello World! Push Gateway!");
            // Increment the number of requests.
            requests.labels("192.168.1.1","1").inc();
            requests.labels("192.168.1.2","0").inc();
            requests.labels("192.168.1.2","0").inc();

            // test push
            try {
                //executeBatchJob();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //PushGateway test
        void executeBatchJob() throws Exception {

            Gauge duration = Gauge.build()
                    .name("my11_batch_job_duration_seconds").help("Duration of my batch job in seconds.").register(registry);
            Gauge.Timer durationTimer = duration.startTimer();
            try {
                // Your code here.
                Thread.sleep(new Random().nextInt(1000));

                // This is only added to the registry after success,
                // so that a previous success in the Pushgateway isn't overwritten on failure.
                Gauge lastSuccess = Gauge.build()
                        .name("my11_batch_job_last_success").help("Last time my batch job succeeded, in unixtime.").register(registry);
                lastSuccess.setToCurrentTime();

                Counter reqs = Counter.build().name("my11_batch_job_total").help("Request number of my batch job.").register(registry);
                reqs.inc();
            } finally {
                durationTimer.setDuration();
                PushGateway pg = new PushGateway("127.0.0.1:9091");
                pg.pushAdd(registry, "my11_batch_job");
            }
        }
    }


    /**
     * Jetty server
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//        Server server = new Server(1234);
//        ServletContextHandler context = new ServletContextHandler();
//        context.setContextPath("/");
//        server.setHandler(context);
//        // Expose our example servlet.
//        context.addServlet(new ServletHolder(new ExampleServlet()), "/");
//        // Expose Promtheus metrics.
//        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
//        // Add metrics about CPU, JVM memory etc.
//        DefaultExports.initialize();
//
//        // add by tony
//
//        // Start the webserver.
//        server.start();
//        server.join();
//    }

    /**
     * Http server
     * @param args
     */
    public static void main(String[] args) throws IOException{
        InetSocketAddress addr = new InetSocketAddress(1234);
        HttpServer server = HttpServer.create(addr, 0);

        server.createContext("/", new MyHandler());
        server.createContext("/metrics", new  MyHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 8080");
    }
}

class MyHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            Headers requestHeaders = exchange.getRequestHeaders();
            Set<String> keySet = requestHeaders.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                List values = requestHeaders.get(key);
                String s = key + " = " + values.toString() + "\n";
                responseBody.write(s.getBytes());
            }
            responseBody.close();
        }
    }
}
