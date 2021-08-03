package com.miraclebay.miracle.rpc.core.registry.zk.util;


import com.miraclebay.miracle.rpc.common.enums.RpcConfigEnum;
import com.miraclebay.miracle.rpc.common.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * curator(zookeeper client)工具
 * @author dangyong
 */
@Slf4j
public class CuratorUtils {

    public static final int BASE_SLEEP_TIME = 1000;
    public static final int MAX_PETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/miracle-rpc";
    public static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    public static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    public static CuratorFramework zkClient;
    public static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    private CuratorUtils(){}


    /**
     * 创建持久化节点：当客户端断开连接时，持久化节点不会被移除
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path){
        try{
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path)!=null){
                log.info("The node already exists. The node is :[{}]",path);
            }else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is: [{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        }catch (Exception e){
            log.error("create persistent node for path [{}] fail", path);
        }
    }


    /**
     * 获取一个节点的子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName){
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)){
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try{
            result  = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName,result);
            registerWatcher(rpcServiceName,zkClient);
        }catch (Exception e){
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }


    /**
     * 清空数据的注册
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress){
        REGISTERED_PATH_SET.stream().parallel().forEach(p->{
            try {
                if (p.endsWith(inetSocketAddress.toString())){
                    zkClient.delete().forPath(p);
                }
            }catch (Exception e){
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("all registered services on server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }


    public static CuratorFramework getZkClient(){
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyvalue());
        String zookeeperAddress = properties!=null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyvalue())!= null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyvalue()) : DEFAULT_ZOOKEEPER_ADDRESS;
        //如果zkclient已经启动，直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED){
            return zkClient;
        }
        //重连策略。重连三次，每次增加时间
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME,MAX_PETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                //连接zookeeper服务器
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            //等待30s连接zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return zkClient;
    }

    /**
     * 给节点添加监听器，子节点有变化时更新子节点路径列表
     * @param rpcSeriveName
     * @param zkClient
     * @throws Exception
     */
    private static void registerWatcher(String rpcSeriveName, CuratorFramework zkClient) throws Exception{
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcSeriveName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = ((curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcSeriveName, serviceAddress);
        });
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
