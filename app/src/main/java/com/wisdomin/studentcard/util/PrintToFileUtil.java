package com.wisdomin.studentcard.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


public class PrintToFileUtil {

    private static ScheduledExecutorService sExecutor ;
    private static int logCount ;
    private static boolean captureLogThreadOpen ;
    /**
     * 将内容直接写过文件中，自己设置路径
     * 这个是一边打印日志，一边将日志写入到file
     * 不建议直接new一个子线程做写入逻辑，建议开启线程池，避免到处开启线程损耗性能
     * @param input                     写入内容
     * @param filePath                  路径
     * @return
     */
    static boolean input2File(final String input, final String filePath) {
        if (sExecutor == null) {
            sExecutor = Executors.newScheduledThreadPool(5);
        }
        Future<Boolean> submit = sExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                BufferedWriter bw = null;
                try {
                    // 构造给定文件名的FileWriter对象，并使用布尔值指示是否追加写入的数据。
                    FileWriter fileWriter = new FileWriter(filePath, true);
                    bw = new BufferedWriter(fileWriter);
                    bw.write(input);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            return submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
