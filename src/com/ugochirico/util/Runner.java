package com.ugochirico.util;

import java.util.Timer;
import java.util.TimerTask;

public class Runner
{
    public static void run(final Runnable run, final RunnerListener listener)
    {
        run(new Runnable()
            {
                @Override
                public void run()
                {
                    run.run();
                    if(listener != null)
                        listener.completed();
                }
            }
        );
    }

    public static void run(Runnable run)
    {
        new Thread(run).start();
    }

    public static void run(final Runnable run, int stackSize, final RunnerListener listener)
    {
        run(new Runnable()
        {
            @Override
            public void run()
            {
                run.run();
                if(listener != null)
                    listener.completed();
            }
        }, stackSize);
    }

    public static void run(Runnable run, int stackSize)
    {
		new Thread(new ThreadGroup("runnable"), run, "runnable", stackSize).start();
    }

    public static void runDelayed(final Runnable run, int delay)
    {
    	TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				run.run();
			}
		};

    	new Timer().schedule(tt, delay);
    }

    public static void runDelayed(final Runnable run, int delay, final RunnerListener listener)
    {
        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                run.run();
                if(listener != null)
                    listener.completed();
            }
        };

        new Timer().schedule(tt, delay);
    }


    public interface RunnerListener
    {
        public void completed();
    }

}
