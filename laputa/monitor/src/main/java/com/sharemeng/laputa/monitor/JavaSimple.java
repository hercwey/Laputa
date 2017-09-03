package com.sharemeng.laputa.monitor;


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
import java.util.Random;

/**
 * Created by Tony on 2017/8/30.
 */
public class JavaSimple {
    static CollectorRegistry registry = new CollectorRegistry();

    static class ExampleServlet extends HttpServlet {
        static final Counter requests = Counter.build()
                .name("hello_worlds_total")
                .help("Number of hello worlds served.").register();


        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
                throws ServletException, IOException {
            resp.getWriter().println("Hello World! Push Gateway!");
            // Increment the number of requests.
            requests.inc();

            // test push
            try {
                executeBatchJob();
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

    public static void main(String[] args) throws Exception {
        Server server = new Server(1234);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);
        // Expose our example servlet.
        context.addServlet(new ServletHolder(new ExampleServlet()), "/");
        // Expose Promtheus metrics.
        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
        // Add metrics about CPU, JVM memory etc.
        DefaultExports.initialize();

        // add by tony

        // Start the webserver.
        server.start();
        server.join();
    }
}

