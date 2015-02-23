package org.cirrus.mobi.probeinterceptors.demo;

import android.app.Activity;
import android.os.Bundle;

import org.cirrus.mobi.probe.interceptors.ImageViewInterceptor;
import org.lucasr.probe.Probe;


public class ImageViewInterceptorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Probe.deploy(this, new ImageViewInterceptor.Builder(this).build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_interceptor);
    }

}
