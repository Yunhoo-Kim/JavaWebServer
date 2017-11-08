package master.inputmodule;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerThread implements Runnable{


    private KafkaStream<byte[],byte[]> mKafkaStream;
    private int mThreadNum;
    private String mTopic;

    private LineListener<String> mListener;

    public ConsumerThread(KafkaStream<byte[], byte[]> mKafkaStream, int mThreadNum, String mTopic, LineListener<String> listener) {
        this.mKafkaStream = mKafkaStream;
        this.mThreadNum = mThreadNum;
        this.mTopic = mTopic;
        this.mListener= listener;
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = mKafkaStream.iterator();
        while(it.hasNext()){
            //TODO business logic here
            mListener.handle(String.valueOf(it.next().message()));
        }
    }

    public LineListener<String> getListener() {
        return mListener;
    }
}