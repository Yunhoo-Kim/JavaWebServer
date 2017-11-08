package master.inputmodule;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KafkaInputModule implements Runnable{
    private static final String TOPIC = "";
    private static final Integer NUM_THREADS = 20;
    private List<String> topicList;
    private LineListener<String> mListener;

    public KafkaInputModule(List<String> topicList, LineListener<String> listener) {
        this.topicList = topicList;
        this.mListener = listener;
    }

    @Override
    public void run() {
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(createConsumerConfig("localhost:9999","collog"));

        Map<String,Integer> topicCountMap = new HashMap<String,Integer>();
        for(String topic : topicList){
            topicCountMap.put(topic,new Integer(NUM_THREADS));
        }
        topicCountMap.put(TOPIC,NUM_THREADS);

        Map<String,List<KafkaStream<byte[],byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);


        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        List<Future<Integer>> futures = new ArrayList<Future<Integer>>(NUM_THREADS * topicList.size());

        for(String topic : topicList) {
            List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
            int threadNum = 0;
            for (final KafkaStream<byte[], byte[]> stream : streams) {
                executor.submit(new ConsumerThread(stream,threadNum,topic,mListener));
                threadNum++;
            }
        }


    }

    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId){
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }


    private void init(){

    }
}