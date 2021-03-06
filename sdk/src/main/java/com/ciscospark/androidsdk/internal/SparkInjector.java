/*
 * Copyright 2016-2017 Cisco Systems Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ciscospark.androidsdk.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Application;

import com.cisco.spark.android.core.ApplicationDelegate;
import com.cisco.spark.android.core.RootModule;
import com.github.benoitdion.ln.DebugLn;
import com.github.benoitdion.ln.Ln;
import com.github.benoitdion.ln.NaturalLog;
import com.squareup.leakcanary.RefWatcher;

import me.helloworld.utils.reflect.Methods;

public class SparkInjector extends ApplicationDelegate {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AfterInjected {
    }

    private final SparkModule _module;

    public SparkInjector(Application application) {
        super(application);
        _module = new SparkModule();
    }

    @Override
    public void create(boolean startAuthenticatedUserTask) {
        super.create(false);
    }

    @Override
    protected Object getApplicationModule() {
        return _module;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void objectGraphCreated() {
        RootModule.setInjector(this);
        _module.setRefWatcher(RefWatcher.DISABLED);
    }

    @Override
    protected void afterInject() {

    }

    @Override
    protected NaturalLog buildLn() {
        return new DebugLn();
    }

    @Override
    protected void initializeExceptionHandlers() {
        // TODO XXX
    }

    public void inject(Object o) {
        super.inject(o);
        List<Method> methods = Methods.getMethodsMarkedWithAnnotation(o.getClass(), AfterInjected.class);
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(o);
            } catch (Throwable t) {
                Ln.e(t);
            }
        }
    }
}
