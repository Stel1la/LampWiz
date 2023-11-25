package com.sasha.lampwiz;

import android.app.Application;

import com.sasha.lampwiz.bluetooth.uuid.Manufacturer;
import com.sasha.lampwiz.bluetooth.uuid.Service;

public class LampWiz extends Application {

    private static transient Manufacturer manufacturers;
    private static transient Service standardServices;
    private static transient Service memberServices;
    private static transient Service lampwizServices;

    private static LampWiz lampWiz = null;

    public void onCreate() {

        super.onCreate();

        if (null == LampWiz.lampWiz) {
            LampWiz.lampWiz = this;
        }

        LampWiz.manufacturers = new Manufacturer(this.getApplicationContext(), "manufacturers.properties", "manufacturer");
        LampWiz.standardServices = new Service(this.getApplicationContext(), "services.properties", "service.standard");
        LampWiz.memberServices = new Service(this.getApplicationContext(), "services.properties", "service.member");
        LampWiz.lampwizServices = new Service(this.getApplicationContext(), "services.properties", "service.lampwiz");
    }

    public static LampWiz application() {

        return LampWiz.lampWiz;
    }

    public static Manufacturer manufacturers() {

        return LampWiz.manufacturers;
    }

    @SuppressWarnings("unused")
    public static Service standardServices() {

        return LampWiz.standardServices;
    }

    @SuppressWarnings("unused")
    public static Service memberServices() {

        return LampWiz.memberServices;
    }

    public static Service lampwizServices() {

        return LampWiz.lampwizServices;
    }
}
