package service;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names  = {"--port","--p"}, description = "port to char server")
    private String port;
    @Parameter(names  = {"--host","--h"}, description = "location of server")
    private String address;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
