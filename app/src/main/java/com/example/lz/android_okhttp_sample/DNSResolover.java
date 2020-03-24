package com.example.lz.android_okhttp_sample;

import java.net.InetAddress;

public class DNSResolover {

    private static DNSResolover instance;

    private DNSResolover() {
    }

    public synchronized static DNSResolover getInstance() {
        if (instance == null) {
            instance = new DNSResolover();
        }
        return instance;
    }

    public InetAddress[] getAllByName() {
        return null;
    }
}
