package com.example.cmotoemployee.ErrorHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final String LINE_SEPARATOR = "\n";

    private final Context myContext;

    public CrashHandler(Context paramContext) {
        this.myContext = paramContext;
    }

    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        StringWriter stringWriter = new StringWriter();
        paramThrowable.printStackTrace(new PrintWriter(stringWriter));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("************ CAUSE OF ERROR ************\n\n");
        stringBuilder.append(stringWriter.toString());
        stringBuilder.append("\n************ DEVICE INFORMATION ***********\n");
        stringBuilder.append("Brand: ");
        stringBuilder.append(Build.BRAND);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Device: ");
        stringBuilder.append(Build.DEVICE);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Model: ");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Id: ");
        stringBuilder.append(Build.ID);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Product: ");
        stringBuilder.append(Build.PRODUCT);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("\n************ FIRMWARE ************\n");
        stringBuilder.append("SDK: ");
        stringBuilder.append(Build.VERSION.SDK);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Release: ");
        stringBuilder.append(Build.VERSION.RELEASE);
        stringBuilder.append(this.LINE_SEPARATOR);
        stringBuilder.append("Incremental: ");
        stringBuilder.append(Build.VERSION.INCREMENTAL);
        stringBuilder.append(this.LINE_SEPARATOR);
        Intent intent = new Intent(this.myContext, ExceptionDisplay.class);
        intent.putExtra("error", stringBuilder.toString());
        intent.addFlags(268468224);
        this.myContext.startActivity(intent);
        Process.killProcess(Process.myPid());
        System.exit(10);
    }
}

