package com.overops.report.service;

import com.takipi.api.client.observe.Observer;

import java.io.PrintStream;

public class ApiClientObserver implements Observer {

    private final PrintStream printStream;
    private final boolean verbose;

    public ApiClientObserver(PrintStream printStream, boolean verbose) {
        this.printStream = printStream;
        this.verbose = verbose;
    }

    @Override
    public void observe(Operation operation, String url, String request, String response, int responseCode, long time) {
        StringBuilder output = new StringBuilder();

        output.append(String.valueOf(operation));
        output.append(" took ");
        output.append(time / 1000);
        output.append("ms for ");
        output.append(url);

        if (verbose) {
            output.append(". Response: ");
            output.append(response);
        }

        printStream.println(output.toString());
    }
}