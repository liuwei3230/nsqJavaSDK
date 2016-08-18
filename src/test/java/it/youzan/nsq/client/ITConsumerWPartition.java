package it.youzan.nsq.client;

import com.youzan.nsq.client.Consumer;
import com.youzan.nsq.client.ConsumerImplV2;
import com.youzan.nsq.client.MessageHandler;
import com.youzan.nsq.client.entity.NSQMessage;
import com.youzan.nsq.client.exception.NSQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lin on 16/8/19.
 */
@Test(groups = {"ITConsumerWPartition-Base"}, dependsOnGroups = {"ITProducerWPartition-Base"}, priority = 5)
public class ITConsumerWPartition extends AbstractITConsumer{

    private static final Logger logger = LoggerFactory.getLogger(ITConsumerWPartition.class);

    public void test() throws NSQException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(10);
        final AtomicInteger received = new AtomicInteger(0);
        consumer = new ConsumerImplV2(config, new MessageHandler() {
            @Override
            public void process(NSQMessage message) {
                received.incrementAndGet();
                latch.countDown();
            }
        });
        consumer.setAutoFinish(true);
        consumer.subscribe(new String[]{"JavaTesting-Producer-Base"}, 0);
        consumer.start();
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
        logger.info("Consumer received {} messages.", received.get());
    }


    //start up two consumer subscribe on different partition, one should recieve and another should NOT
    public void testTwoConsumerOn2Partition() throws NSQException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(10);
        final AtomicInteger received = new AtomicInteger(0);
        Consumer recievedConsumer = new ConsumerImplV2(config, new MessageHandler() {
            @Override
            public void process(NSQMessage message) {
                latch.countDown();
                received.incrementAndGet();
            }
        });

        Consumer recievedNotConsumer = new ConsumerImplV2(config, new MessageHandler() {
            @Override
            public void process(NSQMessage message) {
                Assert.fail("consumer subscribe on partition 1 should not receive msg");
            }
        });


        recievedNotConsumer.setAutoFinish(true);
        recievedNotConsumer.subscribe(new String[]{"JavaTesting-Finish"}, 1);
        recievedNotConsumer.start();

        recievedConsumer.setAutoFinish(true);
        recievedConsumer.subscribe(new String[]{"JavaTesting-Finish"}, 0);
        recievedConsumer.start();
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
        Assert.assertEquals(received.get(), 10);
    }
}
