package com.example.lz.android_okhttp_sample;

import android.util.Log;

import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DNSResolover {

    private static DNSResolover instance;

    public synchronized static DNSResolover getInstance() {
        if (instance == null) {
            instance = new DNSResolover();
        }
        return instance;
    }

    private Resolver resolver;

    private DNSResolover() {
        List<Resolver> resolvers = new ArrayList<Resolver>();
        try {
            resolvers.add(new SimpleResolver("114.114.114.114")); // 114
            resolvers.add(new SimpleResolver("223.5.5.5")); //阿里
            resolvers.add(new SimpleResolver("180.76.76.76"));//百度
            resolver = new ExtendedResolver(resolvers.toArray(new Resolver[resolvers.size()]));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public List<InetAddress> getAllByName(String hostname) {
        List<InetAddress> addressList = new ArrayList<>();
        try {
            Lookup lookup = new Lookup(hostname, Type.A);
            lookup.setResolver(resolver);
            Record[] records = lookup.run();
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                for (int i = 0; i < lookup.getAnswers().length; i++) {
                    String ip = lookup.getAnswers()[i].rdataToString();
                    Log.e("DNSResolover", ip);
                    InetAddress inetAddress = InetAddress.getByName(ip);
                    addressList.add(inetAddress);
                }
            }
        } catch (TextParseException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return addressList;
    }
}
