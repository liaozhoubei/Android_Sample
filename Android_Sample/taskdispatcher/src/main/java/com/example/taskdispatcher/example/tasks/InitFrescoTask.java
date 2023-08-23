package com.example.taskdispatcher.example.tasks;

import com.example.taskdispatcher.example.net.FrescoTraceListener;
import com.example.taskdispatcher.task.Task;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;

import java.util.HashSet;
import java.util.Set;
/**
 * CPU密集型任务: 主要是执行计算任务，响应时间很快，cpu一直在运行，这种任务cpu的利用率很高
 * IO密集型任务：主要是进行IO操作，执行IO操作的时间较长，这时cpu出于空闲状态，导致cpu的利用率不高
 * 为了合理最大限度的使用系统资源同时也要保证的程序的高性能，可以给CPU密集型任务和IO密集型任务配置一些线程数。
 * CPU密集型：线程个数为CPU核数。这几个线程可以并行执行，不存在线程切换到开销，提高了cpu的利用率的同时也减少了切换线程导致的性能损耗
 * IO密集型：线程个数可以较大。其中的线程在IO操作的时候，其他线程可以继续用cpu，提高了cpu的利用率
 */
public class InitFrescoTask extends Task {

    @Override
    public void run() {
        Set<RequestListener> listenerset = new HashSet<>();
        listenerset.add(new FrescoTraceListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(mContext).setRequestListeners(listenerset)
                .build();
        Fresco.initialize(mContext,config);
    }
}
