package com.example.taskdispatcher;

import android.os.Looper;
import android.os.MessageQueue;


import com.example.taskdispatcher.task.DispatchRunnable;
import com.example.taskdispatcher.task.Task;

import java.util.LinkedList;
import java.util.Queue;
/**
 * 延迟初始化分发器
 */
public class DelayInitDispatcher {
    // 需要延迟执行的任务队列
    private Queue<Task> mDelayTasks = new LinkedList<>();

    /**
    * queueIdle方法在消息队列中的消息处理完毕时调用，如果返回true，表示让IdleHandler继续存活，
    * 如果返回false，就把idleHandler移除。所以根据我们延迟执行的任务队列中是否还有需要的处理
    * 的任务，来决定是否让idleHandler继续存活
    */
    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if(mDelayTasks.size()>0){
                // 一次从队列中取出一个任务，可以避免造成额外性能消耗
                Task task = mDelayTasks.poll();
                // Runnable调用run方法，较好比一个普通类调用的普通方法，不涉及线程问题。
                new DispatchRunnable(task).run();
            }
            return !mDelayTasks.isEmpty();
        }
    };

    public DelayInitDispatcher addTask(Task task){
        mDelayTasks.add(task);
        return this;
    }

    public void start(){
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }

}
